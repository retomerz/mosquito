/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito.ev3;

public enum OutputPort {

  A(0x01),
  B(0x02),
  C(0x04),
  D(0x08),
  All(0x0f);

  private final byte code;

  OutputPort(final int code) {
    this.code = (byte) code;
  }

  public byte getCode() {
    return code;
  }
}
