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
import java.awt.image.BufferedImage;
import java.io.File;

public final class TestCV {

  private static final File TEST_VID = new File("C:\\_mosquito\\v3-copy.avi");

  public static void main(String[] args) throws Exception {

    avutil.av_log_set_level(avutil.AV_LOG_ERROR);

    if (false) {
      final VideoTracker tracker = new VideoTracker(TEST_VID);
      long count = 0;
      while (true) {
        tracker.trackFrame();
        count++;
        if (count % 20 == 0) {
          System.out.println("FPS: " + tracker.getFPS());
        }
      }
    }

    EventQueue.invokeLater(TestCV::runMain);
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
                          ", hit: " + videoTracker.getHitNoiseCount());

          g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);

          EventQueue.invokeLater(this::repaint);

        } catch (Exception e) {
          e.printStackTrace();
          System.exit(1);
        }
      }
    };

    window.setSize(800, 600);
    window.getContentPane().add(panel);
    window.setVisible(true);

  }
}
