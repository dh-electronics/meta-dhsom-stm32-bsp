SUMMARY = "Trusted Firmware-A"

#
# Trusted firmware-A
#

# Use TF-A for version
SRCREV_FORMAT = "tfa"

# TF-A
LIC_FILES_CHKSUM = "file://license.rst;md5=1dd070c98a281d18d9eefd938729b031"

SRC_URI = "git://github.com/ARM-software/arm-trusted-firmware.git;protocol=https;nobranch=1;name=tfa"

SRCREV_tfa = "e82c7ced9e76aea35b176e608d67dfe5ebe1c569"

S = "${WORKDIR}/git"

require trusted-firmware-a.inc

do_compile:prepend() {
    # This is still needed to have the native tools executing properly by
    # setting the RPATH
    sed -i '/^LDOPTS/ s,$, \$\{BUILD_LDFLAGS},' ${S}/tools/fiptool/Makefile
    sed -i '/^INCLUDE_PATHS/ s,$, \$\{BUILD_CFLAGS},' ${S}/tools/fiptool/Makefile
    sed -i '/^LIB/ s,$, \$\{BUILD_LDFLAGS},' ${S}/tools/cert_create/Makefile
}
