FILESEXTRAPATHS:prepend := "${THISDIR}/files/common:${THISDIR}/files/${MACHINE}:${THISDIR}/files:"
RPROVIDES:${PN} = "virtual/bootloader"

DEPENDS:append:dh-stm32mp1-dhsom = " u-boot-mainline-tools-native "
do_compile:append:dh-stm32mp1-dhsom () {
	sed -i -e "s/%UBOOT_DTB_LOADADDRESS%/${UBOOT_DTB_LOADADDRESS}/g" \
		-e "s/%UBOOT_DTBO_LOADADDRESS%/${UBOOT_DTBO_LOADADDRESS}/g" \
		${WORKDIR}/boot.cmd
	uboot-mkimage -A arm -T script -C none \
		-d ${WORKDIR}/boot.cmd ${WORKDIR}/boot.scr
}

SRC_URI:append:dh-stm32mp1-dhsom = " \
	file://boot.cmd \
	file://fw_env.config \
	file://0001-ARM-stm32-Increase-USB-power-good-delay.patch \
	file://0002-ARM-stm32-Add-update_sf-script-to-install-U-Boot-int.patch \
	file://0003-ARM-stm32-Add-USB-host-boot-support.patch \
	file://0004-ARM-stm32-Set-soc_type-soc_pkg-soc_rev-env-variables.patch \
	file://0005-ARM-stm32-Add-additional-ID-register-check-for-KSZ88.patch \
	file://0006-ARM-stm32-Enable-UNZIP-on-DHSOM-by-default.patch \
	file://0007-ARM-dts-stm32-Fix-AV96-eMMC-pinmux.patch \
	file://0008-ARM-stm32-Set-environment-sector-size-to-4k-on-DHSOM.patch \
	file://0009-ARM-dts-stm32-Reduce-DHCOR-SPI-NOR-frequency-to-50-M.patch \
	file://0010-Revert-configs-stm32mp1-only-support-SD-card-after-N.patch \
	file://0011-ARM-stm32-Enable-DFU-SF-support-on-DHSOM.patch \
	file://default-device-tree.cfg \
	"
