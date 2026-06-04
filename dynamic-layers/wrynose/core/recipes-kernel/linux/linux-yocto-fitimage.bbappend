FIT_UBOOT_ENV:dh-stm32mp-dhsom = "${UBOOT_ENV}.${UBOOT_ENV_SUFFIX}"

python do_compile:prepend:dh-stm32mp-dhsom () {
    import shutil

    bootscr_deploydir = d.getVar('DEPLOY_DIR_IMAGE')
    fit_uboot_env = d.getVar("FIT_UBOOT_ENV")
    shutil.copyfile(os.path.join(bootscr_deploydir, fit_uboot_env), fit_uboot_env)
}

do_compile[depends] += "${@'u-boot-mainline:do_deploy' if ('dh-stm32mp-dhsom' in d.getVar('MACHINEOVERRIDES', True).split(':')) else ' '}"
