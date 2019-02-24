PACKAGES =+ " ${PN}-rs9113 ${PN}-rs9116 "

LICENSE_${PN}-rs9113 = "WHENCE"
FILES_${PN}-rs9113 = " ${nonarch_base_libdir}/firmware/rsi/rs9113*.rps "
RDEPENDS_${PN}-rs9113 += "${PN}-whence-license"

LICENSE_${PN}-rs9116 = "WHENCE"
FILES_${PN}-rs9116 = " ${nonarch_base_libdir}/firmware/rsi/rs9116*.rps "
RDEPENDS_${PN}-rs9116 += "${PN}-whence-license"
