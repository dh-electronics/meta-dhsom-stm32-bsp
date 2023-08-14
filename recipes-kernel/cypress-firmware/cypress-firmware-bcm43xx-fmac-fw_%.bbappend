do_install:append:dh-stm32mp1-dhcor-avenger96 () {
	# Symlink the firmware name to match board type
	ln -s brcmfmac43455-sdio.clm_blob \
		${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.stm32mp157a-avenger96.clm_blob
	ln -s brcmfmac43455-sdio.bin \
		${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.stm32mp157a-avenger96.bin
}

FILES:${PN}-bcm43455-1mw-sdio:append:dh-stm32mp1-dhcor-avenger96 = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.stm32mp157a-avenger96.clm_blob \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.stm32mp157a-avenger96.bin \
	"
