SUMMARY = "Trusted Firmware-A"

#
# Trusted firmware-A
#

# Use TF-A for version
SRCREV_FORMAT = "tfa"

# TF-A
LIC_FILES_CHKSUM = "file://license.rst;md5=1dd070c98a281d18d9eefd938729b031"

SRC_URI = "git://github.com/ARM-software/arm-trusted-firmware.git;protocol=https;branch=master;name=tfa"

SRC_URI[tfa.md5sum] = "75c8f4958fb493d9bd7a8e5a9636ec18"
SRC_URI[tfa.sha256sum] = "7c4c00a4f28d3cfbb235fd1a1fb28c4d2fc1d657c9301686e7d8824ef575d059"

SRCREV_tfa = "4ec2948fe3f65dba2f19e691e702f7de2949179c"

S = "${WORKDIR}/git"

require trusted-firmware-a.inc

do_compile:prepend() {
    # This is still needed to have the native tools executing properly by
    # setting the RPATH
    sed -i '/^LDOPTS/ s,$, \$\{BUILD_LDFLAGS},' ${S}/tools/fiptool/Makefile
    sed -i '/^INCLUDE_PATHS/ s,$, \$\{BUILD_CFLAGS},' ${S}/tools/fiptool/Makefile
    sed -i '/^LIB/ s,$, \$\{BUILD_LDFLAGS},' ${S}/tools/cert_create/Makefile
}
