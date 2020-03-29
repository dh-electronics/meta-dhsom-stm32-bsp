PACKAGECONFIG_append_dh-stm32mp1-dhcom-pdk2 = " etnaviv kmsro gallium ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} "
DEFAULT_PREFERRENCE_dh-stm32mp1-dhcom-pdk2 = "1"
# MESA_BUILD_TYPE = "debug"
