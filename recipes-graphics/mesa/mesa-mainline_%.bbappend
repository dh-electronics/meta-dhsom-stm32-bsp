PACKAGECONFIG_append_mainlinestm32mp1 = " etnaviv kmsro gallium ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} "
DEFAULT_PREFERRENCE_mainlinestm32mp1 = "1"
# MESA_BUILD_TYPE = "debug"
