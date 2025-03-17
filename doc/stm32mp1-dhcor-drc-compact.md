STM32MP15xx DHCOR SoM on DH DRC Compact carrier board
=====================================================

# Initial board setup

This section explains how to perform initial hardware setup, how to
assemble the hardware, which cables to connect to which ports, how to
power the hardware on, and finally how to obtain serial console access.

## Connect serial console cable

Connect serial console 3.3V UART cable into `Debug UART` 3-pin connector
`X10`. This is LVTTL UART 3.3V voltage level connection. The pinout is
`1-2-3=TX-RX-GND` , pin 1 is closer to ethernet plug.

## Connect ethernet cables (optional)

Connect up to two ethernet cables to 1G ethernet jack `X3` and
100M ethernet jack `X4`.

## Connect power supply

Connect 24V/1A power supply into connector `X13`. The pinout is
`1-2-3=Shield-GND-VIN` , pin 1 is closer to ethernet plugs.

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

The operating system image can be installed onto either microSD card or
into eMMC USER hardware partition.

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
host$ zstd -d dh-image-demo-dh-stm32mp1-dhcor-drc-compact.rootfs.wic.zst
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
    dh-image-demo-dh-stm32mp1-dhcor-drc-compact.rootfs.wic.bmap \
    dh-image-demo-dh-stm32mp1-dhcor-drc-compact.rootfs.wic.zst \
    /dev/sdx
bmaptool: info: block map format version 2.0
bmaptool: info: 400474 blocks of size 4096 (1.5 GiB), mapped 246005 blocks (961.0 MiB or 61.4%)
bmaptool: info: copying image 'dh-image-demo-dh-stm32mp1-dhcor-drc-compact.rootfs.wic' to block device '/dev/sdx' using bmap file 'dh-image-demo-dh-stm32mp1-dhcor-drc-compact.rootfs.wic.bmap'
bmaptool: info: 100% copied
bmaptool: info: synchronizing '/dev/sdx'
bmaptool: info: copying time: 13.2s, copying speed 72.5 MiB/sec
```

It is equally possible to use plain `dd` to write the decompressed
system image into the block device as follows:
```
host$ dd if=dh-image-demo-dh-stm32mp1-dhcor-drc-compact.rootfs.wic \
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
host$ zstd -d dh-image-demo-dh-stm32mp1-dhcor-drc-compact.rootfs.wic.zst
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
    dh-image-demo-dh-stm32mp1-dhcor-drc-compact.rootfs.wic.bmap \
    dh-image-demo-dh-stm32mp1-dhcor-drc-compact.rootfs.wic.zst \
    /dev/mmcblk1
bmaptool: info: block map format version 2.0
bmaptool: info: 400474 blocks of size 4096 (1.5 GiB), mapped 246005 blocks (961.0 MiB or 61.4%)
bmaptool: info: copying image 'dh-image-demo-dh-stm32mp1-dhcor-drc-compact.rootfs.wic' to block device '/dev/mmcblk1' using bmap file 'dh-image-demo-dh-stm32mp1-dhcor-drc-compact.rootfs.wic.bmap'
bmaptool: info: 100% copied
bmaptool: info: synchronizing '/dev/mmcblk1'
bmaptool: info: copying time: 13.2s, copying speed 72.5 MiB/sec
```

It is equally possible to use plain `dd` to write the decompressed
system image into the block device as follows:
```
host$ dd if=dh-image-demo-dh-stm32mp1-dhcor-drc-compact.rootfs.wic \
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

### U-Boot bootloader installation into MicroSD card

This section explains how to perform bootloader installation and update.
This section is only applicable in case it is desireable to replace
bootloader on the system.

The U-Boot bootloader is installed into MicroSD card. Installation into
SPI NOR is possible, however does require hardware modification. U-Boot
bootloader installation into MicroSD card can be performed from within
U-Boot itself, or from running Linux userspace.

The SPI NOR layout on this platform is as follows:
```
0x00_0000..0x03_ffff ... U-Boot SPL (copy 1)
0x04_0000..0x07_ffff ... U-Boot SPL (copy 2)
0x08_0000..0x15_ffff ... U-Boot fitImage
0x06_0000..0x1d_ffff ... UNUSED
0x1e_0000..0x1e_ffff ... U-Boot environment (copy 1)
0x1f_0000..0x1f_ffff ... U-Boot environment (copy 2)
```

### U-Boot bootloader installation into MicroSD card (from U-Boot from SD/eMMC card)

