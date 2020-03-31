SUMMARY = "Cypress fmac firmware files"
SECTION = "kernel"

LICENSE = "Firmware-cypress-fmac-fw"

LIC_FILES_CHKSUM = "file://LICENCE;md5=cbc5f665d04f741f1e006d2096236ba7"

# These are not common licenses, set NO_GENERIC_LICENSE for them
# so that the license files will be copied from fetched source
NO_GENERIC_LICENSE[Firmware-cypress-fmac-fw] = "LICENCE"

SRCREV = "8fc2363758d916a685c7545e4f948e869d16f202"
SRC_URI = "git://github.com/murata-wireless/cyw-fmac-fw;protocol=https"

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
	install -m 0644 LICENCE ${D}${nonarch_base_libdir}/firmware/brcm/LICENSE.cypress-fmac-fw
	install -m 0644 brcmfmac43455-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 brcmfmac43455-sdio.1MW.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/

	# Symlink the firmware names
	ln -s brcmfmac43455-sdio.1MW.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.clm_blob

	# FIXME: Package other firmwares too
	# FIXME: How do we handle the brcmfmac43455-sdio.*.clm_blob options?
}

ALLOW_EMPTY_${PN} = "1"

PACKAGES = " ${PN}-cypress-license ${PN}-bcm43455-1mw-sdio "

LICENSE_${PN}-cypress-license = "Firmware-cypress-fmac-fw"
FILES_${PN}-cypress-license = "${nonarch_base_libdir}/firmware/brcm/LICENSE.cypress-fmac-fw"

FILES_${PN}-bcm43455-1mw-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.bin \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.1MW.clm_blob \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.clm_blob \
	"

LICENSE_${PN}-bcm43455-1mw-sdio = "Firmware-cypress-fmac-fw"
RDEPENDS_${PN}-bcm43455-1mw-sdio += "${PN}-cypress-license"
