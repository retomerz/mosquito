/*
 * Copyright (c) 2019 Reto Merz
 */
package ch.retomerz.mosquito.ueye;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public final class TestUEye {

  private static volatile int[] lastBuf;
  private static volatile boolean closing;

  public static void main(String[] args) throws Exception {

    final int width = 1920;
    final int height = 1200;
    final int bytesPerPixel = 32;

    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {

        final JPanel panel = new JPanel(new BorderLayout()) {
          BufferedImage img;

          @Override
          protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2 = (Graphics2D) g;

            int[] buf = lastBuf;
            if (buf != null) {
              lastBuf = null;
              img = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
              int pos = 0;
              for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                  img.setRGB(x, y, buf[pos]);
                  pos++;
                }
              }
              System.out.println("New paint " + System.currentTimeMillis());
            }
            if (img != null) {
              g2.drawImage(img, 0, 0, width, height, null);
            }

            if (!closing) {
              EventQueue.invokeLater(this::repaint);
            }

          }
        };

        final JFrame frame = new JFrame();
        frame.getContentPane().add(panel);
        frame.setSize(800, 600);
        frame.addWindowListener(new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            closing = true;
          }
        });
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

      }
    });

    final IntByReference phCam = new IntByReference(0); // LATER: switch to device id.
    int res = UEyeLibrary.INSTANCE.is_InitCamera(phCam, Pointer.NULL);
    check(res);

    final int hCam = phCam.getValue();

    res = UEyeLibrary.INSTANCE.is_SetColorMode(hCam, 0);
    check(res);

    final PointerByReference ppcImgMem = new PointerByReference();
    final IntByReference pid = new IntByReference();
    res = UEyeLibrary.INSTANCE.is_AllocImageMem(hCam, width, height, bytesPerPixel, ppcImgMem, pid);
    check(res);

    while (!closing) {

      if (lastBuf == null) {
        res = UEyeLibrary.INSTANCE.is_SetImageMem(hCam, ppcImgMem.getValue(), pid.getValue());
        check(res);

        res = UEyeLibrary.INSTANCE.is_FreezeVideo(hCam, 0);
        check(res);

        int[] buf = new int[width * height];
        ppcImgMem.getValue().read(0, buf, 0, width * height);
        lastBuf = buf;
        System.out.println("New image " + System.currentTimeMillis());
      }
      Thread.sleep(100);
    }

    //final PointerByReference ppcImgMemDest = new PointerByReference();
    //res = UEyeLibrary.INSTANCE.is_CopyImageMem(phCam.getValue(), ppcImgMem.getValue(), pid.getValue(), );

    res = UEyeLibrary.INSTANCE.is_FreeImageMem(hCam, ppcImgMem.getValue(), pid.getValue());
    check(res);

    res = UEyeLibrary.INSTANCE.is_ExitCamera(hCam);
    check(res);

  }

  private static void check(final int res) {
    if (res != UEyeLibrary.IS_SUCCESS) {
      System.out.println("Fail with " + res);
      System.exit(1);
    }
  }
}
