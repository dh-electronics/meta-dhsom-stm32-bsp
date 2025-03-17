STM32MP15xx DHCOM SoM on DH PicoITX carrier board
=================================================

# Initial board setup

This section explains how to perform initial hardware setup, how to
assemble the hardware, which cables to connect to which ports, how to
power the hardware on, and finally how to obtain serial console access.

## Insert SoM

Insert SoM provided with the carrier board into socket `X1`.
It is likely the SoM is already populated.

## Connect serial console cable

Connect serial console RS232 cable into 2x5 pin header `X5`.
This is RS232 up to 12V voltage level connection.

## Connect ethernet cables (optional)

Connect ethernet cable to ethernet jack `U11`.

## Connect power supply

Connect provided 24V/1A power supply into barrel jack `X7` or
compatible supply into plug `X6`.

## Start terminal emulator

Start terminal emulator to access serial console. Suitable tools include
any of `minicom`, `picocom`, `screen`, and many others. The serial console
settings are 115200 Baud 8N1 , HW and SW flow control disabled .

At this point, power on the system to validate serial console access. The
bootloader console should be printed on the serial console in a few seconds
after power on.

# System software boot

This system uses the U-Boot distro bootcommand to detect and control bootable
devices and boot from those devices.

The order in which boot devices are tested is stored in U-Boot `boot_targets`
variable. This variable can be overridden to configure different boot order.
The default setting is as follows:

```
STM32MP> env print boot_targets
boot_targets=mmc1 mmc0 mmc2 usb0 pxe
```

To boot only from microSD card, override `boot_targets` variable as follows:
```
STM32MP> env set boot_targets mmc0
STM32MP> boot
```

To boot only from eMMC card, override `boot_targets` variable as follows:
```
STM32MP> env set boot_targets mmc1
STM32MP> boot
```

To boot only from USB stick, override `boot_targets` variable as follows:
```
STM32MP> env set boot_targets usb0
STM32MP> boot
```

The change to `boot_targets` is discarded after the system booted. To make
the boot order change persistent in U-Boot environment, perform the change
and save U-Boot environment as follows:
```
STM32MP> env set boot_targets mmc0
STM32MP> env save
STM32MP> env save
STM32MP> boot
```

# System software build

