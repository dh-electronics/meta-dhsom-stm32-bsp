if test -n "${distro_bootpart}"; then
  setenv partition "${distro_bootpart}"
else
  setenv partition "${bootpart}"
fi

if test ! -e ${devtype} ${devnum}:${partition} boot/fitImage; then
  echo "This boot medium does not contain a suitable fitImage file for this system."
  echo "devtype=${devtype} devnum=${devnum} partition=${partition} loadaddr=${loadaddr}"
  echo "Aborting boot process."
  exit 1
fi

part uuid ${devtype} ${devnum}:${partition} uuid

# Some STM32MP1-based systems do not encode the baudrate in the console variable
if test "${console}" = "ttySTM0" && test -n "${baudrate}"; then
  setenv console "${console},${baudrate}"
fi

if test -n "${console}"; then
  setenv bootargs "${bootargs} console=${console}"
fi

setenv bootargs "${bootargs} root=PARTUUID=${uuid} rw rootwait consoleblank=0"

load ${devtype} ${devnum}:${partition} ${loadaddr} boot/fitImage
if test $? != 0 ; then
  echo "Failed to load fitImage file for this system from boot medium."
  echo "devtype=${devtype} devnum=${devnum} partition=${partition} loadaddr=${loadaddr}"
  echo "Aborting boot process."
  exit 2
fi

# A custom script exists to load DTOs
if test -n "${loaddtoscustom}" ; then
  # Pull DTOs from fitImage and manually apply them to base DT
  if test -n "${loaddtos}" ; then
    # Matches UBOOT_DTB_LOADADDRESS in OE layer machine config
    setexpr loadaddrdtb 0xcff00000
    # Matches UBOOT_DTBO_LOADADDRESS in OE layer machine config
    setexpr loadaddrdtbo 0xcff80000

    setexpr loaddtossep gsub '#conf' ' fdt' "${loaddtos}"
    setexpr loaddtb 1

    for i in ${loaddtossep} ; do
      if test ${loaddtb} = 1 ; then
        echo "Using base DT ${i}"
        imxtract ${loadaddr} ${i} ${loadaddrdtb} ;
        fdt addr ${loadaddrdtb}
        fdt resize
        setenv loaddtb 0
      else
        echo "Applying DTO ${i}"
        imxtract ${loadaddr} ${i} ${loadaddrdtbo} ;
        fdt apply ${loadaddrdtbo}
      fi
    done

    setenv loaddtb
    setenv loaddtossep
    setenv loadaddrdtbo
    setenv loadaddrdtb
  fi

  # Run the custom DTO loader script
  #
  # In case 'loaddtos' variable is set, then the 'fdt' command is already
  # configured to point to a DT, on top of which all the DTOs present in
  # the fitImage and selected by the 'loaddtos' are applied. Hence, the
  # user is now free to apply any additional custom DTOs loaded from any
  # other source.
  #
  # In case 'loaddtos' variable is not set, the 'loaddtoscustom' script
  # must configure the 'fdt' command to point to the custom DT.
  run loaddtoscustom
  if test -z "${bootm_args}" ; then
    setenv bootm_args "${loadaddr} - ${fdtaddr}"
  fi
else
  setenv bootm_args "${loadaddr}${loaddtos}"
fi

echo "Booting the Linux kernel..." \
&& bootm ${bootm_args}
