SUMMARY = "Infineon fmac firmware files"
SECTION = "kernel"

LICENSE = "Firmware-infineon-fmac-fw"
LICENSE:${PN}-infineon-license = "Firmware-infineon-fmac-fw"
LICENSE:${PN}-bcm43439-sdio = "Firmware-infineon-fmac-fw"
LICENSE:${PN}-bcm43455-sdio = "Firmware-infineon-fmac-fw"
LICENSE:${PN}-bcm4373-sdio = "Firmware-infineon-fmac-fw"
LICENSE:${PN}-bcm55500-sdio = "Firmware-infineon-fmac-fw"

LIC_FILES_CHKSUM = "file://LICENCE;md5=cbc5f665d04f741f1e006d2096236ba7"

# These are not common licenses, set NO_GENERIC_LICENSE for them
# so that the license files will be copied from fetched source
NO_GENERIC_LICENSE[Firmware-infineon-fmac-fw] = "LICENCE"

SRC_URI = "git://github.com/Infineon/ifx-linux-firmware;protocol=https;branch=master"
SRCREV = "ded7e50fed4ab0c3fdfd7c42d1747da628cfd447"

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
	install -m 0644 firmware/LICENCE ${D}${nonarch_base_libdir}/firmware/brcm/LICENSE.infineon-fmac-fw
	install -m 0644 firmware/cyfmac43439-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 firmware/cyfmac43439-sdio.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 firmware/cyfmac43455-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 firmware/cyfmac43455-sdio.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 firmware/cyfmac4373-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 firmware/cyfmac4373-sdio.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 firmware/cyfmac55500-sdio.trxse ${D}${nonarch_base_libdir}/firmware/brcm/
	install -m 0644 firmware/cyfmac55500-sdio.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/

	# Symlink the firmware names
	ln -s cyfmac43439-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43439-sdio.bin
	ln -s cyfmac43439-sdio.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43439-sdio.clm_blob
	ln -s cyfmac43455-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.bin
	ln -s cyfmac43455-sdio.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.clm_blob
	ln -s cyfmac4373-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.bin
	ln -s cyfmac4373-sdio.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.clm_blob
	ln -s cyfmac55500-sdio.trxse ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac55500-sdio.trxse
	ln -s cyfmac55500-sdio.clm_blob ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac55500-sdio.clm_blob

	# FIXME: Package other firmwares too
	# FIXME: How do we handle the brcmfmac43455-sdio.*.clm_blob options?
}

ALLOW_EMPTY:${PN} = "1"

PACKAGES = " ${PN}-infineon-license ${PN}-bcm43439-sdio ${PN}-bcm43455-sdio ${PN}-bcm4373-sdio ${PN}-bcm55500-sdio "

FILES:${PN}-infineon-license = "${nonarch_base_libdir}/firmware/brcm/LICENSE.infineon-fmac-fw"

FILES:${PN}-bcm43439-sdio:append = " \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43439-sdio.bin \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43439-sdio.bin \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43439-sdio.clm_blob \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43439-sdio.clm_blob \
	"

FILES:${PN}-bcm43455-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43455-sdio.bin \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.bin \
	${nonarch_base_libdir}/firmware/brcm/cyfmac43455-sdio.clm_blob \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.clm_blob \
	"

FILES:${PN}-bcm4373-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/cyfmac4373-sdio.bin \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.bin \
	${nonarch_base_libdir}/firmware/brcm/cyfmac4373-sdio.clm_blob \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.clm_blob \
	"

FILES:${PN}-bcm55500-sdio = " \
	${nonarch_base_libdir}/firmware/brcm/cyfmac55500-sdio.trxse \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac55500-sdio.trxse \
	${nonarch_base_libdir}/firmware/brcm/cyfmac55500-sdio.clm_blob \
	${nonarch_base_libdir}/firmware/brcm/brcmfmac55500-sdio.clm_blob \
	"

RDEPENDS:${PN}-bcm43439-sdio += "${PN}-infineon-license"
RDEPENDS:${PN}-bcm43455-sdio += "${PN}-infineon-license"
RDEPENDS:${PN}-bcm4373-sdio += "${PN}-infineon-license"
RDEPENDS:${PN}-bcm55500-sdio += "${PN}-infineon-license"

RCONFLICTS:${PN}-bcm4373-sdio = "linux-firmware-bcm4373"
