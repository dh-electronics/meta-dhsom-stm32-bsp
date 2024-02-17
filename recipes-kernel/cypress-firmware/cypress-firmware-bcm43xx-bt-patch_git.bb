SUMMARY = "Cypress bluetooth patch files"
SECTION = "kernel"

LICENSE = "Firmware-cypress-bt-patch"
LICENSE:${PN}-cypress-license = "Firmware-cypress-bt-patch"
LICENSE:${PN}-bcm43012c0 = "Firmware-cypress-bt-patch"
LICENSE:${PN}-bcm4343a1 = "Firmware-cypress-bt-patch"
LICENSE:${PN}-bcm4343a2 = "Firmware-cypress-bt-patch"
LICENSE:${PN}-bcm4345c0 = "Firmware-cypress-bt-patch"
LICENSE:${PN}-bcm4356a2 = "Firmware-cypress-bt-patch"
LICENSE:${PN}-bcm4359d0-1xa = "Firmware-cypress-bt-patch"
LICENSE:${PN}-bcm4359d0-2bz = "Firmware-cypress-bt-patch"
LICENSE:${PN}-bcm4373a0-2ae = "Firmware-cypress-bt-patch"
LICENSE:${PN}-bcm4373a0-2bc = "Firmware-cypress-bt-patch"
LICENSE:${PN}-cyw43341b0 = "Firmware-cypress-bt-patch"
LICENSE:${PN}-cyw4335c0 = "Firmware-cypress-bt-patch"
LICENSE:${PN}-cyw4350c0 = "Firmware-cypress-bt-patch"

LIC_FILES_CHKSUM = "file://LICENCE.cypress;md5=cbc5f665d04f741f1e006d2096236ba7"

# These are not common licenses, set NO_GENERIC_LICENSE for them
# so that the license files will be copied from fetched source
NO_GENERIC_LICENSE[Firmware-cypress-bt-patch] = "LICENCE.cypress"

SRC_URI = "git://github.com/murata-wireless/cyw-bt-patch;protocol=https;branch=master"
SRCREV = "9d24c254dae92af99ddfd661a4ea30af69190038"

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
	rm ${D}${nonarch_base_libdir}/firmware/brcm/README_BT_PATCHFILE.txt
	mv ${D}${nonarch_base_libdir}/firmware/brcm/LICENCE.cypress \
	   ${D}${nonarch_base_libdir}/firmware/brcm/LICENSE.cypress-bt-patch

	# Symlink the firmware names
	ln -s CYW43012C0.1LV.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM43012C0.hcd
	ln -s BCM43012C0_003.001.015.0303.0267.1LV.sAnt.hcd ${D}${nonarch_base_libdir}/firmware/brcm/CYW43012C0.1LV.hcd
	ln -s CYW43430A1.1DX.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM43430A1.hcd
	ln -s BCM43430A1_001.002.009.0159.0528.1DX.hcd ${D}${nonarch_base_libdir}/firmware/brcm/CYW43430A1.1DX.hcd
	ln -s CYW4343A2.1YN.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM4343A2.hcd
	ln -s CYW4343A2_001.003.016.0031.0000.1YN.hcd ${D}${nonarch_base_libdir}/firmware/brcm/CYW4343A2.1YN.hcd
	ln -s CYW4345C0.1MW.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM4345C0.hcd
	ln -s BCM4345C0_003.001.025.0187.0366.1MW.hcd ${D}${nonarch_base_libdir}/firmware/brcm/CYW4345C0.1MW.hcd
	ln -s CYW4354A2.1CX.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM4356A2.hcd
	ln -s BCM4356A2_001.003.015.0112.0410.1CX.hcd ${D}${nonarch_base_libdir}/firmware/brcm/CYW4354A2.1CX.hcd
	ln -s CYW4359D0.1XA.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM4359D0.hcd
	ln -s BCM4359D0_004.001.016.0241.0275.1XA.sAnt.hcd ${D}${nonarch_base_libdir}/firmware/brcm/CYW4359D0.1XA.hcd
	ln -s BCM4359D0_004.001.016.0241.0275.2BZ.sAnt.hcd ${D}${nonarch_base_libdir}/firmware/brcm/CYW4359D0.2BZ.hcd
	ln -s CYW43341B0.1BW.hcd ${D}${nonarch_base_libdir}/firmware/brcm/CYW43341B0.hcd
	ln -s CYW4335C0.ZP.hcd ${D}${nonarch_base_libdir}/firmware/brcm/CYW4335C0.hcd
	ln -s CYW4350C0.1BB.hcd ${D}${nonarch_base_libdir}/firmware/brcm/CYW4350C0.hcd
	ln -s BCM4373A0.2AE.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM4373A0.hcd
	ln -s BCM4373A0_001.001.025.0103.0155.FCC.CE.2AE.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM4373A0.2AE.hcd
	ln -s BCM4373A0_001.001.025.0103.0155.FCC.CE.2BC.hcd ${D}${nonarch_base_libdir}/firmware/brcm/BCM4373A0.2BC.hcd
}

ALLOW_EMPTY:${PN} = "1"

