SUMMARY = "An image with various examples and demos"

LICENSE = "MIT"

inherit core-image

IMAGE_INSTALL_DH_STM32MP1 = " \
	kernel-modules u-boot-default-env libubootenv-bin \
	ca-certificates iw dropbear iproute2 init-ifupdown \
	i2c-tools canutils \
	libdrm mesa \
	libegl-mesa libgbm libgles1-mesa libgles2-mesa \
	libglapi mesa-megadriver devmem2 \
	\
	gstreamer1.0 gstreamer1.0-plugins-base \
	gstreamer1.0-plugins-good gstreamer1.0-plugins-bad \
	\
	weston weston-init weston-examples kmscube \
	piglit glmark2 vk-gl-cts iperf3 stress-ng \
	\
	nano \
	"

IMAGE_INSTALL_append_dh-stm32mp1-dhcom-pdk2 = " ${IMAGE_INSTALL_DH_STM32MP1} "
IMAGE_INSTALL_append_dh-stm32mp1-dhcor-avenger96 = " ${IMAGE_INSTALL_DH_STM32MP1} "

IMAGE_FEATURES_DH_STM32MP1 = "dev-pkgs tools-sdk tools-debug tools-profile debug-tweaks"
IMAGE_FEATURES_append_dh-stm32mp1-dhcom-pdk2 = " ${IMAGE_FEATURES_DH_STM32MP1} "
IMAGE_FEATURES_append_dh-stm32mp1-dhcor-avenger96 = " ${IMAGE_FEATURES_DH_STM32MP1} "
