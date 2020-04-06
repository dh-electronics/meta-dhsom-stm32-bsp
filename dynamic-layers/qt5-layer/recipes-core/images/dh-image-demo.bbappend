IMAGE_INSTALL_DH_STM32MP1_QT5 = " \
	qtwebengine-minimal qtwebengine-examples qtbase-examples \
	qtmultimedia-examples qtwayland \
	"

IMAGE_INSTALL_append_dh-stm32mp1-dhcom-pdk2 = " ${IMAGE_INSTALL_DH_STM32MP1_QT5} "
IMAGE_INSTALL_append_dh-stm32mp1-dhcor-avenger96 = " ${IMAGE_INSTALL_DH_STM32MP1_QT5} "
