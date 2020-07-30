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
	file://0004-ARM-dts-stm32-Update-eth1addr-from-EEPROM-if-eth1-pr.patch \
	file://0005-ARM-stm32-Add-both-PDK2-and-DRC02-DT-into-DHCOM-fitI.patch \
	file://0006-ARM-stm32-Add-fitImage-its-entry-for-587-200-DHCOR-S.patch \
	file://default-device-tree.cfg \
	"
