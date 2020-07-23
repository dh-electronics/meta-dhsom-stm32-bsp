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
	file://0002-ARM-dts-stm32-Move-ethernet-PHY-into-SoM-DT.patch \
	file://0003-ARM-dts-stm32-Add-DHSOM-based-DRC02-board.patch \
	file://default-device-tree.cfg \
	"
