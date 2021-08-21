PACKAGECONFIG:append:dh-stm32mp1-dhsom = " \
	gbm egl \
	${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
	"
