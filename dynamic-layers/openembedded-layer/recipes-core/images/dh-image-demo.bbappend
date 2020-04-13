IMAGE_INSTALL_DH_STM32MP1_OE = " \
	ttf-dejavu-sans \
	ttf-dejavu-sans-mono \
	ttf-dejavu-sans-condensed \
	ttf-dejavu-serif \
	ttf-dejavu-serif-condensed \
	ttf-dejavu-common \
	v4l-utils yavta libgpiod libiio \
	packagegroup-tools-bluetooth \
	pulseaudio-server pulseaudio-misc \
	libusbgx \
	"

IMAGE_INSTALL_append_dh-stm32mp1-dhcom-pdk2 = " ${IMAGE_INSTALL_DH_STM32MP1_OE} "
IMAGE_INSTALL_append_dh-stm32mp1-dhcor-avenger96 = " ${IMAGE_INSTALL_DH_STM32MP1_OE} "
