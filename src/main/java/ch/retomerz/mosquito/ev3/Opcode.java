/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito.ev3;

/**
 * http://www.monobrick.dk/MonoBrickFirmwareDocumentation/namespace_mono_brick_firmware_1_1_native.html
 * https://github.com/BrianPeek/legoev3/blob/master/Lego.Ev3.Core/Enums.cs
 */
public enum Opcode {

  OutputStop(0xa3),
  OutputPower(0xa4),
  OutputSpeed(0xa5),
  OutputStart(0xa6),
  OutputPolarity(0xa7),
  OutputReady(0xaa),
  OutputStepPower(0xac),
  OutputTimePower(0xad),
  OutputStepSpeed(0xae),
  OutputTimeSpeed(0xaf),
  OutputStepSync(0xb0),
  OutputTimeSync(0xb1);

  private final byte code;

  Opcode(final int code) {
    this.code = (byte) code;
  }

  public byte getCode() {
    return code;
  }
}
