SUMMARY = "A Qt5 image with all examples and demos from meta-qt5"

LICENSE = "MIT"

inherit core-image

IMAGE_INSTALL += "\
	kernel-modules dropbear iw \
	ca-certificates dropbear iproute2 init-ifupdown \
	i2c-tools canutils \
	ttf-dejavu-sans \
	ttf-dejavu-sans-mono \
	ttf-dejavu-sans-condensed \
	ttf-dejavu-serif \
	ttf-dejavu-serif-condensed \
	ttf-dejavu-common \
	libdrm mesa \
	libegl-mesa libgbm libgles1-mesa libgles2-mesa \
	libglapi mesa-megadriver devmem2 \
	\
	gstreamer1.0 gstreamer1.0-plugins-base \
	gstreamer1.0-plugins-good gstreamer1.0-plugins-bad \
	\
	qtwebengine-minimal qtwebengine-examples qtbase-examples \
	qtmultimedia-examples kmscube nginx \
	\
	weston weston-init weston-examples qtwayland \
	piglit glmark2 vk-gl-cts iperf3 stress-ng \
	\
	nano \
	"

IMAGE_FEATURES += "dev-pkgs tools-sdk tools-debug tools-profile debug-tweaks"
