/*
 * Copyright (c) 2019 Reto Merz
 */
package ch.retomerz.mosquito.detect;

import ch.retomerz.mosquito.util.ColorUtil;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class Tracker {

  private final int width;

  private final int height;

  private final List<Area> areas;

  private long trackInvocationCount;

  private long hitNoiseCount;

  public Tracker(
          final int width,
          final int height
  ) {
    this.width = width;
    this.height = height;
    this.areas = new ArrayList<>(128);
  }

  public void track(
          @Nonnull final BufferedImage img,
          @Nonnull final int[] currImage,
          @Nonnull final int[] lastImage
  ) {

    int pos = 0;
    int noiseCount = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {

        final int lastRGB = lastImage[pos];
        final int currRGB = currImage[pos];
        final int lastR = ColorUtil.getRed(lastRGB);
        final int lastG = ColorUtil.getGreen(lastRGB);
        final int lastB = ColorUtil.getBlue(lastRGB);
        final int currR = ColorUtil.getRed(currRGB);
        final int currG = ColorUtil.getGreen(currRGB);
        final int currB = ColorUtil.getBlue(currRGB);
        final int noiseR = Math.abs(lastR - currR);
        final int noiseG = Math.abs(lastG - currG);
        final int noiseB = Math.abs(lastB - currB);
        final int noiseRGB = noiseR + noiseG + noiseB;

        if (y > 1 && noiseRGB > 50) {
          hitNoiseCount++;
          trackPixel(x, y);
        }
        pos++;
      }
    }

    highlightAreas(img);

    //img.getGraphics().drawString("Test", 20, 20);

    trackInvocationCount++;
  }

  private void trackPixel(final int x, final int y) {
    for (final Area area : new ArrayList<>(areas)) {
      if (area.isNear(x, y, 15)) {
        area.extend(x, y, trackInvocationCount);
        // TODO connect area
        return;
      } else {
        if (area.getTrackNrOfLastUpdate() < (trackInvocationCount - 4)) {
          areas.remove(area);
        }
      }
    }
    areas.add(new Area(x, y, trackInvocationCount));
  }

  public long getHitNoiseCount() {
    return hitNoiseCount;
  }

  private void highlightAreas(@Nonnull final BufferedImage img) {
    for (final Area area : areas) {
      if (area.isPoint()) {
        //highlight(img, area.getMinX(), area.getMinY());
      } else {
        for (int y = area.getMinY(); y <= area.getMaxY(); y++) {
          for (int x = area.getMinX(); x <= area.getMaxX(); x++) {
            img.setRGB(x, y, ColorUtil.toRGB(0, 255, 0));
          }
        }
      }
    }
  }

  private void highlight(@Nonnull final BufferedImage img, final int x, final int y) {
    img.setRGB(x, y, ColorUtil.toRGB(255, 0, 0));
    if ((x + 1) < width) {
      img.setRGB(x + 1, y, ColorUtil.toRGB(255, 0, 0));
      if ((y + 1) < height) {
        img.setRGB(x + 1, y + 1, ColorUtil.toRGB(255, 0, 0));
      }
    }
    if ((y + 1) < height) {
      img.setRGB(x, y + 1, ColorUtil.toRGB(255, 0, 0));
    }
  }
}
