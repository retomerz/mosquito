/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito.tf;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickServo;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NetworkException;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.io.IOException;

public final class TfExecutor {

  public static final short X_SERVO = (short) 6;
  public static final short Y_SERVO = (short) 0;

  private final IPConnection connection;
  private final BrickServo servo;
  private final short xServo;
  private final short yServo;
  private short xPos;
  private short yPos;

  private TfExecutor(
          final IPConnection connection,
          final BrickServo servo,
          final short xServo,
          final short yServo
  ) throws TimeoutException, NotConnectedException {
    this.connection = connection;
    this.servo = servo;
    this.xServo = xServo;
    this.yServo = yServo;
    xPos = servo.getPosition(xServo);
    yPos = servo.getPosition(yServo);
  }

  public void close() {
    try {
      connection.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void move(final boolean x, final int add) {
    if (x) {
      moveX(add);
    } else {
      moveY(add);
    }
  }

  public void moveX(int add) {
    xPos += (short) add;
    try {
      servo.setPosition(xServo, xPos);
    } catch (TimeoutException | NotConnectedException e) {
      throw new RuntimeException(e);
    }
  }

  public void moveY(int add) {
    yPos += (short) add;
    try {
      servo.setPosition(yServo, yPos);
    } catch (TimeoutException | NotConnectedException e) {
      throw new RuntimeException(e);
    }
  }

  public static TfExecutor create() {
    final IPConnection connection = new IPConnection();
    try {
      connection.connect("localhost", 4223);
      return new TfExecutor(
              connection,
              new BrickServo("6CRDUM", connection),
              X_SERVO,
              Y_SERVO
      );
    } catch (NetworkException | AlreadyConnectedException | NotConnectedException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }
}