Power on the system. Enter U-Boot console by pressing any key at prompt:
```
Hit any key to stop autoboot:
```

The system drops into U-Boot shell instantly. U-Boot shell on this system
looks as follows:
```
STM32MP>
```

Use the following U-Boot commands to read U-Boot SPL and U-Boot binaries
from a block device, in this case MicroSD card data partition 4, and write
those binaries into MicroSD card partitions 1 (SPL1), 2 (SPL2), 3 (U-Boot):

Update U-Boot SPL from MicroSD card data partition 4 to SPL partition 1:
```
STM32MP> load mmc 0:4 $loadaddr boot/u-boot-spl.stm32 && \
         setexpr blkcnt ${filesize} + 0x1ff && \
         setexpr blkcnt ${blkcnt} / 0x200 && \
         part start mmc 0 fsbl1 blkstt && \
         mmc write $loadaddr $blkstt $blkcnt
193510 bytes read in 10 ms (18.5 MiB/s)
MMC write: dev # 0, block # 34, count 378 ... 378 blocks written: OK
```

Update U-Boot SPL from MicroSD card data partition 4 to SPL partition 2:
```
STM32MP> load mmc 0:4 $loadaddr boot/u-boot-spl.stm32 && \
         setexpr blkcnt ${filesize} + 0x1ff && \
         setexpr blkcnt ${blkcnt} / 0x200 && \
         part start mmc 0 fsbl2 blkstt && \
         mmc write $loadaddr $blkstt $blkcnt
193510 bytes read in 11 ms (16.8 MiB/s)
MMC write: dev # 0, block # 546, count 378 ... 378 blocks written: OK
```

Update U-Boot from MicroSD card data partition 4 to U-Boot partition 3:
```
STM32MP> load mmc 0:4 $loadaddr boot/u-boot.itb && \
         setexpr blkcnt ${filesize} + 0x1ff && \
         setexpr blkcnt ${blkcnt} / 0x200 && \
         part start mmc 0 ssbl blkstt && \
         mmc write $loadaddr $blkstt $blkcnt
951861 bytes read in 42 ms (21.6 MiB/s)
MMC write: dev # 0, block # 1058, count 1860 ... 1860 blocks written: OK
```

Once the installation completed, reset the board. To reset the board, either
press the `RESET` button on the board, or perform reset from U-Boot shell as
follows:

```
STM32MP> reset
```

### U-Boot bootloader installation into MicroSD card (from Linux)

It is possible to update the bootloader from a running Linux userspace.
The MicroSD is exposed as a block device by the Linux kernel with at
least four software partitions.

Verify that the software partitions are present on the MicroSD card:

```
root@dh-stm32mp1-dhcor-drc-compact:~# fdisk -l /dev/disk/by-path/platform-soc-amba-58005000.mmc
...
Device                                                 Start     End Sectors  Size Type
/dev/disk/by-path/platform-soc-amba-58005000.mmc-part1    34     545     512  256K Microsoft basic data
/dev/disk/by-path/platform-soc-amba-58005000.mmc-part2   546    1057     512  256K Microsoft basic data
/dev/disk/by-path/platform-soc-amba-58005000.mmc-part3  1058    5153    4096    2M Microsoft basic data
/dev/disk/by-path/platform-soc-amba-58005000.mmc-part4  8192 4534579 4526388  2.2G Linux filesystem
```

In case the partition layout matches the one listed above, write the
bootloader binaries into MicroSD card partitions 1 (SPL), 2 (SPL) and
3 (U-Boot).

Update U-Boot SPL on MicroSD partition 1:
```
root@dh-stm32mp1-dhcor-drc-compact:~# dd if=/boot/u-boot-spl.stm32 of=/dev/disk/by-path/platform-soc-amba-58005000.mmc-part1
377+1 records in
377+1 records out
193510 bytes (194 kB, 189 KiB) copied, 0.0718475 s, 2.7 MB/s
```

Update U-Boot SPL on MicroSD partition 2:
```
root@dh-stm32mp1-dhcor-drc-compact:~# dd if=/boot/u-boot-spl.stm32 of=/dev/disk/by-path/platform-soc-amba-58005000.mmc-part2
377+1 records in
377+1 records out
193510 bytes (194 kB, 189 KiB) copied, 0.0615774 s, 3.1 MB/s
```

