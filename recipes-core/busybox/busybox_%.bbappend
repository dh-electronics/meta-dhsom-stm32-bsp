FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append_dh-stm32mp1-dhcom-pdk2 = " file://nc.cfg "
SRC_URI_append_dh-stm32mp1-dhcor-avenger96 = " file://nc.cfg "
