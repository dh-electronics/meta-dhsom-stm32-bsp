#!/bin/sh

export QT_QPA_PLATFORM=eglfs
export QT_QPA_EGLFS_ALWAYS_SET_MODE=1
export QT_QPA_EGLFS_KMS_CONFIG=/etc/default/qt5-eglfs-kms.json
