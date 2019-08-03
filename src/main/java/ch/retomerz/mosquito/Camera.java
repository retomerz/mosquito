/*
 * Copyright (c) 2017-2019 Reto Merz
 */
package ch.retomerz.mosquito;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

final class Camera {

  private final int width;
  private final int height;
  private volatile boolean close;
  private volatile boolean closed;
  private volatile BufferedImage image;

  private Camera(int width, int height) {
    this.width = width;
    this.height = height;
  }

  int getWidth() {
    return width;
  }

  int getHeight() {
    return height;
  }

  BufferedImage getImage() {
    return image;
  }

  boolean close(final long timeout, final TimeUnit unit) throws InterruptedException {
    close = true;
    final long startNs = System.nanoTime();
    while (!closed) {
      final long durationNs = System.nanoTime() - startNs;
      if (durationNs > unit.toNanos(timeout)) {
        return false;
      }
      Thread.sleep(32);
    }
    return true;
  }

  @Nonnull
  static Camera open(final int width, final int height, final boolean color) {
    final Camera ret = new Camera(width, height);
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          final FrameGrabber grabber = new OpenCVFrameGrabber(0);
          grabber.setImageWidth(width);
          grabber.setImageHeight(height);
          grabber.setImageMode(color ? FrameGrabber.ImageMode.COLOR : FrameGrabber.ImageMode.GRAY);
          grabber.start();
          try {
            final Java2DFrameConverter converter = new Java2DFrameConverter();
            final boolean flipChannels = false;
            final double inverseGamma = 1.0;
            while (!ret.close) {
              final Frame frame = grabber.grab();
              ret.image = converter.getBufferedImage(
                      frame,
                      Java2DFrameConverter.getBufferedImageType(frame) == BufferedImage.TYPE_CUSTOM ? 1.0 : inverseGamma,
                      flipChannels,
                      null
              );
              Thread.yield();
              //Thread.sleep(1000 / 60);
            }
          } finally {
            grabber.close();
          }
        } catch (Throwable e) {
          e.printStackTrace();
        } finally {
          ret.closed = true;
        }
      }
    }, "Camera").start();
    return ret;
  }
}
