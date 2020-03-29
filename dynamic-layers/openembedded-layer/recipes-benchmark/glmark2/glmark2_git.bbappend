PACKAGECONFIG_dh-stm32mp1-dhcom-pdk2 = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland-gles2', '', d)} drm-gles2"
PACKAGECONFIG_dh-stm32mp1-dhcor-avenger96 = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland-gles2', '', d)} drm-gles2"
