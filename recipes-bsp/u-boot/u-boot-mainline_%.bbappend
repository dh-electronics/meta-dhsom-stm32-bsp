FILESEXTRAPATHS:prepend := "${THISDIR}/files/common:${THISDIR}/files/${MACHINE}:${THISDIR}/files:"

SRC_URI:append:dh-stm32mp1-dhsom = " \
	file://boot.cmd \
	file://fw_env.config \
	file://default-device-tree.cfg \
	"

do_deploy:append:dh-stm32mp13-dhcor-dhsbc() {
        install -D -m 644 ${B}/u-boot.dtb ${DEPLOYDIR}/u-boot.dtb
}

# U-Boot release extra version, used as identifier of a patch
# release. Update this every time this recipe is updated. The
# format is -${MACHINE}-date.extraversion. The date is in the
# format YYYYMMDD, the extraversion is used in case there are
# multiple releases during a single day, which is unlikely.
UBOOT_LOCALVERSION:dh-stm32mp1-dhsom ?= "-${MACHINE}-20260908.01"
