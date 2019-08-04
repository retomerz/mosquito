/*
 * Copyright (c) 2019 Reto Merz
 */
package ch.retomerz.mosquito.detect;

import ch.retomerz.mosquito.util.ColorUtil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public final class VideoTracker {

  private static final boolean USE_CONVERTER = Boolean.valueOf(System.getProperty("mosquito.useConverter", "false"));

  @Nonnull
  private final FrameGrabber grabber;

  @Nonnull
  private final Java2DFrameConverter converter;

  private final int width;

  private final int height;

  @Nonnull
  private final int[] imageBufA;

  @Nonnull
  private final int[] imageBufB;

  @Nonnull
  private final int[] dirtyImage;

  @Nonnull
  private final Tracker tracker;

  private final long startNsec;

  private long frameNr;

  private boolean imageBufBActive;

  private boolean firstFrameProcessed;

  public VideoTracker(@Nonnull final File file) throws Exception {
    grabber = new FFmpegFrameGrabber(file);
    converter = new Java2DFrameConverter();
    grabber.start();
    width = grabber.getImageWidth();
    height = grabber.getImageHeight();
    imageBufA = new int[width * height];
    imageBufB = new int[width * height];
    dirtyImage = new int[width * height];
    tracker = new Tracker(width, height);
    startNsec = System.nanoTime();
  }

  public long getFrameNr() {
    return frameNr;
  }

  public long getAreaCount() {
    return tracker.getAreaCount();
  }

  public long getSkippedFrames() {
    return tracker.getSkippedFrames();
  }

  public long getFPS() {
    final long durationNsec = System.nanoTime() - startNsec;
    final long durationSec = TimeUnit.NANOSECONDS.toSeconds(durationNsec);
    if (frameNr == 0) {
      return 0;
    }
    if (durationSec == 0) {
      return 0;
    }
    return frameNr / durationSec;
  }

  @Nullable
  public BufferedImage trackFrame() throws Exception {

    final Frame frame = grabber.grab();
    if (frame == null) {
      return null;
    }
    frameNr++;

    final int[] lastImage = imageBufBActive ? imageBufA : imageBufB;
    final int[] currImage = imageBufBActive ? imageBufB : imageBufA;
    imageBufBActive = !imageBufBActive;

    final BufferedImage img;
    if (USE_CONVERTER) {
      img = converter.getBufferedImage(frame);
      getRGB(img, currImage);

    } else {

      final ByteBuffer buf = (ByteBuffer) frame.image[0];
      int bufPos = 0;
      int imgPos = 0;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          final int b = buf.get(bufPos++);
          final int g = buf.get(bufPos++);
          final int r = buf.get(bufPos++);
          final int rgb = ColorUtil.toRGB(r, g, b);
          currImage[imgPos++] = rgb;
        }
      }

      img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      final int[] imageData = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
      System.arraycopy(currImage, 0, imageData, 0, currImage.length);
    }

    if (firstFrameProcessed) {
      tracker.track(img, currImage, lastImage, dirtyImage);
    } else {
      firstFrameProcessed = true;
    }

    return img;
  }

  private void getRGB(@Nonnull final BufferedImage img, @Nonnull final int[] currImage) {
    //img.getRGB(0, 0, width, height, currImage, 0, 1);

    int pos = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        currImage[pos] = img.getRGB(x, y);
        pos++;
      }
    }
  }
}
