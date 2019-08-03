/*
 * Copyright (c) 2019 Reto Merz
 */
package ch.retomerz.mosquito.ueye;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * See is_GetSensorInfo
 */
public class SENSORINFO extends Structure {

  public static class ByReference extends SENSORINFO implements Structure.ByReference {
  }

  public short SensorID; // eg: IS_SENSOR_UI224X_C
  public byte[] strSensorName = new byte[32]; // eg: UI224xLE-C
  public byte nColorMode; // 1 = IS_COLORMODE_MONOCHROME, 2 = IS_COLORMODE_BAYER, 4 = IS_COLORMODE_CBYCRY, 8 = IS_COLORMODE_JPEG, 0 = IS_COLORMODE_INVALID
  public int nMaxWidth; // eg: 1920
  public int nMaxHeight; // eg: 1200
  public boolean bMasterGain;
  public boolean bRGain;
  public boolean bGGain;
  public boolean bBGain;
  public boolean bGlobShutter;
  public short wPixelSize; // eg: 465 is 4.65 um
  public byte nUpperLeftBayerPixel; // 0 = BAYER_PIXEL_RED, 1 = BAYER_PIXEL_GREEN, 2 = BAYER_PIXEL_BLUE
  public byte[] Reserved = new byte[13];

  @Override
  protected List<String> getFieldOrder() {
    return Arrays.asList(
            "SensorID",
            "strSensorName",
            "nColorMode",
            "nMaxWidth",
            "nMaxHeight",
            "bMasterGain",
            "bRGain",
            "bGGain",
            "bBGain",
            "bGlobShutter",
            "wPixelSize",
            "nUpperLeftBayerPixel",
            "Reserved"
    );
  }
}
