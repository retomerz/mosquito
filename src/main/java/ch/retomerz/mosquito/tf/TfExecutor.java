/*
 * Copyright (c) 2018 Reto Merz
 */
package ch.retomerz.mosquito.tf;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickServo;
import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NetworkException;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import javax.annotation.Nonnull;
import java.io.IOException;

public final class TfExecutor {

  private static final short X_SERVO = (short) 6;
  private static final short Y_SERVO = (short) 0;
  private static final short LAYER_RELAY = (short) 1;

  @Nonnull
  private final IPConnection connection;

  @Nonnull
  private final BrickServo servo;

  private final short xServo;
  private final short yServo;
  private short xPos;
  private short yPos;

  @Nonnull
  private final BrickletDualRelay relay;

  private final short laserRelay;
  private boolean laserState;

  private TfExecutor(
          @Nonnull final IPConnection connection,
          @Nonnull final BrickServo servo,
          final short xServo,
          final short yServo,
          @Nonnull final BrickletDualRelay relay,
          final short laserRelay
  ) throws TimeoutException, NotConnectedException {
    this.connection = connection;
    this.servo = servo;
    this.xServo = xServo;
    this.yServo = yServo;
    servo.enable(xServo);
    servo.enable(yServo);
    xPos = servo.getPosition(xServo);
    yPos = servo.getPosition(yServo);
    this.relay = relay;
    laserState = relay.getState().relay1;
    this.laserRelay = laserRelay;
  }

  public void close() {
    try {
      connection.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void center() {
    xPos = 0;
    yPos = 0;
    try {
      servo.setPosition(xServo, xPos);
      servo.setPosition(yServo, yPos);
    } catch (TimeoutException | NotConnectedException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean getLaserState() {
    return laserState;
  }

  public void toggleLaserState() {
    laserState = !laserState;
    try {
      relay.setSelectedState(laserRelay, laserState);
    } catch (TimeoutException | NotConnectedException e) {
      throw new RuntimeException(e);
    }
  }

  public void move(final boolean x, final int add) {
    if (x) {
      moveX(add);
    } else {
      moveY(add);
    }
  }

  public void moveX(final int add) {
    xPos += (short) add;
    try {
      servo.setPosition(xServo, xPos);
    } catch (TimeoutException | NotConnectedException e) {
      throw new RuntimeException(e);
    }
  }

  public void moveY(final int add) {
    yPos += (short) add;
    try {
      servo.setPosition(yServo, yPos);
    } catch (TimeoutException | NotConnectedException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  public static TfExecutor create() {
    final IPConnection connection = new IPConnection();
    try {
      connection.connect("localhost", 4223);
      return new TfExecutor(
              connection,
              new BrickServo("6CRDUM", connection),
              X_SERVO,
              Y_SERVO,
              new BrickletDualRelay("AdR", connection),
              LAYER_RELAY
      );
    } catch (NetworkException | AlreadyConnectedException | NotConnectedException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }
}