Update U-Boot on MicroSD partition 3:
```
root@dh-stm32mp1-dhcor-drc-compact:~# dd if=/boot/u-boot.itb of=/dev/disk/by-path/platform-soc-amba-58005000.mmc-part3
1859+1 records in
1859+1 records out
951861 bytes (952 kB, 930 KiB) copied, 0.318543 s, 3.0 MB/s
```

Once the installation completed, reset the board. To perform reset from
Linux kernel, use the following command:

```
root@dh-stm32mp1-dhcor-drc-compact:~# reboot
```

# IO access

This section describe access to various IO of this system.

## Serial ports and UARTs

U-Boot on this system provides serial console access via `Debug UART`
3-pin connector, this is `serial@40010000`. Other serial ports can be
made available by modifying U-Boot control Device Tree.

Linux on this system provides serial console access via `Debug UART`
3-pin connector, this is `40010000.serial`. Linux also provides one
additional serial port accessible via pin header `X11` `40011000.serial`
and two RS485 ports accessible via connectors `X6` or `X12`, those are
ports `4000f000.serial` and `40019000.serial` respectively.

To discern the ports apart, use `udevadm info /dev/ttySTM*` command,
which prints the full hardware path to the selected port:
```
root@dh-stm32mp1-dhcor-drc-compact:~# udevadm info /dev/ttySTM*
P: /devices/platform/soc/5c007000.bus/40010000.serial/40010000.serial:0/40010000.serial:0.0/tty/ttySTM0
                                      ^^^^^^^^^^^^^^^
M: ttySTM0
   ^^^^^^^
```

To access either serial port, use suitable terminate emulator,
for example `minicom`, `picocom`, `screen`, and many others.

## IO

### LEDs

U-Boot on this system provides access to two LEDs via `led` command.

To list available LEDs and their current state, use the `led list` command:
```
STM32MP> led list
yellow:user0    off
red:user1       off
```

To enable an LED, use `led <led_label> on` command:
```
STM32MP> led yellow:user0 on
STM32MP> led red:user1 on
```

To disable an LED, use `led <led_label> off` command:
```
STM32MP> led yellow:user0 off
STM32MP> led red:user1 off
```

Linux on this system provides access to two LEDs via sysfs LED API.

To enable an LED, write 1 into matching sysfs LED attribute:
```
root@dh-stm32mp1-dhcor-drc-compact:~# echo 1 > /sys/class/leds/yellow\:user0/brightness
root@dh-stm32mp1-dhcor-drc-compact:~# echo 1 > /sys/class/leds/red\:user1/brightness
```

To disable an LED, write 0 into matching sysfs LED attribute:
```
root@dh-stm32mp1-dhcor-drc-compact:~# echo 0 > /sys/class/leds/yellow\:user0/brightness
root@dh-stm32mp1-dhcor-drc-compact:~# echo 0 > /sys/class/leds/red\:user1/brightness
```

### GPIOs

U-Boot on this system provides access to multiple GPIO pins.

To list GPIOs and their current state, use the `gpio status` command:
```
STM32MP> gpio status
Bank GPIOH:
GPIOH2: output: 0 [x] vioregulator.gpio

Bank GPIOI:
GPIOI8: input: 0 [x] mmc@58005000.cd-gpios

Bank GPIOZ:
GPIOZ2: output: 1 [x] ethernet@5800a000.phy-reset-gpios
GPIOZ3: output: 0 [x] led2.gpios
GPIOZ6: output: 0 [x] led1.gpios
```

To set a GPIO as Output-High, use `gpio set <pin>` command:
```
STM32MP> gpio set GPIOZ3
gpio: pin GPIOZ3 (gpio 147) value is 1
```

To set a GPIO as Output-Low, use `gpio clear <pin>` command:
```
STM32MP> gpio clear GPIOZ3
gpio: pin GPIOZ3 (gpio 147) value is 0
```

To toggle a GPIO, use `gpio toggle <pin>` command:
```
STM32MP> gpio toggle GPIOZ3
gpio: pin GPIOZ3 (gpio 147) value is 1
STM32MP> gpio toggle GPIOZ3
gpio: pin GPIOZ3 (gpio 147) value is 0
```

Linux on this system provides access to multiple GPIO pins. Access to
GPIOs is provided using GPIO chardev interface and libgpiod. Subset of
GPIO lines is named, which makes them easier to discern in libgpiod
tools output.

