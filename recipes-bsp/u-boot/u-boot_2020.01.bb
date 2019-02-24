require u-boot-common.inc
require u-boot.inc

DEPENDS += "bc-native dtc-native"

DEPENDS_append_mainlinestm32mp1 = "u-boot-mkimage-native"
SRC_URI_append_mainlinestm32mp1 = " file://boot.cmd "
do_compile_append_mainlinestm32mp1 () {
	uboot-mkimage -A arm -T script -C none \
		-d ${WORKDIR}/boot.cmd ${WORKDIR}/boot.scr
}
