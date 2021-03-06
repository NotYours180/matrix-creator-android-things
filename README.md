MATRIX-Creator and MATRIX-Voice Android Things (BETA)
=====================================

This Android Things app runs basic tests for MATRIX Creator sensors and mic array output via Everloop LEDs.

Status
------

- [X] Manual FPGA initialization (MATRIXCreator)
- [X] Automatic FPGA initialization (MATRIXVoice only)
- [X] All sensors
- [X] Everloop control
- [X] Mic Array continuous capture (mics showed on Everloop)
- [X] Mic Array (all mics, output to IP Address, please see notes)
- [ ] Zigbee driver
- [ ] Z-Wave driver
- [ ] LIRC custom control config
- [ ] Matrix vision framework **
- [ ] Matrix Google Things contrib driver **

`**` in progress

Pre-requisites
--------------

- RaspberryPi 3
- MATRIX Creator or MATRIX Voice
- Android Studio 2.2+

### Firmware installation 

**NOTE: (only for MATRIXCreator, for MATRIX Voice skip this step)**

**IMPORTANT:** Please, note that these samples are not necessarily the easiest way because the Android Things source code and documentation have not been published or completed and have some platform [issues](https://github.com/androidthings/sample-simplepio/issues/2)

For now you can test MATRIX Creator with Google Things, for this we need FPGA burner running from root privileges, for it please follow next steps:

On your pc:

1. Flashing rpi3 Android Things image and connect with it via adb. [more info](https://developer.android.com/things/hardware/raspberrypi.html#flashing_the_image)
2. Obtain root privileges:        `adb root`    (take some seconds)
3. Mount partions on write mode:  `adb remount`
4. Clone repository and submodules: 

    ```bash
    git clone --recursive https://github.com/matrix-io/matrix-creator-android-things.git
    ```
5. Copy firmware, burner, flashing script, and sensors test:

    ```bash
    cd matrix-creator-android-things/firmware
    adb push matrix_system.bit /system/bin/
    adb push matrix-xc3sprog /system/bin/
    adb push matrix-firmware-loader.sh /system/bin/
    adb push matrix-sensors-status /system/bin/
   ```
6. Programing FPGA (~1 minute for flashing):

    ```bash
    adb shell matrix-firmware-loader.sh
    ```
    you get output like this:

    ```bash
    disable Matrix Creator microcontroller..done
    reconfigurate FPGA and Micro..
    DNA is 0x79ec27f5572e2dfd
    done
    ```
7. Testing FPGA status

   ```bash
   adb shell matrix-sensors-status
   ```
   you get output like this:

   ```bash
   IMUY:-1.1e+02° IMUR:0.26°     IMUP:1.2°
   HUMI:35%       HTMP:35°C      UVID:0.0032
   PRSS:74960     PrAL:2470.7    PrTP:32.562
   MCU :0x10      VER :0x161026
   ```

#### Troubleshooting

- if you get sensors on 0, please repeat step 6.
- if you get sensors like this, ommit and run demo, all are ok.

    ```bash
    IMUY:0°    IMUR:0°    IMUP:0°
    HUMI:5.7e-42%    HTMP:-3.8e-15°C    UVID:0
    PRSS:-3.8537e-15    PrAL:0    PrTP:0
    MCU :0x0    VER :0xa793c000
    ```
    
- if you shutdown your raspberryPi, please repeat steps: 2 and 6. (root and reprograming FPGA)

### Run demo application

From this point your have a basic Android Things project, for launch Demo (MatrixCreatorGT app) please execute this from main directory:

```bash
    ./gradlew installDebug
    adb shell am start admobilize.matrix.gt/.MainActivity
```

on your adb logcat will obtain sensors status and everloop leds will show mic status. Add filter `HM` 
regex for sensors for example, for mic array data you can filter with `MIC` regex, the output is like this:

```java
    UV: 0.0066934405	AL: 2473.4375	PR: 74932.75	TP: 38.0625	HM: 73.195694	TP: 34.77271	YW: 82.34981	PT: 2.3185472	RL: 6.3011737	
    UV: 0.0066934405	AL: 2473.4375	PR: 74935.25	TP: 38.0625	HM: 73.195694	TP: 34.77271	YW: 82.52538	PT: 2.36045	RL: 6.333514	
    UV: 0.0066934405	AL: 2473.375	PR: 74933.75	TP: 38.0625	HM: 73.196396	TP: 34.79062	YW: 83.13602	PT: 2.2451632	RL: 6.46383	
```

### Micarray record (optional)

You can test mics and capture sound via `netcat` command on another machine for now, because
AndroidThings not have or not support `EXTERNALSTORAGE` permissions for save captures.

1. Config your PC ip address on `app/src/main/java/admobilize/matrix/gt/Config.java`

    ```java
    public static final String EXTERNAL_DEBUG_IP = "10.0.0.140";
    public static final int EXTERNAL_DEBUG_PORT = 2999;
    ```

2. Enable `record` flag on `MainActivity` class:
    ```java
    private static final boolean ENABLE_MICARRAY_RECORD    = true; // 1024 samples ~8 sec
    ```

3. Run netcat on your PC like this: 

    ```bash
    nc -l 2999 > audio.raw
    ```

4. Rebuild and launch demo app, the mic will starting capturing 1024 samples (~8s): 

    ```bash
    ./gradlew installDebug
    adb shell am start admobilize.matrix.gt/.MainActivity
    ```

    You should be get this on `adb logcat`:
    ```java
     D/MainActivity: [MIC] starting capture..
     I/MicArray: [MIC] 1024 samples
     D/MainActivity: [MIC] mic: 7 size :131072
     D/MainActivity: [MIC] mic: 7 data :[-7169, -7169, -6913, -6657, -6657, ...
     D/MainActivity: [MIC] all mics data size:
     D/MainActivity: [MIC] mic:0 size: 131072
     D/MainActivity: [MIC] mic:1 size: 131072
     D/MainActivity: [MIC] mic:2 size: 131072
     D/MainActivity: [MIC] mic:3 size: 131072
     D/MainActivity: [MIC] mic:4 size: 131072
     D/MainActivity: [MIC] mic:5 size: 131072
     D/MainActivity: [MIC] mic:6 size: 131072
     D/MainActivity: [MIC] mic:7 size: 131072
    ```
5. When `netcat` come back to shell your obtain mic data, then reconvert and play it with:
    ```bash
    sox -r 16000 -c 1 -e signed  -b 16 audio.raw audio.wav
    aplay audio.wav
    ```

(OPTIONAL) Contribute or build xc3sprog programer code
------------------------------------------------------

For build the lastest FPGA programmer you need NDK and run:

```bash
    ./scripts/build_xc3sprog.sh
```
you get output like this:

```bash
    ...
    Linking CXX executable xc3sprog
    [100%] Built target xc3sprog
    [ 82%] Built target xc3sproglib
    [100%] Built target xc3sprog
    Installing the project stripped...
    -- Install configuration: "Debug"
    -- Installing: /home/username/src/admobilize/matrix-things/android_lib/xc3sprog/bin/xc3sprog
```
Then repeat steps 2 and 3 (firmware installation section) and copy new programer:

```bash
    cp android_lib/xc3sprog/bin/xc3sprog firmware/matrix-xc3sprog
    adb push firmware/matrix-xc3sprog /system/bin/
```

