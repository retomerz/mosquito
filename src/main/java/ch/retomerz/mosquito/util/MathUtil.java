/*
 * Copyright (c) 2019 Reto Merz
 */
package ch.retomerz.mosquito.util;

public final class MathUtil {
  private MathUtil() {
  }

  public static double distance(final int x1, final int y1, final int x2, final int y2) {
    return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
  }

  public static double distanceRect(
          final int xMin1,
          final int yMin1,
          final int xMax1,
          final int yMax1,
          final int x2, final int y2
  ) {
    final int dx = Math.max(0, Math.max(xMin1 - x2, x2 - xMax1));
    final int dy = Math.max(0, Math.max(yMin1 - y2, y2 - yMax1));
    return Math.sqrt(dx * dx + dy * dy);
  }
}
