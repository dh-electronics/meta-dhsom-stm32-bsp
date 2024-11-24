FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

COMPATIBLE_MACHINE:dh-stm32mp13-dhcor-dhsbc = "dh-stm32mp13-dhcor-dhsbc"
OPTEEMACHINE:dh-stm32mp13-dhcor-dhsbc = "stm32mp1-135F_DHCOR_DHSBC "
EXTRA_OEMAKE:append:dh-stm32mp13-dhcor-dhsbc = " CFG_EMBED_DTB_SOURCE_FILE=stm32mp135f-dhcor-dhsbc.dts "

SRC_URI:append:dh-stm32mp13-dhsom = " \
	file://0001-dts-stm32-add-support-for-STM32MP13xx-DHCOR-SoM-and-.patch \
	"
