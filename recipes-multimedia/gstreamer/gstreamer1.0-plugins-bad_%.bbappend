PACKAGECONFIG:append:dh-stm32mp1-dhsom = " \
	kms \
	${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
	"
