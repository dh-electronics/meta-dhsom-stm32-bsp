FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

KBRANCH_dh-stm32mp1-dhcom-pdk2 ?= "linux-5.4.y"
COMPATIBLE_MACHINE = "dh-stm32mp1-dhcom-pdk2"

SRC_URI_append_dh-stm32mp1-dhcom-pdk2 = " \
	file://dh-stm32mp1-common;type=kmeta;destsuffix=dh-stm32mp1-common \
	file://dh-stm32mp1-dhcom-pdk2;type=kmeta;destsuffix=dh-stm32mp1-dhcom-pdk2 \
	"
KERNEL_FEATURES_dh-stm32mp1-dhcom-pdk2 = " dh-stm32mp1-dhcom-pdk2-standard.scc "
