PACKAGECONFIG_APPEND_DH_STM32MP1 = " \
	wayland-client \
	wayland-server \
	wayland-egl \
	wayland-drm-egl-server-buffer \
	${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xcomposite-egl xcomposite-glx', '', d)} \
	"

PACKAGECONFIG_dh-stm32mp1-dhcom-pdk2 = " ${PACKAGECONFIG_APPEND_DH_STM32MP1} "
PACKAGECONFIG_dh-stm32mp1-dhcor-avenger96 = " ${PACKAGECONFIG_APPEND_DH_STM32MP1} "
