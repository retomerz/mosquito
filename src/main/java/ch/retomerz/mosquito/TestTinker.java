/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito;

import com.tinkerforge.BrickServo;
import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.IPConnection;

public final class TestTinker {
  public static void main(String[] args) throws Exception {
    final IPConnection connection = new IPConnection();
    connection.connect("localhost", 4223);
    try {
      final BrickServo servo = new BrickServo("6CRDUM", connection); // X axis
      servo.setPosition((short) 0, (short) 0);

      final BrickletDualRelay relay = new BrickletDualRelay("AdR", connection);
      relay.setSelectedState((short) 1, false);

    } finally {
      connection.disconnect();
    }
  }
}
