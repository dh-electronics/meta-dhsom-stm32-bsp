LICENSE = "BSD-3-Clause & MIT"

COMPATIBLE_MACHINE ?= "invalid"

# Platform must be set for each machine
TFA_PLATFORM ?= "invalid"

# Build for debug (set TFA_DEBUG to 1 to activate)
TFA_DEBUG ?= "0"

# Sub-directory in which to build.
# This must be coherent with BUILD_PLAT make parameter so that deploy can find
# the produced binaries
TFA_BUILD_DIR ?= "build-${TFA_PLATFORM}"
# set BUILD_PLAT depending on configured BUILD_DIR
EXTRA_OEMAKE += "BUILD_PLAT=${TFA_BUILD_DIR}"

# We need dtc for dtbs compilation
# We need openssl for fiptool
DEPENDS:append = " dtc-native openssl-native"

PROVIDES = "virtual/trusted-firmware-a"

inherit deploy nopackages

PACKAGE_ARCH = "${MACHINE_ARCH}"

# What to build
# By default we only build bl1, do_deploy will copy
# everything listed in this variable (by default bl1.bin)
TFA_BUILD_TARGET ?= "bl1"

# What to install
# do_install and do_deploy will install everything listed in this
# variable. It is set by default to TFA_BUILD_TARGET
TFA_INSTALL_TARGET ?= "${TFA_BUILD_TARGET}"

# Requires CROSS_COMPILE set by hand as there is no configure script
export CROSS_COMPILE="${TARGET_PREFIX}"

# Let the Makefile handle setting up the CFLAGS and LDFLAGS as it is a standalone application
CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"

# No configure
do_configure[noexec] = "1"

# Add platform parameter
EXTRA_OEMAKE += "PLAT=${TFA_PLATFORM}"

# Handle TFA_DEBUG parameter
EXTRA_OEMAKE:append = " ${@bb.utils.contains('TFA_DEBUG', '1', ' DEBUG=${TFA_DEBUG}', '', d)}"

do_compile() {
    oe_runmake ${TFA_BUILD_TARGET}
}

do_install() {
    install -d -m 755 ${D}/boot
    for atfbin in ${TFA_INSTALL_TARGET}; do
        if [ "$atfbin" = "all" ]; then
            # Target all is not handled by default
            bberror "all as TFA_INSTALL_TARGET is not handled by do_install"
            bberror "Please specify valid targets in TFA_INSTALL_TARGET or"
            bberror "rewrite or turn off do_install"
            exit 1
        elif [ -f ${S}/${TFA_BUILD_DIR}/$atfbin.bin ]; then
            echo "Install $atfbin.bin"
            install -m 0644 ${S}/${TFA_BUILD_DIR}/$atfbin.bin \
                ${D}/boot/$atfbin-${TFA_PLATFORM}.bin
        elif [ -f ${S}/${TFA_BUILD_DIR}/$atfbin.elf ]; then
            echo "Install $atfbin.elf"
            install -m 0644 ${S}/${TFA_BUILD_DIR}/$atfbin.elf \
                ${D}/boot/$atfbin-${TFA_PLATFORM}.elf
        elif [ -f ${S}/${TFA_BUILD_DIR}/$atfbin ]; then
            echo "Install $atfbin"
            install -m 0644 ${S}/${TFA_BUILD_DIR}/$atfbin \
                ${D}/boot/$atfbin-${TFA_PLATFORM}
        elif [ "$atfbin" = "dtbs" ]; then
            echo "dtbs install, skipped"
        elif [ -f ${S}/tools/$atfbin/$atfbin ]; then
            echo "Tools $atfbin install, skipped"
        else
            bberror "Unsupported TFA_INSTALL_TARGET target $atfbin"
            exit 1
        fi
    done
}

SYSROOT_DIRS += "/boot"

do_deploy() {
    cp -rf ${D}/boot/* ${DEPLOYDIR}/
}
addtask deploy after do_install
