/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito;

import ch.retomerz.mosquito.ev3.Ev3Executor;
import ch.retomerz.mosquito.ev3.OutputPort;

final class ToAxisMover {

  private final String name;
  private final Ev3Executor exe;
  private final OutputPort port;
  private int lastDistance = -1;
  private int duration = 10;

  ToAxisMover(final String name, final Ev3Executor exe, final OutputPort port) {
    this.name = name;
    this.exe = exe;
    this.port = port;
  }

  void move(final int distance, final boolean inversePower) {
    if (distance < 10 && distance > -10) {
      return; // close enough
    }

    if (lastDistance != -1) {
      final int difference = Math.abs(lastDistance - distance);

      if (difference > 400) {
        System.out.println("Skip because distance to big of " + name);
      }

      if (difference < 10) {
        duration += 10;
        System.out.println("Increment speed of " + name);
      } else {
        duration = 10;
        System.out.println("Reset speed of " + name);
      }
    }
    lastDistance = distance;

    if (distance > 0) {
      exe.turnMotorAtPowerForTime(port, inversePower ? 10 : -10, 0, duration, 0, false);
    } else {
      exe.turnMotorAtPowerForTime(port, inversePower ? -10 : 10, 0, duration, 0, false);
    }
  }
}
