PACKAGECONFIG_append_mainlinestm32mp1 = " gbm egl ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} "
