do_install:append:dh-stm32mp13-dhcor-dhsbc () {
	# Symlink the firmware name to match kernel fallback
	ln -s brcmfmac43439-sdio.1YN.txt \
	      ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43439-sdio.txt
	# Install modified firmware which does match board type.
	#
	# Force muxenab=0x11 which means both UART enabled and
	# WL_GPIO_0_HOST_WAKE configured as HOST_WAKE instead of
	# a GPIO. Without this modification, the WiFi chip would
	# generate massive amount of IRQs on host-wake line, which
	# looks like this in second column of /proc/interrupts:
	# 89:     670547  stm32gpio  14 Level     brcmf_oob_intr
	#         ^^^^^^
	sed '/^muxenab=/ s@.*@muxenab=0x11@' ${S}/cyfmac43439-sdio.1YN.txt > \
	      ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43439-sdio.dh,stm32mp135f-dhcor-dhsbc.txt
}

FILES:${PN}-bcm43439-1yn-sdio:append:dh-stm32mp13-dhcor-dhsbc = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43439-sdio.txt \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43439-sdio.dh,stm32mp135f-dhcor-dhsbc.txt \
	"

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
