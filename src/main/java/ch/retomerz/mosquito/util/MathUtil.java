/*
 * Copyright (c) 2019 Reto Merz
 */
package ch.retomerz.mosquito.util;

public final class MathUtil {
  private MathUtil() {
  }

  public static double distance(final int x1, final int y1, final int x2, final int y2) {
    //return Point2D.distance(x1, y1, x2, y2);
    return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
  }

  public static double distanceRectToPoint(
          final int xMin1, final int yMin1,
          final int xMax1, final int yMax1,
          final int x2, final int y2
  ) {
    final int dx = Math.max(0, Math.max(xMin1 - x2, x2 - xMax1));
    final int dy = Math.max(0, Math.max(yMin1 - y2, y2 - yMax1));
    return Math.sqrt(dx * dx + dy * dy);
  }

  public static double distanceRect(
          final int xMin1, final int yMin1,
          final int xMax1, final int yMax1,
          final int xMin2, final int yMin2,
          final int xMax2, final int yMax2
  ) {
    final boolean left = xMax2 < xMin1;
    final boolean right = xMax1 < xMin2;
    final boolean bottom = yMax2 < yMin1;
    final boolean top = yMax1 < yMin2;
    if (top && left)
      return distance(xMin1, yMax1, xMax2, yMin2);
    else if (left && bottom)
      return distance(xMin1, yMin1, xMax2, yMax2);
    else if (bottom && right)
      return distance(xMax1, yMin1, xMin2, yMax2);
    else if (right && top)
      return distance(xMax1, yMax1, xMin2, yMin2);
    else if (left)
      return xMin1 - xMax2;
    else if (right)
      return xMin2 - xMax1;
    else if (bottom)
      return yMin1 - yMax2;
    else if (top)
      return yMin2 - yMax1;
    else
      return 0;
  }
}
