PERF_SRC:append:dh-stm32mp1-dhsom = " ${@'arch/${ARCH}/include/uapi/asm/ arch/arm64/tools' if (d.getVar('LAYERSERIES_CORENAMES') in ["kirkstone"] and d.getVar('PREFERRED_VERSION_linux-stable') not in ["5.10%"]) else ''}"
RDEPENDS:${PN}-tests:append:dh-stm32mp1-dhsom = " perl"
