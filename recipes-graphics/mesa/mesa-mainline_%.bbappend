FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
# MESA_BUILD_TYPE = "debug"
DEFAULT_PREFERRENCE_dh-stm32mp1-dhsom = "1"
PACKAGECONFIG_append_dh-stm32mp1-dhsom = " \
	etnaviv kmsro gallium \
	${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
	"
SRC_URI_append_dh-stm32mp1-dhsom = " \
	file://0001-etnaviv-Fix-rework-ZSA-into-a-derived-state.patch \
	"
