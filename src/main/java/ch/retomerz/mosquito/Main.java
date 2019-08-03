/*
 * Copyright (c) 2018-2019 Reto Merz
 */
package ch.retomerz.mosquito;

import ch.retomerz.mosquito.detect.DetectLaser;
import ch.retomerz.mosquito.tf.TfExecutor;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

public final class Main {
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        showFrame();
      }
    });
  }

  private static void showFrame() {

    final int width = 1920;
    final int height = 1200;
    final Camera camera = Camera.open(width, height, true);

    final TfExecutor tfExecutor = TfExecutor.create();

    final CamPane camPane = new CamPane(camera);

    final Focus focus = new Focus(camPane, camera, tfExecutor);

    final JPanel camCenterPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    camCenterPane.add(camPane);

    final JSlider slider = new JSlider(JSlider.HORIZONTAL, 50, 500, 400);
    slider.setPaintTicks(true);
    slider.setPaintTrack(true);

    final JLabel valueLabel = new JLabel(String.valueOf(slider.getValue()));
    slider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        valueLabel.setText(String.valueOf(slider.getValue()));
      }
    });

    final JButton findLaserButton = new JButton("find laser");
    findLaserButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        final Point laser = new DetectLaser().detect(camera.getImage());
        System.out.println("Found laser @ " + laser);
        camPane.setLaser(laser);
      }
    });

    final JButton startDumpButton = new JButton("start dump");
    startDumpButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        new Thread(new Runnable() {
          @Override
          public void run() {
            final File dir = new File("C:\\temp\\_dump");
            int count = 0;
            while (!Thread.currentThread().isInterrupted()) {
              final BufferedImage image = camera.getImage();
              if (image != null) {
                count++;
                try {
                  ImageIO.write(image, "png", new File(dir, "c" + count + ".png"));
                  Thread.sleep(1000L / 120L);
                } catch (Exception e1) {
                  throw new RuntimeException(e1);
                }
              }
            }
          }
        }, "Test").start();
      }
    });

    final JPanel moveButtonsPane = new JPanel(new GridLayout(3, 3));

    moveButtonsPane.add(Box.createGlue());
    moveButtonsPane.add(createButton("arrow-up.png", e -> tfExecutor.moveY(slider.getValue())));
    moveButtonsPane.add(Box.createGlue());

    moveButtonsPane.add(createButton("arrow-left.png", e -> tfExecutor.moveX(slider.getValue() * -1)));
    moveButtonsPane.add(createButton("center.png", e -> tfExecutor.center()));
    moveButtonsPane.add(createButton("arrow-right.png", e -> tfExecutor.moveX(slider.getValue())));

    moveButtonsPane.add(Box.createGlue());
    moveButtonsPane.add(createButton("arrow-down.png", e -> tfExecutor.moveY(slider.getValue() * -1)));
    moveButtonsPane.add(Box.createGlue());

    final Icon laserOff = createIcon("laser-off.png");
    final Icon laserOn = createIcon("laser-on.png");
    final JButton laserButton = createButton(tfExecutor.getLaserState() ? laserOn : laserOff, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        final JButton laserButton = (JButton) e.getSource();
        tfExecutor.toggleLaserState();
        laserButton.setIcon(tfExecutor.getLaserState() ? laserOn : laserOff);
      }
    });

    final Icon focusStart = createIcon("focus.png");
    final Icon focusStop = createIcon("stop.png");
    final JButton focusButton = createButton(focusStart, new ActionListener() {
      boolean running;

      @Override
      public void actionPerformed(ActionEvent e) {
        final JButton focusButton = (JButton) e.getSource();
        if (running) {
          focusButton.setEnabled(false);
          if (focus.close(5, TimeUnit.SECONDS)) {
            focusButton.setIcon(focusStart);
            focusButton.setEnabled(true);
            running = false;
          } else {
            focusButton.setText("ERROR focus");
          }
        } else {
          running = true;
          focusButton.setIcon(focusStop);
          focus.now(new Runnable() {
            @Override
            public void run() {
              running = false;
              focusButton.setText("start focus");
            }
          });
        }
      }
    });

    final JPanel buttonsPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
    buttonsPane.add(moveButtonsPane);
    buttonsPane.add(laserButton);
    buttonsPane.add(focusButton);
    buttonsPane.add(slider);
    buttonsPane.add(valueLabel);
    buttonsPane.add(findLaserButton);
    buttonsPane.add(startDumpButton);

    final JPanel mainPane = new JPanel(new BorderLayout());
    mainPane.add(new JScrollPane(camCenterPane), BorderLayout.NORTH);
    mainPane.add(buttonsPane, BorderLayout.CENTER);

    final JFrame frame = new JFrame("Mosquito");
    frame.getContentPane().add(mainPane);
    frame.setSize(1300, 830);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent event) {
        try {
          try {
            if (!focus.close(5, TimeUnit.SECONDS)) {
              System.out.println("Could not close focus");
            }
            if (!camera.close(5, TimeUnit.SECONDS)) {
              System.out.println("Could not close camera");
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        } finally {
          tfExecutor.close();
        }
      }
    });
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  @Nonnull
  private static Icon createIcon(@Nonnull final String iconName) {
    return new ImageIcon(Main.class.getResource(iconName));
  }

  @Nonnull
  private static JButton createButton(@Nonnull final String iconName, @Nonnull final ActionListener action) {
    return createButton(createIcon(iconName), action);
  }

  @Nonnull
  private static JButton createButton(@Nonnull final Icon icon, @Nonnull final ActionListener action) {
    final JButton ret = new JButton(icon);
    ret.setBorder(BorderFactory.createEmptyBorder());
    ret.setBorderPainted(false);
    ret.setOpaque(false);
    ret.setContentAreaFilled(false);
    ret.setFocusable(false);
    ret.addActionListener(action);
    return ret;
  }
}
