FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
# MESA_BUILD_TYPE = "debug"
PACKAGECONFIG:append:dh-stm32mp1-dhsom = " \
	etnaviv gallium \
	${@'kmsro' if (bb.utils.vercmp_string_op(d.getVar('PV'), '25.0.0', '<')) else ''} \
	${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
	"
