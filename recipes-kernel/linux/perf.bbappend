PACKAGECONFIG:append:dh-stm32mp-dhsom = " jevents"

PERF_SRC:append:dh-stm32mp-dhsom = " include/uapi/asm-generic/Kbuild"

RDEPENDS:${PN}-tests:append:dh-stm32mp-dhsom = " perl"
