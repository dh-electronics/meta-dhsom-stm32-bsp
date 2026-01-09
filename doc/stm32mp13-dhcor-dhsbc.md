STM32MP13xx DHCOR SoM on DHSBC carrier board
============================================

# Initial board setup

This section explains how to perform initial hardware setup, how to
assemble the hardware, which cables to connect to which ports, how to
power the hardware on, and finally how to obtain serial console access.

## Connect serial console cable

Connect serial console 3.3V UART cable into `UART4` 6-pin connector
`X9`. This is LVTTL UART 3.3V voltage level connection. The pinout is
`1-2-3-4-5-6=GND-NC-VCC-RX-TX-NC` , pin 1 is closer to board center.

## Connect ethernet cables (optional)

Connect up to two ethernet cables to ethernet jacks `X4` and `X5`.
The ethernet jack closer to device corner is connected to SoC MAC 0.
The ethernet jack closer to device center is connected to SoC MAC 1.

## Connect USB OTG cables (optional)

Connect USB-C cable between host PC and carrier board into plug `X2` `Data`.

## Connect power supply

Connect USB-C PD capable power supply into USB-C plug `X1` `Power`.

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

The operating system image can be installed into eMMC USER hardware partition.

The operating system image installation into either media can be performed
from within U-Boot itself using USB OTG UMS upload, or from running Linux
userspace.

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
STM32MP> ums 0 mmc 0
```

The USB mass storage device will now enumerate on the host PC as follows:
```
host$ dmesg -w
usb 5-2.2.2.1: new high-speed USB device number 106 using xhci_hcd
usb 5-2.2.2.1: New USB device found, idVendor=0483, idProduct=df11, bcdDevice= 2.00
usb 5-2.2.2.1: New USB device strings: Mfr=1, Product=2, SerialNumber=3
usb 5-2.2.2.1: Product: USB download gadget
usb 5-2.2.2.1: Manufacturer: dh
usb 5-2.2.2.1: SerialNumber: 003800000000000012345678
usb-storage 5-2.2.2.1:1.0: USB Mass Storage device detected
scsi host3: usb-storage 5-2.2.2.1:1.0
scsi 3:0:0:0: Direct-Access     Linux    UMS disk 0       ffff PQ: 0 ANSI: 2
sd 3:0:0:0: Attached scsi generic sg4 type 0
sd 3:0:0:0: [sdx] 7634944 512-byte logical blocks: (3.91 GB/3.64 GiB)
sd 3:0:0:0: [sdx] Write Protect is off
sd 3:0:0:0: [sdx] Mode Sense: 0f 00 00 00
sd 3:0:0:0: [sdx] Write cache: enabled, read cache: enabled, doesn't support DPO or FUA
GPT:Primary header thinks Alt. header is not at the end of the disk.
GPT:4540601 != 7634943
GPT:Alternate GPT header not at the end of the disk.
GPT:4540601 != 7634943
GPT: Use GNU Parted to correct GPT errors.
 sdx: sdx1 sdx2
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
host$ zstd -d dh-image-demo-dh-stm32mp13-dhcor-dhsbc.rootfs.wic.zst
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
    dh-image-demo-dh-stm32mp13-dhcor-dhsbc.rootfs.wic.bmap \
    dh-image-demo-dh-stm32mp13-dhcor-dhsbc.rootfs.wic.zst \
    /dev/sdx
bmaptool: info: block map format version 2.0
bmaptool: info: 400474 blocks of size 4096 (1.5 GiB), mapped 246005 blocks (961.0 MiB or 61.4%)
bmaptool: info: copying image 'dh-image-demo-dh-stm32mp13-dhcor-dhsbc.rootfs.wic' to block device '/dev/sdx' using bmap file 'dh-image-demo-dh-stm32mp13-dhcor-dhsbc.rootfs.wic.bmap'
bmaptool: info: 100% copied
bmaptool: info: synchronizing '/dev/sdx'
bmaptool: info: copying time: 2m 14.3s, copying speed 10.3 MiB/sec
```

It is equally possible to use plain `dd` to write the decompressed
system image into the block device as follows:
```
host$ dd if=dh-image-demo-dh-stm32mp13-dhcor-dhsbc.rootfs.wic \
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

#### Operating system image installation into eMMC (from Linux)

Boot into the Linux operation system image and reach userspace. It is
mandatory that the system is booted from USB stick or NFS root and
specifically not booted from eMMC.

To determine which block device is the eMMC, list block devices with
eMMC BOOT hardware partitions as follows:
```
$ ls -1 /dev/mmcblk0boot*
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
host$ zstd -d dh-image-demo-dh-stm32mp13-dhcor-dhsbc.rootfs.wic.zst
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
    dh-image-demo-dh-stm32mp13-dhcor-dhsbc.rootfs.wic.bmap \
    dh-image-demo-dh-stm32mp13-dhcor-dhsbc.rootfs.wic.zst \
    /dev/mmcblk0
bmaptool: info: block map format version 2.0
bmaptool: info: 400474 blocks of size 4096 (1.5 GiB), mapped 246005 blocks (961.0 MiB or 61.4%)
bmaptool: info: copying image 'dh-image-demo-dh-stm32mp13-dhcor-dhsbc.rootfs.wic' to block device '/dev/mmcblk0' using bmap file 'dh-image-demo-dh-stm32mp13-dhcor-dhsbc.rootfs.wic.bmap'
bmaptool: info: 100% copied
bmaptool: info: synchronizing '/dev/mmcblk0'
bmaptool: info: copying time: 2m 14.3s, copying speed 10.3 MiB/sec
```

