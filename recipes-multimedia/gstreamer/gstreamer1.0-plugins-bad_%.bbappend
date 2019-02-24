PACKAGECONFIG_append_mainlinestm32mp1 = " kms ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} "
