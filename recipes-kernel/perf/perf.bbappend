PERF_SRC:append:dh-stm32mp-dhsom = " \
	${@'include/uapi/asm-generic/Kbuild' if bb.utils.vercmp_string_op(d.getVar('PREFERRED_VERSION_linux-stable').strip('%'), '6.15', '>=') else ''} \
	"

PACKAGECONFIG:append:dh-stm32mp-dhsom = " jevents"
PACKAGECONFIG_CONFARGS:remove:dh-stm32mp-dhsom = "${@'BUILD_BPF_SKEL=0' if bb.utils.vercmp_string_op(d.getVar('PREFERRED_VERSION_linux-stable').strip('%'), '6.6', '<=') else ''}"

RDEPENDS:${PN}-tests:append:dh-stm32mp-dhsom = " perl"
