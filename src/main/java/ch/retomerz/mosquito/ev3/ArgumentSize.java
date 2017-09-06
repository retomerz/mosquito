/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito.ev3;

public enum ArgumentSize {
  Byte(0x81),    // 1 byte
  Short(0x82),    // 2 bytes
  Int(0x83),        // 4 bytes
  String(0x84);    // null-terminated string

  private final byte code;

  ArgumentSize(final int code) {
    this.code = (byte) code;
  }

  public byte getCode() {
    return code;
  }
}
