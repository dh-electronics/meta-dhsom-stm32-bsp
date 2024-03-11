do_install:append:dh-stm32mp13-dhcor-dhsbc () {
	# Symlink the firmware name to match board type
	ln -s CYW4343A2.1YN.hcd \
	      ${D}${nonarch_base_libdir}/firmware/brcm/BCM4343A2.dh,stm32mp135f-dhcor-dhsbc.hcd
}

FILES:${PN}-bcm4343a2:append:dh-stm32mp13-dhcor-dhsbc = " \
	${nonarch_base_libdir}/firmware/brcm/BCM4343A2.dh,stm32mp135f-dhcor-dhsbc.hcd \
	"

do_install:append:dh-stm32mp1-dhcor-avenger96 () {
	# Symlink the firmware name to match board type
	ln -s CYW4345C0.1MW.hcd \
	      ${D}${nonarch_base_libdir}/firmware/brcm/BCM4345C0.arrow,stm32mp157a-avenger96.hcd
}

FILES:${PN}-bcm4345c0:append:dh-stm32mp1-dhcor-avenger96 = " \
	${nonarch_base_libdir}/firmware/brcm/BCM4345C0.arrow,stm32mp157a-avenger96.hcd \
	"
