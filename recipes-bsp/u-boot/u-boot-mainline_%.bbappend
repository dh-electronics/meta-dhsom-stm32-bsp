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
	file://0001-ARM-stm32-Fix-OF_LIST-on-DHCOR.patch \
	file://0002-ARM-stm32-Add-IWDG-handling-into-PSCI-suspend-code.patch \
	file://0003-ARM-stm32-Power-cycle-Buck3-in-reset-on-DHSOM.patch \
	file://0004-ARM-dts-stm32-Switch-DWMAC-RMII-clock-to-MCO2-on-DHC.patch \
	"

# U-Boot release extra version, used as identifier of a patch
# release. Update this every time this recipe is updated. The
# format is -${MACHINE}-date.extraversion. The date is in the
# format YYYYMMDD, the extraversion is used in case there are
# multiple releases during a single day, which is unlikely.
UBOOT_LOCALVERSION:dh-stm32mp1-dhsom ?= "-${MACHINE}-20230727.01"
