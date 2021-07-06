OpenEmbedded BSP layer for DH electronics STM32MP1 platforms with the RSI-provided RED driver
============================================================

This layer provides BSP for DH electronics STM32MP1 platforms including the RSI-provided RED driver.

Dependencies
------------

This layer depends on:

* URI: git://git.yoctoproject.org/poky
  - branch: dunfell
  - layers: meta

* URI: https://source.denx.de/denx/meta-mainline-common.git
  - branch: dunfell-3.1

Building image
--------------

A good starting point for setting up the build environment is is the official
Yocto Project wiki.

* https://www.yoctoproject.org/docs/3.1/brief-yoctoprojectqs/brief-yoctoprojectqs.html

Before attempting the build, the following metalayer git repositories shall
be cloned into a location accessible to the build system and a branch listed
below shall be checked out. The examples below will use /path/to/OE/ as a
location of the metalayers.

* https://source.denx.de/denx/meta-mainline-common.git			(branch: dunfell-3.1)
* https://github.com/dh-electronics/meta-dhsom-stm32-bsp.git		(branch: dunfell-3.1)
* https://github.com/dh-electronics/meta-dhsom-stm32-extras.git		(branch: dunfell-3.1)
* git://git.yoctoproject.org/poky					(branch: dunfell)
* git://github.com/openembedded/meta-openembedded.git			(branch: dunfell)
* git://git.openembedded.org/meta-python2				(branch: dunfell)

With all the source artifacts in place, proceed with setting up the build
using oe-init-build-env as specified in the Yocto Project wiki.

In addition to the content in the wiki, the aforementioned metalayers shall
be referenced in bblayers.conf in this order:

```
BBLAYERS ?= " \
         /home/oe/oe-core/meta \
         /home/oe/meta-python2 \
         /home/oe/meta-openembedded/meta-oe \
         /home/oe/meta-openembedded/meta-networking \
         /home/oe/meta-openembedded/meta-perl \
         /home/oe/meta-openembedded/meta-python \
         /home/oe/meta-mainline-common \
         /home/oe/meta-dhsom-stm32-bsp \
         /home/oe/meta-dhsom-stm32-extras \
  "
```

The following specifics should be placed into local.conf  to build the RSI driver:

```
MACHINE = "dh-stm32mp1-dhcom-pdk2"
DISTRO = "nodistro"
PREFERRED_VERSION_linux-stable = "5.4%"
CORE_IMAGE_EXTRA_INSTALL += " kernel-dev kernel-devsrc kernel-modules packagegroup-core-tools-profile packagegroup-core-buildessential vim "
EXTRA_IMAGE_FEATURES += "dev-pkgs tools-sdk tools-debug debug-tweaks"
```

Note that MACHINE must be either of:

* dh-stm32mp1-dhcom-pdk2

Adapt the suffixes of all the files and names of directories further in
this documentation according to MACHINE.

Both local.conf and bblayers.conf are included verbatim in full at the end
of this readme.

Once the configuration is complete, a basic demo system image suitable for
evaluation can be built using:

```
$ bitbake dh-image-demo 
```

Once the build completes, the images are available in:

```
tmp/deploy/images/dh-stm32mp1-dhcom-pdk2/
```

The SD card image is specifically in:

```
dh-image-demo-dh-stm32mp1-dhcom-pdk2.wic
```

And shall be written to the SD card using dd:

```
$ dd if=dh-image-demo-dh-stm32mp1-dhcom-pdk2.wic of=/dev/sdX bs=8M
```

