FILESEXTRAPATHS:prepend := "${THISDIR}/files/common:${THISDIR}/files/${MACHINE}:${THISDIR}/files:"

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
	file://default-device-tree.cfg \
	file://0001-Revert-i2c-fix-stack-buffer-overflow-vulnerability-i.patch \
	file://0002-i2c-fix-stack-buffer-overflow-vulnerability-in-i2c-m.patch \
	"

# U-Boot release extra version, used as identifier of a patch
# release. Update this every time this recipe is updated. The
# format is -${MACHINE}-date.extraversion. The date is in the
# format YYYYMMDD, the extraversion is used in case there are
# multiple releases during a single day, which is unlikely.
UBOOT_LOCALVERSION:dh-stm32mp1-dhsom ?= "-${MACHINE}-20220925.01"
