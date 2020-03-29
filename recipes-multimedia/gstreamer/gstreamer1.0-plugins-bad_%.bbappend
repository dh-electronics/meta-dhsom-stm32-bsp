PACKAGECONFIG_append_dh-stm32mp1-dhcom-pdk2 = " kms ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} "
