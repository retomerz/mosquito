/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito;

import ch.retomerz.mosquito.ev3.Ev3Executor;
import ch.retomerz.mosquito.ev3.Ev3Util;
import ch.retomerz.mosquito.ev3.OutputPort;
import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;

import javax.imageio.ImageIO;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

public final class Main {
  private static final boolean USE_EV3 = false;

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
    final int height = 1080;
    final Camera camera = Camera.open(width, height, true);

    final HidServices hidServices = HidManager.getHidServices();

    final HidDevice ev3Device;
    final Ev3Executor ev3Exe;
    if (USE_EV3) {
      ev3Device = Ev3Util.findEv3(hidServices);
      ev3Device.open();
      ev3Exe = new Ev3Executor(ev3Device);
    } else {
      ev3Device = null;
      ev3Exe = null;
    }

    final CamPane camPane = new CamPane(camera);

    final Focus focus = new Focus(camPane, camera, ev3Exe);

    final JPanel camCenterPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    camCenterPane.add(camPane);

    final JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
    slider.setPaintTicks(true);
    slider.setPaintTrack(true);

    final JLabel valueLabel = new JLabel(String.valueOf(slider.getValue()));
    slider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        valueLabel.setText(String.valueOf(slider.getValue()));
      }
    });

    final JButton moveLeftButton = new JButton("<");
    moveLeftButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ev3Exe.turnMotorAtPowerForTime(OutputPort.B, -10, 0, slider.getValue(), 0, false);
      }
    });

    final JButton moveRightButton = new JButton(">");
    moveRightButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ev3Exe.turnMotorAtPowerForTime(OutputPort.B, 10, 0, slider.getValue(), 0, false);
      }
    });

    final JButton moveUpButton = new JButton("^");
    moveUpButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ev3Exe.turnMotorAtPowerForTime(OutputPort.A, -10, 0, slider.getValue(), 0, false);
      }
    });

    final JButton moveDownButton = new JButton("v");
    moveDownButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ev3Exe.turnMotorAtPowerForTime(OutputPort.A, 10, 0, slider.getValue(), 0, false);
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

    final JButton focusButton = new JButton("start focus");
    focusButton.addActionListener(new ActionListener() {
      boolean running;

      @Override
      public void actionPerformed(ActionEvent e) {
        if (running) {
          focusButton.setEnabled(false);
          if (focus.close(5, TimeUnit.SECONDS)) {
            focusButton.setText("start focus");
            focusButton.setEnabled(true);
            running = false;
          } else {
            focusButton.setText("ERROR focus");
          }
        } else {
          running = true;
          focusButton.setText("stop focus");
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
    buttonsPane.add(moveLeftButton);
    buttonsPane.add(moveRightButton);
    buttonsPane.add(moveUpButton);
    buttonsPane.add(moveDownButton);
    buttonsPane.add(focusButton);
    buttonsPane.add(slider);
    buttonsPane.add(valueLabel);
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
          try {
            if (ev3Device != null) {
              ev3Device.close();
            }
          } finally {
            hidServices.shutdown();
          }
        }
      }
    });
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
