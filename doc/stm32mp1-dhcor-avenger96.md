STM32MP15xx DHCOR SoM on DH Avenger96 carrier board
===================================================

# Initial board setup

This section explains how to perform initial hardware setup, how to
assemble the hardware, which cables to connect to which ports, how to
power the hardware on, and finally how to obtain serial console access.

## Insert serial console adapter

Insert serial console adapter into Low Speed Expansion Connector, header `X6`.
Suitable adapter is the [96Boards UART Serial Mezzanine](https://www.96boards.org/documentation/mezzanine/uartserial/) .

## Connect serial console cable

Connect MicroUSB-to-USB-A cable into serial console adapter.

## Connect ethernet cable (optional)

Connect ethernet cable to ethernet jack `X11`.

## Connect USB OTG cables (optional)

Connect USB OTG cable between host PC and carrier board into plug `X5`,
located between HDMI port and two USB-A ports.

## Connect power supply

Connect 8V..18V/1A power supply into barrel jack `X1`.

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

The operating system image can be installed onto either microSD card or
into eMMC USER hardware partition.

The operating system image installation into either media can be performed
from within U-Boot itself using USB OTG UMS upload, or using microSD or SD
card reader on a host PC, or from running Linux userspace.

### Operating system image installation (from U-Boot using USB OTG UMS upload)

The USB OTG UMS USB Mass Storage mode exports the storage media on board
as a USB Mass Storage device to the host PC. The host PC can access the
storage as any other USB attached mass storage device and read and write
data from and to it.

Power on the system. Enter U-Boot console by pressing any key at prompt:
```
Hit any key to stop autoboot:
```

The system drops into U-Boot shell instantly. U-Boot shell on this system
looks as follows:
```
STM32MP>
```

Use one of the following commands to start UMS on USB OTG port and
export a storage device.
```
# microSD on Avenger96
STM32MP> ums 0 mmc 0
# eMMC on Avenger96
STM32MP> ums 0 mmc 1
```

The USB mass storage device will now enumerate on the host PC as follows:
```
host$ dmesg -w
usb 5-2.2.2.1: new high-speed USB device number 106 using xhci_hcd
usb 5-2.2.2.1: New USB device found, idVendor=0483, idProduct=5720, bcdDevice= 2.00
usb 5-2.2.2.1: New USB device strings: Mfr=1, Product=2, SerialNumber=3
usb 5-2.2.2.1: Product: USB download gadget
usb 5-2.2.2.1: Manufacturer: dh
usb 5-2.2.2.1: SerialNumber: 003800000000000012345678
usb-storage 5-2.2.2.1:1.0: USB Mass Storage device detected
scsi host3: usb-storage 5-2.2.2.1:1.0
scsi 3:0:0:0: Direct-Access     Linux    UMS disk 0       ffff PQ: 0 ANSI: 2
sd 3:0:0:0: Attached scsi generic sg4 type 0
sd 3:0:0:0: [sdx] 124735488 512-byte logical blocks: (63.9 GB/59.5 GiB)
sd 3:0:0:0: [sdx] Write Protect is off
sd 3:0:0:0: [sdx] Mode Sense: 0f 00 00 00
sd 3:0:0:0: [sdx] Write cache: enabled, read cache: enabled, doesn't support DPO or FUA
GPT:Primary header thinks Alt. header is not at the end of the disk.
GPT:3203785 != 124735487
GPT:Alternate GPT header not at the end of the disk.
GPT:3203785 != 124735487
GPT: Use GNU Parted to correct GPT errors.
 sdx: sdx1 sdx2 sdx3 sdx4
sd 3:0:0:0: [sdx] Attached SCSI removable disk
```

Notice that in the aforementioned case, the device is enumerated as block
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
host$ zstd -d dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic.zst
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
    dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic.bmap \
    dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic.zst \
    /dev/sdx
bmaptool: info: block map format version 2.0
bmaptool: info: 400474 blocks of size 4096 (1.5 GiB), mapped 246005 blocks (961.0 MiB or 61.4%)
bmaptool: info: copying image 'dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic' to block device '/dev/sdx' using bmap file 'dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic.bmap'
bmaptool: info: 100% copied
bmaptool: info: synchronizing '/dev/sdx'
bmaptool: info: copying time: 13.2s, copying speed 72.5 MiB/sec
```

It is equally possible to use plain `dd` to write the decompressed
system image into the block device as follows:
```
host$ dd if=dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic \
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

Once the installation completed, terminate UMS from U-Boot console
by pressing `Ctrl + C`.

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
host$ zstd -d dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic.zst
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
    dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic.bmap \
    dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic.zst \
    /dev/sdx
bmaptool: info: block map format version 2.0
bmaptool: info: 400474 blocks of size 4096 (1.5 GiB), mapped 246005 blocks (961.0 MiB or 61.4%)
bmaptool: info: copying image 'dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic' to block device '/dev/sdx' using bmap file 'dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic.bmap'
bmaptool: info: 100% copied
bmaptool: info: synchronizing '/dev/sdx'
bmaptool: info: copying time: 13.2s, copying speed 72.5 MiB/sec
```

It is equally possible to use plain `dd` to write the decompressed
system image into the block device as follows:
```
host$ dd if=dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic \
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
/dev/mmcblk2boot0
/dev/mmcblk2boot1
```
The listing above which implies the eMMC block device is `/dev/mmcblk2`.
This section assumes that the eMMC block device is recognized as a block
device `/dev/mmcblk2`.

Install system image into the block device. It is recommended to use
[bmaptool](https://github.com/yoctoproject/bmaptool) to expedite the
installation by skipping unused blocks. It is recommended to use up
to date bmaptool version at least 3.6.0 which supports decompression
of ZSTD compressed images, otherwise it is necessary to decompress the
ZSTD compressed images first, as follows:
```
host$ zstd -d dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic.zst
```

To prevent any MBR or GPT contamination, it is recommended to erase
previous MBR and GPT partition tables:
```
host$ sgdisk -Z /dev/mmcblk2

host$ fdisk /dev/mmcblk2
Command (m for help): x                     # x to enter expert menu
Expert command (m for help): i              # i to change identifier
Enter the new disk identifier: 0x1234abcd   # pick random identifier
Expert command (m for help): r              # r to return to main menu
Command (m for help): w                     # w to write change to MBR
```

Use bmaptool to write system image into block device as follows:
```
host$ bmaptool copy --bmap \
    dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic.bmap \
    dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic.zst \
    /dev/mmcblk2
bmaptool: info: block map format version 2.0
bmaptool: info: 400474 blocks of size 4096 (1.5 GiB), mapped 246005 blocks (961.0 MiB or 61.4%)
bmaptool: info: copying image 'dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic' to block device '/dev/mmcblk2' using bmap file 'dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic.bmap'
bmaptool: info: 100% copied
bmaptool: info: synchronizing '/dev/mmcblk2'
bmaptool: info: copying time: 13.2s, copying speed 72.5 MiB/sec
```

It is equally possible to use plain `dd` to write the decompressed
system image into the block device as follows:
```
host$ dd if=dh-image-demo-dh-stm32mp1-dhcor-avenger96.rootfs.wic \
         of=/dev/mmcblk2 bs=4M
```

This system uses GPT GUID Partition Table. It is highly recommended to
randomize GUIDs after writing system image to storage to avoid identical
PARTUUID for multiple partitions on different storage media in the system.
Identical PARTUUID for multiple partitions may lead to system using an
unexpected root device, which accidentally shares the same PARTUUID. To
randomize PARTUUID, use the following command:
```
host$ sgdisk -G /dev/mmcblk2
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
either using USB OTG DFU upload or from SD/eMMC card, or from running
Linux userspace.

The SPI NOR layout on this platform is as follows:
```
0x00_0000..0x03_ffff ... U-Boot SPL (copy 1)
0x04_0000..0x07_ffff ... U-Boot SPL (copy 2)
0x08_0000..0x15_ffff ... U-Boot fitImage
0x06_0000..0x1d_ffff ... UNUSED
0x1e_0000..0x1e_ffff ... U-Boot environment (copy 1)
0x1f_0000..0x1f_ffff ... U-Boot environment (copy 2)
```

### U-Boot bootloader installation into SPI NOR (from U-Boot using USB OTG DFU upload)

This section depends on [dfu-util](https://dfu-util.sourceforge.net/) tool.
This tool must be installed on the host system.

Power on the system. Enter U-Boot console by pressing any key at prompt:
```
Hit any key to stop autoboot:
```

The system drops into U-Boot shell instantly. U-Boot shell on this system
looks as follows:
```
STM32MP>
```

Use the following command to start DFU on USB OTG port:
```
STM32MP> env set dfu_alt_info "mtd nor0=fsbl1 raw 0x0 0x40000;fsbl2 raw 0x40000 0x40000;uboot raw 0x80000 0x160000;env1 raw 0x1e0000 0x10000;env2 raw 0x1f0000 0x10000"
STM32MP> sf probe
STM32MP> dfu 0 mtd
```

The system will now enumerate on the host PC as follows:
```
host$ dmesg -w
usb 5-2.2.2.1: new high-speed USB device number 21 using xhci_hcd
usb 5-2.2.2.1: New USB device found, idVendor=0483, idProduct=df11, bcdDevice= 2.00
usb 5-2.2.2.1: New USB device strings: Mfr=1, Product=2, SerialNumber=3
usb 5-2.2.2.1: Product: USB download gadget
usb 5-2.2.2.1: Manufacturer: dh
usb 5-2.2.2.1: SerialNumber: 003800000000000012345678
```

It is possible to list all DFU devices accessible to the host PC as follows:
```
host$ dfu-util -l
dfu-util 0.11

Copyright 2005-2009 Weston Schmidt, Harald Welte and OpenMoko Inc.
Copyright 2010-2021 Tormod Volden and Stefan Schmidt
This program is Free Software and has ABSOLUTELY NO WARRANTY
Please report bugs to http://sourceforge.net/p/dfu-util/tickets/

Found DFU: [0483:df11] ver=0200, devnum=21, cfg=1, intf=0, path="5-2.2.2.1", alt=4, name="env2", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=21, cfg=1, intf=0, path="5-2.2.2.1", alt=3, name="env1", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=21, cfg=1, intf=0, path="5-2.2.2.1", alt=2, name="uboot", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=21, cfg=1, intf=0, path="5-2.2.2.1", alt=1, name="fsbl2", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=21, cfg=1, intf=0, path="5-2.2.2.1", alt=0, name="fsbl1", serial="003800000000000012345678"
```

Install bootloader into SPI NOR using dfu-util as follows. The bootloader
consists of two components, U-Boot SPL file `u-boot-spl.stm32` and U-Boot
fitImage file `u-boot.itb`, and each must be written into correct location
in SPI NOR. The U-Boot SPL file `u-boot-spl.stm32` is written into two
locations, `U-Boot SPL (copy 1)` and `U-Boot SPL (copy 2)`. The U-Boot
fitImage `u-boot.itb` is written into one location `U-Boot fitImage` .
The bootloader installation consists of three `dfu-util` invocations in
total:
```
hostpc$ dfu-util -w -a 'fsbl1' -D u-boot-spl.stm32
dfu-util 0.11

Copyright 2005-2009 Weston Schmidt, Harald Welte and OpenMoko Inc.
Copyright 2010-2021 Tormod Volden and Stefan Schmidt
This program is Free Software and has ABSOLUTELY NO WARRANTY
Please report bugs to http://sourceforge.net/p/dfu-util/tickets/

dfu-util: Warning: Invalid DFU suffix signature
dfu-util: A valid DFU suffix will be required in a future dfu-util release
Waiting for device, exit with ctrl-C
Opening DFU capable USB device...
Device ID 0483:df11
Device DFU version 0110
Claiming USB DFU Interface...
Setting Alternate Interface #0 ...
Determining device status...
DFU state(2) = dfuIDLE, status(0) = No error condition is present
DFU mode device DFU version 0110
Device returned transfer size 4096
Copying data from PC to DFU device
Download        [=========================] 100%       193885 bytes
Download done.
DFU state(7) = dfuMANIFEST, status(0) = No error condition is present
DFU state(2) = dfuIDLE, status(0) = No error condition is present
Done!
```

```
hostpc$ dfu-util -w -a 'fsbl2' -D u-boot-spl.stm32
dfu-util 0.11

Copyright 2005-2009 Weston Schmidt, Harald Welte and OpenMoko Inc.
Copyright 2010-2021 Tormod Volden and Stefan Schmidt
This program is Free Software and has ABSOLUTELY NO WARRANTY
Please report bugs to http://sourceforge.net/p/dfu-util/tickets/

dfu-util: Warning: Invalid DFU suffix signature
dfu-util: A valid DFU suffix will be required in a future dfu-util release
Waiting for device, exit with ctrl-C
Opening DFU capable USB device...
Device ID 0483:df11
Device DFU version 0110
Claiming USB DFU Interface...
Setting Alternate Interface #1 ...
Determining device status...
DFU state(2) = dfuIDLE, status(0) = No error condition is present
DFU mode device DFU version 0110
Device returned transfer size 4096
Copying data from PC to DFU device
Download        [=========================] 100%       193885 bytes
Download done.
DFU state(7) = dfuMANIFEST, status(0) = No error condition is present
DFU state(2) = dfuIDLE, status(0) = No error condition is present
Done!
```

```
hostpc$ dfu-util -w -a 'uboot' -D u-boot.itb
dfu-util 0.11

Copyright 2005-2009 Weston Schmidt, Harald Welte and OpenMoko Inc.
Copyright 2010-2021 Tormod Volden and Stefan Schmidt
This program is Free Software and has ABSOLUTELY NO WARRANTY
Please report bugs to http://sourceforge.net/p/dfu-util/tickets/

dfu-util: Warning: Invalid DFU suffix signature
dfu-util: A valid DFU suffix will be required in a future dfu-util release
Waiting for device, exit with ctrl-C
Opening DFU capable USB device...
Device ID 0483:df11
Device DFU version 0110
Claiming USB DFU Interface...
Setting Alternate Interface #2 ...
Determining device status...
DFU state(2) = dfuIDLE, status(0) = No error condition is present
DFU mode device DFU version 0110
Device returned transfer size 4096
Copying data from PC to DFU device
Download        [=========================] 100%       942253 bytes
Download done.
DFU state(7) = dfuMANIFEST, status(0) = No error condition is present
DFU state(2) = dfuIDLE, status(0) = No error condition is present
Done!
```

Once the installation completed, terminate DFU from U-Boot console
by pressing `Ctrl + C`, then reset the board.

```
STM32MP> dfu 0 mtd
crq->brequest:0x0
################################################DOWNLOAD ... OK
Ctrl+C to exit ...
################################################DOWNLOAD ... OK
Ctrl+C to exit ...
################################################
################################################
################################################
################################################
#######################################DOWNLOAD ... OK
Ctrl+C to exit ...
STM32MP>
```

To reset the board, either press the `RESET` button on the board,
or perform reset from U-Boot shell as follows:

```
STM32MP> reset
```

Once the board resets, new U-Boot bootloader version will start.

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
root@dh-stm32mp1-dhcor-avenger96:~# tr '\0' "$(printf '\377')" < /dev/zero | dd bs=1024 count=1920 of=flash.bin
root@dh-stm32mp1-dhcor-avenger96:~# dd if=u-boot-spl.stm32 of=flash.bin conv=notrunc
root@dh-stm32mp1-dhcor-avenger96:~# dd if=u-boot-spl.stm32 of=flash.bin conv=notrunc bs=262144 seek=1
root@dh-stm32mp1-dhcor-avenger96:~# dd if=u-boot.itb       of=flash.bin conv=notrunc bs=524288 seek=1
```

To determine the MTD block device which represents the boot SPI NOR, iterate
over all of `/dev/mtdblock*` device nodes and select the one where the output
of `udevadm info /dev/mtdblockN` command matches the following output:

```
root@dh-stm32mp1-dhcor-avenger96:~# udevadm info /dev/mtdblock0
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
root@dh-stm32mp1-dhcor-avenger96:~# dd if=flash.bin of=/dev/mtdblock0
3840+0 records in
3840+0 records out
1966080 bytes (2.0 MB, 1.9 MiB) copied, 17.7132 s, 111 kB/s
```

Once the installation completed, reset the board. To perform reset from
Linux kernel, use the following command:

```
root@dh-stm32mp1-dhcor-avenger96:~# reboot
```

### U-Boot bootloader recovery (using USB OTG DFU upload)

This section depends on [dfu-util](https://dfu-util.sourceforge.net/) tool.
This tool must be installed on the host system.

In case the bootloader is damaged, malfunctioning or missing, it is still
possible to recover the system by starting a replacement bootloader using
USB OTG DFU upload.

Make sure the system is powered off and USB OTG cable is connected to port
`USB OTG` `X5`. Switch `BOOT_CONFIG` DIP switches to `BOOT[0:2]=off-off-off`.
Power on the system.

The system will now enumerate on the host PC as follows:
```
host$ dmesg -w
usb 5-2.2.2.1: new high-speed USB device number 27 using xhci_hcd
usb 5-2.2.2.1: New USB device found, idVendor=0483, idProduct=df11, bcdDevice= 2.00
usb 5-2.2.2.1: New USB device strings: Mfr=1, Product=2, SerialNumber=3
usb 5-2.2.2.1: Product: DFU in HS Mode @Device ID /0x500, @Revision ID /0x0000
usb 5-2.2.2.1: Manufacturer: STMicroelectronics
usb 5-2.2.2.1: SerialNumber: 003800000000000012345678
```

Use `dfu-util` to send U-Boot SPL binary using DFU via USB OTG to the SoC:
```
host$ dfu-util -w -a '@FSBL /0x01/1*1Me' -D u-boot-spl.stm32
dfu-util 0.11

Copyright 2005-2009 Weston Schmidt, Harald Welte and OpenMoko Inc.
Copyright 2010-2021 Tormod Volden and Stefan Schmidt
This program is Free Software and has ABSOLUTELY NO WARRANTY
Please report bugs to http://sourceforge.net/p/dfu-util/tickets/

dfu-util: Warning: Invalid DFU suffix signature
dfu-util: A valid DFU suffix will be required in a future dfu-util release
Waiting for device, exit with ctrl-C
Opening DFU capable USB device...
Device ID 0483:df11
Device DFU version 0110
Claiming USB DFU Interface...
Setting Alternate Interface #1 ...
Determining device status...
DFU state(2) = dfuIDLE, status(0) = No error condition is present
DFU mode device DFU version 0110
Device returned transfer size 1024
Copying data from PC to DFU device
Download        [=========================] 100%       193632 bytes
Download done.
DFU state(7) = dfuMANIFEST, status(0) = No error condition is present
dfu-util: unable to read DFU status after completion (LIBUSB_ERROR_IO)
```

The system will now disconnect from the host PC and re-enumerate on the
host PC as follows:
```
host$ dmesg -w
usb 5-2.2.2.1: USB disconnect, device number 31
usb 5-2.2.2.1: new high-speed USB device number 32 using xhci_hcd
usb 5-2.2.2.1: New USB device found, idVendor=0483, idProduct=df11, bcdDevice=7e.8a
usb 5-2.2.2.1: New USB device strings: Mfr=1, Product=2, SerialNumber=0
usb 5-2.2.2.1: Product: USB download gadget
usb 5-2.2.2.1: Manufacturer: dh
```

Use `dfu-util` to send U-Boot fitImage using DFU via USB OTG to the SoC:
```
host$ dfu-util -w -a 'u-boot.itb' -D u-boot.itb
dfu-util 0.11

Copyright 2005-2009 Weston Schmidt, Harald Welte and OpenMoko Inc.
Copyright 2010-2021 Tormod Volden and Stefan Schmidt
This program is Free Software and has ABSOLUTELY NO WARRANTY
Please report bugs to http://sourceforge.net/p/dfu-util/tickets/

dfu-util: Warning: Invalid DFU suffix signature
dfu-util: A valid DFU suffix will be required in a future dfu-util release
Waiting for device, exit with ctrl-C
Opening DFU capable USB device...
Device ID 0483:df11
Device DFU version 0110
Claiming USB DFU Interface...
Setting Alternate Interface #0 ...
Determining device status...
DFU state(2) = dfuIDLE, status(0) = No error condition is present
DFU mode device DFU version 0110
Device returned transfer size 4096
Copying data from PC to DFU device
Download        [=========================] 100%      1022744 bytes
Download done.
DFU state(7) = dfuMANIFEST, status(0) = No error condition is present
DFU state(2) = dfuIDLE, status(0) = No error condition is present
Done!
```

Use `dfu-util` to send disconnected event using DFU via USB OTG to the SoC.
This is used to start executing U-Boot:
```
host$ dfu-util -e
dfu-util 0.11

Copyright 2005-2009 Weston Schmidt, Harald Welte and OpenMoko Inc.
Copyright 2010-2021 Tormod Volden and Stefan Schmidt
This program is Free Software and has ABSOLUTELY NO WARRANTY
Please report bugs to http://sourceforge.net/p/dfu-util/tickets/

Opening DFU capable USB device...
Device ID 0483:df11
Device DFU version 0110
Claiming USB DFU Interface...
Setting Alternate Interface #0 ...
Determining device status...
DFU state(2) = dfuIDLE, status(0) = No error condition is present
DFU mode device DFU version 0110
Device returned transfer size 4096
```

The system will now disconnect from the host PC as follows:
```
host$ dmesg -w
usb 5-2.2.2.1: USB disconnect, device number 32
usb 5-2.2.2.1: new high-speed USB device number 33 using xhci_hcd
usb 5-2.2.2.1: New USB device found, idVendor=0483, idProduct=df11, bcdDevice= 2.00
usb 5-2.2.2.1: New USB device strings: Mfr=1, Product=2, SerialNumber=3
usb 5-2.2.2.1: Product: USB download gadget@Device ID /0x500, @Revision ID /0x2000, @Name /STM32MP157CAA Rev.B,
usb 5-2.2.2.1: Manufacturer: dh
usb 5-2.2.2.1: SerialNumber: 003800000000000012345678
usb 5-2.2.2.1: USB disconnect, device number 33
```

At this point, the uploaded U-Boot will start on the system and would
become accessible via serial console. Perform regular U-Boot bootloader
installation into SPI NOR procedure as documented above to recover the
system.

Once the procedure is complete, Switch `BOOT_CONFIG` DIP switches to
`BOOT[0:2]=ON-off-off` for SPI NOR boot.

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

- 96boards OV5640 mezzanine card with sensor connected to port J3.
  - `stm32mp15xx-avenger96-overlay-ov5640-x7.dtbo`
- AT25AA010A SPI EEPROM on low-speed expansion X6 SPI2
  - `stm32mp15xx-avenger96-overlay-spi2-eeprom-x6.dtbo`
- AT24C04 I2C EEPROM on low-speed expansion X6 I2C1
  - `stm32mp15xx-avenger96-overlay-i2c1-eeprom-x6.dtbo`
- AT24C04 I2C EEPROM on low-speed expansion X6 I2C2
  - `stm32mp15xx-avenger96-overlay-i2c2-eeprom-x6.dtbo`
- DH 644-100 mezzanine card with Orisetech OTM8009A DSI display attached on top
  - `stm32mp15xx-avenger96-overlay-644-100-x6-otm8009a.dtbo`
- DH 644-100 mezzanine card with RPi 7" DSI display attached on top
  - `stm32mp15xx-avenger96-overlay-644-100-x6-rpi7inch.dtbo`
- FDCAN1 on low-speed expansion X6
  - `stm32mp15xx-avenger96-overlay-fdcan1-x6.dtbo`
- FDCAN2 on low-speed expansion X6
  - `stm32mp15xx-avenger96-overlay-fdcan2-x6.dtbo`

## List available Device Tree Overlays (from U-Boot)

To list Device Tree Overlays supported by the current operating system image,
inspect the Linux kernel fitImage from within U-Boot as follows.

First, load the kernel fitImage from SD or eMMC into memory:
```
STM32MP> load mmc 0:4 ${loadaddr} boot/fitImage
5981000 bytes read in 256 ms (22.3 MiB/s)
```

Second, list the available fitImage configurations, which also
match the available base Device Tree and Device Tree Overlays:
```
STM32MP> iminfo $loadaddr

## Checking Image at c2000000 ...
   FIT image found
   FIT description: Kernel fitImage for DH Linux Distribution/6.12.51+git/dh-stm32mp1-dhcor-avenger96
   Created:         2025-10-06   9:17:53 UTC
    Image 0 (kernel-1)
     Description:  Linux kernel
     Created:      2025-10-06   9:17:53 UTC
     Type:         Kernel Image
     Compression:  uncompressed
     Data Start:   0xc2000104
     Data Size:    5863416 Bytes = 5.6 MiB
     Architecture: ARM
     OS:           Linux
     Load Address: 0xc4000000
     Entry Point:  0xc4000000
     Hash algo:    sha256
     Hash value:   7d1e8998591fffed819488805f78d5693b5bff04c2fe9e25261c0a4a1e719a31
    Image 1 (fdt-stm32mp157a-avenger96.dtb)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc2597a14
     Data Size:    102204 Bytes = 99.8 KiB
     Architecture: ARM
     Load Address: 0xcff00000
     Hash algo:    sha256
     Hash value:   769b1316e5c02959721a4669ead0d46970719b8da482da7d9f8d92d1fc45625d
    Image 2 (fdt-stm32mp15xx-avenger96-overlay-644-100-x6-otm8009a.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b0a60
     Data Size:    1827 Bytes = 1.8 KiB
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   c9f7bf4c05267b8b620a2625debfcdc1675288b8295b963151f7e511fd91ff2d
    Image 3 (fdt-stm32mp15xx-avenger96-overlay-644-100-x6-rpi7inch.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b1294
     Data Size:    3098 Bytes = 3 KiB
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   3c6706f5f68d9827e5de6e2e7c69fbf1b27f7b5ca54dffc9dcedc7d314540714
    Image 4 (fdt-stm32mp15xx-avenger96-overlay-fdcan1-x6.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b1fb8
     Data Size:    225 Bytes = 225 Bytes
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   2f467cc9fec89e3ad546ab6a100a2c2cd0fb3005b177d4a2893e22903524dc57
    Image 5 (fdt-stm32mp15xx-avenger96-overlay-fdcan2-x6.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b21a4
     Data Size:    225 Bytes = 225 Bytes
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   d9073a2c2b54455a9b3f16374c2099f0e5264f45275e26ce37f93f0e31e731e2
    Image 6 (fdt-stm32mp15xx-avenger96-overlay-i2c1-eeprom-x6.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b2394
     Data Size:    355 Bytes = 355 Bytes
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   a5dcff1791b05e1ba098ce8b368fe8d46e0fc666f29a19a8dd3567de96d29d29
    Image 7 (fdt-stm32mp15xx-avenger96-overlay-i2c2-eeprom-x6.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b2604
     Data Size:    355 Bytes = 355 Bytes
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   e53bef24f2bd6fc50a074a077eb1c996c9efe3d57f648bab4646b8361bff285c
    Image 8 (fdt-stm32mp15xx-avenger96-overlay-ov5640-x7.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b2870
     Data Size:    3246 Bytes = 3.2 KiB
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   2c30bb03b5491d6477fdbb0080063fb07f29465e33439bcb5a50ad715ebf7534
    Image 9 (fdt-stm32mp15xx-avenger96-overlay-spi2-eeprom-x6.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b362c
     Data Size:    745 Bytes = 745 Bytes
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   fa3f3374d53ea7d30f89e77c937348427f4267a985eb754660237e7bf8f7eb68
    Default Configuration: 'conf-stm32mp157a-avenger96.dtb'
    Configuration 0 (conf-stm32mp157a-avenger96.dtb)
     Description:  1 Linux kernel, FDT blob
     Kernel:       kernel-1
     FDT:          fdt-stm32mp157a-avenger96.dtb
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 1 (conf-stm32mp15xx-avenger96-overlay-644-100-x6-otm8009a.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-avenger96-overlay-644-100-x6-otm8009a.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 2 (conf-stm32mp15xx-avenger96-overlay-644-100-x6-rpi7inch.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-avenger96-overlay-644-100-x6-rpi7inch.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 3 (conf-stm32mp15xx-avenger96-overlay-fdcan1-x6.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-avenger96-overlay-fdcan1-x6.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 4 (conf-stm32mp15xx-avenger96-overlay-fdcan2-x6.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-avenger96-overlay-fdcan2-x6.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 5 (conf-stm32mp15xx-avenger96-overlay-i2c1-eeprom-x6.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-avenger96-overlay-i2c1-eeprom-x6.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 6 (conf-stm32mp15xx-avenger96-overlay-i2c2-eeprom-x6.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-avenger96-overlay-i2c2-eeprom-x6.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 7 (conf-stm32mp15xx-avenger96-overlay-ov5640-x7.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-avenger96-overlay-ov5640-x7.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 8 (conf-stm32mp15xx-avenger96-overlay-spi2-eeprom-x6.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-avenger96-overlay-spi2-eeprom-x6.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
## Checking hash(es) for FIT Image at c2000000 ...
   Hash(es) for Image 0 (kernel-1): sha256+
   Hash(es) for Image 1 (fdt-stm32mp157a-avenger96.dtb): sha256+
   Hash(es) for Image 2 (fdt-stm32mp15xx-avenger96-overlay-644-100-x6-otm8009a.dtbo): sha256+
   Hash(es) for Image 3 (fdt-stm32mp15xx-avenger96-overlay-644-100-x6-rpi7inch.dtbo): sha256+
   Hash(es) for Image 4 (fdt-stm32mp15xx-avenger96-overlay-fdcan1-x6.dtbo): sha256+
   Hash(es) for Image 5 (fdt-stm32mp15xx-avenger96-overlay-fdcan2-x6.dtbo): sha256+
   Hash(es) for Image 6 (fdt-stm32mp15xx-avenger96-overlay-i2c1-eeprom-x6.dtbo): sha256+
   Hash(es) for Image 7 (fdt-stm32mp15xx-avenger96-overlay-i2c2-eeprom-x6.dtbo): sha256+
   Hash(es) for Image 8 (fdt-stm32mp15xx-avenger96-overlay-ov5640-x7.dtbo): sha256+
   Hash(es) for Image 9 (fdt-stm32mp15xx-avenger96-overlay-spi2-eeprom-x6.dtbo): sha256+
```

The Device Tree and Device Tree Overlays lists below are based on fitImage
configuration listing printed in the example above, with the `conf-` prefix
removed.

The listing contains one base Device Tree:

- `stm32mp157a-dhcor-avenger96.dtb`

The listing contains the following Device Tree Overlays:

- `stm32mp15xx-avenger96-overlay-fdcan1-x6.dtbo`
- `stm32mp15xx-avenger96-overlay-fdcan2-x6.dtbo`
- `stm32mp15xx-avenger96-overlay-i2c1-eeprom-x6.dtbo`
- `stm32mp15xx-avenger96-overlay-i2c2-eeprom-x6.dtbo`
- `stm32mp15xx-avenger96-overlay-spi2-eeprom-x6.dtbo`
- `stm32mp15xx-avenger96-overlay-ov5640-x7.dtbo`
- `stm32mp15xx-avenger96-overlay-644-100-x6-otm8009a.dtbo`
- `stm32mp15xx-avenger96-overlay-644-100-x6-rpi7inch.dtbo`

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

To configure the system to use `stm32mp157a-dhcor-avenger96.dtb` base Device
Tree and apply additional `stm32mp15xx-avenger96-overlay-644-100-x6-otm8009a.dtbo`
Device Tree Overlay, configure the `loaddtos` environment variable as
follows:
```
                     .------------------------------.----- '#' Separator
                     |                              |
                     v                              v
=> env set loaddtos '#conf-stm32mp157a-avenger96.dtb#conf-stm32mp15xx-avenger96-overlay-644-100-x6-otm8009a.dtbo
                       ^                              ^
                       |                              |
                       |                              '--- 644-100 display DTO
                       |
                       '---------------------------------- Base Device Tree
```

To make configuration persistent, store U-Boot environment:
```
=> env save
```

To boot the system with additional DTO applied:
```
STM32MP> env set loaddtos '#conf-stm32mp157a-avenger96.dtb#conf-stm32mp15xx-avenger96-overlay-644-100-x6-otm8009a.dtbo'
STM32MP> boot
Boot over nor0!
switch to partitions #0, OK
mmc1(part 0) is current device
Scanning mmc 1:4...
Found U-Boot script /boot/boot.scr
4988 bytes read in 1 ms (4.8 MiB/s)
## Executing script at c4100000
5981000 bytes read in 73 ms (78.1 MiB/s)
Booting the Linux kernel...

## Loading kernel (any) from FIT Image at c2000000 ...
   Using 'conf-stm32mp157a-avenger96.dtb' configuration
   Trying 'kernel-1' kernel subimage
     Description:  Linux kernel
     Created:      2025-10-06   9:17:53 UTC
     Type:         Kernel Image
     Compression:  uncompressed
     Data Start:   0xc2000104
     Data Size:    5863416 Bytes = 5.6 MiB
     Architecture: ARM
     OS:           Linux
     Load Address: 0xc4000000
     Entry Point:  0xc4000000
     Hash algo:    sha256
     Hash value:   7d1e8998591fffed819488805f78d5693b5bff04c2fe9e25261c0a4a1e719a31
   Verifying Hash Integrity ... sha256+ OK
## Loading fdt (any) from FIT Image at c2000000 ...
   Using 'conf-stm32mp157a-avenger96.dtb' configuration
   Trying 'fdt-stm32mp157a-avenger96.dtb' fdt subimage
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc2597a14
     Data Size:    102204 Bytes = 99.8 KiB
     Architecture: ARM
     Load Address: 0xcff00000
     Hash algo:    sha256
     Hash value:   769b1316e5c02959721a4669ead0d46970719b8da482da7d9f8d92d1fc45625d
   Verifying Hash Integrity ... sha256+ OK
   Loading fdt from 0xc2597a14 to 0xcff00000
   Loading Device Tree to cffe4000, end cfffffff ... OK
Working FDT set to cffe4000
## Loading fdt (any) from FIT Image at c2000000 ...
   Using 'conf-stm32mp15xx-avenger96-overlay-644-100-x6-otm8009a.dtbo' configuration
   Trying 'fdt-stm32mp15xx-avenger96-overlay-644-100-x6-otm8009a.dtbo' fdt subimage
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b0a60
     Data Size:    1827 Bytes = 1.8 KiB
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   c9f7bf4c05267b8b620a2625debfcdc1675288b8295b963151f7e511fd91ff2d
   Verifying Hash Integrity ... sha256+ OK
   Booting using the fdt blob at 0xcffe4000
Working FDT set to cffe4000
   Loading Kernel Image to c4000000
   Loading Device Tree to cffc7000, end cffe3132 ... OK
Working FDT set to cffc7000

Starting kernel ...
```

# IO access

This section describe access to various IO of this system.

## Serial ports and UARTs

U-Boot on this system provides serial console access via Low Speed
Expansion Connector, header `X6`, this is `serial@40010000`. Other
serial ports can be made available by modifying U-Boot control Device
Tree.

Linux on this system provides serial console access via Low Speed
Expansion Connector, header `X6`, this is `40010000.serial`. Linux
also provides one additional serial ports accessible via Low Speed
Expansion Connector, header `X6` `40019000.serial`. The remaining
UART `4000e000.serial` is used by Bluetooth chip. These ports are
accessible via `/dev/ttySTM*` device nodes. To discern the ports
apart, use `udevadm info /dev/ttySTM*` command, which prints the full
hardware path to the selected port:
```
root@dh-stm32mp1-dhcor-avenger96:~# udevadm info /dev/ttySTM*
P: /devices/platform/soc/5c007000.bus/40010000.serial/40010000.serial:0/40010000.serial:0.0/tty/ttySTM0
                                      ^^^^^^^^^^^^^^^
M: ttySTM0
   ^^^^^^^
```

To access either serial port, use suitable terminate emulator,
for example `minicom`, `picocom`, `screen`, and many others.

## IO

### Power button

Linux on this system provides access to PMIC On-Key power button via
event device. To access PMIC On-Key power button on this platform,
open the matching `/dev/input/event*` event device.

Example using `evtest` tool:

```
root@dh-stm32mp1-dhcor-avenger96:~# evtest
No device specified, trying to scan all of /dev/input/event*
Available devices:
/dev/input/event0:      pmic_onkey
Select the device event number [0-0]: 0
Input driver version is 1.0.1
Input device ID: bus 0x0 vendor 0x0 product 0x0 version 0x0
Input device name: "pmic_onkey"
Supported events:
  Event type 0 (EV_SYN)
  Event type 1 (EV_KEY)
    Event code 116 (KEY_POWER)
Properties:
Testing ... (interrupt to exit)
Event: time 1761262469.635829, type 1 (EV_KEY), code 116 (KEY_POWER), value 1
Event: time 1761262469.635829, -------------- SYN_REPORT ------------
Event: time 1761262469.821989, type 1 (EV_KEY), code 116 (KEY_POWER), value 0
Event: time 1761262469.821989, -------------- SYN_REPORT ------------
```

### LEDs

U-Boot on this system provides access to four LEDs via `led` command.

To list available LEDs and their current state, use the `led list` command:
```
STM32MP> led list
green:user0      off
green:user1      off
green:user2      off
green:user3      off
```

To enable an LED, use `led <led_label> on` command:
```
STM32MP> led green:user0 on
STM32MP> led green:user1 on
STM32MP> led green:user2 on
STM32MP> led green:user3 on
```

To disable an LED, use `led <led_label> off` command:
```
STM32MP> led green:user0 off
STM32MP> led green:user1 off
STM32MP> led green:user2 off
STM32MP> led green:user3 off
```

Linux on this system provides access to four LEDs via sysfs LED API.

To enable an LED, write 1 into matching sysfs LED attribute:
```
root@dh-stm32mp1-dhcor-avenger96:~# echo 1 > /sys/class/leds/green\:user0/brightness
root@dh-stm32mp1-dhcor-avenger96:~# echo 1 > /sys/class/leds/green\:user1/brightness
root@dh-stm32mp1-dhcor-avenger96:~# echo 1 > /sys/class/leds/green\:user2/brightness
root@dh-stm32mp1-dhcor-avenger96:~# echo 1 > /sys/class/leds/green\:user3/brightness
```

To disable an LED, write 0 into matching sysfs LED attribute:
```
root@dh-stm32mp1-dhcor-avenger96:~# echo 0 > /sys/class/leds/green\:user0/brightness
root@dh-stm32mp1-dhcor-avenger96:~# echo 0 > /sys/class/leds/green\:user1/brightness
root@dh-stm32mp1-dhcor-avenger96:~# echo 0 > /sys/class/leds/green\:user2/brightness
root@dh-stm32mp1-dhcor-avenger96:~# echo 0 > /sys/class/leds/green\:user3/brightness
```

### GPIOs

U-Boot on this system provides access to multiple GPIO pins.

To list GPIOs and their current state, use the `gpio status` command:
```
STM32MP> gpio status
Bank GPIOF:
GPIOF3: output: 0 [x] led2.gpios

Bank GPIOG:
GPIOG0: output: 0 [x] led3.gpios
GPIOG1: output: 0 [x] led4.gpios

Bank GPIOI:
GPIOI5: output: 0 [x] regulator-sd_switch.gpios
GPIOI8: input: 0 [x] mmc@58005000.cd-gpios

Bank GPIOZ:
GPIOZ2: output: 1 [x] ethernet@5800a000.phy-reset-gpios
GPIOZ7: output: 0 [x] led1.gpios
```

To set a GPIO as Output-High, use `gpio set <pin>` command:
```
STM32MP> gpio set GPIOF3
gpio: pin GPIOF3 (gpio 83) value is 1
```

To set a GPIO as Output-Low, use `gpio clear <pin>` command:
```
STM32MP> gpio clear GPIOF3
gpio: pin GPIOF3 (gpio 83) value is 0
```

To toggle a GPIO, use `gpio toggle <pin>` command:
```
STM32MP> gpio toggle GPIOF3
gpio: pin GPIOF3 (gpio 83) value is 1
STM32MP> gpio toggle GPIOF3
gpio: pin GPIOF3 (gpio 83) value is 0
```

Linux on this system provides access to multiple GPIO pins. Access to
GPIOs is provided using GPIO chardev interface and libgpiod. Subset of
GPIO lines is named, which makes them easier to discern in libgpiod
tools output.

To list available GPIOs, use the `gpioinfo` command:
```
root@dh-stm32mp1-dhcor-avenger96:~# gpioinfo
gpiochip0 - 16 lines:
        line   0:       "PA0"                   input consumer="kernel"
        line   1:       "PA1"                   input consumer="kernel"
        line   2:       "PA2"                   input consumer="kernel"
        line   3:       "PA3"                   input consumer="kernel"
        line   4:       "PA4"                   input
...
        line  11:       "AV96-K"                input
...
...
```

To get current state of a GPIO, use the `gpioget` command:
```
root@dh-stm32mp1-dhcor-avenger96:~# gpioget -c /dev/gpiochip0 11
"11"=inactive
...
```

To set a GPIO as Output-High, use `gpioset ... <line>=1` command:
```
root@dh-stm32mp1-dhcor-avenger96:~# gpioset -c /dev/gpiochip0 11=1
```

To set a GPIO as Output-Low, use `gpioset ... <line>=0` command:
```
root@dh-stm32mp1-dhcor-avenger96:~# gpioset -c /dev/gpiochip0 11=0
```

It is possible to refer to GPIOs using symbolic names:
```
# Numerical reference to GPIO on gpiochip0 line 11:
root@dh-stm32mp1-dhcor-avenger96:~# gpioget -c /dev/gpiochip0 11
"11"=inactive

# Symbolic name reference to GPIO on gpiochip0 line 11:
root@dh-stm32mp1-dhcor-avenger96:~# gpioget AV96-K
"AV96-K"=inactive
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
In the example below, major and minor numbers for microSD card are 179 and 24,
which are represented by device node `/dev/mmcblk2`:
```
root@dh-stm32mp1-dhcor-avenger96:~# cat /sys/devices/platform/soc/58005000.mmc/mmc_host/mmc*/mmc*/block/mmcblk*/dev
179:24
^^^ ^^

root@dh-stm32mp1-dhcor-avenger96:~# ls -la /dev/mmcblk*
...
brw-rw---- 1 root disk 179, 24 Mar 10 03:53 /dev/mmcblk2
                       ^^^  ^^              ^^^^^^^^^^^^
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
In the example below, major and minor numbers for eMMC card are 179 and 0,
which are represented by device node `/dev/mmcblk0`:
```
root@dh-stm32mp1-dhcor-avenger96:~# cat /sys/devices/platform/soc/58007000.mmc/mmc_host/mmc*/mmc*/block/mmcblk*/dev
179:0
^^^ ^

root@dh-stm32mp1-dhcor-avenger96:~# ls -la /dev/mmcblk*
...
brw-rw---- 1 root disk 179,  0 Mar 10 03:53 /dev/mmcblk0
                       ^^^   ^              ^^^^^^^^^^^^
...
```

## Network

### Ethernet

U-Boot on this system provides access to one ethernet interface via U-Boot
networking stack. Use `net list` command to list available ethernet interface:
```
STM32MP> net list
eth0 : ethernet@5800a000 00:11:22:33:44:55 active
```

To select active ethernet interface, set U-Boot environment variable `ethact`.

Linux on this system provides access to one ethernet interfaces via Linux
networking stack. The interface is assigned deterministic interface name
via systemd udevd rules.

```
root@dh-stm32mp1-dhcor-avenger96:~# ip addr show
...
4: ethsom0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 ...
...
```

### WiFi and Bluetooth

Linux on this system provides access to WiFi interface via Linux networking
stack. This interface is assigned deterministic interface name via systemd
udevd rules.

```
root@dh-stm32mp1-dhcor-avenger96:~# udevadm info /sys/class/net/wlansom0
P: /devices/platform/soc/5c007000.bus/48004000.mmc/mmc_host/mmc1/mmc1:0001/mmc1:0001:1/net/wlansom0
M: wlansom0
...
```

To operate the WiFi interface, use `iw` and `wpa-supplicant` tools, or any
other suitable network management tooling. Example scan for nearby networks.
The WiFi interface might be blocked via software RFkill, it may be necessary
to unblock the interface first:

```
root@dh-stm32mp1-dhcor-avenger96:~# rfkill unblock wifi

root@dh-stm32mp1-dhcor-avenger96:~# ip link set wlansom0 up
root@dh-stm32mp1-dhcor-avenger96:~# iw dev wlansom0 scan
BSS 00:11:22:33:44:55(on wlansom0)
...
```

Linux on this system provides access to Bluetooth interface via Linux bluetooth
stack. To operate the Bluetooth interface, use the Bluez stack, for example via
the `bluetoothctl` command. The bluetooth interface might be blocked via software
RFkill, it may be necessary to unblock the interface first:

```
root@dh-stm32mp1-dhcor-avenger96:~# rfkill unblock bluetooth

root@dh-stm32mp1-dhcor-avenger96:~# bluetoothctl
hci0 new_settings: powered bondable ssp br/edr le secure-conn
Agent registered
[CHG] Controller 10:98:00:11:22:33 Pairable: yes
[bluetooth]# power on
Changing power on succeeded
[bluetooth]# scan on
SetDiscoveryFilter success
hci0 type 7 discovering on
Discovery started
[CHG] Controller 10:98:00:11:22:33 Discovering: yes
...
```

### CAN

Linux on this system provides access to CAN/CANFD interface via Linux networking
stack and socketcan interface. This requires application of a DT Overlay, either
`FDCAN1 on low-speed expansion X6` or `FDCAN2 on low-speed expansion X6`. Please
refer to "Supported Device Tree Overlays" section above.

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

This system provides one USB host port connected to a USB 2.0 hub located
on the carrier board. Access to the USB hub ports is possible via two USB
connectors `X3` and `X4`.

This system provides one USB OTG port connected to a micro-USB connector `X5`.

U-Boot on this system provides access to USB host and peripheral ports via
standard U-Boot USB stack. To enumerate USB devices attached to the host
port, use `usb` command:

```
STM32MP> usb reset
resetting USB...
Bus usb@5800d000: USB EHCI 1.00
scanning bus usb@5800d000 for devices... 3 USB Device(s) found
       scanning usb for storage devices... 1 Storage Device(s) found

STM32MP> usb info
1: Hub,  USB Revision 2.0
...
3: Mass Storage,  USB Revision 2.0
 - SanDisk Cruzer Edge
...

STM32MP> usb tree
USB device tree:
  1  Hub (480 Mb/s, 0mA)
  |  u-boot EHCI Host Controller
  |
  +-2  Hub (480 Mb/s, 2mA)
    |
    +-3  Mass Storage (480 Mb/s, 200mA)
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
root@dh-stm32mp1-dhcor-avenger96:~# lsusb
Bus 001 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub
Bus 002 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub
Bus 002 Device 002: ID 0424:2513 Microchip Technology, Inc. (formerly SMSC) 2.0 Hub
```

### USB Peripheral

To operate the USB OTG port in peripheral mode and set up emulation
of various complex peripherals, it is recommended to use
[libusbgx](https://github.com/linux-usb-gadgets/libusbgx).
It is also possible to emulate simple predefined USB peripherals
by loading a matching kernel module. The `g_zero` USB peripheral
driver which is used for testing purposes can be loaded using the
following command:
```
root@dh-stm32mp1-dhcor-avenger96:~# modprobe g_zero
zero gadget.0: Gadget Zero, version: Cinco de Mayo 2008
zero gadget.0: zero ready
dwc2 49000000.usb-otg: bound driver zero
```

Connect USB A-to-microB cable between the board USB OTG port and host PC.

The newly established USB connection is reported on the board side as follows:
```
dwc2 49000000.usb-otg: new device is high-speed
dwc2 49000000.usb-otg: new device is high-speed
dwc2 49000000.usb-otg: new address 18
```

The newly established USB connection is reported on the host PC side as follows:
```
usb 5-2.2.2.4: new high-speed USB device number 18 using xhci_hcd
usb 5-2.2.2.4: New USB device found, idVendor=1a0a, idProduct=badd, bcdDevice= 6.12
usb 5-2.2.2.4: New USB device strings: Mfr=1, Product=2, SerialNumber=3
usb 5-2.2.2.4: Product: Gadget Zero
usb 5-2.2.2.4: Manufacturer: Linux 6.12.51-stable-standard-00010-g9bb1faec694c with 49000000.usb-otg
usb 5-2.2.2.4: SerialNumber: 0123456789.0123456789.0123456789
```
