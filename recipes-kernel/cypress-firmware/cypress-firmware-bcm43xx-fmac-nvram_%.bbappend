do_install:append:dh-stm32mp1-dhcor-avenger96 () {
	# Symlink the firmware name to match kernel fallback
	ln -s brcmfmac43455-sdio.1MW.txt \
	      ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.txt
	# Symlink the firmware name to match board type
	ln -s brcmfmac43455-sdio.1MW.txt \
	      ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.arrow,stm32mp157a-avenger96.txt
}

FILES:${PN}-bcm43455-1mw-sdio:append:dh-stm32mp1-dhcor-avenger96 = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.txt \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.arrow,stm32mp157a-avenger96.txt \
	"
