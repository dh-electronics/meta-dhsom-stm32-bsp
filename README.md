OpenEmbedded BSP layer for DH electronics STM32MP1 platforms
============================================================

This layer provides BSP for DH electronics STM32MP1 platforms.

# Dependencies
--------------

This layer depends on:

* URI: git://git.yoctoproject.org/poky
  - branch: dunfell or kirkstone
  - layers: meta

* URI: https://source.denx.de/denx/meta-mainline-common.git
  - branch: dunfell or kirkstone

# Building image
----------------

The OE build of this layer can be set up either manually or automatically using
the [KAS](https://github.com/siemens/kas) tool. Both options yield equal result.
The manual option is more flexible, the kas option is easier for starters.

## Building image (kas)
-----------------------

The [KAS](https://github.com/siemens/kas) tool downloads select OE layers, emits
matching bblayers.conf and local.conf, sets up required OE build variables and
even executes the build. The tool also provides menu-based build configuration.

Refer to [kas-dhsom](https://github.com/dh-electronics/kas-dhsom) repository for
build instructions and YAML files.

## Building image (manual)
--------------------------

A good starting point for setting up the build environment is is the official
Yocto Project wiki.

* https://www.yoctoproject.org/docs/3.1/brief-yoctoprojectqs/brief-yoctoprojectqs.html

Before attempting the build, the following metalayer git repositories shall
be cloned into a location accessible to the build system and a branch listed
below shall be checked out. The examples below will use /path/to/OE/ as a
location of the metalayers.

* https://source.denx.de/denx/meta-mainline-common.git			(branch: dunfell or kirkstone)
* https://github.com/dh-electronics/meta-dhsom-stm32-bsp.git    	(branch: dunfell or kirkstone)
* git://git.yoctoproject.org/poky					(branch: dunfell or kirkstone)

With all the source artifacts in place, proceed with setting up the build
using oe-init-build-env as specified in the Yocto Project wiki.

In addition to the content in the wiki, the aforementioned metalayers shall
be referenced in bblayers.conf in this order:

```
BBLAYERS ?= " \
  /path/to/OE/poky/meta \
  /path/to/OE/meta-mainline-common \
  /path/to/OE/meta-dhsom-stm32-bsp \
  "
```

The following specifics should be placed into local.conf:

```
MACHINE = "dh-stm32mp1-dhcom-pdk2"
DISTRO = "nodistro"
```

Note that MACHINE must be either of:

* dh-stm32mp1-dhcom-drc02
* dh-stm32mp1-dhcom-pdk2
* dh-stm32mp1-dhcom-picoitx
* dh-stm32mp1-dhcor-avenger96
* dh-stm32mp1-dhcor-drc-compact

Adapt the suffixes of all the files and names of directories further in
this documentation according to MACHINE.

Both local.conf and bblayers.conf are included verbatim in full at the end
of this readme.

Once the configuration is complete, a basic demo system image suitable for
evaluation can be built using:

```
$ bitbake core-image-minimal
```

Once the build completes, the images are available in:

```
tmp-glibc/deploy/images/dh-stm32mp1-dhcom-pdk2/
```

The SD card image is specifically in:

```
core-image-minimal-dh-stm32mp1-dhcom-pdk2.wic
```

And shall be written to the SD card using dd:

```
$ dd if=core-image-minimal-dh-stm32mp1-dhcom-pdk2.wic of=/dev/sdX bs=8M
```

### Example local.conf
----------------------
```
MACHINE = "dh-stm32mp1-dhcom-pdk2"
DL_DIR = "/path/to/OE/downloads"
DISTRO ?= "nodistro"
PACKAGE_CLASSES ?= "package_rpm"
EXTRA_IMAGE_FEATURES = "debug-tweaks"
USER_CLASSES ?= "buildstats"
PATCHRESOLVE = "noop"
BB_DISKMON_DIRS = "\
    STOPTASKS,${TMPDIR},1G,100K \
    STOPTASKS,${DL_DIR},1G,100K \
    STOPTASKS,${SSTATE_DIR},1G,100K \
    STOPTASKS,/tmp,100M,100K \
    ABORT,${TMPDIR},100M,1K \
    ABORT,${DL_DIR},100M,1K \
    ABORT,${SSTATE_DIR},100M,1K \
    ABORT,/tmp,10M,1K"
PACKAGECONFIG:append:pn-qemu-native = " sdl"
PACKAGECONFIG:append:pn-nativesdk-qemu = " sdl"
CONF_VERSION = "1"
```

### Example bblayers.conf
-------------------------
```
# LAYER_CONF_VERSION is increased each time build/conf/bblayers.conf
# changes incompatibly
POKY_BBLAYERS_CONF_VERSION = "2"

BBPATH = "${TOPDIR}"
BBFILES ?= ""

BBLAYERS ?= " \
	/path/to/OE/poky/meta \
	/path/to/OE/meta-mainline-common \
	/path/to/OE/meta-dhsom-stm32-bsp \
	"
```
