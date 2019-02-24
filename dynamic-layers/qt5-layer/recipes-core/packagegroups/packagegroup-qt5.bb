DESCRIPTION = "Packagegroup for qt5 image."
MAINTAINER = "Diego Sueiro <diego.sueiro@embarcados.com.br>"

LICENSE = "MIT"

inherit packagegroup

PACKAGES += "\
	packagegroup-qt5-base \
	packagegroup-qt5-graphics \
	packagegroup-qt5-extra \
	"

RDEPENDS_packagegroup-qt5-base = "\
	qtbase \
	qtbase-tools \
	qtbase-plugins \
	"

RDEPENDS_packagegroup-qt5-graphics ="\
	qt3d \
	qt3d-qmlplugins \
	qt3d-tools \
	qtdeclarative \
	qtdeclarative-qmlplugins \
	qtdeclarative-tools \
	qtgraphicaleffects-qmlplugins \
	qtimageformats-plugins \
	qtmultimedia \
	qtmultimedia-plugins \
	qtmultimedia-qmlplugins \
	qtsvg \
	qtsvg-plugins \
	"

RDEPENDS_packagegroup-qt5-extra ="\
	qttools \
	qttools-plugins \
	qttools-tools \
	qtconnectivity \
	qtconnectivity-qmlplugins \
	qtenginio \
	qtenginio-qmlplugins \
	qtlocation \
	qtlocation-plugins \
	qtlocation-qmlplugins \
	qtscript \
	qtsensors \
	qtsensors-plugins \
	qtsensors-qmlplugins \
	qtserialport \
	qtsystems \
	qtsystems-qmlplugins \
	qtsystems-tools \
	qtwebsockets \
	qtwebsockets-qmlplugins \
	qtxmlpatterns \
	qtxmlpatterns-tools \
	qtquickcontrols-qmlplugins \
	"
