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
	file://0007-ARM-stm32-Add-update_sf-script-to-install-U-Boot-int.patch \
	file://0008-ARM-dts-stm32-Adjust-PLL4-settings-on-AV96-again.patch \
	file://0009-ARM-dts-stm32-Pull-UART4-RX-high-on-AV96.patch \
	file://0010-arm-stm32mp-spl-add-bsec-driver-in-SPL.patch \
	file://0011-ARM-dts-stm32-add-cpufreq-support-on-stm32mp15x.patch \
	file://0012-board-st-create-common-file-stpmic1.c.patch \
	file://0013-stm32mp1-clk-configure-pll1-with-OPP.patch \
	file://0014-arm-stm32mp-add-weak-function-to-save-vddcore.patch \
	file://0015-board-st-stpmic1-add-function-stpmic1_init.patch \
	file://0016-board-stm32mp1-update-vddcore-in-SPL.patch \
	file://0017-ARM-dts-stm32mp1-use-OPP-information-for-PLL1-settin.patch \
	file://0018-power-regulator-stm32-vrefbuf-fix-a-possible-oversho.patch \
	file://0019-ARM-dts-stm32-Add-missing-dm-spl-props-for-SPI-NOR-o.patch \
	file://0020-net-ks8851-Implement-EEPROM-MAC-address-readout.patch \
	file://0021-ARM-dts-stm32-Do-not-set-eth1addr-if-KS8851-has-EEPR.patch \
	file://0022-ARM-dts-stm32-Enable-internal-pull-ups-for-SDMMC1-on.patch \
	file://0023-ARM-dts-stm32-Disable-SDMMC1-CKIN-feedback-clock.patch \
	file://0024-ARM-dts-stm32-Add-DHCOM-based-PicoITX-board.patch \
	file://0025-ARM-dts-stm32-Enable-SDMMC3-on-DH-DRC02.patch \
	file://0026-ARM-dts-stm32-Add-USB-OTG-ID-pin-on-DH-AV96.patch \
	file://default-device-tree.cfg \
	"