The OE build of this layer can be set up either manually or automatically using
the [KAS](https://github.com/siemens/kas) tool. Both options yield equal result.
For details, please refer to [kas-dhsom](https://github.com/dh-electronics/kas-dhsom)
and top level README in this OE layer.

# System software installation

## Operating system image installation

The operating system image can be installed onto either microSD card or into
eMMC USER hardware partition.

The operating system image installation into either media can be performed
from within U-Boot itself using USB OTG UMS upload, or using microSD or SD
card reader on a host PC, or from running Linux userspace.

### Operating system image installation (using SD card reader)

This section assumes that the SD card reader is recognized as a block
device `/dev/sdx` on the host PC. This may differ on other host PCs, make
sure the correct block device is used for image installation below. Using
incorrect block device may lead to data loss on the host PC.

Install system image into the block device. It is recommended to use
[bmaptool](https://github.com/yoctoproject/bmaptool) to expedite the
installation by skipping unused blocks. It is recommended to use up
to date bmaptool version at least 3.6.0 which supports decompression
of ZSTD compressed images, otherwise it is necessary to decompress the
ZSTD compressed images first, as follows:
```
host$ zstd -d dh-image-demo-dh-stm32mp1-dhcom-picoitx.rootfs.wic.zst
```

To prevent any MBR or GPT contamination, it is recommended to erase
previous MBR and GPT partition tables:
```
host$ sgdisk -Z /dev/sdx

host$ fdisk /dev/sdx
Command (m for help): x                     # x to enter expert menu
Expert command (m for help): i              # i to change identifier
Enter the new disk identifier: 0x1234abcd   # pick random identifier
Expert command (m for help): r              # r to return to main menu
Command (m for help): w                     # w to write change to MBR
```

Use bmaptool to write system image into block device as follows:
```
host$ bmaptool copy --bmap \
    dh-image-demo-dh-stm32mp1-dhcom-picoitx.rootfs.wic.bmap \
    dh-image-demo-dh-stm32mp1-dhcom-picoitx.rootfs.wic.zst \
    /dev/sdx
bmaptool: info: block map format version 2.0
bmaptool: info: 400474 blocks of size 4096 (1.5 GiB), mapped 246005 blocks (961.0 MiB or 61.4%)
bmaptool: info: copying image 'dh-image-demo-dh-stm32mp1-dhcom-picoitx.rootfs.wic' to block device '/dev/sdx' using bmap file 'dh-image-demo-dh-stm32mp1-dhcom-picoitx.rootfs.wic.bmap'
bmaptool: info: 100% copied
bmaptool: info: synchronizing '/dev/sdx'
bmaptool: info: copying time: 13.2s, copying speed 72.5 MiB/sec
```

It is equally possible to use plain `dd` to write the decompressed
system image into the block device as follows:
```
host$ dd if=dh-image-demo-dh-stm32mp1-dhcom-picoitx.rootfs.wic \
         of=/dev/sdx bs=4M
```

This system uses GPT GUID Partition Table. It is highly recommended to
randomize GUIDs after writing system image to storage to avoid identical
PARTUUID for multiple partitions on different storage media in the system.
Identical PARTUUID for multiple partitions may lead to system using an
unexpected root device, which accidentally shares the same PARTUUID. To
randomize PARTUUID, use the following command:
```
host$ sgdisk -G /dev/sdx
```

Once the installation completed, insert the SD card into matching
slot on the device.

#### Operating system image installation into eMMC (from Linux)

Boot into the Linux operation system image and reach userspace. It is
mandatory that the system is booted from either microSD or SD card and
specifically not booted from eMMC.

To determine which block device is the eMMC, list block devices with
eMMC BOOT hardware partitions as follows:
```
$ ls -1 /dev/mmcblk*boot*
/dev/mmcblk0boot0
/dev/mmcblk0boot1
```
The listing above which implies the eMMC block device is `/dev/mmcblk0`.
This section assumes that the eMMC block device is recognized as a block
device `/dev/mmcblk0`.

Install system image into the block device. It is recommended to use
[bmaptool](https://github.com/yoctoproject/bmaptool) to expedite the
installation by skipping unused blocks. It is recommended to use up
to date bmaptool version at least 3.6.0 which supports decompression
of ZSTD compressed images, otherwise it is necessary to decompress the
ZSTD compressed images first, as follows:
```
host$ zstd -d dh-image-demo-dh-stm32mp1-dhcom-picoitx.rootfs.wic.zst
```

To prevent any MBR or GPT contamination, it is recommended to erase
previous MBR and GPT partition tables:
```
host$ sgdisk -Z /dev/mmcblk0

host$ fdisk /dev/mmcblk0
Command (m for help): x                     # x to enter expert menu
Expert command (m for help): i              # i to change identifier
Enter the new disk identifier: 0x1234abcd   # pick random identifier
Expert command (m for help): r              # r to return to main menu
Command (m for help): w                     # w to write change to MBR
```

Use bmaptool to write system image into block device as follows:
```
host$ bmaptool copy --bmap \
    dh-image-demo-dh-stm32mp1-dhcom-picoitx.rootfs.wic.bmap \
    dh-image-demo-dh-stm32mp1-dhcom-picoitx.rootfs.wic.zst \
    /dev/mmcblk0
bmaptool: info: block map format version 2.0
bmaptool: info: 400474 blocks of size 4096 (1.5 GiB), mapped 246005 blocks (961.0 MiB or 61.4%)
bmaptool: info: copying image 'dh-image-demo-dh-stm32mp1-dhcom-picoitx.rootfs.wic' to block device '/dev/mmcblk0' using bmap file 'dh-image-demo-dh-stm32mp1-dhcom-picoitx.rootfs.wic.bmap'
bmaptool: info: 100% copied
bmaptool: info: synchronizing '/dev/mmcblk0'
bmaptool: info: copying time: 13.2s, copying speed 72.5 MiB/sec
```

It is equally possible to use plain `dd` to write the decompressed
system image into the block device as follows:
```
host$ dd if=dh-image-demo-dh-stm32mp1-dhcom-picoitx.rootfs.wic \
         of=/dev/mmcblk0 bs=4M
```

This system uses GPT GUID Partition Table. It is highly recommended to
randomize GUIDs after writing system image to storage to avoid identical
PARTUUID for multiple partitions on different storage media in the system.
Identical PARTUUID for multiple partitions may lead to system using an
unexpected root device, which accidentally shares the same PARTUUID. To
randomize PARTUUID, use the following command:
```
host$ sgdisk -G /dev/mmcblk0
```

Once the installation completed, reboot the system and attempt to
boot from the eMMC.

## U-Boot bootloader installation

### U-Boot bootloader installation into SPI NOR

This section explains how to perform bootloader installation, update and
recovery. This section is only applicable in case it is desireable to
replace bootloader on the system.

The U-Boot bootloader is installed into SPI NOR. Installation into SPI NOR
is recommended to prevent accidental overwrite of the bootloader while the
system software in SD/eMMC is being replaced or updated. U-Boot bootloader
installation into SPI NOR can be performed from within U-Boot itself,
either from SD/eMMC card, or from running Linux userspace.

The SPI NOR layout on this platform is as follows:
```
0x00_0000..0x03_ffff ... U-Boot SPL (copy 1)
0x04_0000..0x07_ffff ... U-Boot SPL (copy 2)
0x08_0000..0x15_ffff ... U-Boot fitImage
0x06_0000..0x1d_ffff ... UNUSED
0x1e_0000..0x1e_ffff ... U-Boot environment (copy 1)
0x1f_0000..0x1f_ffff ... U-Boot environment (copy 2)
```

### U-Boot bootloader installation into SPI NOR (from U-Boot from SD/eMMC card)

Power on the system. Enter U-Boot console by pressing any key at prompt:
```
Hit any key to stop autoboot:
```

The system drops into U-Boot shell instantly. U-Boot shell on this system
looks as follows:
```
STM32MP>
```

Use either of the following U-Boot scripts from update U-Boot in SPI NOR
from bootloader binaries located on either microSD card or eMMC card.

Read bootloader update from eMMC and write to SPI NOR:
```
STM32MP> run dh_update_emmc_to_sf
193885 bytes read in 13 ms (14.2 MiB/s)
942253 bytes read in 25 ms (35.9 MiB/s)
SF: Detected w25q16cl with page size 256 Bytes, erase size 4 KiB, total 2 MiB
SF: 2097152 bytes @ 0x0 Erased: OK
device 0 offset 0x0, size 0x2f55d
193885 bytes written, 0 bytes skipped in 1.977s, speed 100322 B/s
device 0 offset 0x40000, size 0x2f55d
193885 bytes written, 0 bytes skipped in 2.29s, speed 97753 B/s
device 0 offset 0x80000, size 0xe60ad
934061 bytes written, 8192 bytes skipped in 9.725s, speed 99194 B/s
```

Read bootloader update from microSD and write to SPI NOR:
```
STM32MP> run dh_update_sd_to_sf
193885 bytes read in 9 ms (20.5 MiB/s)
942253 bytes read in 41 ms (21.9 MiB/s)
SF: Detected w25q16cl with page size 256 Bytes, erase size 4 KiB, total 2 MiB
SF: 2097152 bytes @ 0x0 Erased: OK
device 0 offset 0x0, size 0x2f55d
193885 bytes written, 0 bytes skipped in 1.993s, speed 99517 B/s
device 0 offset 0x40000, size 0x2f55d
193885 bytes written, 0 bytes skipped in 2.61s, speed 96237 B/s
device 0 offset 0x80000, size 0xe60ad
934061 bytes written, 8192 bytes skipped in 9.809s, speed 98345 B/s
```

Note that the bootloader update removes old U-Boot environment. In case the
old environment contains any useful content, it is recommended to back up
that content at this point.

Once the installation completed, reset the board. To reset the board, either
press the `RESET` button on the board, or perform reset from U-Boot shell as
follows:

```
STM32MP> reset
```

### U-Boot bootloader installation into SPI NOR (from Linux)

It is possible to update the bootloader from a running Linux userspace.
The SPI NOR is exposed as single continuous MTD device by the Linux
kernel, therefore it is necessary to first assemble a suitable SPI NOR
image, and second write such an image to the MTD device.

To assemble suitable SPI NOR image with the U-Boot SPL placed at offsets
0 kiB and 256 kiB from the start of SPI NOR, and U-Boot fitImage placed
at offset 512 kiB from the start of SPI NOR, use the following commands.
The U-Boot SPL is located in `u-boot-spl.stm32` file, the U-Boot fitImage
is located in `u-boot.itb` file.

```
root@dh-stm32mp1-dhcom-picoitx:~# tr '\0' "$(printf '\377')" < /dev/zero | dd bs=1024 count=1920 of=flash.bin
root@dh-stm32mp1-dhcom-picoitx:~# dd if=u-boot-spl.stm32 of=flash.bin conv=notrunc
root@dh-stm32mp1-dhcom-picoitx:~# dd if=u-boot-spl.stm32 of=flash.bin conv=notrunc bs=262144 seek=1
root@dh-stm32mp1-dhcom-picoitx:~# dd if=u-boot.itb       of=flash.bin conv=notrunc bs=524288 seek=1
```

To determine the MTD block device which represents the boot SPI NOR, iterate
over all of `/dev/mtdblock*` device nodes and select the one where the output
of `udevadm info /dev/mtdblockN` command matches the following output:

```
root@dh-stm32mp1-dhcom-picoitx:~# udevadm info /dev/mtdblock0
P: /devices/platform/soc/5c007000.bus/58003000.spi/spi_master/spi0/spi0.0/mtd/mtd0/mtdblock0
```

In case neither MTD block device hardware path matches the output above
for any of the `/dev/mtdblock*` device nodes, do not continue. Writing
data into another MTD block device may lead to data corruption or boot
failure.

In case a matching MTD block device node is found, write data into the
block device. Note that it is mandatory to use `/dev/mtdblockN` and not
use `/dev/mtdN` device, as the former is an emulated block device, while
the later is a control device which is only meant to be operated by the
mtd-utils. Use the following command to write U-Boot to SPI NOR:

```
root@dh-stm32mp1-dhcom-picoitx:~# dd if=flash.bin of=/dev/mtdblock0
3840+0 records in
3840+0 records out
1966080 bytes (2.0 MB, 1.9 MiB) copied, 17.7132 s, 111 kB/s
```

Once the installation completed, reset the board. To perform reset from
Linux kernel, use the following command:

```
root@dh-stm32mp1-dhcom-picoitx:~# reboot
```

### U-Boot bootloader recovery (using USB OTG DFU upload)

While it may be possible to perform USB OTG based bootloader recovery on
this board directly, the procedure is highly non-trivial. It is recommended
to remove the SoM and insert it into another board, for example the PDK2,
and perform bootloader recovery there. Please contact the vendor.

# Expansion module handling using Device Tree Overlays (DTO)

The capabilities of this system can be extended by attaching pluggable
expansion modules to various pin headers and connectors. For the Linux
kernel to recognize any such modules, those modules have to be described
in the Device Tree passed to the Linux kernel.

The system software implementation on this system uses Device Tree Overlays
(DTO) to describe expansion modules separately from the base Device Tree.
The base DT as well as DTOs are included in the Linux kernel fitImage and
applied by on top of the base DT by the U-Boot bootloader before booting
the Linux kernel. The combined DT with DTOs applied is then passed to the
Linux kernel. Which DTOs are applied is configurable by the user.

## Supported Device Tree Overlays

The following Device Tree Overlays are currently supported:

 - DH 548-200 adapter card with Multi-Inno MI0700D4T-6 7" DPI display attached to it.
   `stm32mp15xx-dhcom-picoitx-overlay-548-200-x2-mi0700s4t-6.dtbo`
 - DH 553-100 adapter card with Team Source Display TST043015CMHX 4.3" DPI display attached to it.
   `stm32mp15xx-dhcom-picoitx-overlay-553-100-x2-tst043015cmhx.dtbo`
 - DH 626-100 adapter card with Chefree CH101OLHLWH-002 LVDS display attached to it.
   `fdt-stm32mp15xx-dhcom-picoitx-overlay-626-100-x2-ch101olhlwh.dtbo`

## List available Device Tree Overlays (from U-Boot)

To list Device Tree Overlays supported by the current operating system image,
inspect the Linux kernel fitImage from within U-Boot as follows.

First, load the kernel fitImage from SD or eMMC into memory:
```
STM32MP> load mmc 0:4 ${loadaddr} boot/fitImage
5975836 bytes read in 250 ms (22.8 MiB/s)
```

Second, list the available fitImage configurations, which also
match the available base Device Tree and Device Tree Overlays:
```
STM32MP> iminfo $loadaddr

## Checking Image at c2000000 ...
   FIT image found
   FIT description: Kernel fitImage for DH Linux Distribution/6.12.53+git/dh-stm32mp1-dhcom-picoitx
   Created:         2025-10-15  10:00:25 UTC
    Image 0 (kernel-1)
     Description:  Linux kernel
     Created:      2025-10-15  10:00:25 UTC
     Type:         Kernel Image
     Compression:  uncompressed
     Data Start:   0xc2000100
     Data Size:    5862776 Bytes = 5.6 MiB
     Architecture: ARM
     OS:           Linux
     Load Address: 0xc4000000
     Entry Point:  0xc4000000
     Hash algo:    sha256
     Hash value:   1987cb79585ca4b599ad00d49721943f28f7b19aef26fba6b468185072dae1c5
    Image 1 (fdt-stm32mp157c-dhcom-picoitx.dtb)
     Description:  Flattened Device Tree blob
     Created:      2025-10-15  10:00:25 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc2597794
     Data Size:    99493 Bytes = 97.2 KiB
     Architecture: ARM
     Load Address: 0xcff00000
     Hash algo:    sha256
     Hash value:   8f552d2aeb1c548aebb4bb3224e0e4dfb98bc8e8902651ab987f489902e7b027
    Image 2 (fdt-stm32mp15xx-dhcom-picoitx-overlay-548-200-x2-mi0700s4t-6.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-15  10:00:25 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25afd54
     Data Size:    3019 Bytes = 2.9 KiB
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   bf914396ea222b3beeec1838124bbc3ec6e89dd47f72d2202ca779e1bfc92947
    Image 3 (fdt-stm32mp15xx-dhcom-picoitx-overlay-553-100-x2-tst043015cmhx.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-15  10:00:25 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b0a38
     Data Size:    3031 Bytes = 3 KiB
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   f06decc76f7981d90b1a0be383120537a209516467fe3aea5c86f963ce5ab615
    Image 4 (fdt-stm32mp15xx-dhcom-picoitx-overlay-626-100-x2-ch101olhlwh.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-15  10:00:25 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b1728
     Data Size:    4168 Bytes = 4.1 KiB
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   2e05d9cf07b27acf9865a0a22098230eb791cea953729a6e59b5db85f635ea1f
    Default Configuration: 'conf-stm32mp157c-dhcom-picoitx.dtb'
    Configuration 0 (conf-stm32mp157c-dhcom-picoitx.dtb)
     Description:  1 Linux kernel, FDT blob
     Kernel:       kernel-1
     FDT:          fdt-stm32mp157c-dhcom-picoitx.dtb
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 1 (conf-stm32mp15xx-dhcom-picoitx-overlay-548-200-x2-mi0700s4t-6.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-dhcom-picoitx-overlay-548-200-x2-mi0700s4t-6.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 2 (conf-stm32mp15xx-dhcom-picoitx-overlay-553-100-x2-tst043015cmhx.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-dhcom-picoitx-overlay-553-100-x2-tst043015cmhx.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 3 (conf-stm32mp15xx-dhcom-picoitx-overlay-626-100-x2-ch101olhlwh.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-dhcom-picoitx-overlay-626-100-x2-ch101olhlwh.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
## Checking hash(es) for FIT Image at c2000000 ...
   Hash(es) for Image 0 (kernel-1): sha256+
   Hash(es) for Image 1 (fdt-stm32mp157c-dhcom-picoitx.dtb): sha256+
   Hash(es) for Image 2 (fdt-stm32mp15xx-dhcom-picoitx-overlay-548-200-x2-mi0700s4t-6.dtbo): sha256+
   Hash(es) for Image 3 (fdt-stm32mp15xx-dhcom-picoitx-overlay-553-100-x2-tst043015cmhx.dtbo): sha256+
   Hash(es) for Image 4 (fdt-stm32mp15xx-dhcom-picoitx-overlay-626-100-x2-ch101olhlwh.dtbo): sha256+
```

The Device Tree and Device Tree Overlays lists below are based on fitImage
configuration listing printed in the example above, with the `conf-` prefix
removed.

The listing contains one base Device Tree:

- `stm32mp157c-dhcom-picoitx.dtb`

The listing contains the following Device Tree Overlays:

- `stm32mp15xx-dhcom-picoitx-overlay-548-200-x2-mi0700s4t-6.dtbo`
- `stm32mp15xx-dhcom-picoitx-overlay-553-100-x2-tst043015cmhx.dtbo`
- `stm32mp15xx-dhcom-picoitx-overlay-626-100-x2-ch101olhlwh.dtbo`

## Select Device Tree Overlays applied on top of Linux DT (from U-Boot)

The application of additional Device Tree Overlays available in the Linux
kernel fitImage on top of a base Device Tree is governed by the `loaddtos`
U-Boot environment variable.

The `loaddtos` U-Boot environment variable refers to configurations
available in the Linux kernel fitImage. Those configurations refer
to Device Tree or Device Tree Overlays embedded in the fitImage. The
`loaddtos` environment variable must list one base Device Tree and
zero or more additional Device Tree Overlays which are to be applied
on top of the base Device Tree. Entries listed in the `loaddtos`
environment variable must each be prefixed by the `#` character.

To configure the system to use `stm32mp157c-dhcom-picoitx.dtb` base Device
Tree and apply additional `stm32mp15xx-dhcom-picoitx-overlay-626-100-x2-ch101olhlwh.dtbo`
Device Tree Overlay, configure the `loaddtos` environment variable as
follows:
```
                     .----------------------------------.----- '#' Separator
                     |                                  |
                     v                                  v
=> env set loaddtos '#conf-stm32mp157c-dhcom-picoitx.dtb#conf-stm32mp15xx-dhcom-picoitx-overlay-626-100-x2-ch101olhlwh.dtbo'
                       ^                                  ^
                       |                                  |
                       |                                  '--- 626-100 display DTO
                       |
                       '-------------------------------------- Base Device Tree
```

To make configuration persistent, store U-Boot environment:
```
=> env save
```

To boot the system with additional DTO applied:
```
STM32MP> env set loaddtos '#conf-stm32mp157c-dhcom-picoitx.dtb#conf-stm32mp15xx-dhcom-picoitx-overlay-626-100-x2-ch101olhlwh.dtbo'
STM32MP> boot
Boot over nor0!
switch to partitions #0, OK
mmc1(part 0) is current device
Scanning mmc 1:4...
Found U-Boot script /boot/boot.scr
4988 bytes read in 2 ms (2.4 MiB/s)
## Executing script at c4100000
5987204 bytes read in 132 ms (43.3 MiB/s)
Booting the Linux kernel...

## Loading kernel (any) from FIT Image at c2000000 ...
   Using 'conf-stm32mp157c-dhcom-picoitx.dtb' configuration
   Trying 'kernel-1' kernel subimage
     Description:  Linux kernel
     Created:      2025-10-06   9:17:53 UTC
     Type:         Kernel Image
     Compression:  uncompressed
     Data Start:   0xc2000100
     Data Size:    5863416 Bytes = 5.6 MiB
     Architecture: ARM
     OS:           Linux
     Load Address: 0xc4000000
     Entry Point:  0xc4000000
     Hash algo:    sha256
     Hash value:   7d1e8998591fffed819488805f78d5693b5bff04c2fe9e25261c0a4a1e719a31
   Verifying Hash Integrity ... sha256+ OK
## Loading fdt (any) from FIT Image at c2000000 ...
   Using 'conf-stm32mp157c-dhcom-picoitx.dtb' configuration
   Trying 'fdt-stm32mp157c-dhcom-picoitx.dtb' fdt subimage
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc2597a10
     Data Size:    102199 Bytes = 99.8 KiB
     Architecture: ARM
     Load Address: 0xcff00000
     Hash algo:    sha256
     Hash value:   0d16f852daf16c041c0a0cde5a5351e5f7f4547869829c4fc55e4a1b05296306
   Verifying Hash Integrity ... sha256+ OK
   Loading fdt from 0xc2597a10 to 0xcff00000
   Loading Device Tree to cffe4000, end cfffffff ... OK
Working FDT set to cffe4000
## Loading fdt (any) from FIT Image at c2000000 ...
   Using 'conf-stm32mp15xx-dhcom-picoitx-overlay-626-100-x2-ch101olhlwh.dtbo' configuration
   Trying 'fdt-stm32mp15xx-dhcom-picoitx-overlay-626-100-x2-ch101olhlwh.dtbo' fdt subimage
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b3370
     Data Size:    3519 Bytes = 3.4 KiB
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   59c70bd3a9cac17e892e9a8fdd2632ec2c4cac17b9d4a3b11c4d02e2fbea8bc8
   Verifying Hash Integrity ... sha256+ OK
   Booting using the fdt blob at 0xcffe4000
Working FDT set to cffe4000
   Loading Kernel Image to c4000000
   Loading Device Tree to cffc7000, end cffe3478 ... OK
Working FDT set to cffc7000

Starting kernel ...
```

# IO access

This section describe access to various IO of this system.

## Serial ports and UARTs

U-Boot on this system provides serial console access via 2x5 pin
header `X5`, this is `serial@40010000`. Other serial ports can be
made available by modifying U-Boot control Device Tree.

Linux on this system provides serial console access via 2x5 pin
header `X5`, this is `40010000.serial`. Linux also provides one
additional serial port accessible via display connector `X2`,
`4000f000.serial` and one RS485 port on external connector `X8`
`40019000.serial` .

These ports are accessible via `/dev/ttySTM*` device nodes. To
discern the ports apart, use `udevadm info /dev/ttySTM*` command,
which prints the full hardware path to the selected port:
```
root@dh-stm32mp1-dhcom-picoitx:~# udevadm info /dev/ttySTM*
P: /devices/platform/soc/5c007000.bus/40010000.serial/40010000.serial:0/40010000.serial:0.0/tty/ttySTM0
                                      ^^^^^^^^^^^^^^^
M: ttySTM0
   ^^^^^^^
```

To access either serial port, use suitable terminate emulator,
for example `minicom`, `picocom`, `screen`, and many others.

## IO

### LEDs

U-Boot on this system provides access to one LED via `led` command.

To list available LEDs and their current state, use the `led list` command:
```
STM32MP> led list
yellow:led      off
```

To enable an LED, use `led <led_label> on` command:
```
STM32MP> led yellow:led on
```

To disable an LED, use `led <led_label> off` command:
```
STM32MP> led yellow:led off
```

Linux on this system provides access to one LED via sysfs LED API.

To enable an LED, write 1 into matching sysfs LED attribute:
```
root@dh-stm32mp1-dhcom-picoitx:~# echo 1 > /sys/class/leds/yellow\:led/brightness
```

To disable an LED, write 0 into matching sysfs LED attribute:
```
root@dh-stm32mp1-dhcom-picoitx:~# echo 0 > /sys/class/leds/yellow\:led/brightness
```

### GPIOs

U-Boot on this system provides access to multiple GPIO pins.

To list GPIOs and their current state, use the `gpio status` command:
```
STM32MP> gpio status
Bank GPIOA:
GPIOA13: output: 0 [x] usb-port-power.gpio-hog

Bank GPIOG:
GPIOG1: input: 0 [x] mmc@58005000.cd-gpios
GPIOG3: output: 0 [x] vioregulator.gpio

Bank GPIOH:
GPIOH3: output: 1 [x] ethernet@5800a000.phy-reset-gpios

Bank GPIOI:
GPIOI3: output: 0 [x] led-0.gpios
```

To set a GPIO as Output-High, use `gpio set <pin>` command:
```
STM32MP> gpio set GPIOI3
gpio: pin GPIOI3 (gpio 131) value is 1
```

To set a GPIO as Output-Low, use `gpio clear <pin>` command:
```
STM32MP> gpio clear GPIOI3
gpio: pin GPIOI3 (gpio 131) value is 0
```

To toggle a GPIO, use `gpio toggle <pin>` command:
```
STM32MP> gpio toggle GPIOI3
gpio: pin GPIOI3 (gpio 131) value is 1
STM32MP> gpio toggle GPIOI3
gpio: pin GPIOI3 (gpio 131) value is 0
```

Linux on this system provides access to multiple GPIO pins. Access to
GPIOs is provided using GPIO chardev interface and libgpiod. Subset of
GPIO lines is named, which makes them easier to discern in libgpiod
tools output.

To list available GPIOs, use the `gpioinfo` command:
```
root@dh-stm32mp1-dhcom-picoitx:~# gpioinfo
gpiochip1 - 16 lines:
        line   0:       "PA0"                   input consumer="kernel"
        line   1:       "PA1"                   input consumer="kernel"
        line   2:       "PA2"                   input consumer="kernel"
...
gpiochip1 - 16 lines:
...
        line   8:       "DHCOM-Q"               input
...
```

To get current state of a GPIO, use the `gpioget` command:
```
root@dh-stm32mp1-dhcom-picoitx:~# gpioget -c /dev/gpiochip1 8
"8"=inactive
...
```

To set a GPIO as Output-High, use `gpioset ... <line>=1` command:
```
root@dh-stm32mp1-dhcom-picoitx:~# gpioset -c /dev/gpiochip1 8=1
```

To set a GPIO as Output-Low, use `gpioset ... <line>=0` command:
```
root@dh-stm32mp1-dhcom-picoitx:~# gpioset -c /dev/gpiochip1 8=0
```

It is possible to refer to GPIOs using symbolic names:
```
# Numerical reference to GPIO on gpiochip1 line 8:
root@dh-stm32mp1-dhcom-picoitx:~# gpioget -c /dev/gpiochip1 8
"8"=inactive

# Symbolic name reference to GPIO on gpiochip1 line 8:
root@dh-stm32mp1-dhcom-picoitx:~# gpioget DHCOM-Q
"DHCOM-Q"=inactive
```

Note that once the `gpioset` command exits, the line returns to undefined state.

## Storage

### MicroSD

U-Boot on this system provides access to microSD card via standard MMC and
block device interface. To detect and print information about the microSD
card, use `mmc dev` and `mmc info` commands. The microSD card is MMC device
number 0:
```
STM32MP> mmc dev 0
switch to partitions #0, OK
mmc0 is current device

STM32MP> mmc info
Device: STM32 SD/MMC
Manufacturer ID: 3
OEM: 5344
Name: SR64G
Bus Speed: 50000000
Mode: SD High Speed (50MHz)
Rd Block Len: 512
SD version 3.0
High Capacity: Yes
Capacity: 59.5 GiB
Bus Width: 4-bit
Erase Group Size: 512 Bytes
```

Access to filesystem is available using U-Boot generic filesystem interface commands:
```
STM32MP> mmc part

Partition Map for mmc device 0  --   Partition Type: EFI

Part    Start LBA       End LBA         Name
        Attributes
        Type GUID
        Partition GUID
  1     0x00000022      0x00000221      "fsbl1"
        attrs:  0x0000000000000000
        type:   ebd0a0a2-b9e5-4433-87c0-68b6b72699c7
                (data)
        guid:   bab351e0-501e-4205-a7ee-29214b4d1ea4
...

STM32MP> ls mmc 0:4
            ./
            ../
            lost+found/
    <SYM>   bin
            boot/
...
```

Linux on this system provides access to microSD card via standard block device
interface. It is recommended to use deterministic device name symlink generated
by `udev` to access block devices. The microSD card block device is accessible
via the following deterministic device name symlink:
```
/dev/disk/by-path/platform-soc-amba-58005000.mmc
```

In case the deterministic device name symlink is not available, it is possible
to resolve microSD card block device to `/dev/mmcblk*` device node manually by
reading out the character device major and minor numbers from sysfs, and then
by performing look up in the `/dev/` directory, using the following commands.
In the example below, major and minor numbers for microSD card are 179 and 0,
which are represented by device node `/dev/mmcblk1`:
```
root@dh-stm32mp1-dhcom-picoitx:~# cat /sys/devices/platform/soc/58005000.mmc/mmc_host/mmc*/mmc*/block/mmcblk*/dev
179:0
^^^ ^

root@dh-stm32mp1-dhcom-picoitx:~# ls -la /dev/mmcblk*
...
brw-rw---- 1 root disk 179,  0 Mar 10 03:53 /dev/mmcblk1
                       ^^^   ^              ^^^^^^^^^^^^
...
```

### eMMC

U-Boot on this system provides access to eMMC card via standard MMC and
block device interface. To detect and print information about the eMMC
card, use `mmc dev` and `mmc info` commands. The eMMC card is MMC device
number 1:
```
STM32MP> mmc dev 1
switch to partitions #0, OK
mmc1(part 0) is current device

STM32MP> mmc info
Device: STM32 SD/MMC
Manufacturer ID: 45
OEM: 0
Name: DG4008
Bus Speed: 52000000
Mode: MMC High Speed (52MHz)
Rd Block Len: 512
MMC version 5.1
High Capacity: Yes
Capacity: 7.3 GiB
Bus Width: 8-bit
Erase Group Size: 512 KiB
HC WP Group Size: 8 MiB
User Capacity: 7.3 GiB WRREL
Boot Capacity: 4 MiB ENH
RPMB Capacity: 4 MiB ENH
Boot area 0 is not write protected
Boot area 1 is not write protected
```

Access to filesystem is available using U-Boot generic filesystem interface commands:
```
STM32MP> mmc part

Partition Map for mmc device 1  --   Partition Type: EFI

Part    Start LBA       End LBA         Name
        Attributes
        Type GUID
        Partition GUID
  1     0x00000022      0x00000221      "fsbl1"
        attrs:  0x0000000000000000
        type:   ebd0a0a2-b9e5-4433-87c0-68b6b72699c7
                (data)
        guid:   5ba0bf70-ad91-4c71-8135-d5a29b58f5be
...

STM32MP> ls mmc 1:4
            ./
            ../
            lost+found/
    <SYM>   bin
            boot/
...
```

Linux on this system provides access to eMMC card via standard block device
interface. It is recommended to use deterministic device name symlink generated
by `udev` to access block devices. The eMMC card block device is accessible
via the following deterministic device name symlink:
```
/dev/disk/by-path/platform-soc-amba-58007000.mmc
```

In case the deterministic device name symlink is not available, it is possible
to resolve eMMC card block device to `/dev/mmcblk*` device node manually by
reading out the character device major and minor numbers from sysfs, and then
by performing look up in the `/dev/` directory, using the following commands.
In the example below, major and minor numbers for eMMC card are 179 and 8,
which are represented by device node `/dev/mmcblk1`:
```
root@dh-stm32mp1-dhcom-picoitx:~# cat /sys/devices/platform/soc/58007000.mmc/mmc_host/mmc*/mmc*/block/mmcblk*/dev
179:8
^^^ ^

root@dh-stm32mp1-dhcom-picoitx:~# ls -la /dev/mmcblk*
...
brw-rw---- 1 root disk 179,  8 Mar 10 03:53 /dev/mmcblk1
                       ^^^   ^              ^^^^^^^^^^^^
...
```

## Network

### Ethernet

U-Boot on this system provides access to one ethernet interface via U-Boot
networking stack. Use `net list` command to list available ethernet interfaces:
```
STM32MP> net list
eth0 : ethernet@5800a000 00:11:22:33:44:55 active
```

To select active ethernet interface, set U-Boot environment variable `ethact`.

Linux on this system provides access to one ethernet interface via Linux
networking stack. These interfaces are assigned deterministic interface
names via systemd udevd rules.

```
root@dh-stm32mp1-dhcom-picoitx:~# ip addr show
...
4: ethsom0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 ...
...
```

To discern the ports apart, use `udevadm info /sys/class/net/ethsom*` command,
which prints the full hardware path to the selected ethernet interface. The
default assignment `5800a000.ethernet` is on-SoC ethernet, `64000000.ethernet`
is additional KSZ8851-16MLL on-SoM ethernet:

```
root@dh-stm32mp1-dhcom-picoitx:~# udevadm info /sys/class/net/ethsom0
P: /devices/platform/soc/5c007000.bus/5800a000.ethernet/net/ethsom0
M: ethsom0
...
```

### WiFi and Bluetooth

Linux on this system provides access to WiFi interface via Linux networking
stack. This interface is assigned deterministic interface name via systemd
udevd rules.

```
root@dh-stm32mp1-dhcom-picoitx:~# udevadm info /sys/class/net/wlansom0
P: /devices/platform/soc/5c007000.bus/48004000.mmc/mmc_host/mmc2/mmc2:fffd/mmc2:fffd:1/net/wlansom0
M: wlansom0
...
```

To operate the WiFi interface, use `iw` and `wpa-supplicant` tools, or any
other suitable network management tooling. Example scan for nearby networks:

```
root@dh-stm32mp1-dhcom-picoitx:~# ip link set wlansom0 up
root@dh-stm32mp1-dhcom-picoitx:~# iw dev wlansom0 scan
BSS 00:11:22:33:44:55(on wlansom0)
...
```

### CAN

Linux on this system provides access to CAN/CANFD interface via Linux networking
stack and socketcan interface.

To bring the CAN interface up in FD mode, 125 kbps bitrate, 250 kbps data rate,
use the following command:

```
$ ip link set can0 up type can bitrate 125000 dbitrate 250000 fd on
```

To bring the CAN interface up in classic mode, 125 kbps bitrate,
use the following command:

```
$ ip link set can0 up type can bitrate 125000 fd off
```

To bring the CAN interface down, use the following command:

```
$ ip link set can0 down
```

Use suitable socketcan tools to communicate via CAN, for example `candump`,
`cansend`, `cangen`.

## USB

### USB Host

This system provides one USB host port accessible via USB-A connector `X10`.

U-Boot on this system provides access to USB host and peripheral ports via
standard U-Boot USB stack. To enumerate USB devices attached to the host
port, use `usb` command:

```
STM32MP> usb reset
resetting USB...
USB OHCI 1.0
USB EHCI 1.00
Bus usb@5800c000: No USB Device found
Bus usb@5800d000: 2 USB Device(s) found
       scanning usb for storage devices... 1 Storage Device(s) found

STM32MP> usb info
1: Hub,  USB Revision 2.0
...
2: Mass Storage,  USB Revision 2.0
 - SanDisk Cruzer Edge
...

STM32MP> usb tree
USB device tree:
  1  Hub (480 Mb/s, 0mA)
  |  u-boot EHCI Host Controller
  |
  +-2  Mass Storage (480 Mb/s, 200mA)
       SanDisk Cruzer Edge
```

Access to block devices enumerated on the USB host port is available
using U-Boot generic filesystem interface commands:

```
STM32MP> ls usb 0:1
            ./
            ../
            lost+found/

0 file(s), 3 dir(s)
```

Linux on this system provides access to USB host and peripheral ports
via standard Linux kernel USB stack.

Devices attached to the USB host ports can be conveniently listed using
`usbutils` tool `lsusb`. In case `usbutils` are not part of the system
image, the information can be extracted by traversing sysfs directory
structure in `/sys/bus/usb/devices/` :
```
root@dh-stm32mp1-dhcom-picoitx:~# lsusb
Bus 001 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub
Bus 002 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub
Bus 003 Device 001: ID 1d6b:0001 Linux Foundation 1.1 root hub
```
