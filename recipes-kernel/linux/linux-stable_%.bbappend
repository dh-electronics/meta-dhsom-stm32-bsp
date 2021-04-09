FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

BPV := "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
KBRANCH_dh-stm32mp1-dhsom ?= "linux-${BPV}.y"
KMACHINE_dh-stm32mp1-dhsom ?= "dh-stm32mp1-dhsom"
COMPATIBLE_MACHINE = "(dh-stm32mp1-dhsom)"

SRC_URI_append_dh-stm32mp1-dhsom = " \
	file://${BPV}/dh-stm32mp1-dhsom;type=kmeta;destsuffix=${BPV}/dh-stm32mp1-dhsom \
	file://common/dh-stm32mp1-dhsom;type=kmeta;destsuffix=common/dh-stm32mp1-dhsom \
	"
KERNEL_FEATURES_dh-stm32mp1-dhsom = " dh-stm32mp1-dhsom-standard.scc "