It is equally possible to use plain `dd` to write the decompressed
system image into the block device as follows:
```
host$ dd if=dh-image-demo-dh-stm32mp13-dhcor-dhsbc.rootfs.wic \
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

This section explains how to perform bootloader installation, update and
recovery. This section is only applicable in case it is desireable to
replace bootloader on the system.

The U-Boot bootloader is installed into SPI NOR or eMMC. Installation into
SPI NOR is recommended to prevent accidental overwrite of the bootloader
while the system software in eMMC is being replaced or updated. U-Boot
bootloader installation can be performed from within U-Boot itself, using
USB OTG DFU upload or from running Linux userspace.

The SPI NOR layout on this platform is as follows:
```
0x00_0000..0x03_ffff ... U-Boot SPL (copy 1)
0x04_0000..0x07_ffff ... U-Boot SPL (copy 2)
0x08_0000..0x3d_ffff ... U-Boot fitImage
0x3e_0000..0x1e_ffff ... U-Boot environment (copy 1)
0x3f_0000..0x1f_ffff ... U-Boot environment (copy 2)
```

The boot media is selected using `BOOT_CONFIG` DIP switches located near
the POWER and RESET buttons, close to the board edge. The following boot
modes are supported in this documentation:

- `BOOT[1:3]=ON-off-off` ... SPI NOR boot
- `BOOT[1:3]=off-ON-off` ... eMMC boot
- `BOOT[1:3]=ON-ON-ON` ..... USB DFU boot

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

Found DFU: [0483:df11] ver=0200, devnum=23, cfg=1, intf=0, path="5-2.2.2.3", alt=13, name="PMIC", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=23, cfg=1, intf=0, path="5-2.2.2.3", alt=12, name="OTP", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=23, cfg=1, intf=0, path="5-2.2.2.3", alt=11, name="nor0_u-boot-env-b", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=23, cfg=1, intf=0, path="5-2.2.2.3", alt=10, name="nor0_u-boot-env-a", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=23, cfg=1, intf=0, path="5-2.2.2.3", alt=9, name="nor0_u-boot", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=23, cfg=1, intf=0, path="5-2.2.2.3", alt=8, name="nor0_fsbl2", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=23, cfg=1, intf=0, path="5-2.2.2.3", alt=7, name="nor0_fsbl1", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=23, cfg=1, intf=0, path="5-2.2.2.3", alt=6, name="nor0", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=23, cfg=1, intf=0, path="5-2.2.2.3", alt=5, name="mmc0_fit", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=23, cfg=1, intf=0, path="5-2.2.2.3", alt=4, name="mmc0_boot2", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=23, cfg=1, intf=0, path="5-2.2.2.3", alt=3, name="mmc0_boot1", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=23, cfg=1, intf=0, path="5-2.2.2.3", alt=2, name="uramdisk.image.gz", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=23, cfg=1, intf=0, path="5-2.2.2.3", alt=1, name="devicetree.dtb", serial="003800000000000012345678"
Found DFU: [0483:df11] ver=0200, devnum=23, cfg=1, intf=0, path="5-2.2.2.3", alt=0, name="uImage", serial="003800000000000012345678"
```

Install bootloader into SPI NOR using dfu-util as follows. The bootloader
consists of two components, TFA `tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1`
and OpTee-OS and U-Boot `fip.bin-stm32mp1`, and each must be written into
correct location in SPI NOR. The TFA file is written into two locations,
`nor0_fsbl1` (copy 1) and `nor0_fsbl2` (copy 2). The OpTee-OS and U-Boot
is written into one location `nor0_u-boot` . The bootloader installation
consists of three `dfu-util` invocations in total:
```
hostpc$ dfu-util -w -a 'nor_fsbl1' -D tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1
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
Setting Alternate Interface #7 ...
Determining device status...
DFU state(2) = dfuIDLE, status(0) = No error condition is present
DFU mode device DFU version 0110
Device returned transfer size 4096
Copying data from PC to DFU device
Download        [=========================] 100%        80382 bytes
Download done.
DFU state(7) = dfuMANIFEST, status(0) = No error condition is present
DFU state(2) = dfuIDLE, status(0) = No error condition is present
Done!
```

```
hostpc$ dfu-util -w -a 'nor_fsbl2' -D tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1
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
Setting Alternate Interface #8 ...
Determining device status...
DFU state(2) = dfuIDLE, status(0) = No error condition is present
DFU mode device DFU version 0110
Device returned transfer size 4096
Copying data from PC to DFU device
Download        [=========================] 100%        80382 bytes
Download done.
DFU state(7) = dfuMANIFEST, status(0) = No error condition is present
DFU state(2) = dfuIDLE, status(0) = No error condition is present
Done!
```

```
hostpc$ dfu-util -w -a 'nor0_u-boot' -D fip.bin-stm32mp1
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
Setting Alternate Interface #9 ...
Determining device status...
DFU state(2) = dfuIDLE, status(0) = No error condition is present
DFU mode device DFU version 0110
Device returned transfer size 4096
Copying data from PC to DFU device
Download        [=========================] 100%      1396006 bytes
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

### U-Boot bootloader installation into SPI NOR (from U-Boot from eMMC card)

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
from bootloader binaries located on eMMC card.

Read bootloader update from eMMC and write to SPI NOR:
```
STM32MP> setenv dh_update_iface mmc && \
         setenv dh_update_dev 0:2 && \
         setexpr loadaddr1 ${loadaddr} + 0x1000000 && \
         load ${dh_update_iface} ${dh_update_dev} ${loadaddr1} \
              /boot/tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1 && \
         env set filesize1 ${filesize} && \
         load ${dh_update_iface} ${dh_update_dev} ${loadaddr} \
              /boot/fip.bin-stm32mp1 && \
         sf probe && \
         sf erase 0 0x200000 && \
         sf update ${loadaddr1} 0 ${filesize1} && \
         sf update ${loadaddr1} 0x40000 ${filesize1} && \
         sf update ${loadaddr} 0x80000 ${filesize} && \
         env set filesize1 && env set loadaddr1
