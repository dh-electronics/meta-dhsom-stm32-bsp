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
	file://0001-clk-stm32-Pass-udevice-pointer-to-clk_register_compo.patch \
	file://0002-phy-Reset-init-count-on-phy-exit-failure.patch \
	file://0003-ARM-dts-stm32-Keep-the-reg11-and-reg18-regulators-al.patch \
	file://0004-ARM-dts-stm32-Introduce-DH-STM32MP13x-target.patch \
	file://0005-board-dhelectronics-Move-dh_add_item_number_and_seri.patch \
	file://0006-ARM-stm32-Read-values-from-M24256-write-lockable-pag.patch \
	file://0007-ARM-stm32-Add-MAC-address-readout-from-fuses-on-DH-S.patch \
	file://0008-board-dhelectronics-Check-pointer-before-access-in-d.patch \
	file://0009-board-dhelectronics-Use-isascii-before-isprint-in-dh.patch \
	file://0010-ARM-stm32-Perform-node-compatible-check-for-KS8851-e.patch \
	file://0011-ARM-dts-stm32-Fix-STM32MP15xx-DHSOM-boot-breakage-du.patch \
	"

do_deploy:append:dh-stm32mp13-dhcor-dhsbc() {
        install -D -m 644 ${B}/u-boot.dtb ${DEPLOYDIR}/u-boot.dtb
}

# U-Boot release extra version, used as identifier of a patch
# release. Update this every time this recipe is updated. The
# format is -${MACHINE}-date.extraversion. The date is in the
# format YYYYMMDD, the extraversion is used in case there are
# multiple releases during a single day, which is unlikely.
UBOOT_LOCALVERSION:dh-stm32mp1-dhsom ?= "-${MACHINE}-20251024.01"
