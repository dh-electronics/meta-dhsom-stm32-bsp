FILESEXTRAPATHS:prepend := "${THISDIR}/files/common:${THISDIR}/files/${MACHINE}:${THISDIR}/files:"

do_compile:prepend:dh-stm32mp1-dhsom () {
	sed -i -e "s/%UBOOT_DTB_LOADADDRESS%/${UBOOT_DTB_LOADADDRESS}/g" \
		-e "s/%UBOOT_DTBO_LOADADDRESS%/${UBOOT_DTBO_LOADADDRESS}/g" \
		${WORKDIR}/${UBOOT_ENV_SRC}
}

SRC_URI:append:dh-stm32mp1-dhsom = " \
	file://boot.cmd \
	file://fw_env.config \
	file://default-device-tree.cfg \
	file://0001-scripts-setlocalversion-Reinstate-.scmversion-suppor.patch \
	file://0002-ARM-dts-stm32-Increase-CPU-core-voltage-on-STM32MP13.patch \
	file://0003-mmc-Fix-size-calculation-for-sector-addressed-MMC-ve.patch \
	file://0004-env-mmc-Make-redundant-env-in-both-eMMC-boot-partiti.patch \
	file://0005-env-mmc-Clean-up-env_mmc_load-ifdeffery.patch \
	file://0006-ARM-dts-stm32-Add-support-for-environment-in-eMMC-on.patch \
	file://0007-ARM-stm32mp-Fix-dram_bank_mmu_setup-for-ram_top-0.patch \
	file://0008-ARM-dts-stm32-Add-support-for-STM32MP13xx-DHCOR-SoM-.patch \
	"

do_deploy:append:dh-stm32mp13-dhcor-dhsbc() {
        install -D -m 644 ${B}/u-boot.dtb ${DEPLOYDIR}/u-boot.dtb
}

# U-Boot release extra version, used as identifier of a patch
# release. Update this every time this recipe is updated. The
# format is -${MACHINE}-date.extraversion. The date is in the
# format YYYYMMDD, the extraversion is used in case there are
# multiple releases during a single day, which is unlikely.
UBOOT_LOCALVERSION:dh-stm32mp1-dhsom ?= "-${MACHINE}-20250309.02"