80382 bytes read in 13 ms (14.2 MiB/s)
1396006 bytes read in 25 ms (35.9 MiB/s)
SF: Detected w25q16cl with page size 256 Bytes, erase size 4 KiB, total 2 MiB
SF: 2097152 bytes @ 0x0 Erased: OK
device 0 offset 0x0, size 0x139fe
80382 bytes written, 0 bytes skipped in 1.977s, speed 100322 B/s
device 0 offset 0x40000, size 0x139fe
80382 bytes written, 0 bytes skipped in 2.29s, speed 97753 B/s
device 0 offset 0x80000, size 0x154d26
1396006 bytes written, 8192 bytes skipped in 9.725s, speed 99194 B/s
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

To assemble suitable SPI NOR image with the TFA placed at offsets 0 kiB
and 256 kiB from the start of SPI NOR, and OpTee-OS and U-Boot placed
at offset 512 kiB from the start of SPI NOR, use the following commands.
The TFA is located in `tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1` file,
the OpTee-OS U-Boot fitImage is located in `fip.bin-stm32mp1` file.

```
root@dh-stm32mp13-dhcor-dhsbc:~# tr '\0' "$(printf '\377')" < /dev/zero | dd bs=1024 count=1920 of=flash.bin
root@dh-stm32mp13-dhcor-dhsbc:~# dd if=tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1 of=flash.bin conv=notrunc
root@dh-stm32mp13-dhcor-dhsbc:~# dd if=tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1 of=flash.bin conv=notrunc bs=262144 seek=1
root@dh-stm32mp13-dhcor-dhsbc:~# dd if=fip.bin-stm32mp1 of=flash.bin conv=notrunc bs=524288 seek=1
```

To determine the MTD block device which represents the boot SPI NOR, iterate
over all of `/dev/mtdblock*` device nodes and select the one where the output
of `udevadm info /dev/mtdblockN` command matches the following output:

```
root@dh-stm32mp13-dhcor-dhsbc:~# udevadm info /dev/mtdblock0
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
root@dh-stm32mp13-dhcor-dhsbc:~# dd if=flash.bin of=/dev/mtdblock0
3840+0 records in
3840+0 records out
1966080 bytes (2.0 MB, 1.9 MiB) copied, 17.7132 s, 111 kB/s
```

Once the installation completed, reset the board. To perform reset from
Linux kernel, use the following command:

```
root@dh-stm32mp13-dhcor-dhsbc:~# reboot
```

### U-Boot bootloader installation into eMMC (from U-Boot using USB UMS)

Power on the system. Enter U-Boot console by pressing any key at prompt:
```
Hit any key to stop autoboot:
```

The system drops into U-Boot shell instantly. U-Boot shell on this system
looks as follows:
```
STM32MP>
```

Install bootloader into eMMC using UMS as follows. The bootloader consists
of two components, TFA `tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1` and
OpTee-OS and U-Boot `fip.bin-stm32mp1`, and each must be written into the
correct location in eMMC. The TFA file is written into two locations, the
eMMC HW partitions BOOT0 and BOOT1. The OpTee-OS and U-Boot is written into
one location, eMMC HW partition USER SW partition `fip`. The bootloader
installation consists of three UMS invocations in total:

Use the following command to start UMS on USB OTG port and export eMMC HW
BOOT0 partition as USB Mass Storage device.
```
STM32MP> ums 0 mmc 0.1
```

The USB mass storage device will now enumerate on the host PC as follows:
```
host$ dmesg -w
usb 5-2.2.2.7: new high-speed USB device number 37 using xhci_hcd
usb 5-2.2.2.7: New USB device found, idVendor=0483, idProduct=5720, bcdDevice= 2.00
usb 5-2.2.2.7: New USB device strings: Mfr=1, Product=2, SerialNumber=3
usb 5-2.2.2.7: Product: USB download gadget
usb 5-2.2.2.7: Manufacturer: dh
usb 5-2.2.2.7: SerialNumber: 002F00203331510134323331
usb-storage 5-2.2.2.7:1.0: USB Mass Storage device detected
scsi host3: usb-storage 5-2.2.2.7:1.0
scsi 3:0:0:0: Direct-Access     Linux    UMS disk 0       ffff PQ: 0 ANSI: 2
sd 3:0:0:0: Attached scsi generic sg4 type 0
sd 3:0:0:0: [sdx] 8192 512-byte logical blocks: (4.19 MB/4.00 MiB)
sd 3:0:0:0: [sdx] Write Protect is off
sd 3:0:0:0: [sdx] Mode Sense: 0f 00 00 00
sd 3:0:0:0: [sdx] Write cache: enabled, read cache: enabled, doesn't support DPO or FUA
sd 3:0:0:0: [sdx] Attached SCSI removable disk
```

Notice that in the aforementioned case, the device is enumerated as block
device `/dev/sdx` on the host PC. This may differ on other host PCs, make
sure the correct block device is used for image installation below. Using
incorrect block device may lead to data loss on the host PC.

Write the TFA file into eMMC HW BOOT0 partition:
```
host$ dd if=tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1 of=/dev/sdx
```

Once the write completed, terminate UMS from U-Boot console by
pressing `Ctrl + C`. It is also possible to terminate UMS from
the host side using the `eject` command.

Use the following command to start UMS on USB OTG port and export eMMC HW
BOOT1 partition as USB Mass Storage device.
```
STM32MP> ums 0 mmc 0.2
```

The USB mass storage device will now enumerate on the host PC as follows:
```
host$ dmesg -w
usb 5-2.2.2.7: new high-speed USB device number 37 using xhci_hcd
usb 5-2.2.2.7: New USB device found, idVendor=0483, idProduct=5720, bcdDevice= 2.00
usb 5-2.2.2.7: New USB device strings: Mfr=1, Product=2, SerialNumber=3
usb 5-2.2.2.7: Product: USB download gadget
usb 5-2.2.2.7: Manufacturer: dh
usb 5-2.2.2.7: SerialNumber: 002F00203331510134323331
usb-storage 5-2.2.2.7:1.0: USB Mass Storage device detected
scsi host3: usb-storage 5-2.2.2.7:1.0
scsi 3:0:0:0: Direct-Access     Linux    UMS disk 0       ffff PQ: 0 ANSI: 2
sd 3:0:0:0: Attached scsi generic sg4 type 0
sd 3:0:0:0: [sdx] 8192 512-byte logical blocks: (4.19 MB/4.00 MiB)
sd 3:0:0:0: [sdx] Write Protect is off
sd 3:0:0:0: [sdx] Mode Sense: 0f 00 00 00
sd 3:0:0:0: [sdx] Write cache: enabled, read cache: enabled, doesn't support DPO or FUA
sd 3:0:0:0: [sdx] Attached SCSI removable disk
```

