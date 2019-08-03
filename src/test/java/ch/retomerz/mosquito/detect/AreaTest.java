/*
 * Copyright (c) 2019 Reto Merz
 */
package ch.retomerz.mosquito.detect;

import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;

public final class AreaTest {

  @Test
  public void extend() {
    assertArea(new Area(10, 20, 0).extend(10, 21, 1),
            10, 20, 10, 21);

    assertArea(new Area(10, 20, 0).extend(11, 20, 1),
            10, 20, 11, 20);

    assertArea(new Area(10, 20, 0).extend(11, 19, 1),
            10, 19, 11, 20);
  }

  private static void assertArea(
          @Nonnull final Area have,
          final int wantMinX, final int wantMinY,
          final int wantMaxX, final int wantMaxY
  ) {
    Assert.assertEquals(wantMinX, have.getMinX());
    Assert.assertEquals(wantMinY, have.getMinY());
    Assert.assertEquals(wantMaxX, have.getMaxX());
    Assert.assertEquals(wantMaxY, have.getMaxY());
  }
}
