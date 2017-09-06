/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito;

import ch.retomerz.mosquito.ev3.Ev3Executor;
import ch.retomerz.mosquito.ev3.OutputPort;
import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.event.HidServicesEvent;

import java.util.List;

public final class TestUSB {
  public static void main(String[] args) {

    final HidServices hidServices = HidManager.getHidServices();
    try {

      addVerboseListener(hidServices);

      final HidDevice ev3 = getEV3(hidServices);
      ev3.open();
      try {

        final Ev3Executor exe = new Ev3Executor(ev3);

        exe.turnMotorAtPowerForTime(OutputPort.A, 10, 0, 80, 0, false);


      } finally {
        ev3.close();
      }

    } finally {
      hidServices.shutdown();
    }
  }

  private static void addVerboseListener(final HidServices hidServices) {
    hidServices.addHidServicesListener(new HidServicesListener() {
      @Override
      public void hidDeviceAttached(HidServicesEvent hidServicesEvent) {
        System.out.println("hidDeviceAttached: " + hidServicesEvent);
      }

      @Override
      public void hidDeviceDetached(HidServicesEvent hidServicesEvent) {
        System.out.println("hidDeviceDetached: " + hidServicesEvent);
      }

      @Override
      public void hidFailure(HidServicesEvent hidServicesEvent) {
        System.out.println("hidFailure: " + hidServicesEvent);
      }
    });
  }

  private static HidDevice getEV3(final HidServices hidServices) {
    final List<HidDevice> hidDevices = hidServices.getAttachedHidDevices();
    for (final HidDevice hidDevice : hidDevices) {
      if ("EV3".equals(hidDevice.getProduct())) {
        return hidDevice;
      }
    }
    throw new IllegalStateException("No EV3 found");
  }
}
