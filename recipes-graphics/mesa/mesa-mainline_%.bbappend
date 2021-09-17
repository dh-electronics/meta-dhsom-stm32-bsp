FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
# MESA_BUILD_TYPE = "debug"
DEFAULT_PREFERENCE:dh-stm32mp1-dhsom = "1"
PACKAGECONFIG:append:dh-stm32mp1-dhsom = " \
	etnaviv kmsro gallium \
	${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
	"
