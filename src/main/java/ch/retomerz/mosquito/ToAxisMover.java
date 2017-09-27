/*
 * Copyright (c) 2017 Reto Merz
 */
package ch.retomerz.mosquito;

import ch.retomerz.mosquito.tf.TfExecutor;

final class ToAxisMover {

  private final String name;
  private final TfExecutor exe;
  private final boolean x;
  private int step = 500;
  private boolean incrementing;

  ToAxisMover(final String name, final TfExecutor exe, final boolean x) {
    this.name = name;
    this.exe = exe;
    this.x = x;
  }

  boolean isIncrementing() {
    return incrementing;
  }

  void move(final int distance) {
    if (distance < 10 && distance > -10) {
      return; // close enough
    }

    final int absDistance = Math.abs(distance);
    step = absDistance * 2;

    incrementing = distance > 0;
    if (incrementing) {
      exe.move(x, step * (x ? -1 : 1));
    } else {
      exe.move(x, step * (x ? 1 : -1));
    }
  }
}
