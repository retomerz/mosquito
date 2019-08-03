/*
 * Copyright (c) 2019 Reto Merz
 */
package ch.retomerz.mosquito.util;

public final class ColorUtil {
  private ColorUtil() {
  }

  public static int toRGB(final int red, final int green, final int blue) {
    return ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff);
  }

  public static int getRed(final int rgb) {
    return (rgb >> 16) & 0x0ff;
  }

  public static int getGreen(final int rgb) {
    return (rgb >> 8) & 0x0ff;
  }

  public static int getBlue(final int rgb) {
    return (rgb) & 0x0ff;
  }
}
