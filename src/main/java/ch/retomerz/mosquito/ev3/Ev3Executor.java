/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito.ev3;

import org.hid4java.HidDevice;

public final class Ev3Executor {

  private final HidDevice ev3;

  public Ev3Executor(HidDevice ev3) {
    this.ev3 = ev3;
  }

  public void turnMotorAtPowerForTime(OutputPort port, int power, int msRampUp, int msConstant, int msRampDown, boolean brake) {
    final byte[] data = CommandBuilder.turnMotorAtPowerForTime(port, power, msRampUp, msConstant, msRampDown, brake);
    ev3.write(data, data.length, (byte) 0);
  }
}
