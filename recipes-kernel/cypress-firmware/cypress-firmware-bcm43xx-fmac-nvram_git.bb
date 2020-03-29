SUMMARY = "Cypress fmac NVRAM files"
SECTION = "kernel"

LICENSE = "Firmware-cypress-fmac-nvram"

LIC_FILES_CHKSUM = "file://LICENCE.cypress;md5=cbc5f665d04f741f1e006d2096236ba7"

# These are not common licenses, set NO_GENERIC_LICENSE for them
# so that the license files will be copied from fetched source
NO_GENERIC_LICENSE[Firmware-cypress-fmac-nvram] = "LICENCE.cypress"

SRCREV = "4a7974090495b7f8e8c1d332b151109d3350e40b"
SRC_URI = "git://github.com/murata-wireless/cyw-fmac-nvram;protocol=https"

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

inherit allarch

CLEANBROKEN = "1"

do_compile() {
	:
}

do_install() {
	install -d ${D}${nonarch_base_libdir}/firmware/
	install -d ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 LICENCE.cypress ${D}${nonarch_base_libdir}/firmware/brcm/LICENSE.cypress-fmac-nvram
	install -m 0644 brcmfmac43455-sdio.1MW.txt ${D}${nonarch_base_libdir}/firmware/brcm/

	# Symlink the firmware names
	ln -s brcmfmac43455-sdio.1MW.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.txt

	# FIXME: Package other firmwares too
	# FIXME: How do we handle the brcmfmac43455-sdio.*.txt options?
}

ALLOW_EMPTY_${PN} = "1"

PACKAGES = " ${PN}-cypress-license ${PN}-bcm43455-1mw-sdio "

LICENSE_${PN}-cypress-license = "Firmware-cypress-fmac-nvram"
FILES_${PN}-cypress-license = "${nonarch_base_libdir}/firmware/brcm/LICENSE.cypress-fmac-nvram"

FILES_${PN}-bcm43455-1mw-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.1MW.txt \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.txt \
	"

LICENSE_${PN}-bcm43455-1mw-sdio = "Firmware-cypress-fmac-nvram"
RDEPENDS_${PN}-bcm43455-1mw-sdio += "${PN}-cypress-license"

do_install_append_dh-stm32mp1-dhcor-avenger96 () {
	# Symlink the firmware name to match board type
	ln -s brcmfmac43455-sdio.1MW.txt \
	      ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.arrow,stm32mp157a-avenger96.txt
}

FILES_${PN}-bcm43455-1mw-sdio_append_dh-stm32mp1-dhcor-avenger96 = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.arrow,stm32mp157a-avenger96.txt \
	"
