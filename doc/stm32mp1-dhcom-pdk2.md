STM32MP15xx DHCOM SoM on DH PDK2 carrier board
==============================================

# Initial board setup

This section explains how to perform initial hardware setup, how to
assemble the hardware, which cables to connect to which ports, how to
power the hardware on, and finally how to obtain serial console access.

## Insert SoM

Insert SoM provided with the carrier board into socket `U38`.
It is likely the SoM is already populated.

## Connect serial console cable

Connect serial console RS232 cable into `RS232 UART1` DB9 plug `X8`.
This is RS232 up to 12V voltage level connection.

## Connect ethernet cables (optional)

Connect up to two ethernet cables to dual stacked ethernet jack
`100MB Ethernet` `X5`.

## Connect USB OTG cables (optional)

Connect USB OTG cable between host PC and carrier board into plug
`USB OTG` `X14`.

## Connect power supply

Connect provided 24V/1A power supply into barrel jack `X24` or
compatible supply into plug `X23`.

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

To boot only from full size SD card, override `boot_targets` variable as follows:
```
STM32MP> env set boot_targets mmc2
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

The operating system image can be installed onto either microSD card, full
size SD card or into eMMC USER hardware partition.

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
# microSD on DHCOM
STM32MP> ums 0 mmc 0
# eMMC on DHCOM
STM32MP> ums 0 mmc 1
# SD on PDK2
STM32MP> ums 0 mmc 2
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
host$ zstd -d dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic.zst
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
    dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic.bmap \
    dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic.zst \
    /dev/sdx
bmaptool: info: block map format version 2.0
bmaptool: info: 400474 blocks of size 4096 (1.5 GiB), mapped 246005 blocks (961.0 MiB or 61.4%)
bmaptool: info: copying image 'dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic' to block device '/dev/sdx' using bmap file 'dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic.bmap'
bmaptool: info: 100% copied
bmaptool: info: synchronizing '/dev/sdx'
bmaptool: info: copying time: 13.2s, copying speed 72.5 MiB/sec
```

It is equally possible to use plain `dd` to write the decompressed
system image into the block device as follows:
```
host$ dd if=dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic \
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
host$ zstd -d dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic.zst
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
    dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic.bmap \
    dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic.zst \
    /dev/sdx
bmaptool: info: block map format version 2.0
bmaptool: info: 400474 blocks of size 4096 (1.5 GiB), mapped 246005 blocks (961.0 MiB or 61.4%)
bmaptool: info: copying image 'dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic' to block device '/dev/sdx' using bmap file 'dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic.bmap'
bmaptool: info: 100% copied
bmaptool: info: synchronizing '/dev/sdx'
bmaptool: info: copying time: 13.2s, copying speed 72.5 MiB/sec
```

It is equally possible to use plain `dd` to write the decompressed
system image into the block device as follows:
```
host$ dd if=dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic \
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
/dev/mmcblk1boot0
/dev/mmcblk1boot1
```
The listing above which implies the eMMC block device is `/dev/mmcblk1`.
This section assumes that the eMMC block device is recognized as a block
device `/dev/mmcblk1`.