Notice that in the aforementioned case, the device is enumerated as block
device `/dev/sdx` on the host PC. This may differ on other host PCs, make
sure the correct block device is used for image installation below. Using
incorrect block device may lead to data loss on the host PC.

Write the TFA file into eMMC HW BOOT1 partition:
```
host$ dd if=tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1 of=/dev/sdx
```

Once the write completed, terminate UMS from U-Boot console by
pressing `Ctrl + C`. It is also possible to terminate UMS from
the host side using the `eject` command.

Use the following command to start UMS on USB OTG port and export eMMC HW
USER partition as USB Mass Storage device.
```
STM32MP> ums 0 mmc 0
```

The USB mass storage device will now enumerate on the host PC as follows:
```
host$ dmesg -w
usb 5-2.2.2.7: new high-speed USB device number 38 using xhci_hcd
usb 5-2.2.2.7: New USB device found, idVendor=0483, idProduct=5720, bcdDevice= 2.00
usb 5-2.2.2.7: New USB device strings: Mfr=1, Product=2, SerialNumber=3
usb 5-2.2.2.7: Product: USB download gadget
usb 5-2.2.2.7: Manufacturer: dh
usb 5-2.2.2.7: SerialNumber: 002F00203331510134323331
usb-storage 5-2.2.2.7:1.0: USB Mass Storage device detected
scsi host3: usb-storage 5-2.2.2.7:1.0
scsi 3:0:0:0: Direct-Access     Linux    UMS disk 0       ffff PQ: 0 ANSI: 2
sd 3:0:0:0: Attached scsi generic sg4 type 0
sd 3:0:0:0: [sdx] 7634944 512-byte logical blocks: (3.91 GB/3.64 GiB)
sd 3:0:0:0: [sdx] Write Protect is off
sd 3:0:0:0: [sdx] Mode Sense: 0f 00 00 00
sd 3:0:0:0: [sdx] Write cache: enabled, read cache: enabled, doesn't support DPO or FUA
GPT:Primary header thinks Alt. header is not at the end of the disk.
GPT:4540601 != 7634943
GPT:Alternate GPT header not at the end of the disk.
GPT:4540601 != 7634943
GPT: Use GNU Parted to correct GPT errors.
 sdx: sdx1 sdx2
sd 3:0:0:0: [sdx] Attached SCSI removable disk
```

Notice that in the aforementioned case, the device is enumerated as block
device `/dev/sdx` on the host PC. This may differ on other host PCs, make
sure the correct block device is used for image installation below. Using
incorrect block device may lead to data loss on the host PC.

Write the OpTee-OS and U-Boot file into eMMC HW USER partition SW partition `fip`:
```
host$ dd if=fip.bin-stm32mp1 of=/dev/sdx1
```

Once the write completed, terminate UMS from U-Boot console by
pressing `Ctrl + C`. It is also possible to terminate UMS from
the host side using the `eject` command.

To reset the board, either press the `RESET` button on the board,
or perform reset from U-Boot shell as follows:

```
STM32MP> reset
```

Once the board resets, new U-Boot bootloader version will start.

### U-Boot bootloader installation into eMMC (from U-Boot from eMMC card)

Power on the system. Enter U-Boot console by pressing any key at prompt:
```
Hit any key to stop autoboot:
```

The system drops into U-Boot shell instantly. U-Boot shell on this system
looks as follows:
```
STM32MP>
```

Use either of the following U-Boot scripts from update U-Boot in eMMC
from bootloader binaries located on eMMC card.

Read bootloader update from eMMC and write to eMMC:
```
STM32MP> setenv dh_update_iface mmc && \
         setenv dh_update_dev 0:2 && \
         setexpr loadaddr1 ${loadaddr} + 0x1000000 && \
         load ${dh_update_iface} ${dh_update_dev} ${loadaddr1} \
              /boot/tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1 && \
         setexpr filesize1 ${filesize} + 0x1ff && \
         setexpr filesize1 ${filesize1} / 0x200 && \
         load ${dh_update_iface} ${dh_update_dev} ${loadaddr} \
              /boot/fip.bin-stm32mp1 && \
         setexpr filesize ${filesize} + 0x1ff && \
         setexpr filesize ${filesize} / 0x200 && \
         mmc dev 0 1 && \
         mmc write ${loadaddr1} 0 ${filesize1} && \
         mmc dev 0 2 && \
         mmc write ${loadaddr1} 0 ${filesize1} && \
         mmc dev 0 && \
         part start mmc 0 fip partoff && \
         mmc write ${loadaddr} ${partoff} ${filesize} && \
         env set filesize1 && env set loadaddr1 && env set partoff
80382 bytes read in 3 ms (25.6 MiB/s)
1396110 bytes read in 30 ms (44.4 MiB/s)
switch to partitions #1, OK
mmc0(part 1) is current device
MMC write: dev # 0, block # 0, count 157 ... 157 blocks written: OK
switch to partitions #2, OK
mmc0(part 2) is current device
MMC write: dev # 0, block # 0, count 157 ... 157 blocks written: OK
switch to partitions #0, OK
mmc0(part 0) is current device
MMC write: dev # 0, block # 34, count 2727 ... 2727 blocks written: OK
```

Note that the bootloader update may remove old U-Boot environment. In case
the old environment contains any useful content, it is recommended to back
up that content at this point.

Once the installation completed, reset the board. To reset the board, either
press the `RESET` button on the board, or perform reset from U-Boot shell as
follows:

```
STM32MP> reset
```

### U-Boot bootloader installation into eMMC (from Linux)

