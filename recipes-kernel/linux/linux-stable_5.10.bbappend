FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/5.10:${THISDIR}/${PN}/common:"

KBRANCH_dh-stm32mp1-dhsom ?= "linux-5.10.y"
COMPATIBLE_MACHINE = "(dh-stm32mp1-dhsom)"

SRC_URI_append_dh-stm32mp1-dhsom = " \
	file://dh-stm32mp1-common;type=kmeta;destsuffix=dh-stm32mp1-common \
	"

SRC_URI_append_dh-stm32mp1-dhcom-drc02 = " \
	file://dh-stm32mp1-dhcom-drc02;type=kmeta;destsuffix=dh-stm32mp1-dhcom-drc02 \
	"
KERNEL_FEATURES_dh-stm32mp1-dhcom-drc02 = " dh-stm32mp1-dhcom-drc02-standard.scc "

SRC_URI_append_dh-stm32mp1-dhcom-pdk2 = " \
	file://dh-stm32mp1-dhcom-pdk2;type=kmeta;destsuffix=dh-stm32mp1-dhcom-pdk2 \
	"
KERNEL_FEATURES_dh-stm32mp1-dhcom-pdk2 = " dh-stm32mp1-dhcom-pdk2-standard.scc "

SRC_URI_append_dh-stm32mp1-dhcom-picoitx = " \
	file://dh-stm32mp1-dhcom-picoitx;type=kmeta;destsuffix=dh-stm32mp1-dhcom-picoitx \
	"
KERNEL_FEATURES_dh-stm32mp1-dhcom-picoitx = " dh-stm32mp1-dhcom-picoitx-standard.scc "

SRC_URI_append_dh-stm32mp1-dhcor-avenger96 = " \
	file://dh-stm32mp1-dhcor-avenger96;type=kmeta;destsuffix=dh-stm32mp1-dhcor-avenger96 \
	"
KERNEL_FEATURES_dh-stm32mp1-dhcor-avenger96 = " dh-stm32mp1-dhcor-avenger96-standard.scc "
