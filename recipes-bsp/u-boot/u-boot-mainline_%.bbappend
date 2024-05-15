FILESEXTRAPATHS:prepend := "${THISDIR}/files/common:${THISDIR}/files/${MACHINE}:${THISDIR}/files:"

do_compile:prepend:dh-stm32mp1-dhsom () {
	sed -i -e "s/%UBOOT_DTB_LOADADDRESS%/${UBOOT_DTB_LOADADDRESS}/g" \
		-e "s/%UBOOT_DTBO_LOADADDRESS%/${UBOOT_DTBO_LOADADDRESS}/g" \
		${WORKDIR}/${UBOOT_ENV_SRC}
}

SRC_URI:append:dh-stm32mp1-dhsom = " \
	file://boot.cmd \
	file://fw_env.config \
	file://default-device-tree.cfg \
	file://0001-ARM-stm32-Set-stdio-to-serial-on-DH-STM32MP15xx-DHSO.patch \
	file://0002-arm-stm32-Enable-OHCI-HCD-support-on-STM32MP15xx-DHS.patch \
	file://0003-treewide-Remove-clk_free.patch \
	file://0004-board-st-common-Fix-board_get_alt_info_mtd.patch \
	file://0005-board-st-common-simplify-MTD-device-parsing.patch \
	file://0006-net-dwc_eth_qos-Split-STM32-glue-into-separate-file.patch \
	file://0007-net-dwc_eth_qos-Rename-eqos_stm32_config-to-eqos_stm.patch \
	file://0008-net-dwc_eth_qos-Fold-board_interface_eth_init-into-S.patch \
	file://0009-net-dwc_eth_qos-Scrub-ifdeffery.patch \
	file://0010-net-dwc_eth_qos-Use-FIELD_PREP-for-ETH_SEL-bitfield.patch \
	file://0011-net-dwc_eth_qos-Move-log_debug-statements-on-top-of-.patch \
	file://0012-net-dwc_eth_qos-Use-consistent-logging-prints.patch \
	file://0013-net-dwc_eth_qos-Constify-st-eth-values-parsed-out-of.patch \
	file://0014-net-dwc_eth_qos-Add-DT-parsing-for-STM32MP13xx-platf.patch \
	file://0015-net-dwc_eth_qos-Add-support-of-STM32MP13xx-platform.patch \
	file://0016-net-dwc_eth_qos-Add-support-for-st-ext-phyclk-proper.patch \
	file://0017-ARM-dts-stm32-add-eth1-and-eth2-support-on-stm32mp13.patch \
	file://0018-ARM-dts-stm32-add-PWR-regulators-support-on-stm32mp1.patch \
	file://0019-ARM-stm32-Make-PWR-regulator-driver-available-on-STM.patch \
	file://0020-ARM-dts-stm32-Add-pinmux-nodes-for-DH-electronics-ST.patch \
	file://0021-ARM-dts-stm32-Add-support-for-STM32MP13xx-DHCOR-SoM-.patch \
	file://0022-ARM-stm32-Jump-to-ep-on-successful-resume-in-PSCI-su.patch \
	file://0023-ARM-stm32-Report-OTP-CLOSED-instead-of-rev.-on-close.patch \
	file://0024-ARM-stm32-Initialize-TAMP_SMCR-BKP.PROT-fields-on-ST.patch \
	file://0025-ARM-stm32-Ping-IWDG-on-exit-from-PSCI-suspend-code.patch \
	"


do_deploy:append:dh-stm32mp13-dhcor-dhsbc() {
        install -D -m 644 ${B}/u-boot.dtb ${DEPLOYDIR}/u-boot.dtb
}

# U-Boot release extra version, used as identifier of a patch
# release. Update this every time this recipe is updated. The
# format is -${MACHINE}-date.extraversion. The date is in the
# format YYYYMMDD, the extraversion is used in case there are
# multiple releases during a single day, which is unlikely.
UBOOT_LOCALVERSION:dh-stm32mp1-dhsom ?= "-${MACHINE}-20240516.01"
