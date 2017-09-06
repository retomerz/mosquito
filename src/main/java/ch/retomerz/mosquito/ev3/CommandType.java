/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito.ev3;

public enum CommandType {

  DirectReply(0x00),
  DirectNoReply(0x80),
  SystemReply(0x01),
  SystemNoReply(0x81);

  private final byte code;

  CommandType(final int code) {
    this.code = (byte) code;
  }

  public byte getCode() {
    return code;
  }
}
