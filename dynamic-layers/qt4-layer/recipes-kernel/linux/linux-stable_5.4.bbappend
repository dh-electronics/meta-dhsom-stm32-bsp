FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:dh-stm32mp1-dhcom-pdk2 = " \
	file://dh-stm32mp1-dhcom-pdk2-edt;type=kmeta;destsuffix=dh-stm32mp1-dhcom-pdk2-edt \
	"
KERNEL_FEATURES:append:dh-stm32mp1-dhcom-pdk2 = " dh-stm32mp1-dhcom-pdk2-edt.scc "