Install system image into the block device. It is recommended to use
[bmaptool](https://github.com/yoctoproject/bmaptool) to expedite the
installation by skipping unused blocks. It is recommended to use up
to date bmaptool version at least 3.6.0 which supports decompression
of ZSTD compressed images, otherwise it is necessary to decompress the
ZSTD compressed images first, as follows:
```
host$ zstd -d dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic.zst
```

To prevent any MBR or GPT contamination, it is recommended to erase
previous MBR and GPT partition tables:
```
host$ sgdisk -Z /dev/mmcblk1

host$ fdisk /dev/mmcblk1
Command (m for help): x                     # x to enter expert menu
Expert command (m for help): i              # i to change identifier
Enter the new disk identifier: 0x1234abcd   # pick random identifier
Expert command (m for help): r              # r to return to main menu
Command (m for help): w                     # w to write change to MBR
```

Use bmaptool to write system image into block device as follows:
```
host$ bmaptool copy --bmap \
    dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic.bmap \
    dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic.zst \
    /dev/mmcblk1
bmaptool: info: block map format version 2.0
bmaptool: info: 400474 blocks of size 4096 (1.5 GiB), mapped 246005 blocks (961.0 MiB or 61.4%)
bmaptool: info: copying image 'dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic' to block device '/dev/mmcblk1' using bmap file 'dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic.bmap'
bmaptool: info: 100% copied
bmaptool: info: synchronizing '/dev/mmcblk1'
bmaptool: info: copying time: 13.2s, copying speed 72.5 MiB/sec
```

It is equally possible to use plain `dd` to write the decompressed
system image into the block device as follows:
```
host$ dd if=dh-image-demo-dh-stm32mp1-dhcom-pdk2.rootfs.wic \
         of=/dev/mmcblk1 bs=4M
```

This system uses GPT GUID Partition Table. It is highly recommended to
randomize GUIDs after writing system image to storage to avoid identical
PARTUUID for multiple partitions on different storage media in the system.
Identical PARTUUID for multiple partitions may lead to system using an
unexpected root device, which accidentally shares the same PARTUUID. To
randomize PARTUUID, use the following command:
```
host$ sgdisk -G /dev/mmcblk1
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
root@dh-stm32mp1-dhcom-pdk2:~# tr '\0' "$(printf '\377')" < /dev/zero | dd bs=1024 count=1920 of=flash.bin
root@dh-stm32mp1-dhcom-pdk2:~# dd if=u-boot-spl.stm32 of=flash.bin conv=notrunc
root@dh-stm32mp1-dhcom-pdk2:~# dd if=u-boot-spl.stm32 of=flash.bin conv=notrunc bs=262144 seek=1
root@dh-stm32mp1-dhcom-pdk2:~# dd if=u-boot.itb       of=flash.bin conv=notrunc bs=524288 seek=1
```

To determine the MTD block device which represents the boot SPI NOR, iterate
over all of `/dev/mtdblock*` device nodes and select the one where the output
of `udevadm info /dev/mtdblockN` command matches the following output:

```
root@dh-stm32mp1-dhcom-pdk2:~# udevadm info /dev/mtdblock0
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
root@dh-stm32mp1-dhcom-pdk2:~# dd if=flash.bin of=/dev/mtdblock0
3840+0 records in
3840+0 records out
1966080 bytes (2.0 MB, 1.9 MiB) copied, 17.7132 s, 111 kB/s
```

Once the installation completed, reset the board. To perform reset from
Linux kernel, use the following command:

```
root@dh-stm32mp1-dhcom-pdk2:~# reboot
```

### U-Boot bootloader recovery (using USB OTG DFU upload)

This section depends on [dfu-util](https://dfu-util.sourceforge.net/) tool.
This tool must be installed on the host system.

In case the bootloader is damaged, malfunctioning or missing, it is still
possible to recover the system by starting a replacement bootloader using
USB OTG DFU upload.

Make sure the system is powered off and USB OTG cable is connected to port
`USB OTG` `X14`. Press and hold the button on the STM32MP15xx DHCOM SoM that
is located just above the middle part of DH electronics logo. Power on the
system.

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

Release the button on SoM.

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

- DH 460-200 SRAM board in header X11
  - `stm32mp15xx-dhcom-pdk2-overlay-460-200-x11.dtbo`
- DH 497-200 adapter card with EDT ETM0700G0EDH6 Parallel RGB display attached to it
  - `stm32mp15xx-dhcom-pdk2-overlay-497-200-x12.dtbo`
- DH 505-200 adapter card with Chefree CH101OLHLWH-002 LVDS display attached to it
  - `stm32mp15xx-dhcom-pdk2-overlay-505-200-x12-ch101olhlwh.dtbo`
- DH 531-100 SPI/I2C board in header X21
  - `stm32mp15xx-dhcom-pdk2-overlay-531-100-x21.dtbo`
- DH 531-200 SPI/I2C board in header X22
  - `stm32mp15xx-dhcom-pdk2-overlay-531-100-x22.dtbo`
- DH 560-200 7" LCD board in header X12
  - `stm32mp15xx-dhcom-pdk2-overlay-560-200-x12.dtbo`
- DH 638-100 mezzanine card with RPi 7" DSI display attached on top
  - `stm32mp15xx-dhcom-pdk2-overlay-638-100-x12-rpi7inch.dtbo`
- DH 672-100 expansion card, which contains CAN/FD transceiver and enables PDK2 to use one more CAN/FD interface
  - `stm32mp15xx-dhcom-pdk2-overlay-672-100-x18.dtbo`

## List available Device Tree Overlays (from U-Boot)

To list Device Tree Overlays supported by the current operating system image,
inspect the Linux kernel fitImage from within U-Boot as follows.

First, load the kernel fitImage from SD or eMMC into memory:
```
STM32MP> load mmc 0:4 ${loadaddr} boot/fitImage
5965452 bytes read in 250 ms (22.8 MiB/s)
```

Second, list the available fitImage configurations, which also
match the available base Device Tree and Device Tree Overlays:
```
STM32MP> iminfo $loadaddr

## Checking Image at c2000000 ...
   FIT image found
   FIT description: Kernel fitImage for DH Linux Distribution/6.12.51+git/dh-stm32mp1-dhcom-pdk2
   Created:         2025-10-06   9:17:53 UTC
    Image 0 (kernel-1)
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
    Image 1 (fdt-stm32mp157c-dhcom-pdk2.dtb)
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
    Image 2 (fdt-stm32mp15xx-dhcom-pdk2-overlay-460-200-x11.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b0a50
     Data Size:    719 Bytes = 719 Bytes
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   bd5747de783c6a9a91b31206870c3b1b3cf8a7e7e198da17639a77af3a3ad267
    Image 3 (fdt-stm32mp15xx-dhcom-pdk2-overlay-497-200-x12.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b0e28
     Data Size:    2809 Bytes = 2.7 KiB
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   5af304fe1a3a981196d9a8fb8cac78efa6bb7cbab38d38569001a7a9325e3ec2
    Image 4 (fdt-stm32mp15xx-dhcom-pdk2-overlay-531-100-x21.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b1a2c
     Data Size:    959 Bytes = 959 Bytes
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   25e640658bc885339c2f42a363eba9dbed95d9610c228ebd305f8edd34d9d8d3
    Image 5 (fdt-stm32mp15xx-dhcom-pdk2-overlay-531-100-x22.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b1ef4
     Data Size:    355 Bytes = 355 Bytes
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   e53bef24f2bd6fc50a074a077eb1c996c9efe3d57f648bab4646b8361bff285c
    Image 6 (fdt-stm32mp15xx-dhcom-pdk2-overlay-505-200-x12-ch101olhlwh.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b216c
     Data Size:    4348 Bytes = 4.2 KiB
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   7e60d36779f6efbcd39b9b8503ea016b08838f31111a2a53dcc5384174adb742
    Image 7 (fdt-stm32mp15xx-dhcom-pdk2-overlay-560-200-x12.dtbo)
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
    Image 8 (fdt-stm32mp15xx-dhcom-pdk2-overlay-638-100-x12-rpi7inch.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b4244
     Data Size:    3118 Bytes = 3 KiB
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   687f7569767b78ea4b41744d1a9b670eed34a45c10fad47ea201f702a4748fab
    Image 9 (fdt-stm32mp15xx-dhcom-pdk2-overlay-672-100-x18.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-06   9:17:53 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25b4f7c
     Data Size:    449 Bytes = 449 Bytes
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   b01470482b9ab33d7a5db24441746be9e1390e99757ebf3aa5684853c812b10e
    Default Configuration: 'conf-stm32mp157c-dhcom-pdk2.dtb'
    Configuration 0 (conf-stm32mp157c-dhcom-pdk2.dtb)
     Description:  1 Linux kernel, FDT blob
     Kernel:       kernel-1
     FDT:          fdt-stm32mp157c-dhcom-pdk2.dtb
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 1 (conf-stm32mp15xx-dhcom-pdk2-overlay-460-200-x11.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-dhcom-pdk2-overlay-460-200-x11.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 2 (conf-stm32mp15xx-dhcom-pdk2-overlay-497-200-x12.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-dhcom-pdk2-overlay-497-200-x12.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 3 (conf-stm32mp15xx-dhcom-pdk2-overlay-531-100-x21.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-dhcom-pdk2-overlay-531-100-x21.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 4 (conf-stm32mp15xx-dhcom-pdk2-overlay-531-100-x22.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-dhcom-pdk2-overlay-531-100-x22.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 5 (conf-stm32mp15xx-dhcom-pdk2-overlay-505-200-x12-ch101olhlwh.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-dhcom-pdk2-overlay-505-200-x12-ch101olhlwh.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 6 (conf-stm32mp15xx-dhcom-pdk2-overlay-560-200-x12.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-dhcom-pdk2-overlay-560-200-x12.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 7 (conf-stm32mp15xx-dhcom-pdk2-overlay-638-100-x12-rpi7inch.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-dhcom-pdk2-overlay-638-100-x12-rpi7inch.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 8 (conf-stm32mp15xx-dhcom-pdk2-overlay-672-100-x18.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp15xx-dhcom-pdk2-overlay-672-100-x18.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
## Checking hash(es) for FIT Image at c2000000 ...
   Hash(es) for Image 0 (kernel-1): sha256+
   Hash(es) for Image 1 (fdt-stm32mp157c-dhcom-pdk2.dtb): sha256+
   Hash(es) for Image 2 (fdt-stm32mp15xx-dhcom-pdk2-overlay-460-200-x11.dtbo): sha256+
   Hash(es) for Image 3 (fdt-stm32mp15xx-dhcom-pdk2-overlay-497-200-x12.dtbo): sha256+
   Hash(es) for Image 4 (fdt-stm32mp15xx-dhcom-pdk2-overlay-531-100-x21.dtbo): sha256+
   Hash(es) for Image 5 (fdt-stm32mp15xx-dhcom-pdk2-overlay-531-100-x22.dtbo): sha256+
   Hash(es) for Image 6 (fdt-stm32mp15xx-dhcom-pdk2-overlay-505-200-x12-ch101olhlwh.dtbo): sha256+
   Hash(es) for Image 7 (fdt-stm32mp15xx-dhcom-pdk2-overlay-560-200-x12.dtbo): sha256+
   Hash(es) for Image 8 (fdt-stm32mp15xx-dhcom-pdk2-overlay-638-100-x12-rpi7inch.dtbo): sha256+
   Hash(es) for Image 9 (fdt-stm32mp15xx-dhcom-pdk2-overlay-672-100-x18.dtbo): sha256+
```

The Device Tree and Device Tree Overlays lists below are based on fitImage
configuration listing printed in the example above, with the `conf-` prefix
removed.

The listing contains one base Device Tree:

- `stm32mp157c-dhcom-pdk2.dtb`

The listing contains the following Device Tree Overlays:

- `stm32mp15xx-dhcom-pdk2-overlay-460-200-x11.dtbo`
- `stm32mp15xx-dhcom-pdk2-overlay-497-200-x12.dtbo`
- `stm32mp15xx-dhcom-pdk2-overlay-531-100-x21.dtbo`
- `stm32mp15xx-dhcom-pdk2-overlay-531-100-x22.dtbo`
- `stm32mp15xx-dhcom-pdk2-overlay-505-200-x12-ch101olhlwh.dtbo`
- `stm32mp15xx-dhcom-pdk2-overlay-560-200-x12.dtbo`
- `stm32mp15xx-dhcom-pdk2-overlay-638-100-x12-rpi7inch.dtbo`
- `stm32mp15xx-dhcom-pdk2-overlay-672-100-x18.dtbo`

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

To configure the system to use `stm32mp157c-dhcom-pdk2.dtb` base Device
Tree and apply additional `stm32mp15xx-dhcom-pdk2-overlay-560-200-x12.dtbo`
Device Tree Overlay, configure the `loaddtos` environment variable as
follows:
```
                     .-------------------------------.----- '#' Separator
                     |                               |
                     v                               v
=> env set loaddtos '#conf-stm32mp157c-dhcom-pdk2.dtb#conf-stm32mp15xx-dhcom-pdk2-overlay-560-200-x12.dtbo'
                       ^                               ^
                       |                               |
                       |                               '--- 560-200 display DTO
                       |
                       '----------------------------------- Base Device Tree
```

To make configuration persistent, store U-Boot environment:
```
=> env save
```

To boot the system with additional DTO applied:
```
STM32MP> env set loaddtos '#conf-stm32mp157c-dhcom-pdk2.dtb#conf-stm32mp15xx-dhcom-pdk2-overlay-560-200-x12.dtbo'
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
   Using 'conf-stm32mp157c-dhcom-pdk2.dtb' configuration
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
   Using 'conf-stm32mp157c-dhcom-pdk2.dtb' configuration
   Trying 'fdt-stm32mp157c-dhcom-pdk2.dtb' fdt subimage
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
   Using 'conf-stm32mp15xx-dhcom-pdk2-overlay-560-200-x12.dtbo' configuration
   Trying 'fdt-stm32mp15xx-dhcom-pdk2-overlay-560-200-x12.dtbo' fdt subimage
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

U-Boot on this system provides serial console access via `RS232 UART1`
DB9 plug `X8`, this is `serial@40010000`. Other serial ports can be
made available by modifying U-Boot control Device Tree.

Linux on this system provides serial console access via `RS232 UART1`
DB9 plug `X8`, this is `40010000.serial`. Linux also provides two
additional serial ports accessible via pin header `UART2 TTL` `X6`
`40019000.serial` and `UART3 TTL` `X7` `4000f000.serial` . These ports
are accessible via `/dev/ttySTM*` device nodes. To discern the ports
apart, use `udevadm info /dev/ttySTM*` command, which prints the full
hardware path to the selected port:
```
root@dh-stm32mp1-dhcom-pdk2:~# udevadm info /dev/ttySTM*
P: /devices/platform/soc/5c007000.bus/40010000.serial/40010000.serial:0/40010000.serial:0.0/tty/ttySTM0
                                      ^^^^^^^^^^^^^^^
M: ttySTM0
   ^^^^^^^
```

To access either serial port, use suitable terminate emulator,
for example `minicom`, `picocom`, `screen`, and many others.

## IO

### Buttons

U-Boot on this system provides access to four buttons via plain GPIOs.
Use the following `gpio input` command to sample button states. Note
that `gpio input` command sets return value according to the GPIO
state, which can be used in conditional statements:

```
STM32MP> gpio input GPIOF3
gpio: pin GPIOF3 (gpio 83) value is 1
STM32MP> echo $?
1

STM32MP> gpio input GPIOF3
gpio: pin GPIOF3 (gpio 83) value is 0
STM32MP> echo $?
0
```

Linux on this system provides access to four buttons via two input event
devices, one for `gpio-keys` and one for `gpio-keys-polled`. Not all
keys can be assigned an interrupt line due to the interrupt controller
limitation of the SoC. To access GPIO keys on this platform, open the
matching `/dev/input/event*` event device.

Example using `evtest` tool:

```
root@dh-stm32mp1-dhcom-pdk2:~# evtest
No device specified, trying to scan all of /dev/input/event*
Available devices:
/dev/input/event0:      gpio-keys-polled
/dev/input/event1:      pmic_onkey
/dev/input/event2:      TSC200X touchscreen
/dev/input/event3:      gpio-keys
Select the device event number [0-3]: 0

Input driver version is 1.0.1
Input device ID: bus 0x19 vendor 0x1 product 0x1 version 0x100
Input device name: "gpio-keys-polled"
Supported events:
  Event type 0 (EV_SYN)
  Event type 1 (EV_KEY)
    Event code 30 (KEY_A)
    Event code 46 (KEY_C)
    Event code 48 (KEY_B)
Properties:
Testing ... (interrupt to exit)
Event: time 1741569958.349613, type 1 (EV_KEY), code 30 (KEY_A), value 1
Event: time 1741569958.349613, -------------- SYN_REPORT ------------
Event: time 1741569958.493591, type 1 (EV_KEY), code 30 (KEY_A), value 0
Event: time 1741569958.493591, -------------- SYN_REPORT ------------
Event: time 1741569958.925603, type 1 (EV_KEY), code 48 (KEY_B), value 1
Event: time 1741569958.925603, -------------- SYN_REPORT ------------
Event: time 1741569959.045599, type 1 (EV_KEY), code 48 (KEY_B), value 0
Event: time 1741569959.045599, -------------- SYN_REPORT ------------
Event: time 1741569959.429609, type 1 (EV_KEY), code 46 (KEY_C), value 1
Event: time 1741569959.429609, -------------- SYN_REPORT ------------
Event: time 1741569959.597607, type 1 (EV_KEY), code 46 (KEY_C), value 0
Event: time 1741569959.597607, -------------- SYN_REPORT ------------

root@dh-stm32mp1-dhcom-pdk2:~# evtest
No device specified, trying to scan all of /dev/input/event*
Available devices:
/dev/input/event0:      gpio-keys-polled
/dev/input/event1:      pmic_onkey
/dev/input/event2:      TSC200X touchscreen
/dev/input/event3:      gpio-keys
Select the device event number [0-3]: 3

Input driver version is 1.0.1
Input device ID: bus 0x19 vendor 0x1 product 0x1 version 0x100
Input device name: "gpio-keys"
Supported events:
  Event type 0 (EV_SYN)
  Event type 1 (EV_KEY)
    Event code 32 (KEY_D)
Properties:
Testing ... (interrupt to exit)
Event: time 1741569969.563722, type 1 (EV_KEY), code 32 (KEY_D), value 1
Event: time 1741569969.563722, -------------- SYN_REPORT ------------
Event: time 1741569969.675104, type 1 (EV_KEY), code 32 (KEY_D), value 0
Event: time 1741569969.675104, -------------- SYN_REPORT ------------
```

### LEDs

U-Boot on this system provides access to four LEDs via `led` command.

To list available LEDs and their current state, use the `led list` command:
```
STM32MP> led list
green:led5      off
green:led6      off
green:led7      off
green:led8      off
```

To enable an LED, use `led <led_label> on` command:
```
STM32MP> led green:led5 on
STM32MP> led green:led6 on
STM32MP> led green:led7 on
STM32MP> led green:led8 on
```

To disable an LED, use `led <led_label> off` command:
```
STM32MP> led green:led5 off
STM32MP> led green:led6 off
STM32MP> led green:led7 off
STM32MP> led green:led8 off
```

Linux on this system provides access to four LEDs via sysfs LED API.

To enable an LED, write 1 into matching sysfs LED attribute:
```
root@dh-stm32mp1-dhcom-pdk2:~# echo 1 > /sys/class/leds/green\:led5/brightness
root@dh-stm32mp1-dhcom-pdk2:~# echo 1 > /sys/class/leds/green\:led6/brightness
root@dh-stm32mp1-dhcom-pdk2:~# echo 1 > /sys/class/leds/green\:led7/brightness
root@dh-stm32mp1-dhcom-pdk2:~# echo 1 > /sys/class/leds/green\:led8/brightness
```

To disable an LED, write 0 into matching sysfs LED attribute:
```
root@dh-stm32mp1-dhcom-pdk2:~# echo 0 > /sys/class/leds/green\:led5/brightness
root@dh-stm32mp1-dhcom-pdk2:~# echo 0 > /sys/class/leds/green\:led6/brightness
root@dh-stm32mp1-dhcom-pdk2:~# echo 0 > /sys/class/leds/green\:led7/brightness
root@dh-stm32mp1-dhcom-pdk2:~# echo 0 > /sys/class/leds/green\:led8/brightness
```

### GPIOs

U-Boot on this system provides access to multiple GPIO pins.

To list GPIOs and their current state, use the `gpio status` command:
```
STM32MP> gpio status
Bank GPIOC:
GPIOC6: output: 1 [x] led-0.gpios

Bank GPIOD:
GPIOD11: output: 0 [x] led-1.gpios

Bank GPIOG:
GPIOG1: input: 0 [x] mmc@58005000.cd-gpios
GPIOG3: output: 0 [x] vioregulator.gpio

Bank GPIOH:
GPIOH3: output: 1 [x] ethernet@5800a000.phy-reset-gpios

Bank GPIOI:
GPIOI2: output: 0 [x] led-2.gpios
GPIOI3: output: 0 [x] led-3.gpios
```

To set a GPIO as Output-High, use `gpio set <pin>` command:
```
STM32MP> gpio set GPIOC6
gpio: pin GPIOC6 (gpio 38) value is 1
```

To set a GPIO as Output-Low, use `gpio clear <pin>` command:
```
STM32MP> gpio clear GPIOC6
gpio: pin GPIOC6 (gpio 38) value is 0
```

To toggle a GPIO, use `gpio toggle <pin>` command:
```
STM32MP> gpio toggle GPIOC6
gpio: pin GPIOC6 (gpio 38) value is 1
STM32MP> gpio toggle GPIOC6
gpio: pin GPIOC6 (gpio 38) value is 0
```

Linux on this system provides access to multiple GPIO pins. Access to
GPIOs is provided using GPIO chardev interface and libgpiod. Subset of
GPIO lines is named, which makes them easier to discern in libgpiod
tools output.

To list available GPIOs, use the `gpioinfo` command:
```
root@dh-stm32mp1-dhcom-pdk2:~# gpioinfo
gpiochip0 - 16 lines:
        line   0:       "PA0"                   input consumer="kernel"
        line   1:       "PA1"                   input consumer="kernel"
        line   2:       "PA2"                   input consumer="kernel"
        line   3:       "PA3"                   input
        line   4:       "PA4"                   input consumer="kernel"
        line   5:       "PA5"                   input consumer="kernel"
        line   6:       "DHCOM-K"               input
...
```

To get current state of a GPIO, use the `gpioget` command:
```
root@dh-stm32mp1-dhcom-pdk2:~# gpioget -c /dev/gpiochip0 6
"6"=inactive
...
```

To set a GPIO as Output-High, use `gpioset ... <line>=1` command:
```
root@dh-stm32mp1-dhcom-pdk2:~# gpioset -c /dev/gpiochip0 6=1
```

To set a GPIO as Output-Low, use `gpioset ... <line>=0` command:
```
root@dh-stm32mp1-dhcom-pdk2:~# gpioset -c /dev/gpiochip0 6=0
```

It is possible to refer to GPIOs using symbolic names:
```
# Numerical reference to GPIO on gpiochip0 line 6:
root@dh-stm32mp1-dhcom-pdk2:~# gpioget -c /dev/gpiochip0 6
"6"=inactive

# Symbolic name reference to GPIO on gpiochip0 line 6:
root@dh-stm32mp1-dhcom-pdk2:~# gpioget DHCOM-K
"DHCOM-K"=inactive
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
root@dh-stm32mp1-dhcom-pdk2:~# cat /sys/devices/platform/soc/58005000.mmc/mmc_host/mmc*/mmc*/block/mmcblk*/dev
179:24
^^^ ^^

root@dh-stm32mp1-dhcom-pdk2:~# ls -la /dev/mmcblk*
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
which are represented by device node `/dev/mmcblk1`:
```
root@dh-stm32mp1-dhcom-pdk2:~# cat /sys/devices/platform/soc/58007000.mmc/mmc_host/mmc*/mmc*/block/mmcblk*/dev
179:0
^^^ ^

root@dh-stm32mp1-dhcom-pdk2:~# ls -la /dev/mmcblk*
...
brw-rw---- 1 root disk 179,  0 Mar 10 03:53 /dev/mmcblk1
                       ^^^   ^              ^^^^^^^^^^^^
...
```

### Full-size SD

Full size SD card slot is accessible only on systems with customized
STM32MP15xx DHCOM SoM without populated WiFi module. On such systems,
the full size SD card is accessible using controller `48004000.mmc` .

## Network

### Ethernet

U-Boot on this system provides access to two ethernet interfaces via U-Boot
networking stack. Use `net list` command to list available ethernet interfaces:
```
STM32MP> net list
eth1 : ethernet@1,0 00:11:22:33:44:55
eth0 : ethernet@5800a000 00:11:22:33:44:55 active
```

To select active ethernet interface, set U-Boot environment variable `ethact`.

Linux on this system provides access to two ethernet interfaces via Linux
networking stack. These interfaces are assigned deterministic interface
names via systemd udevd rules.

```
root@dh-stm32mp1-dhcom-pdk2:~# ip addr show
...
4: ethsom0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 ...
5: ethsom1: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 ...
...
```

To discern the ports apart, use `udevadm info /sys/class/net/ethsom*` command,
which prints the full hardware path to the selected ethernet interface. The
default assignment `5800a000.ethernet` is on-SoC ethernet, `64000000.ethernet`
is additional KSZ8851-16MLL on-SoM ethernet:

```
root@dh-stm32mp1-dhcom-pdk2:~# udevadm info /sys/class/net/ethsom0
P: /devices/platform/soc/5c007000.bus/5800a000.ethernet/net/ethsom0
M: ethsom0
...

root@dh-stm32mp1-dhcom-pdk2:~# udevadm info /sys/class/net/ethsom1
P: /devices/platform/soc/5c007000.bus/58002000.memory-controller/64000000.ethernet/net/ethsom1
M: ethsom1
...
```

### WiFi and Bluetooth

Linux on this system provides access to WiFi interface via Linux networking
stack. This interface is assigned deterministic interface name via systemd
udevd rules.

```
root@dh-stm32mp1-dhcom-pdk2:~# udevadm info /sys/class/net/wlansom0
P: /devices/platform/soc/5c007000.bus/48004000.mmc/mmc_host/mmc1/mmc1:fffd/mmc1:fffd:1/net/wlansom0
M: wlansom0
...
```

To operate the WiFi interface, use `iw` and `wpa-supplicant` tools, or any
other suitable network management tooling. Example scan for nearby networks:

```
root@dh-stm32mp1-dhcom-pdk2:~# ip link set wlansom0 up
root@dh-stm32mp1-dhcom-pdk2:~# iw dev wlansom0 scan
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

This system provides one USB host port connected to a USB 2.0 hub located
on the carrier board. Access to the USB hub ports is possible via stacked
USB connector `X13`.

This system provides one USB OTG port connected to a mini-USB connector `X14`.

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
root@dh-stm32mp1-dhcom-pdk2:~# lsusb
Bus 001 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub
Bus 002 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub
Bus 002 Device 002: ID 0424:2514 Microchip Technology, Inc. (formerly SMSC) USB 2.0 Hub
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
root@dh-stm32mp1-dhcom-pdk2:~# modprobe g_zero
zero gadget.0: Gadget Zero, version: Cinco de Mayo 2008
zero gadget.0: zero ready
dwc2 49000000.usb-otg: bound driver zero
```

Connect USB A-to-miniB cable between the board USB OTG port and host PC.

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
