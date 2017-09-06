/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito;

import org.bytedeco.javacv.*;

import static org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.cvFlip;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;

public class Test implements Runnable {
  final int INTERVAL = 100;///you may use interval
  CanvasFrame canvas = new CanvasFrame("Web Cam");

  public Test() {
    canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
  }

  public void run() {

    try {
      //FrameGrabber grabber = new VideoInputFrameGrabber(0); // 1 for next camera
      FrameGrabber grabber = new OpenCVFrameGrabber(0); // 1 for next camera
      //FrameGrabber grabber = new DC1394FrameGrabber(0); // 1 for next camera
      try {
        //grabber.setImageWidth(1920);
        //grabber.setImageHeight(1080);
        grabber.setImageWidth(4032);
        grabber.setImageHeight(3024);

        grabber.setImageMode(FrameGrabber.ImageMode.GRAY);
        //grabber.setPixelFormat(16);
        grabber.setFrameRate(1);
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        IplImage img;
        try {
          grabber.start();
          while (true) {
            Frame frame = grabber.grab();

            img = converter.convert(frame);


            canvas.showImage(converter.convert(img));

            Thread.sleep(INTERVAL);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      } finally {
        grabber.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    Test gs = new Test();
    Thread th = new Thread(gs);
    th.start();
  }
}