PACKAGECONFIG_mainlinestm32mp1 = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland-gles2', '', d)} drm-gles2"