It is possible to update the bootloader from a running Linux userspace.
The eMMC is exposed as a block device by the Linux kernel, both eMMC HW
partitions and SW partitions are exposed as block devices too, therefore
it is necessary to write bootloader components into separate block devices.

The TFA is located in `tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1` file
and has to be written into eMMC HW BOOT0 and BOOT1 partitions, the OpTee-OS
and U-Boot is located in `fip.bin-stm32mp1` file and has to be written into
eMMC HW USER partition SW partition `fip`. The eMMC HW BOOT0 and BOOT1
partition access have to be unlocked before write, and should be re-locked
after write:

```
root@dh-stm32mp13-dhcor-dhsbc:~# echo 0 > /sys/devices/platform/soc/5c007000.bus/58007000.mmc/mmc_host/mmc0/mmc0:0001/block/mmcblk0/mmcblk0boot0/force_ro
root@dh-stm32mp13-dhcor-dhsbc:~# echo 0 > /sys/devices/platform/soc/5c007000.bus/58007000.mmc/mmc_host/mmc0/mmc0:0001/block/mmcblk0/mmcblk0boot1/force_ro

root@dh-stm32mp13-dhcor-dhsbc:~# dd if=/boot/tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1 of=/dev/disk/by-path/platform-5c007000.bus-amba-58007000.mmc-boot0
root@dh-stm32mp13-dhcor-dhsbc:~# dd if=/boot/tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1 of=/dev/disk/by-path/platform-5c007000.bus-amba-58007000.mmc-boot1
root@dh-stm32mp13-dhcor-dhsbc:~# dd if=/boot/fip.bin-stm32mp1 of=/dev/disk/by-path/platform-5c007000.bus-amba-58007000.mmc-part1

root@dh-stm32mp13-dhcor-dhsbc:~# echo 1 > /sys/devices/platform/soc/5c007000.bus/58007000.mmc/mmc_host/mmc0/mmc0:0001/block/mmcblk0/mmcblk0boot0/force_ro
root@dh-stm32mp13-dhcor-dhsbc:~# echo 1 > /sys/devices/platform/soc/5c007000.bus/58007000.mmc/mmc_host/mmc0/mmc0:0001/block/mmcblk0/mmcblk0boot1/force_ro
```

Once the installation completed, reset the board. To perform reset from
Linux kernel, use the following command:

```
root@dh-stm32mp13-dhcor-dhsbc:~# reboot
```

### U-Boot bootloader recovery (using USB OTG DFU upload)

