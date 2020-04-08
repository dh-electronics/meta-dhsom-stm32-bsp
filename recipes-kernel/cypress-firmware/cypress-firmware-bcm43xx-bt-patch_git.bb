SUMMARY = "Cypress bluetooth patch files"
SECTION = "kernel"

LICENSE = "Firmware-cypress-bt-patch"

LIC_FILES_CHKSUM = "file://LICENCE.cypress;md5=cbc5f665d04f741f1e006d2096236ba7"

# These are not common licenses, set NO_GENERIC_LICENSE for them
# so that the license files will be copied from fetched source
NO_GENERIC_LICENSE[Firmware-cypress-bt-patch] = "LICENCE.cypress"

SRCREV = "748462f0b02ec4aeb500bedd60780ac51c37be31"
SRC_URI = "git://github.com/murata-wireless/cyw-bt-patch;protocol=https"

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
	install -m 0644 * ${D}${nonarch_base_libdir}/firmware/brcm/

	# Remove README, move LICENSE
	rm ${D}${nonarch_base_libdir}/firmware/brcm/README_BT_PATCHFILE
	mv ${D}${nonarch_base_libdir}/firmware/brcm/LICENCE.cypress \
	   ${D}${nonarch_base_libdir}/firmware/brcm/LICENSE.cypress-bt-patch

	# Symlink the firmware names
	ln -s CYW43012C0.1LV.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM43012C0.hcd
	ln -s CYW43430A1.1DX.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM4343A1.hcd
	ln -s CYW4345C0.1MW.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM4345C0.hcd
	ln -s CYW4354A2.1CX.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM4356A2.hcd
	ln -s CYW43341B0.1BW.hcd ${D}${nonarch_base_libdir}/firmware/brcm/CYW43341B0.hcd
	ln -s CYW4335C0.ZP.hcd ${D}${nonarch_base_libdir}/firmware/brcm/CYW4335C0.hcd
	ln -s CYW4350C0.1BB.hcd ${D}${nonarch_base_libdir}/firmware/brcm/CYW4350C0.hcd
}

ALLOW_EMPTY_${PN} = "1"

PACKAGES = " \
	${PN}-cypress-license \
	${PN}-bcm43012c0 ${PN}-bcm4343a1 ${PN}-bcm4345c0 ${PN}-bcm4356a2 \
	${PN}-cyw43341b0 ${PN}-cyw4335c0 ${PN}-cyw4350c0 \
	"

LICENSE_${PN}-cypress-license = "Firmware-cypress-bt-patch"
FILES_${PN}-cypress-license = "${nonarch_base_libdir}/firmware/brcm/LICENSE.cypress-bt-patch"

FILES_${PN}-bcm43012c0 = " \
	${nonarch_base_libdir}/firmware/brcm/CYW43012C0.1LV.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM43012C0.hcd \
	"
FILES_${PN}-bcm4343a1 = " \
	${nonarch_base_libdir}/firmware/brcm/CYW43430A1.1DX.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM4343A1.hcd \
	"
FILES_${PN}-bcm4345c0 = " \
	${nonarch_base_libdir}/firmware/brcm/CYW4345C0.1MW.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM4345C0.hcd \
	"
FILES_${PN}-bcm4356a2 = " \
	${nonarch_base_libdir}/firmware/brcm/CYW4354A2.1CX.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM4356A2.hcd \
	"
FILES_${PN}-cyw43341b0 = " \
	${nonarch_base_libdir}/firmware/brcm/CYW43341B0.1BW.hcd \
	${nonarch_base_libdir}/firmware/brcm/CYW43341B0.hcd \
	"
FILES_${PN}-cyw4335c0 = " \
	${nonarch_base_libdir}/firmware/brcm/CYW4335C0.ZP.hcd \
	${nonarch_base_libdir}/firmware/brcm/CYW4335C0.hcd \
	"
FILES_${PN}-cyw4350c0 = " \
	${nonarch_base_libdir}/firmware/brcm/CYW4350C0.1BB.hcd \
	${nonarch_base_libdir}/firmware/brcm/CYW4350C0.hcd \
	"

LICENSE_${PN}-bcm43012c0 = "Firmware-cypress-bt-patch"
RDEPENDS_${PN}-bcm43012c0 += "${PN}-cypress-license"
LICENSE_${PN}-bcm4343a1 = "Firmware-cypress-bt-patch"
RDEPENDS_${PN}-bcm4343a1 += "${PN}-cypress-license"
LICENSE_${PN}-bcm4345c0 = "Firmware-cypress-bt-patch"
RDEPENDS_${PN}-bcm4345c0 += "${PN}-cypress-license"
LICENSE_${PN}-bcm4356a2 = "Firmware-cypress-bt-patch"
RDEPENDS_${PN}-bcm4356a2 += "${PN}-cypress-license"
LICENSE_${PN}-cyw43341b0 = "Firmware-cypress-bt-patch"
RDEPENDS_${PN}-cyw43341b0 += "${PN}-cypress-license"
LICENSE_${PN}-cyw4335c0 = "Firmware-cypress-bt-patch"
RDEPENDS_${PN}-cyw4335c0 += "${PN}-cypress-license"
LICENSE_${PN}-cyw4350c0 = "Firmware-cypress-bt-patch"
RDEPENDS_${PN}-cyw4350c0 += "${PN}-cypress-license"
