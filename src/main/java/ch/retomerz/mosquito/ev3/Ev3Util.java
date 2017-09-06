/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito.ev3;

import org.hid4java.HidDevice;
import org.hid4java.HidServices;

import java.util.List;

public final class Ev3Util {
  private Ev3Util() {
  }

  public static HidDevice findEv3(final HidServices hidServices) {
    final List<HidDevice> hidDevices = hidServices.getAttachedHidDevices();
    for (final HidDevice hidDevice : hidDevices) {
      if ("EV3".equals(hidDevice.getProduct())) {
        return hidDevice;
      }
    }
    throw new IllegalStateException("No EV3 found");
  }
}