To list available GPIOs, use the `gpioinfo` command:
```
root@dh-stm32mp1-dhcor-drc-compact:~# gpioinfo
gpiochip7 - 16 lines:
        line   0:       "PA0"                   input consumer="kernel"
        line   1:       "PA1"                   input consumer="kernel"
        line   2:       "PA2"                   input consumer="kernel"
        line   3:       "PA3"                   input
        line   4:       "DRCC-VAR2"             input
...
gpiochip7 - 16 lines:
        line   0:       "PH0"                   input
        line   1:       "PH1"                   input
        line   2:       "PH2"                   output active-low consumer="vioregulator"
        line   3:       "DRCC-HW2"              input
        line   4:       "DRCC-GPIO4"            input
...
...
```

To get current state of a GPIO, use the `gpioget` command:
```
root@dh-stm32mp1-dhcor-drc-compact:~# gpioget -c /dev/gpiochip7 4
"4"=inactive
...
```

To set a GPIO as Output-High, use `gpioset ... <line>=1` command:
```
root@dh-stm32mp1-dhcor-drc-compact:~# gpioset -c /dev/gpiochip7 4=1
```

To set a GPIO as Output-Low, use `gpioset ... <line>=0` command:
```
root@dh-stm32mp1-dhcor-drc-compact:~# gpioset -c /dev/gpiochip7 4=0
```

It is possible to refer to GPIOs using symbolic names:
```
# Numerical reference to GPIO on gpiochip7 line 4:
root@dh-stm32mp1-dhcor-drc-compact:~# gpioget -c /dev/gpiochip7 4
"4"=inactive

# Symbolic name reference to GPIO on gpiochip7 line 4:
root@dh-stm32mp1-dhcor-drc-compact:~# gpioget DRCC-GPIO4
"DHCOR-K"=inactive
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
which are represented by device node `/dev/mmcblk0`:
```
root@dh-stm32mp1-dhcor-drc-compact:~# cat /sys/devices/platform/soc/58005000.mmc/mmc_host/mmc*/mmc*/block/mmcblk*/dev
179:0
^^^ ^

root@dh-stm32mp1-dhcor-drc-compact:~# ls -la /dev/mmcblk*
...
brw-rw---- 1 root disk 179, 0 Mar 10 03:53 /dev/mmcblk0
                       ^^^  ^              ^^^^^^^^^^^^
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
root@dh-stm32mp1-dhcor-drc-compact:~# cat /sys/devices/platform/soc/58007000.mmc/mmc_host/mmc*/mmc*/block/mmcblk*/dev
179:8
^^^ ^

root@dh-stm32mp1-dhcor-drc-compact:~# ls -la /dev/mmcblk*
...
brw-rw---- 1 root disk 179,  8 Mar 10 03:53 /dev/mmcblk1
                       ^^^   ^              ^^^^^^^^^^^^
...
```

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
root@dh-stm32mp1-dhcor-drc-compact:~# ip addr show
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
root@dh-stm32mp1-dhcor-drc-compact:~# udevadm info /sys/class/net/ethsom0
P: /devices/platform/soc/5c007000.bus/5800a000.ethernet/net/ethsom0
M: ethsom0
...

root@dh-stm32mp1-dhcor-drc-compact:~# udevadm info /sys/class/net/ethsom1
P: /devices/platform/soc/5c007000.bus/58002000.memory-controller/64000000.ethernet/net/ethsom1
M: ethsom1
...
```

### WiFi and Bluetooth

Linux on this system provides access to WiFi interface via Linux networking
stack. This interface is assigned deterministic interface name via systemd
udevd rules.

```
root@dh-stm32mp1-dhcor-drc-compact:~# udevadm info /sys/class/net/wlansom0
P: /devices/platform/soc/5c007000.bus/48004000.mmc/mmc_host/mmc2/mmc2:fffd/mmc2:fffd:1/net/wlansom0
M: wlansom0
...
```

To operate the WiFi interface, use `iw` and `wpa-supplicant` tools, or any
other suitable network management tooling. Example scan for nearby networks:

```
root@dh-stm32mp1-dhcor-drc-compact:~# ip link set wlansom0 up
root@dh-stm32mp1-dhcor-drc-compact:~# iw dev wlansom0 scan
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

This system provides one USB host port accessible via USB-A connector `X2`.

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
root@dh-stm32mp1-dhcor-drc-compact:~# lsusb
Bus 001 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub
Bus 002 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub
Bus 003 Device 001: ID 1d6b:0001 Linux Foundation 1.1 root hub
```
