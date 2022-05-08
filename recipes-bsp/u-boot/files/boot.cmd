if test -n "${distro_bootpart}"; then
  setenv bootpartition "${distro_bootpart}"
else
  setenv bootpartition "${bootpart}"
fi

setenv bootfile boot/fitImage
setenv rootpartition ${bootpartition}

# Handle legacy split boot and root partition layout
if test ! -e ${devtype} ${devnum}:${bootpartition} ${bootfile}; then
	setenv bootfile fitImage
	setexpr rootpartition ${bootpartition} + 1
fi

if test ! -e ${devtype} ${devnum}:${bootpartition} ${bootfile}; then
  echo "This boot medium does not contain a suitable fitImage file for this system."
  echo "devtype=${devtype} devnum=${devnum} partition=${bootpartition} loadaddr=${loadaddr}"
  echo "Aborting boot process."
  exit 1
fi

part uuid ${devtype} ${devnum}:${rootpartition} uuid

# Some STM32MP1-based systems do not encode the baudrate in the console variable
if test "${console}" = "ttySTM0" && test -n "${baudrate}"; then
  setenv console "${console},${baudrate}"
fi

if test -n "${console}"; then
  setenv bootargs "${bootargs} console=${console}"
fi

setenv bootargs "${bootargs} root=PARTUUID=${uuid} rw rootwait consoleblank=0"

load ${devtype} ${devnum}:${bootpartition} ${loadaddr} ${bootfile}
if test $? != 0 ; then
  echo "Failed to load fitImage file for this system from boot medium."
  echo "devtype=${devtype} devnum=${devnum} partition=${bootpartition} loadaddr=${loadaddr}"
  echo "Aborting boot process."
  exit 2
fi

# Assure that initrd relocation to the end of DRAM will not interfere
# with application of relocated DT and DTOs at %UBOOT_DTB_LOADADDRESS% , clamp the
# initrd relocation address below UBOOT_DTB_LOADADDRESS = %UBOOT_DTB_LOADADDRESS%.
if test -z "${initrd_high}" ; then
  setenv initrd_high %UBOOT_DTB_LOADADDRESS%
fi

# A custom script exists to load DTOs
if test -n "${loaddtoscustom}" ; then
  if test -z "${loaddtos}" ; then
    echo "Using base DT from fitImage default configuration as fallback."
    fdt addr ${loadaddr}
    fdt get value loaddtos /configurations default
    setenv loaddtos "#${loaddtos}"
    echo "To select different base DT to be adjusted using 'loaddtoscustom'"
    echo "script and passed to Linux kernel, set 'loaddtos' variable:"
    echo "  => env set loaddtos \#conf@...dtb"
  fi

  # Matches UBOOT_DTB_LOADADDRESS in OE layer machine config
  setenv loadaddrdtb %UBOOT_DTB_LOADADDRESS%
  # Matches UBOOT_DTBO_LOADADDRESS in OE layer machine config
  setenv loadaddrdtbo %UBOOT_DTBO_LOADADDRESS%

  setexpr loaddtossep gsub '#conf' ' fdt' "${loaddtos}"
  setexpr loaddtb 1

  for i in ${loaddtossep} ; do
    if test ${loaddtb} = 1 ; then
      echo "Using base DT ${i}"
      imxtract ${loadaddr} ${i} ${loadaddrdtb} ;
      fdt addr ${loadaddrdtb}
      fdt resize 0x40000
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
