require optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "8f645256efc0dc66bd5c118778b0b50c44469ae1"
SRC_URI:append = " file://0003-optee-enable-clang-support.patch"
