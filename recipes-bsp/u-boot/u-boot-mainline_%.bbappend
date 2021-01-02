FILESEXTRAPATHS_prepend := "${THISDIR}/files/common:${THISDIR}/files/${MACHINE}:${THISDIR}/files:"

DEPENDS_append_dh-stm32mp1-dhsom = "u-boot-mkimage-native"
do_compile_append_dh-stm32mp1-dhsom () {
	uboot-mkimage -A arm -T script -C none \
		-d ${WORKDIR}/boot.cmd ${WORKDIR}/boot.scr
}

SRC_URI_append_dh-stm32mp1-dhsom = " \
	file://boot.cmd \
	file://fw_env.config \
	file://0001-ARM-stm32-Increase-USB-power-good-delay.patch \
	file://0002-ARM-stm32-Add-update_sf-script-to-install-U-Boot-int.patch \
	file://default-device-tree.cfg \
	"
