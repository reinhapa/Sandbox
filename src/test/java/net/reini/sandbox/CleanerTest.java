/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.reini.sandbox;

import java.lang.ref.Cleaner;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

class CleanerTest {
  private static final Cleaner CLEANER = Cleaner.create();

  @Test
  void testClean() throws InterruptedException {
    callCleanable();

    try (var two = new CleanningExample("two")) {
      two.close();
    }
    System.gc();
    TimeUnit.SECONDS.sleep(1);
  }

  void callCleanable() {
    new CleanningExample("one");
  }

  static class State implements Runnable {
    private final String id;

    State(String id) {
      this.id = id;
    }

    @Override
    public void run() {
      System.out.println("clean up state " + id);
    }
  }

  static class CleanningExample implements AutoCloseable {
    private final State state;
    private final Cleaner.Cleanable cleanable;

    CleanningExample(String id) {
      this.state = new State(id);
      this.cleanable = CLEANER.register(this, state);
    }

    @Override
    public void close() {
      cleanable.clean();
    }
  }
}
