DEFAULTTUNE ?= "cortexa7thf-neon-vfpv4"
SOC_FAMILY ?= "stm32mp1"

MACHINEOVERRIDES =. "dh-dhsom:dh-stm32mp-dhsom:dh-stm32mp1-dhsom:"

ARM_INSTRUCTION_SET = "thumb"

require conf/machine/include/arm/armv7a/tune-cortexa7.inc
require conf/machine/include/soc-family.inc

MACHINE_FEATURES ?= "apm alsa ext2 keyboard mainline screen serial touchscreen usbgadget usbhost"

SERIAL_CONSOLES = "115200;ttySTM0"

# U-Boot bootloader
UBOOT_ENTRYPOINT := "0xc4000000"
UBOOT_MAKE_TARGET := "all"
UBOOT_BINARY := "u-boot.itb"
UBOOT_SUFFIX ?= "itb"
SPL_BINARY := "u-boot-spl.stm32"
UBOOT_ENV = "boot"
UBOOT_ENV_SUFFIX = "scr"
# fitImage DT and DTO load addresses
UBOOT_DTB_LOADADDRESS = "0xcff00000"
UBOOT_DTBO_LOADADDRESS = "0xcff80000"

PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-mainline"
PREFERRED_PROVIDER_u-boot ?= "u-boot-mainline"
PREFERRED_PROVIDER_u-boot-native ?= "u-boot-mainline"
PREFERRED_PROVIDER_u-boot-fw-utils ?= "u-boot-mainline-fw-utils"
PREFERRED_PROVIDER_u-boot-fw-utils-native ?= "u-boot-mainline-fw-utils-native"
PREFERRED_PROVIDER_u-boot-mkimage-native ?= "u-boot-mainline-tools-native"
PREFERRED_PROVIDER_u-boot-tools ?= "u-boot-mainline-tools"
PREFERRED_PROVIDER_u-boot-tools-native ?= "u-boot-mainline-tools-native"
PREFERRED_VERSION_u-boot-mainline ?= "2025.01%"
PREFERRED_VERSION_u-boot-mainline-native ?= "2025.01%"
PREFERRED_VERSION_u-boot-mainline-fw-utils ?= "2025.01%"
PREFERRED_VERSION_u-boot-mainline-fw-utils-native ?= "2025.01%"
PREFERRED_VERSION_u-boot-mainline-tools ?= "2025.01%"
PREFERRED_VERSION_u-boot-mainline-tools-native ?= "2025.01%"

# Linux kernel
KERNEL_IMAGETYPE = "fitImage"
KERNEL_CLASSES += " kernel-fitimage "
KERNEL_MODULES_AUTOLOAD += " etnaviv "

PREFERRED_PROVIDER_virtual/kernel ?= "linux-stable"
PREFERRED_VERSION_linux-stable ?= "6.12%"
KERNEL_DEVICETREE_PATH_PREFIX = "${@'st/' if (d.getVar('PREFERRED_VERSION_linux-stable') in ["6.6%", "6.12%"]) else ''}"
KERNEL_DTBVENDORED = "0"
KERNEL_DTC_FLAGS += "-@"

# Mesa
PREFERRED_PROVIDER_virtual/egl ?= "mesa-mainline"
PREFERRED_PROVIDER_virtual/libgl ?= "mesa-mainline"
PREFERRED_PROVIDER_virtual/libgles1 ?= "mesa-mainline"
PREFERRED_PROVIDER_virtual/libgles2 ?= "mesa-mainline"
PREFERRED_PROVIDER_virtual/libgles3 ?= "mesa-mainline"
PREFERRED_PROVIDER_virtual/mesa ?= "mesa-mainline"
PREFERRED_PROVIDER_drm ?= "libdrm-mainline"
PREFERRED_PROVIDER_libdrm ?= "libdrm-mainline"
PREFERRED_PROVIDER_nativesdk-libdrm ?= "nativesdk-libdrm-mainline"
PREFERRED_PROVIDER_wayland-protocols ?= "wayland-protocols-mainline"
PREFERRED_PROVIDER_nativesdk-wayland-protocols ?= "nativesdk-wayland-protocols-mainline"

# WPE (from meta-webkit)
PREFERRED_RPROVIDER_virtual/wpebackend ?= "wpebackend-fdo"

# Ship kernel modules
MACHINE_ESSENTIAL_EXTRA_RDEPENDS += " kernel-modules kernel-devicetree u-boot-default-env "

# List of images to be built
IMAGE_FSTYPES ?= "tar.zst wic.zst wic.gz wic.bmap"
INITRAMFS_FSTYPES ?= "cpio.zst"

EXTRA_IMAGEDEPENDS += "u-boot-mainline"
WKS_FILE = "sdimage-stm32mp1.wks"
IMAGE_BOOT_FILES ?= "${KERNEL_IMAGETYPE}"
IMAGE_INSTALL:append = " kernel-devicetree kernel-image-fitimage u-boot-mainline "
do_image_wic[depends] += "mtools-native:do_populate_sysroot dosfstools-native:do_populate_sysroot u-boot-mainline:do_deploy"

# Add any optional config files that might be present e.g. in other layers
include conf/machine/include/dh-stm32mp1-common-extra.inc
include conf/machine/${MACHINE}-extra.conf
