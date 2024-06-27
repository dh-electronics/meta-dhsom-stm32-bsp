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
	file://0001-ARM-dts-stm32-add-PWR-regulators-support-on-stm32mp1.patch \
	file://0002-ARM-dts-stm32-Make-PWR-regulator-driver-available-on.patch \
	file://0003-ARM-dts-stm32-add-eth1-and-eth2-support-on-stm32mp13.patch \
	file://0004-ARM-dts-stm32-Add-pinmux-nodes-for-DH-electronics-ST.patch \
	file://0005-ARM-dts-stm32-Add-support-for-STM32MP13xx-DHCOR-SoM-.patch \
	file://0006-ARM-dts-stm32-Add-generic-SoM-compatible-to-STM32MP1.patch \
	file://0007-ARM-dts-stm32-Auto-detect-second-MAC-on-STM32MP15xx-.patch \
	file://0008-ARM-stm32-Fix-TAMP_SMCR-BKP.PROT-fields-on-STM32MP15.patch \
	file://0009-ARM-stm32-Fix-secure_waitbits-mask-check.patch \
	file://0010-ARM-dts-stm32-Increase-CPU-core-voltage-on-STM32MP13.patch \
	file://0011-ARM-stm32-Add-optional-manufacturing-environment-to-.patch \
	"

do_deploy:append:dh-stm32mp13-dhcor-dhsbc() {
        install -D -m 644 ${B}/u-boot.dtb ${DEPLOYDIR}/u-boot.dtb
}

# U-Boot release extra version, used as identifier of a patch
# release. Update this every time this recipe is updated. The
# format is -${MACHINE}-date.extraversion. The date is in the
# format YYYYMMDD, the extraversion is used in case there are
# multiple releases during a single day, which is unlikely.
UBOOT_LOCALVERSION:dh-stm32mp1-dhsom ?= "-${MACHINE}-20241107.01"
