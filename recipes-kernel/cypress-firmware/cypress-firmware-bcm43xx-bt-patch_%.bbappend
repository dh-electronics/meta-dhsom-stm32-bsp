do_install:append:dh-stm32mp1-dhcor-avenger96 () {
	# Symlink the firmware name to match board type
	ln -s CYW4345C0.1MW.hcd \
	      ${D}${nonarch_base_libdir}/firmware/brcm/BCM4345C0.arrow,stm32mp157a-avenger96.hcd
}

FILES:${PN}-bcm4345c0:append:dh-stm32mp1-dhcor-avenger96 = " \
	${nonarch_base_libdir}/firmware/brcm/BCM4345C0.arrow,stm32mp157a-avenger96.hcd \
	"
