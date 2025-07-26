SUMMARY = "Cypress fmac NVRAM files"
SECTION = "kernel"

LICENSE = "Firmware-cypress-fmac-nvram"
LICENSE:${PN}-cypress-license = "Firmware-cypress-fmac-nvram"
LICENSE:${PN}-bcm43012-1lv-sdio = "Firmware-cypress-fmac-nvram"
LICENSE:${PN}-bcm43022-2gf-sdio = "Firmware-cypress-fmac-nvram"
LICENSE:${PN}-bcm43340-1bw-sdio = "Firmware-cypress-fmac-nvram"
LICENSE:${PN}-bcm43430-1dx-sdio = "Firmware-cypress-fmac-nvram"
LICENSE:${PN}-bcm43430-1fx-sdio = "Firmware-cypress-fmac-nvram"
LICENSE:${PN}-bcm43439-1yn-sdio = "Firmware-cypress-fmac-nvram"
LICENSE:${PN}-bcm43455-1lc-sdio = "Firmware-cypress-fmac-nvram"
LICENSE:${PN}-bcm43455-1mw-sdio = "Firmware-cypress-fmac-nvram"
LICENSE:${PN}-bcm4373-2ae-sdio = "Firmware-cypress-fmac-nvram"
LICENSE:${PN}-bcm4373-2bc-sdio = "Firmware-cypress-fmac-nvram"
LICENSE:${PN}-bcm54591-1xa-pcie = "Firmware-cypress-fmac-nvram"
LICENSE:${PN}-bcm55500-2fy-sdio = "Firmware-cypress-fmac-nvram"

LIC_FILES_CHKSUM = "file://LICENCE.cypress;md5=cbc5f665d04f741f1e006d2096236ba7"

# These are not common licenses, set NO_GENERIC_LICENSE for them
# so that the license files will be copied from fetched source
NO_GENERIC_LICENSE[Firmware-cypress-fmac-nvram] = "LICENCE.cypress"

SRC_URI = "git://github.com/murata-wireless/cyw-fmac-nvram;protocol=https;branch=master"
SRCREV = "22189fb5173dd0de796cf520ca6f5a6496af74e0"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"
EXCLUDE_FROM_SHLIBS = "1"
INHIBIT_DEFAULT_DEPS = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

UPSTREAM_CHECK_COMMITS = "1"

CLEANBROKEN = "1"

do_compile() {
	:
}

do_install() {
	install -d ${D}${nonarch_base_libdir}/firmware/
	install -d ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 LICENCE.cypress ${D}${nonarch_base_libdir}/firmware/brcm/LICENSE.cypress-fmac-nvram
	install -m 0644 cyfmac43*.txt ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 cyfmac54591-pcie.1XA.txt ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 cyfmac55500-sdio.2FY.txt ${D}${nonarch_base_libdir}/firmware/brcm/

	# Symlink the firmware files
	ln -s cyfmac43012-sdio.1LV.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43012-sdio.1LV.txt
	ln -s cyfmac43022-sdio.2GF.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43022-sdio.2GF.txt
	ln -s cyfmac43340-sdio.1BW.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43340-sdio.1BW.txt
	ln -s cyfmac43430-sdio.1DX.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.1DX.txt
	ln -s cyfmac43430-sdio.1FX.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.1FX.txt
	ln -s cyfmac43439-sdio.1YN.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43439-sdio.1YN.txt
	ln -s cyfmac43455-sdio.1LC.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.1LC.txt
	ln -s cyfmac43455-sdio.1MW.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.1MW.txt
	ln -s cyfmac4373-sdio.2AE.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.2AE.txt
	ln -s cyfmac4373-sdio.2BC.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.2BC.txt
	ln -s cyfmac54591-pcie.1XA.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac54591-pcie.1XA.txt
	ln -s cyfmac55500-sdio.2FY.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac55500-sdio.2FY.txt
}

ALLOW_EMPTY:${PN} = "1"

PACKAGES = " \
	${PN}-cypress-license \
	${PN}-bcm43012-1lv-sdio \
	${PN}-bcm43022-2gf-sdio \
	${PN}-bcm43340-1bw-sdio \
	${PN}-bcm43430-1dx-sdio \
	${PN}-bcm43430-1fx-sdio \
	${PN}-bcm43439-1yn-sdio \
	${PN}-bcm43455-1lc-sdio \
	${PN}-bcm43455-1mw-sdio \
	${PN}-bcm4373-2ae-sdio \
	${PN}-bcm4373-2bc-sdio \
	${PN}-bcm54591-1xa-pcie \
	${PN}-bcm55500-2fy-sdio \
	"

FILES:${PN}-cypress-license = "${nonarch_base_libdir}/firmware/brcm/LICENSE.cypress-fmac-nvram"

FILES:${PN}-bcm43012-1lv-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43012-sdio.1LV.txt \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43012-sdio.1LV.txt \
	"

FILES:${PN}-bcm43022-2gf-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43022-sdio.2GF.txt \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43022-sdio.2GF.txt \
	"

FILES:${PN}-bcm43340-1bw-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43340-sdio.1BW.txt \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43340-sdio.1BW.txt \
	"

FILES:${PN}-bcm43430-1dx-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.1DX.txt \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43430-sdio.1DX.txt \
	"

FILES:${PN}-bcm43430-1fx-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.1FX.txt \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43430-sdio.1FX.txt \
	"

FILES:${PN}-bcm43439-1yn-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43439-sdio.1YN.txt \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43439-sdio.1YN.txt \
	"

FILES:${PN}-bcm43455-1lc-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.1LC.txt \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43455-sdio.1LC.txt \
	"

FILES:${PN}-bcm43455-1mw-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.1MW.txt \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43455-sdio.1MW.txt \
	"

FILES:${PN}-bcm4373-2ae-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.2AE.txt \
	${nonarch_base_libdir}/firmware/brcm/cyfmac4373-sdio.2AE.txt \
	"

FILES:${PN}-bcm4373-2bc-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.2BC.txt \
	${nonarch_base_libdir}/firmware/brcm/cyfmac4373-sdio.2BC.txt \
	"

FILES:${PN}-bcm54591-1xa-pcie = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac54591-pcie.1XA.txt \
	${nonarch_base_libdir}/firmware/brcm/cyfmac54591-pcie.1XA.txt \
	"

FILES:${PN}-bcm55500-2fy-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac55500-sdio.2FY.txt \
	${nonarch_base_libdir}/firmware/brcm/cyfmac55500-sdio.2FY.txt \
	"

RDEPENDS:${PN}-bcm43012-1lv-sdio += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm43022-2gf-sdio += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm43340-1bw-sdio += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm43430-1dx-sdio += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm43430-1fx-sdio += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm43439-1yn-sdio += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm43455-1lc-sdio += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm43455-1mw-sdio += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm4373-2ae-sdio += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm4373-2bc-sdio += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm54591-1xa-pcie += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm55500-2fy-sdio += "${PN}-cypress-license"
