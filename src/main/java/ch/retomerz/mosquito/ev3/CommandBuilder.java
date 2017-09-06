/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito.ev3;

import java.io.ByteArrayOutputStream;

public final class CommandBuilder {

  private static short sequence;
  private final ByteArrayOutputStream cmd = new ByteArrayOutputStream();

  private CommandBuilder() {
    cmd.write(0); // size byte A
    cmd.write(0); // size byte B
    sequence++;
    if (sequence == Short.MAX_VALUE) {
      sequence = 1;
    }
    sequence = 3;
    writeShort(sequence);
    cmd.write(CommandType.DirectNoReply.getCode());

    cmd.write(0); // global size
    cmd.write(0); // local size
  }


  public CommandBuilder writeInt(int v) {
    cmd.write(0xFF & v);
    cmd.write(0xFF & (v >> 8));
    cmd.write(0xFF & (v >> 16));
    cmd.write(0xFF & (v >> 24));
    return this;
  }

  private CommandBuilder writeShort(short value) {
    cmd.write((byte) (value & 0xff));
    cmd.write((byte) ((value >> 8) & 0xff));
    return this;
  }

  private CommandBuilder addOpcode(Opcode opcode) {
    cmd.write(opcode.getCode());
    return this;
  }

  private CommandBuilder addParameter(byte parameter) {
    cmd.write(ArgumentSize.Byte.getCode());
    cmd.write(parameter);
    return this;
  }

  private CommandBuilder addParameter(int parameter) {
    cmd.write(ArgumentSize.Int.getCode());
    writeInt(parameter);
    return this;
  }

  private byte[] getData() {
    final byte[] ret = cmd.toByteArray();

    final int size = ret.length - 2;

    // little-endian
    ret[0] = (byte) size;
    ret[1] = (byte) (size >> 8);

    return ret;
  }

  public static byte[] turnMotorAtPowerForTime(OutputPort port, int power, int msRampUp, int msConstant, int msRampDown, boolean brake) {
    if (power < -100 || power > 100)
      throw new IllegalArgumentException("Power must be between -100 and 100 inclusive.");

    final CommandBuilder cb = new CommandBuilder();
    cb.addOpcode(Opcode.OutputTimePower);
    cb.addParameter((byte) 0x00);            // layer
    cb.addParameter(port.getCode());    // ports
    cb.addParameter((byte) power);    // power
    cb.addParameter(msRampUp);        // step1
    cb.addParameter(msConstant);    // step2
    cb.addParameter(msRampDown);    // step3
    cb.addParameter((byte) (brake ? 0x01 : 0x00));        // brake (0 = coast, 1 = brake)
    return cb.getData();
  }
}
