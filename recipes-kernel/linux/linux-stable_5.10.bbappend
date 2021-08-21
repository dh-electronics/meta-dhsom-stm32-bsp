FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

BPV := "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
KBRANCH:dh-stm32mp1-dhsom ?= "linux-${BPV}.y"
KMACHINE:dh-stm32mp1-dhsom ?= "dh-stm32mp1-dhsom"
COMPATIBLE_MACHINE = "(dh-stm32mp1-dhsom)"

SRC_URI:append:dh-stm32mp1-dhsom = " \
	file://${BPV}/dh-stm32mp1-dhsom;type=kmeta;destsuffix=${BPV}/dh-stm32mp1-dhsom \
	file://common/dh-stm32mp1-dhsom;type=kmeta;destsuffix=common/dh-stm32mp1-dhsom \
	"
KERNEL_FEATURES:dh-stm32mp1-dhsom = " dh-stm32mp1-dhsom-standard.scc "
DEPENDS:append:dh-stm32mp1-dhsom = " lzop-native "
