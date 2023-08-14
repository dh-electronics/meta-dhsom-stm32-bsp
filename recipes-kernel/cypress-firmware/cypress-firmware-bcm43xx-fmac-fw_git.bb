SUMMARY = "Cypress fmac firmware files"
SECTION = "kernel"

LICENSE = "Firmware-cypress-fmac-fw"

LIC_FILES_CHKSUM = "file://LICENCE;md5=cbc5f665d04f741f1e006d2096236ba7"

# These are not common licenses, set NO_GENERIC_LICENSE for them
# so that the license files will be copied from fetched source
NO_GENERIC_LICENSE[Firmware-cypress-fmac-fw] = "LICENCE"

SRCREV = "de83c37edb8d65ea8e511161c9b5119e0b7a2e1f"
SRC_URI = "git://github.com/murata-wireless/cyw-fmac-fw;protocol=https;branch=master"

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
	install -m 0644 cyfmac43430-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 cyfmac43430-sdio.1DX.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 cyfmac43455-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 cyfmac43455-sdio.1MW.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 cyfmac4373-sdio.2AE.bin ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 cyfmac4373-sdio.2AE.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/

	# Symlink the firmware names
	ln -s cyfmac43430-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.bin
	ln -s cyfmac43430-sdio.1DX.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.clm_blob
	ln -s cyfmac43455-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.bin
	ln -s cyfmac43455-sdio.1MW.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.1MW.clm_blob
	ln -s brcmfmac43455-sdio.1MW.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.clm_blob
	ln -s cyfmac4373-sdio.2AE.bin ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.bin
	ln -s cyfmac4373-sdio.2AE.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.2AE.clm_blob
	ln -s cyfmac4373-sdio.2AE.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.clm_blob

	# FIXME: Package other firmwares too
	# FIXME: How do we handle the brcmfmac43455-sdio.*.clm_blob options?
}

ALLOW_EMPTY:${PN} = "1"

PACKAGES = " ${PN}-cypress-license ${PN}-brcm43430-1dx-sdio ${PN}-bcm43455-1mw-sdio ${PN}-bcm4373-2ae-sdio "

LICENSE:${PN}-cypress-license = "Firmware-cypress-fmac-fw"
FILES:${PN}-cypress-license = "${nonarch_base_libdir}/firmware/brcm/LICENSE.cypress-fmac-fw"

FILES:${PN}-brcm43430-1dx-sdio:append = " \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43430-sdio.bin \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.bin \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43430-sdio.1DX.clm_blob \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.clm_blob \
	"

LICENSE:${PN}-brcm43430-1dx-sdio = "Firmware-cypress-fmac-fw"
RDEPENDS:${PN}-brcm43430-1dx-sdio += "${PN}-cypress-license"

FILES:${PN}-bcm43455-1mw-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43455-sdio.bin \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.bin \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43455-sdio.1MW.clm_blob \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.1MW.clm_blob \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.clm_blob \
	"

LICENSE:${PN}-bcm43455-1mw-sdio = "Firmware-cypress-fmac-fw"
RDEPENDS:${PN}-bcm43455-1mw-sdio += "${PN}-cypress-license"

FILES:${PN}-bcm4373-2ae-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/cyfmac4373-sdio.2AE.bin \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.bin \
	${nonarch_base_libdir}/firmware/brcm/cyfmac4373-sdio.2AE.clm_blob \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.2AE.clm_blob \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.clm_blob \
	"

LICENSE:${PN}-bcm4373-2ae-sdio = "Firmware-cypress-fmac-fw"
RDEPENDS:${PN}-bcm4373-2ae-sdio += "${PN}-cypress-license"
RCONFLICTS:${PN}-bcm4373-2ae-sdio = "linux-firmware-bcm4373"
