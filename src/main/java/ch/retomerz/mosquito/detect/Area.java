/*
 * Copyright (c) 2019 Reto Merz
 */
package ch.retomerz.mosquito.detect;

import ch.retomerz.mosquito.util.MathUtil;

import javax.annotation.Nonnull;

final class Area {

  private int minX;
  private int minY;
  private int maxX = -1;
  private int maxY = -1;
  private long trackNrOfLastUpdate;

  Area(final int x, final int y, final long trackNr) {
    this.minX = x;
    this.minY = y;
    this.trackNrOfLastUpdate = trackNr;
  }

  int getMinX() {
    return minX;
  }

  int getMinY() {
    return minY;
  }

  int getMaxX() {
    return maxX;
  }

  int getMaxY() {
    return maxY;
  }

  long getTrackNrOfLastUpdate() {
    return trackNrOfLastUpdate;
  }

  public boolean isPoint() {
    return maxX == -1;
  }

  public boolean isNear(final int x, final int y, final double maxDistance) {
    final double distance = distance(x, y);
    return distance < maxDistance;
  }

  @Nonnull
  public Area extend(final int x, final int y, final long trackNr) {
    if (x > minX) {
      maxX = x;
    } else {
      maxX = minX;
      minX = x;
    }

    if (y > minY) {
      maxY = y;
    } else {
      maxY = minY;
      minY = y;
    }

    if (maxX != -1 || maxY != -1) {
      if (maxX == -1)
        maxX = minX;
      if (maxY == -1)
        maxY = minY;
    }

    this.trackNrOfLastUpdate = trackNr;
    return this;
  }

  private double distance(final int x, final int y) {
    if (maxX > 0) {
      return MathUtil.distanceRect(minX, minY, maxX, maxY, x, y);
    }
    return MathUtil.distance(minX, minY, x, y);
  }
}