This section depends on [dfu-util](https://dfu-util.sourceforge.net/) tool.
This tool must be installed on the host system.

In case the bootloader is damaged, malfunctioning or missing, it is still
possible to recover the system by starting a replacement bootloader using
USB OTG DFU upload.

Make sure the system is powered off and USB OTG cable is connected to port
`X2` `Data`. Switch `BOOT_CONFIG` DIP switches to `BOOT[1:3]=ON-ON-ON`.
Power on the system.

The system will now enumerate on the host PC as follows:
```
host$ dmesg -w
usb 5-2.2.2.1: new high-speed USB device number 27 using xhci_hcd
usb 5-2.2.2.1: New USB device found, idVendor=0483, idProduct=df11, bcdDevice= 2.00
usb 5-2.2.2.1: New USB device strings: Mfr=1, Product=2, SerialNumber=3
usb 5-2.2.2.1: Product: DFU in HS Mode @Device ID /0x501, @Revision ID /0x1003
usb 5-2.2.2.1: Manufacturer: STMicroelectronics
usb 5-2.2.2.1: SerialNumber: 003800000000000012345678
```

Use `dfu-util` to send TFA binary using DFU via USB OTG to the SoC:
```
host$ dfu-util -w -a '@FSBL /0x01/1*128Ke' -D tf-a-stm32mp135f-dhcor-dhsbc.stm32-stm32mp1 -R
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
Device returned transfer size 1024
Copying data from PC to DFU device
Download        [=========================] 100%        80382 bytes
Download done.
DFU state(7) = dfuMANIFEST, status(0) = No error condition is present
DFU state(2) = dfuIDLE, status(0) = No error condition is present
Done!
Resetting USB to switch back to Run-Time mode
```

The system will now disconnect from the host PC and re-enumerate on the
host PC as follows:
```
host$ dmesg -w
usb 5-2.2.2.1: USB disconnect, device number 31
usb 5-2.2.2.1: new high-speed USB device number 32 using xhci_hcd
usb 5-2.2.2.1: New USB device found, idVendor=0483, idProduct=df11, bcdDevice=7e.8a
usb 5-2.2.2.1: New USB device strings: Mfr=1, Product=2, SerialNumber=0
usb 5-2.2.2.1: Product: USB download gadget@Device ID /0x501, @Revision ID /0x1003, @Name /STM32MP135F Rev.Y,
usb 5-2.2.2.1: Manufacturer: dh
```

Use `dfu-util` to send OpTee-OS and U-Boot using DFU via USB OTG to the SoC:
```
host$ dfu-util -w -a '@SSBL /0x03/1*16Me' -D fip.bin-stm32mp1 -R
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
Device returned transfer size 1024
Copying data from PC to DFU device
Download        [=========================] 100%      1396006 bytes
Download done.
DFU state(7) = dfuMANIFEST, status(0) = No error condition is present
DFU state(2) = dfuIDLE, status(0) = No error condition is present
Done!
Resetting USB to switch back to Run-Time mode
```

At this point, the uploaded U-Boot will start on the system and would
become accessible via serial console. Perform regular U-Boot bootloader
installation into SPI NOR procedure as documented above to recover the
system.

Once the procedure is complete, switch `BOOT_CONFIG` DIP switches to
`BOOT[1:3]=ON-off-off` for SPI NOR boot or `BOOT[1:3]=off-ON-off` for
eMMC boot.

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

- joy-IT RB-TFT3.2-V2 240x320 SPI LCD and XPT2046 resistive touch controller
  - `stm32mp13xx-dhcor-dhsbc-overlay-rb-tft32-v2.dtbo`

## List available Device Tree Overlays (from U-Boot)

To list Device Tree Overlays supported by the current operating system image,
inspect the Linux kernel fitImage from within U-Boot as follows.

First, load the kernel fitImage from eMMC into memory:
```
STM32MP> load mmc 0:2 ${loadaddr} boot/fitImage
5933324 bytes read in 129 ms (43.9 MiB/s)
```

Second, list the available fitImage configurations, which also
match the available base Device Tree and Device Tree Overlays:
```
STM32MP> iminfo $loadaddr

## Checking Image at c2000000 ...
   FIT image found
   FIT description: Kernel fitImage for DH Linux Distribution/6.12.53+git/dh-stm32mp13-dhcor-dhsbc
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
    Image 1 (fdt-stm32mp135f-dhcor-dhsbc.dtb)
     Description:  Flattened Device Tree blob
     Created:      2025-10-15  10:00:25 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc2597790
     Data Size:    65990 Bytes = 64.4 KiB
     Architecture: ARM
     Load Address: 0xcff00000
     Hash algo:    sha256
     Hash value:   93d9d7a7e4032aa5fb4daf95a90f6148a49c4760a41a97281083101914cacb5f
    Image 2 (fdt-stm32mp13xx-dhcor-dhsbc-overlay-rb-tft32-v2.dtbo)
     Description:  Flattened Device Tree blob
     Created:      2025-10-15  10:00:25 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25a7a64
     Data Size:    2161 Bytes = 2.1 KiB
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   3b50da5c66b4cc20a919ddd750be3020947dc7ac06632609bcfc95b2fe3d5fb3
    Default Configuration: 'conf-stm32mp135f-dhcor-dhsbc.dtb'
    Configuration 0 (conf-stm32mp135f-dhcor-dhsbc.dtb)
     Description:  1 Linux kernel, FDT blob
     Kernel:       kernel-1
     FDT:          fdt-stm32mp135f-dhcor-dhsbc.dtb
     Hash algo:    sha256
     Hash value:   unavailable
    Configuration 1 (conf-stm32mp13xx-dhcor-dhsbc-overlay-rb-tft32-v2.dtbo)
     Description:  0 FDT blob
     Kernel:       unavailable
     FDT:          fdt-stm32mp13xx-dhcor-dhsbc-overlay-rb-tft32-v2.dtbo
     Hash algo:    sha256
     Hash value:   unavailable
## Checking hash(es) for FIT Image at c2000000 ...
   Hash(es) for Image 0 (kernel-1): sha256+
   Hash(es) for Image 1 (fdt-stm32mp135f-dhcor-dhsbc.dtb): sha256+
   Hash(es) for Image 2 (fdt-stm32mp13xx-dhcor-dhsbc-overlay-rb-tft32-v2.dtbo): sha256+
```

The Device Tree and Device Tree Overlays lists below are based on fitImage
configuration listing printed in the example above, with the `conf-` prefix
removed.

The listing contains one base Device Tree:

- `stm32mp135f-dhcor-dhsbc.dtb`

The listing contains the following Device Tree Overlays:

- `stm32mp13xx-dhcor-dhsbc-overlay-rb-tft32-v2.dtbo`

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

To configure the system to use `stm32mp135f-dhcor-dhsbc.dtb` base Device
Tree and apply additional `stm32mp13xx-dhcor-dhsbc-overlay-rb-tft32-v2.dtbo`
Device Tree Overlay, configure the `loaddtos` environment variable as
follows:
```
                     .--------------------------------.----- '#' Separator
                     |                                |
                     v                                v
=> env set loaddtos '#conf-stm32mp135f-dhcor-dhsbc.dtb#conf-stm32mp13xx-dhcor-dhsbc-overlay-rb-tft32-v2.dtbo'
                       ^                                ^
                       |                                |
                       |                                '--- SPI display DTO
                       |
                       '------------------------------------ Base Device Tree
```

To make configuration persistent, store U-Boot environment:
```
=> env save
```

To boot the system with additional DTO applied:
```
STM32MP> env set loaddtos '#conf-stm32mp135f-dhcor-dhsbc.dtb#conf-stm32mp13xx-dhcor-dhsbc-overlay-rb-tft32-v2.dtbo'
STM32MP> boot
Boot over nor0!
MMC Device 1 not found
no mmc device at slot 1
switch to partitions #0, OK
mmc0(part 0) is current device
Scanning mmc 0:2...
Found U-Boot script /boot/boot.scr
4988 bytes read in 1 ms (4.8 MiB/s)
## Executing script at c4100000
5933324 bytes read in 129 ms (43.9 MiB/s)
Booting the Linux kernel...

## Loading kernel (any) from FIT Image at c2000000 ...
   Using 'conf-stm32mp135f-dhcor-dhsbc.dtb' configuration
   Trying 'kernel-1' kernel subimage
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
   Verifying Hash Integrity ... sha256+ OK
## Loading fdt (any) from FIT Image at c2000000 ...
   Using 'conf-stm32mp135f-dhcor-dhsbc.dtb' configuration
   Trying 'fdt-stm32mp135f-dhcor-dhsbc.dtb' fdt subimage
     Description:  Flattened Device Tree blob
     Created:      2025-10-15  10:00:25 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc2597790
     Data Size:    65990 Bytes = 64.4 KiB
     Architecture: ARM
     Load Address: 0xcff00000
     Hash algo:    sha256
     Hash value:   93d9d7a7e4032aa5fb4daf95a90f6148a49c4760a41a97281083101914cacb5f
   Verifying Hash Integrity ... sha256+ OK
   Loading fdt from 0xc2597790 to 0xcff00000
   Loading Device Tree to cffec000, end cfffffff ... OK
Working FDT set to cffec000
## Loading fdt (any) from FIT Image at c2000000 ...
   Using 'conf-stm32mp13xx-dhcor-dhsbc-overlay-rb-tft32-v2.dtbo' configuration
   Trying 'fdt-stm32mp13xx-dhcor-dhsbc-overlay-rb-tft32-v2.dtbo' fdt subimage
     Description:  Flattened Device Tree blob
     Created:      2025-10-15  10:00:25 UTC
     Type:         Flat Device Tree
     Compression:  uncompressed
     Data Start:   0xc25a7a64
     Data Size:    2161 Bytes = 2.1 KiB
     Architecture: ARM
     Load Address: 0xcff80000
     Hash algo:    sha256
     Hash value:   3b50da5c66b4cc20a919ddd750be3020947dc7ac06632609bcfc95b2fe3d5fb3
   Verifying Hash Integrity ... sha256+ OK
   Booting using the fdt blob at 0xcffec000
Working FDT set to cffec000
   Loading Kernel Image to c4000000
   Loading Device Tree to cffd8000, end cffeb4e9 ... OK
Working FDT set to cffd8000

Starting kernel ...
```

# IO access

This section describe access to various IO of this system.

## Serial ports and UARTs

U-Boot on this system provides serial console access via `UART4` 6-pin
header `X9`, this is `serial@40010000`. Other serial ports can be made
available by modifying U-Boot control Device Tree.

Linux on this system provides serial console access via `UART4` 6-pin
header `X9`, this is `40010000.serial`. Linux also provides three
additional serial ports accessible via expansion connector `X7`,
`4c000000.serial` on RX:pin33 TX:pin37 and `4c001000.serial` on
RX:pin10 TX:pin8 RTS:pin11 CTS:pin36 . The `40018000.serial` is
used by Bluetooth chip. These ports are accessible via `/dev/ttySTM*`
device nodes. To discern the ports apart, use `udevadm info /dev/ttySTM*`
command, which prints the full hardware path to the selected port:
```
root@dh-stm32mp13-dhcor-dhsbc:~# udevadm info /dev/ttySTM*
P: /devices/platform/soc/40010000.serial/40010000.serial:0/40010000.serial:0.0/tty/ttySTM0
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
root@dh-stm32mp13-dhcor-dhsbc:~# evtest
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
Event: time 1761683825.137023, type 1 (EV_KEY), code 116 (KEY_POWER), value 1
Event: time 1761683825.137023, -------------- SYN_REPORT ------------
Event: time 1761683825.292340, type 1 (EV_KEY), code 116 (KEY_POWER), value 0
Event: time 1761683825.292340, -------------- SYN_REPORT ------------
```

### GPIOs

U-Boot on this system provides access to multiple GPIO pins.

To list GPIOs and their current state, use the `gpio status` command:
```
STM32MP> gpio status
Bank GPIOA:
GPIOA11: output: 1 [x] ethernet-phy@1.reset-gpios

Bank GPIOG:
GPIOG8: output: 1 [x] ethernet-phy@1.reset-gpios
```

To set a GPIO as Output-High, use `gpio set <pin>` command:
```
STM32MP> gpio set GPIOA10
gpio: pin GPIOA10 (gpio 10) value is 1
```

To set a GPIO as Output-Low, use `gpio clear <pin>` command:
```
STM32MP> gpio clear GPIOA10
gpio: pin GPIOA10 (gpio 10) value is 0
```

To toggle a GPIO, use `gpio toggle <pin>` command:
```
STM32MP> gpio toggle GPIOA10
gpio: pin GPIOA10 (gpio 10) value is 1
STM32MP> gpio toggle GPIOA10
gpio: pin GPIOA10 (gpio 10) value is 0
```

Linux on this system provides access to multiple GPIO pins. Access to
GPIOs is provided using GPIO chardev interface and libgpiod. Subset of
GPIO lines is named, which makes them easier to discern in libgpiod
tools output.

To list available GPIOs, use the `gpioinfo` command:
```
root@dh-stm32mp13-dhcor-dhsbc:~# gpioinfo
gpiochip0 - 16 lines:
        line   0:       "PA0"                   input
        line   1:       "PA1"                   input consumer="kernel"
        line   2:       "PA2"                   input consumer="kernel"
        line   3:       "PA3"                   input consumer="kernel"
        line   4:       "PA4"                   input
...
        line  10:       "PA10"                  input
...
...
```

To get current state of a GPIO, use the `gpioget` command:
```
root@dh-stm32mp13-dhcor-dhsbc:~# gpioget -c /dev/gpiochip0 10
"10"=inactive
...
```

To set a GPIO as Output-High, use `gpioset ... <line>=1` command:
```
root@dh-stm32mp13-dhcor-dhsbc:~# gpioset -c /dev/gpiochip0 10=1
```

To set a GPIO as Output-Low, use `gpioset ... <line>=0` command:
```
root@dh-stm32mp13-dhcor-dhsbc:~# gpioset -c /dev/gpiochip0 10=0
```

It is possible to refer to GPIOs using symbolic names:
```
# Numerical reference to GPIO on gpiochip0 line 10:
root@dh-stm32mp13-dhcor-dhsbc:~# gpioget -c /dev/gpiochip0 10
"10"=inactive

# Symbolic name reference to GPIO on gpiochip0 line 10:
root@dh-stm32mp13-dhcor-dhsbc:~# gpioget PA10
"PA10"=inactive
```

Note that once the `gpioset` command exits, the line returns to undefined state.

## Storage

### eMMC

U-Boot on this system provides access to eMMC card via standard MMC and
block device interface. To detect and print information about the eMMC
card, use `mmc dev` and `mmc info` commands. The eMMC card is MMC device
number 0:
```
STM32MP> mmc dev 0
switch to partitions #0, OK
mmc0(part 0) is current device

STM32MP> mmc info
Device: STM32 SD/MMC
Manufacturer ID: 15
OEM: 0
Name: 4FTE4R
Bus Speed: 52000000
Mode: MMC High Speed (52MHz)
Rd Block Len: 512
MMC version 5.1
High Capacity: Yes
Capacity: 3.6 GiB
Bus Width: 8-bit
Erase Group Size: 512 KiB
HC WP Group Size: 8 MiB
User Capacity: 3.6 GiB WRREL
Boot Capacity: 4 MiB ENH
RPMB Capacity: 512 KiB ENH
Boot area 0 is not write protected
Boot area 1 is not write protected
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
        guid:   5ba0bf70-ad91-4c71-8135-d5a29b58f5be
...

STM32MP> ls mmc 0:2
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
/dev/disk/by-path/platform-5c007000.bus-amba-58007000.mmc
```

In case the deterministic device name symlink is not available, it is possible
to resolve eMMC card block device to `/dev/mmcblk*` device node manually by
reading out the character device major and minor numbers from sysfs, and then
by performing look up in the `/dev/` directory, using the following commands.
In the example below, major and minor numbers for eMMC card are 179 and 0,
which are represented by device node `/dev/mmcblk0`:
```
root@dh-stm32mp13-dhcor-dhsbc:~# cat /sys/devices/platform/soc/5c007000.bus/58007000.mmc/mmc_host/mmc*/mmc*/block/mmcblk0/dev
179:0
^^^ ^

root@dh-stm32mp13-dhcor-dhsbc:~# ls -la /dev/mmcblk0
...
brw-rw---- 1 root disk 179,  0 Mar 10 03:53 /dev/mmcblk0
                       ^^^   ^              ^^^^^^^^^^^^
...
```

## Network

### Ethernet

U-Boot on this system provides access to two ethernet interfaces via U-Boot
networking stack. Use `net list` command to list available ethernet interfaces:
```
STM32MP> net list
eth0 : ethernet@5800a000 00:11:22:33:44:55 active
eth1 : ethernet@5800e000 00:11:22:33:44:56
```

To select active ethernet interface, set U-Boot environment variable `ethact`.

Linux on this system provides access to two ethernet interfaces via Linux
networking stack. These interfaces are assigned deterministic interface
names via systemd udevd rules.

```
root@dh-stm32mp13-dhcor-dhsbc:~# ip addr show
...
4: ethsom0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 ...
5: ethsom1: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 ...
...
```

To discern the ports apart, use `udevadm info /sys/class/net/ethsom*` command,
which prints the full hardware path to the selected ethernet interface. The
default assignment of `5800a000.ethernet` is on-SoC ethernet MAC 0, which is
the ethernet jack closer to board corner, and `5800e000.ethernet` is on-Soc
ethernet MAC 1, which is the ethernet jack closer to board center:

```
root@dh-stm32mp13-dhcor-dhsbc:~# udevadm info /sys/class/net/ethsom0
P: /devices/platform/soc/5c007000.bus/5800a000.ethernet/net/ethsom0
M: ethsom0
...

root@dh-stm32mp13-dhcor-dhsbc:~# udevadm info /sys/class/net/ethsom1
P: /devices/platform/soc/5c007000.bus/5800e000.ethernet/net/ethsom1
M: ethsom1
...
```

### WiFi and Bluetooth

Linux on this system provides access to WiFi interface via Linux networking
stack. This interface is assigned deterministic interface name via systemd
udevd rules.

```
root@dh-stm32mp13-dhcor-dhsbc:~# udevadm info /sys/class/net/wlansom0
P: /devices/platform/soc/5c007000.bus/58005000.mmc/mmc_host/mmc1/mmc1:0001/mmc1:0001:1/net/wlansom0
M: wlansom0
...
```

To operate the WiFi interface, use `iw` and `wpa-supplicant` tools, or any
other suitable network management tooling. Example scan for nearby networks.
The WiFi interface might be blocked via software RFkill, it may be necessary
to unblock the interface first:

```
root@dh-stm32mp13-dhcor-dhsbc:~# rfkill unblock wifi

root@dh-stm32mp13-dhcor-dhsbc:~# ip link set wlansom0 up
root@dh-stm32mp13-dhcor-dhsbc:~# iw dev wlansom0 scan
BSS 00:11:22:33:44:55(on wlansom0)
...
```

Linux on this system provides access to Bluetooth interface via Linux bluetooth
stack. To operate the Bluetooth interface, use the Bluez stack, for example via
the `bluetoothctl` command. The bluetooth interface might be blocked via software
RFkill, it may be necessary to unblock the interface first:

```
root@dh-stm32mp13-dhcor-dhsbc:~# rfkill unblock bluetooth

root@dh-stm32mp13-dhcor-dhsbc:~# bluetoothctl
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

## USB

### USB Host

This system provides one USB host port accessible via USB-A connector `X6`.

This system provides one USB OTG port connected to a USB-C connector `X2`.

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
root@dh-stm32mp13-dhcor-dhsbc:~# lsusb
Bus 001 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub
Bus 002 Device 001: ID 1d6b:0001 Linux Foundation 1.1 root hub
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
root@dh-stm32mp13-dhcor-dhsbc:~# modprobe g_zero
zero gadget.0: Gadget Zero, version: Cinco de Mayo 2008
zero gadget.0: zero ready
dwc2 49000000.usb-otg: bound driver zero
```

Connect USB A-to-C cable between the board USB OTG port and host PC.

The newly established USB connection is reported on the board side as follows:
```
dwc2 49000000.usb-otg: new device is high-speed
dwc2 49000000.usb-otg: new device is high-speed
dwc2 49000000.usb-otg: new address 20
```

The newly established USB connection is reported on the host PC side as follows:
```
usb 5-2.2.2.4: new high-speed USB device number 18 using xhci_hcd
usb 5-2.2.2.4: New USB device found, idVendor=1a0a, idProduct=badd, bcdDevice= 6.12
usb 5-2.2.2.4: New USB device strings: Mfr=1, Product=2, SerialNumber=3
usb 5-2.2.2.4: Product: Gadget Zero
usb 5-2.2.2.4: Manufacturer: Linux 6.12.53-stable-standard-00010-gefa63a30e914 with 49000000.usb
usb 5-2.2.2.4: SerialNumber: 0123456789.0123456789.0123456789
```
