/*
 * Copyright (c) 2019 Reto Merz
 */
package ch.retomerz.mosquito.detect;

import ch.retomerz.mosquito.util.ColorUtil;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

final class Tracker {

  private final int width;

  private final int height;

  private final List<Area> areas;

  private long skippedFrames;

  private long trackInvocationCount;

  Tracker(
          final int width,
          final int height
  ) {
    this.width = width;
    this.height = height;
    this.areas = new ArrayList<>(128);
  }

  void track(
          @Nonnull final BufferedImage img,
          @Nonnull final int[] currImage,
          @Nonnull final int[] lastImage,
          @Nonnull final int[] dirtyImage
  ) {

    int pos = 0;
    int dirtyCount = 0;
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

        final boolean dirty = y > 1 && noiseRGB > 50;
        dirtyImage[pos] = dirty ? noiseRGB : 0;
        if (dirty) {
          dirtyCount++;
        }
        pos++;
      }
    }

    final double dirtyPercent = (100.0 / (width * height)) * dirtyCount;
    if (dirtyPercent > 0.1) {
      skippedFrames++;
      //System.out.println("Skip ; too many changes ; percent: " + dirtyPercent);

    } else {
      pos = 0;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (dirtyImage[pos] > 0) {
            trackPixel(x, y);
          }
          pos++;
        }
      }
      highlightAreas(img);
      trackInvocationCount++;
    }
  }

  private void trackPixel(final int x, final int y) {
    for (final Area area : new ArrayList<>(areas)) {
      if (area.isNear(x, y, 15)) {
        area.extend(x, y, trackInvocationCount);
        connectArea(area);
        return;
      } else {
        if (area.getTrackNrOfLastUpdate() < (trackInvocationCount - 3)) {
          areas.remove(area);
        }
      }
    }
    areas.add(new Area(x, y, trackInvocationCount));
  }

  private void connectArea(@Nonnull final Area area) {
    for (final Area otherArea : new ArrayList<>(areas)) {
      if (otherArea != area) {
        if (area.isNear(otherArea, 15)) {
          area.extend(otherArea, trackInvocationCount);
          areas.remove(otherArea);
          return;
        }
      }
    }
  }

  long getAreaCount() {
    return areas.size();
  }

  long getSkippedFrames() {
    return skippedFrames;
  }

  private void highlightAreas(@Nonnull final BufferedImage img) {
    for (final Area area : areas) {
      if (area.isPoint()) {
        //highlight(img, area.getMinX(), area.getMinY());
      } else {
        final Graphics g = img.getGraphics();
        g.setColor(Color.red);
        g.drawRect(
                area.getMinX(),
                area.getMinY(),
                area.getMaxX() - area.getMinX(),
                area.getMaxY() - area.getMinY()
        );
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
