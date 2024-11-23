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

SRCREV_tfa = "b6c0948400594e3cc4dbb5a4ef04b815d2675808"

S = "${WORKDIR}/git"

require trusted-firmware-a.inc

# The following hack is needed to fit properly in yocto build environment
# TFA is forcing the host compiler and its flags in the Makefile using :=
# assignment for GCC and CFLAGS.
# To properly use the native toolchain of yocto and the right libraries we need
# to pass the proper flags to gcc. This is achieved here by creating a gcc
# script to force passing to gcc the right CFLAGS and LDFLAGS
do_compile:prepend() {
    #Create an host gcc build parser to ensure the proper include path is used
    mkdir -p bin
    echo "#!/usr/bin/env bash" > bin/gcc
    echo "$(which ${BUILD_CC}) ${BUILD_CFLAGS} ${BUILD_LDFLAGS} \$@" >> bin/gcc
    chmod a+x bin/gcc
    export PATH="$PWD/bin:$PATH"
}
