FILESEXTRAPATHS_prepend := "${THISDIR}/files/common:${THISDIR}/files/${MACHINE}:${THISDIR}/files:"
RPROVIDES_${PN} = "virtual/bootloader"

DEPENDS_append_dh-stm32mp1-dhsom = "u-boot-tools-native"
do_compile_append_dh-stm32mp1-dhsom () {
	sed -i -e "s/%UBOOT_DTB_LOADADDRESS%/${UBOOT_DTB_LOADADDRESS}/g" \
		-e "s/%UBOOT_DTBO_LOADADDRESS%/${UBOOT_DTBO_LOADADDRESS}/g" \
		${WORKDIR}/boot.cmd
	uboot-mkimage -A arm -T script -C none \
		-d ${WORKDIR}/boot.cmd ${WORKDIR}/boot.scr
}

SRC_URI_append_dh-stm32mp1-dhsom = " \
	file://boot.cmd \
	file://fw_env.config \
	file://0001-ARM-stm32-Increase-USB-power-good-delay.patch \
	file://0002-ARM-stm32-Add-update_sf-script-to-install-U-Boot-int.patch \
	file://0003-ARM-stm32-Add-USB-host-boot-support.patch \
	file://0004-ARM-stm32-Set-soc_type-soc_pkg-soc_rev-env-variables.patch \
	file://default-device-tree.cfg \
	"
