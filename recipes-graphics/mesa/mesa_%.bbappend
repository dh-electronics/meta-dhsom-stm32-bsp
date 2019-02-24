FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

PACKAGECONFIG_append_mainlinestm32mp1 = " etnaviv kmsro gallium ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} "
EXTRA_OEMESON_append_mainlinestm32mp1 = " --buildtype=debug "

SRC_URI_append_mainlinestm32mp1 = " \
	file://0001-etnaviv-fix-FRONT_AND_BACK-culling.patch \
	file://0002-etnaviv-fix-two-sided-stencil.patch \
	file://0003-etnaviv-Check-for-icache-availability-if-required.patch \
	file://0004-etnaviv-Fix-flat-shading-handling-on-GCnano.patch \
	"
