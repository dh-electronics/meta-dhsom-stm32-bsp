FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

KBRANCH_mainlinestm32mp1 ?= "linux-5.4.y"
COMPATIBLE_MACHINE = "mainlinestm32mp1"

SRC_URI_append_mainlinestm32mp1 = " \
	file://mainlinestm32mp1;type=kmeta;destsuffix=mainlinestm32mp1 \
	"
KERNEL_FEATURES_mainlinestm32mp1 = " mainlinestm32mp1-standard.scc "
