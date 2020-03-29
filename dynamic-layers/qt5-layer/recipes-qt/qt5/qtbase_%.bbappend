FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG_APPEND_DH_STM32MP1 = " examples eglfs linuxfb gles2 kms gbm fontconfig freetype accessibility "

PACKAGECONFIG_append_dh-stm32mp1-dhcom-pdk2 = " ${PACKAGECONFIG_APPEND_DH_STM32MP1} "
PACKAGECONFIG_append_dh-stm32mp1-dhcor-avenger96 = " ${PACKAGECONFIG_APPEND_DH_STM32MP1} "

SRC_URI_append_dh-stm32mp1-dhcom-pdk2 = " \
	file://qt5-eglfs-kms.json \
	file://qt5-eglfs.sh \
	"

SRC_URI_append_dh-stm32mp1-dhcor-avenger96 = " \
	file://qt5-eglfs-kms.json \
	file://qt5-eglfs.sh \
	"

do_install_append_dh-stm32mp1-dhcom-pdk2() {
	install -d ${D}${sysconfdir}/default/
	install -m 0644 ${WORKDIR}/qt5-eglfs-kms.json ${D}${sysconfdir}/default/

	install -d ${D}${sysconfdir}/profile.d/
	install -m 0755 ${WORKDIR}/qt5-eglfs.sh ${D}${sysconfdir}/profile.d/
}

do_install_append_dh-stm32mp1-dhcor-avenger96() {
	install -d ${D}${sysconfdir}/default/
	install -m 0644 ${WORKDIR}/qt5-eglfs-kms.json ${D}${sysconfdir}/default/

	install -d ${D}${sysconfdir}/profile.d/
	install -m 0755 ${WORKDIR}/qt5-eglfs.sh ${D}${sysconfdir}/profile.d/
}
