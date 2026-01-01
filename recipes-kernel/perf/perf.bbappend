PERF_SRC:append:dh-stm32mp-dhsom = " \
	${@'arch/${ARCH}/include/uapi/asm/ arch/arm64/tools' if (d.getVar('LAYERSERIES_CORENAMES') in ["kirkstone"] and bb.utils.vercmp_string_op(d.getVar('PREFERRED_VERSION_linux-stable').strip('%'), '5.10', '>')) else ''} \
	${@'include/uapi/asm-generic/Kbuild' if bb.utils.vercmp_string_op(d.getVar('PREFERRED_VERSION_linux-stable').strip('%'), '6.15', '>=') else ''} \
	"

PACKAGECONFIG:append:dh-stm32mp-dhsom = " jevents"

RDEPENDS:${PN}-tests:append:dh-stm32mp-dhsom = " perl"
