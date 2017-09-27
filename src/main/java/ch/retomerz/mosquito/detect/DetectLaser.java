/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito.detect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

public final class DetectLaser {

  private static final int HOT_SIZE_MIN = 2; // TODO
  private static final int HOT_SIZE_MAX = 4; // TODO

  @Nullable
  public Point detect(@Nullable final BufferedImage image) {
    if (image == null) {
      return null;
    }
    final int width = image.getWidth();
    final int height = image.getHeight();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        final int rgb = image.getRGB(x, y);
        final Color c = new Color(rgb);
        if (isHot(c)) {
          return new Point(x, y);
        }
      }
    }
    return null;
  }

  @SuppressWarnings("RedundantIfStatement")
  private static boolean isHot(@Nonnull final Color c) {
    if (c.getGreen() < 250) {
      return false;
    }
    if (c.getRed() < 254) {
      return false;
    }
    if (c.getBlue() < 253) {
      return false;
    }
    return true;
  }
}
