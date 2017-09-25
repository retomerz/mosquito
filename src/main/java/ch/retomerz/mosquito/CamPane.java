/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class CamPane extends JPanel {

  private final Camera camera;
  private volatile Point target;
  private volatile Point laser;
  private final List<Point> noise = new CopyOnWriteArrayList<>();

  public CamPane(final Camera camera) {
    this.camera = camera;
    setOpaque(true);
    setBackground(Color.white);
    setPreferredSize(new Dimension(camera.getWidth() / 2, camera.getHeight() / 2));
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        target = new Point(e.getX(), e.getY());
      }
    });
  }

  public Point getTarget() {
    return target;
  }

  public void setLaser(final Point laser) {
    this.laser = laser;
  }

  public void clearNoise() {
    noise.clear();
  }

  public void addNoise(final int x, final int y) {
    noise.add(new Point(x, y));
  }

  @Override
  protected void paintComponent(final Graphics g) {
    super.paintComponent(g);

    final Image image = camera.getImage();
    if (image != null) {
      g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }

    if (target != null) {
      final int radius = 4;
      g.setColor(Color.green);
      g.fillOval(
              (target.x / 2) - radius,
              (target.y / 2) - radius,
              radius * 2,
              radius * 2
      );
    }

    if (laser != null) {
      g.setColor(Color.blue);
      g.fillRect((laser.x / 2) - 5, (laser.y / 2) - 5, 10, 10);
    }

    g.setColor(Color.red);
    for (final Point p : noise) {
      g.fillRect((p.x / 2), (p.y / 2), 1, 1);
    }

    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        repaint();
      }
    });
  }
}
