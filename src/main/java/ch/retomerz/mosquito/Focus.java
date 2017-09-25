/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito;

import ch.retomerz.mosquito.ev3.Ev3Executor;
import ch.retomerz.mosquito.ev3.OutputPort;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

final class Focus {

  private final CamPane camPane;
  private final Camera camera;
  private final Ev3Executor ev3Exe;
  private volatile boolean close;
  private volatile boolean closed;
  private Point latestLaser;

  Focus(CamPane camPane, Camera camera, Ev3Executor ev3Exe) {
    this.camPane = camPane;
    this.camera = camera;
    this.ev3Exe = ev3Exe;
  }

  void now(final Runnable onFinish) {
    close = false;
    closed = false;

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          nowImpl();
        } finally {
          closed = true;
        }
        EventQueue.invokeLater(onFinish);
      }
    }, "focus").start();
  }

  boolean close(final long timeout, final TimeUnit unit) {
    close = true;
    final long startNs = System.nanoTime();
    while (!closed) {
      final long durationNs = System.nanoTime() - startNs;
      if (durationNs > unit.toNanos(timeout)) {
        return false;
      }
      try {
        Thread.sleep(32);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
    }
    return true;
  }

  private void nowImpl() {

    final Point target = camPane.getTarget();
    if (target == null) {
      System.out.println("No target");
      return;
    }

    final ToAxisMover xMover = new ToAxisMover("X", ev3Exe, OutputPort.B);
    final ToAxisMover yMover = new ToAxisMover("Y", ev3Exe, OutputPort.A);

    while (!close) {

      final Point laser = findLaser();
      camPane.setLaser(laser);
      if (close || laser == null) {
        return;
      }

      final int distanceX = laser.x - target.x;
      final int distanceY = laser.y - target.y;

      xMover.move(distanceX);
      yMover.move(distanceY);

      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private Point findLaser() {
    int laserThreshold = 244;
    while (!close) {

      final Point laser = findLaserImpl(laserThreshold);
      if (laser != null) {
        System.out.println("Laser found with threshold " + laserThreshold);
        latestLaser = laser;
        return laser;
      }

      laserThreshold--;
      if (laserThreshold < 220) {
        System.out.println("No laser found");
        return null;
      }

    }
    return null;
  }

  private Point findLaserImpl(final int threshold) {
    camPane.clearNoise();

    final Image image = camera.getImage();
    if (image == null) {
      return null;
    }
    final BufferedImage b = (BufferedImage) image;
    final int width = image.getWidth(null);
    final int height = image.getHeight(null);

    return findLaserImpl(
            threshold,
            b,
            width,
            height
    );
  }

  private Point findLaserImpl(
          final int threshold,
          final BufferedImage b,
          final int width,
          final int height
  ) {

    int foundX = -1;
    int foundY = -1;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {

        final int rgb = b.getRGB(x, y);
        final Color c = new Color(rgb);
        if (c.getRed() > threshold && c.getGreen() > threshold && c.getBlue() > threshold) {
          camPane.addNoise(x, y);
          if (foundX == -1) {
            foundX = x;
            foundY = y;
          }
        }
      }
    }
    if (foundX != -1) {
      return new Point(foundX, foundY);
    }
    return null;
  }
}
