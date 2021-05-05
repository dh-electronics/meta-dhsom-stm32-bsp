SUMMARY = "Cypress fmac NVRAM files"
SECTION = "kernel"

LICENSE = "Firmware-cypress-fmac-nvram"

LIC_FILES_CHKSUM = "file://LICENCE.cypress;md5=cbc5f665d04f741f1e006d2096236ba7"

# These are not common licenses, set NO_GENERIC_LICENSE for them
# so that the license files will be copied from fetched source
NO_GENERIC_LICENSE[Firmware-cypress-fmac-nvram] = "LICENCE.cypress"

SRCREV = "4a7974090495b7f8e8c1d332b151109d3350e40b"
SRC_URI = "git://github.com/murata-wireless/cyw-fmac-nvram;protocol=https"

PACKAGE_ARCH = "${MACHINE_ARCH}"
EXCLUDE_FROM_SHLIBS = "1"
INHIBIT_DEFAULT_DEPS = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

CLEANBROKEN = "1"

do_compile() {
	:
}

do_install() {
	install -d ${D}${nonarch_base_libdir}/firmware/
	install -d ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 LICENCE.cypress ${D}${nonarch_base_libdir}/firmware/brcm/LICENSE.cypress-fmac-nvram
	install -m 0644 brcmfmac43*.txt ${D}${nonarch_base_libdir}/firmware/brcm/
}

ALLOW_EMPTY_${PN} = "1"

PACKAGES = " \
	${PN}-cypress-license \
	${PN}-bcm43012-1lv-sdio \
	${PN}-bcm43340-1bw-sdio \
	${PN}-bcm43362-sn8000-sdio \
	${PN}-bcm4339-1ck-sdio \
	${PN}-bcm4339-zp-sdio \
	${PN}-bcm43430-1dx-sdio \
	${PN}-bcm43430-1fx-sdio \
	${PN}-bcm43430-1ln-sdio \
	${PN}-bcm43455-1hk-sdio \
	${PN}-bcm43455-1lc-sdio \
	${PN}-bcm43455-1mw-sdio \
	${PN}-bcm4354-1bb-sdio \
	${PN}-bcm4356-1cx-pcie \
	"

LICENSE_${PN}-cypress-license = "Firmware-cypress-fmac-nvram"
FILES_${PN}-cypress-license = "${nonarch_base_libdir}/firmware/brcm/LICENSE.cypress-fmac-nvram"

FILES_${PN}-bcm43012-1lv-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43012-sdio.1LV.txt \
	"
LICENSE_${PN}-bcm43012-1lv-sdio = "Firmware-cypress-fmac-nvram"
RDEPENDS_${PN}-bcm43012-1lv-sdio += "${PN}-cypress-license"

FILES_${PN}-bcm43340-1bw-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43340-sdio.1BW.txt \
	"
LICENSE_${PN}-bcm43340-1bw-sdio = "Firmware-cypress-fmac-nvram"
RDEPENDS_${PN}-bcm43340-1bw-sdio += "${PN}-cypress-license"

FILES_${PN}-bcm43362-sn8000-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43362-sdio.SN8000.txt \
	"
LICENSE_${PN}-bcm43362-sn8000-sdio = "Firmware-cypress-fmac-nvram"
RDEPENDS_${PN}-bcm43362-sn8000-sdio += "${PN}-cypress-license"

FILES_${PN}-bcm4339-1ck-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac4339-sdio.1CK.txt \
	"
LICENSE_${PN}-bcm4339-1ck-sdio = "Firmware-cypress-fmac-nvram"
RDEPENDS_${PN}-bcm4339-1ck-sdio += "${PN}-cypress-license"

FILES_${PN}-bcm4339-zp-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac4339-sdio.ZP.txt \
	"
LICENSE_${PN}-bcm4339-zp-sdio = "Firmware-cypress-fmac-nvram"
RDEPENDS_${PN}-bcm4339-zp-sdio += "${PN}-cypress-license"

FILES_${PN}-bcm43430-1dx-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.1DX.txt \
	"
LICENSE_${PN}-bcm43430-1dx-sdio = "Firmware-cypress-fmac-nvram"
RDEPENDS_${PN}-bcm43430-1dx-sdio += "${PN}-cypress-license"

FILES_${PN}-bcm43430-1fx-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.1FX.txt \
	"
LICENSE_${PN}-bcm43430-1fx-sdio = "Firmware-cypress-fmac-nvram"
RDEPENDS_${PN}-bcm43430-1fx-sdio += "${PN}-cypress-license"

FILES_${PN}-bcm43430-1ln-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.1LN.txt \
	"
LICENSE_${PN}-bcm43430-1ln-sdio = "Firmware-cypress-fmac-nvram"
RDEPENDS_${PN}-bcm43430-1ln-sdio += "${PN}-cypress-license"

FILES_${PN}-bcm43455-1hk-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.1HK.txt \
	"
LICENSE_${PN}-bcm43455-1hk-sdio = "Firmware-cypress-fmac-nvram"
RDEPENDS_${PN}-bcm43455-1hk-sdio += "${PN}-cypress-license"

FILES_${PN}-bcm43455-1lc-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.1LC.txt \
	"
LICENSE_${PN}-bcm43455-1lc-sdio = "Firmware-cypress-fmac-nvram"
RDEPENDS_${PN}-bcm43455-1lc-sdio += "${PN}-cypress-license"

FILES_${PN}-bcm43455-1mw-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.1MW.txt \
	"
LICENSE_${PN}-bcm43455-1mw-sdio = "Firmware-cypress-fmac-nvram"
RDEPENDS_${PN}-bcm43455-1mw-sdio += "${PN}-cypress-license"

FILES_${PN}-bcm4354-1bb-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac4354-sdio.1BB.txt \
	"
LICENSE_${PN}-bcm4354-1bb-sdio = "Firmware-cypress-fmac-nvram"
RDEPENDS_${PN}-bcm4354-1bb-sdio += "${PN}-cypress-license"

FILES_${PN}-bcm4356-1cx-pcie = " \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac4356-pcie.1CX.txt \
	"
LICENSE_${PN}-bcm4356-1cx-pcie = "Firmware-cypress-fmac-nvram"
RDEPENDS_${PN}-bcm4356-1cx-pcie += "${PN}-cypress-license"