PACKAGES = " \
	${PN}-cypress-license \
	${PN}-bcm43012c0 ${PN}-bcm4343a1 ${PN}-bcm4343a2 ${PN}-bcm4345c0 ${PN}-bcm4356a2 \
	${PN}-bcm4359d0-1xa ${PN}-bcm4359d0-2bz ${PN}-cyw43341b0 ${PN}-cyw4335c0 \
	${PN}-cyw4350c0 ${PN}-bcm4373a0-2ae ${PN}-bcm4373a0-2bc \
	"

FILES:${PN}-cypress-license = "${nonarch_base_libdir}/firmware/brcm/LICENSE.cypress-bt-patch"

FILES:${PN}-bcm43012c0 = " \
	${nonarch_base_libdir}/firmware/brcm/BCM43012C0_003.001.015.0303.0267.1LV.sAnt.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM43012C0_003.001.015.0300.0266.1LV.dAnt.hcd \
	${nonarch_base_libdir}/firmware/brcm/CYW43012C0.1LV.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM43012C0.hcd \
	"
FILES:${PN}-bcm4343a1 = " \
	${nonarch_base_libdir}/firmware/brcm/BCM43430A1_001.002.009.0159.0528.1DX.hcd \
	${nonarch_base_libdir}/firmware/brcm/CYW43430A1.1DX.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM43430A1.hcd \
	"
FILES:${PN}-bcm4343a2 = " \
	${nonarch_base_libdir}/firmware/brcm/CYW4343A2_001.003.016.0031.0000.1YN.hcd \
	${nonarch_base_libdir}/firmware/brcm/CYW4343A2.1YN.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM4343A2.hcd \
	"
FILES:${PN}-bcm4345c0 = " \
	${nonarch_base_libdir}/firmware/brcm/BCM4345C0_003.001.025.0187.0366.1MW.hcd \
	${nonarch_base_libdir}/firmware/brcm/CYW4345C0.1MW.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM4345C0.hcd \
	"
FILES:${PN}-bcm4356a2 = " \
	${nonarch_base_libdir}/firmware/brcm/BCM4356A2_001.003.015.0112.0410.1CX.hcd \
	${nonarch_base_libdir}/firmware/brcm/CYW4354A2.1CX.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM4356A2.hcd \
	"
FILES:${PN}-bcm4359d0-1xa = " \
	${nonarch_base_libdir}/firmware/brcm/BCM4359D0_004.001.016.0241.0274.1XA.dAnt.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM4359D0_004.001.016.0241.0275.1XA.sAnt.hcd \
	${nonarch_base_libdir}/firmware/brcm/CYW4359D0.1XA.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM4359D0.hcd \
	"
FILES:${PN}-bcm4359d0-2bz = " \
	${nonarch_base_libdir}/firmware/brcm/BCM4359D0_004.001.016.0241.0274.2BZ.dAnt.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM4359D0_004.001.016.0241.0275.2BZ.sAnt.hcd \
	${nonarch_base_libdir}/firmware/brcm/CYW4359D0.2BZ.hcd \
	"
FILES:${PN}-cyw43341b0 = " \
	${nonarch_base_libdir}/firmware/brcm/CYW43341B0.1BW.hcd \
	${nonarch_base_libdir}/firmware/brcm/CYW43341B0.hcd \
	"
FILES:${PN}-cyw4335c0 = " \
	${nonarch_base_libdir}/firmware/brcm/CYW4335C0.ZP.hcd \
	${nonarch_base_libdir}/firmware/brcm/CYW4335C0.hcd \
	"
FILES:${PN}-cyw4350c0 = " \
	${nonarch_base_libdir}/firmware/brcm/CYW4350C0.1BB.hcd \
	${nonarch_base_libdir}/firmware/brcm/CYW4350C0.hcd \
	"
FILES:${PN}-bcm4373a0-2ae = " \
	${nonarch_base_libdir}/firmware/brcm/BCM4373A0_001.001.025.0103.0155.FCC.CE.2AE.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM4373A0_001.001.025.0103.0156.JRL.2AE.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM4373A0.2AE.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM4373A0.hcd \
	"

FILES:${PN}-bcm4373a0-2bc = " \
	${nonarch_base_libdir}/firmware/brcm/BCM4373A0_001.001.025.0103.0155.FCC.CE.2BC.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM4373A0_001.001.025.0103.0156.JRL.2BC.hcd \
	${nonarch_base_libdir}/firmware/brcm/BCM4373A0.2BC.hcd \
	"

RDEPENDS:${PN}-bcm43012c0 += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm4343a1 += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm4343a2 += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm4345c0 += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm4356a2 += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm4359d0-1xa += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm4359d0-2bz += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm4373a0-2ae += "${PN}-cypress-license"
RDEPENDS:${PN}-bcm4373a0-2bc += "${PN}-cypress-license"
RDEPENDS:${PN}-cyw43341b0 += "${PN}-cypress-license"
RDEPENDS:${PN}-cyw4335c0 += "${PN}-cypress-license"
RDEPENDS:${PN}-cyw4350c0 += "${PN}-cypress-license"
