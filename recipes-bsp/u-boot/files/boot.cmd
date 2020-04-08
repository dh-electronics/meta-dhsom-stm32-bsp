if test -n "${distro_bootpart}"; then
  setenv partition "${distro_bootpart}"
else
  setenv partition "${bootpart}"
fi

if test ! -e ${devtype} ${devnum}:${partition} boot/fitImage; then
  echo "This boot medium does not contain a suitable fitImage file for"
  echo "this system. Aborting boot process."
  exit 0
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

load ${devtype} ${devnum}:${partition} ${loadaddr} boot/fitImage \
&& echo "Booting the Linux kernel..." \
&& bootm ${loadaddr}${loaddtos}
