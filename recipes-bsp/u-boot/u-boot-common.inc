HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
DESCRIPTION = "U-Boot, a boot loader for Embedded boards based on PowerPC, \
ARM, MIPS and several other processors, which can be installed in a boot \
ROM and used to initialize and test the hardware or to download and run \
application code."
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=30503fd321432fc713238f582193b78e"
PE = "1"

# Drop this line when updating to 2020.01 final
PV = "2020.01"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "0b0c6af38738f2c132cfd41a240889acaa031c8f"

SRC_URI = "git://git.denx.de/u-boot.git \
          "

S = "${WORKDIR}/git"

SRC_URI_append_mainlinestm32mp1 = " \
	file://0001-include-env.h-Ensure-ulong-is-defined.patch \
	file://0002-stm32mp1-configs-Make-all-boot-devices-in-distro-boo.patch \
	file://0003-ARM-stm32-Permit-multiple-board-targets.patch \
	file://0004-ARM-stm32-Allow-overriding-setup_mac_address.patch \
	file://0005-ARM-stm32-Fill-in-missing-loadaddr.patch \
	file://0006-ARM-dts-stm32-Add-missing-ETHCK-clock-to-ethernet-no.patch \
	file://0007-ARM-dts-stm32-Add-DH-Electronics-DHCOM-SoM-and-PDK2-.patch \
	"
