/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito.detect;

import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public final class DetectLaserTest {

  @Test
  public void detect() throws Exception {
    _d(1, new Point(85, 129));
  }

  private static void _d(final int nr, @Nullable final Point want) throws Exception {
    final InputStream in = DetectLaserTest.class.getResourceAsStream("laser" + nr + ".png");
    if (in == null) {
      Assert.fail("Resource not found");
    }
    try {
      final BufferedImage img = ImageIO.read(in);
      final Point have = new DetectLaser().detect(img);
      Assert.assertEquals(want, have);
    } finally {
      in.close();
    }
  }
}
