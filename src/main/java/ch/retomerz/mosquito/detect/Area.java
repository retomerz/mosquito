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

  boolean isPoint() {
    return maxX == -1;
  }

  boolean isNear(final int x, final int y, final double maxDistance) {
    final double distance = distance(x, y);
    return distance < maxDistance;
  }

  boolean isNear(@Nonnull final Area area, final double maxDistance) {
    final double distance = distance(area);
    return distance < maxDistance;
  }

  @Nonnull
  Area extend(@Nonnull final Area area, final long trackNr) {
    extend(area.getMinX(), area.getMinY(), trackNr);
    if (!area.isPoint()) {
      extend(area.getMaxX(), area.getMaxY(), trackNr);
    }
    return this;
  }

  @Nonnull
  Area extend(final int x, final int y, final long trackNr) {
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
    if (isPoint()) {
      return MathUtil.distance(minX, minY, x, y);
    }
    return MathUtil.distanceRectToPoint(minX, minY, maxX, maxY, x, y);
  }

  private double distance(@Nonnull final Area area) {
    if (area.isPoint()) {
      return distance(area.getMinX(), area.getMinY());
    }
    if (isPoint()) {
      return MathUtil.distanceRectToPoint(
              area.getMinX(), area.getMinY(),
              area.getMaxX(), area.getMaxY(),
              minX,
              minY
      );
    }
    return MathUtil.distanceRect(
            minX, minY,
            maxX, maxY,
            area.getMinX(), area.getMinY(),
            area.getMaxX(), area.getMaxY()
    );
  }
}
