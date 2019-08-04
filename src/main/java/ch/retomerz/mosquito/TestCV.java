/*
 * Copyright (c) 2019 Reto Merz
 */
package ch.retomerz.mosquito;

import ch.retomerz.mosquito.detect.VideoTracker;
import org.bytedeco.ffmpeg.global.avutil;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

public final class TestCV {

  private static final boolean HEADLESS = Boolean.valueOf(System.getProperty("mosquito.testCvHeadless", "false"));
  private static final boolean SCALE_IMAGE = Boolean.valueOf(System.getProperty("mosquito.testCvScaleImage", "true"));

  private static final File TEST_VID = new File("C:\\_mosquito\\v3-copy.avi");

  public static void main(String[] args) throws Exception {

    avutil.av_log_set_level(avutil.AV_LOG_ERROR);

    if (HEADLESS) {
      final VideoTracker tracker = new VideoTracker(TEST_VID);
      long count = 0;
      while (tracker.trackFrame() != null) {
        tracker.trackFrame();
        count++;
        if (count % 20 == 0) {
          System.out.println("FPS: " + tracker.getFPS());
        }
      }
    } else {
      EventQueue.invokeLater(TestCV::runMain);
    }
  }

  private static void runMain() {

    final JFrame window = new JFrame();

    final JPanel panel = new JPanel(new BorderLayout()) {

      VideoTracker videoTracker;

      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
          if (videoTracker == null) {
            videoTracker = new VideoTracker(TEST_VID);
          }

          final BufferedImage img = videoTracker.trackFrame();
          if (img == null) {
            window.setTitle(window.getTitle() + " - Finished");
            return;
          }

          window.setTitle(
                  "Frame: " + videoTracker.getFrameNr() +
                          ", FPS: " + videoTracker.getFPS() +
                          ", areas: " + videoTracker.getAreaCount() +
                          ", skipped frames: " + videoTracker.getSkippedFrames());

          if (SCALE_IMAGE) {
            BufferedImage after = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            final AffineTransform at = new AffineTransform();
            at.scale(0.7, 0.7);
            final AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            after = scaleOp.filter(img, after);
            g.drawImage(after, 0, 0, img.getWidth(), img.getHeight(), null);
          } else {
            g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
          }

          EventQueue.invokeLater(this::repaint);

        } catch (Exception e) {
          e.printStackTrace();
          System.exit(1);
        }
      }
    };

    window.setSize(1400, 900);
    window.getContentPane().add(panel);
    window.setVisible(true);
  }
}
