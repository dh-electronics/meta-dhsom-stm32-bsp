FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
COMPATIBLE_MACHINE:dh-stm32mp13-dhcor-dhsbc = "dh-stm32mp13-dhcor-dhsbc"
TFA_PLATFORM:dh-stm32mp13-dhcor-dhsbc = "stm32mp1"
TFA_BUILD_TARGET:dh-stm32mp13-dhcor-dhsbc = "all fip"
TFA_INSTALL_TARGET:dh-stm32mp13-dhcor-dhsbc = "tf-a-stm32mp135f-dhcor-dhsbc.stm32 fip.bin"

do_compile[depends] += "${@'optee-os:do_deploy u-boot-mainline:do_deploy' if 'dh-stm32mp13-dhcor-dhsbc' in d.getVar('MACHINEOVERRIDES', True).split(':') else ' '}"

EXTRA_OEMAKE:append:dh-stm32mp13-dhcor-dhsbc = " \
	AARCH32_SP=optee \
	ARCH=aarch32 \
	ARM_ARCH_MAJOR=7 \
	BL32=${DEPLOY_DIR_IMAGE}/optee/tee-header_v2.bin \
	BL32_EXTRA1=${DEPLOY_DIR_IMAGE}/optee/tee-pager_v2.bin \
	BL32_EXTRA2=${DEPLOY_DIR_IMAGE}/optee/tee-pageable_v2.bin \
	BL33=${DEPLOY_DIR_IMAGE}/u-boot-nodtb.bin \
	BL33_CFG=${DEPLOY_DIR_IMAGE}/u-boot.dtb \
	DTB_FILE_NAME=stm32mp135f-dhcor-dhsbc.dtb \
	STM32MP_EMMC=1 \
	STM32MP_RCC_NS=1 \
	STM32MP_SPI_NOR=1 \
	STM32MP_USB_PROGRAMMER=1 \
	VERSION=${PV} \
	"

SRC_URI:append:dh-stm32mp13-dhcor-dhsbc = " \
	file://0001-feat-stm32mp1-add-optional-clearing-of-RCC_SECCFGR.patch \
	file://0002-feat-stm32mp1-fdts-add-support-for-STM32MP13xx-DHCOR.patch \
	"
