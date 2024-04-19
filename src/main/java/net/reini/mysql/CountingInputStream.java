/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016, 2024 Patrick Reinhart
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

package net.reini.mysql;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.IntConsumer;

final class CountingInputStream extends FilterInputStream {
  private final CalculationFunction readBytesFunction;

  protected CountingInputStream(InputStream in, IntConsumer readConsumer) {
    super(in);
    readBytesFunction =
        read -> {
          if (read > 0) {
            readConsumer.accept(read);
          }
          return read;
        };
  }

  @Override
  public int read() throws IOException {
    return readBytesFunction.apply(super.read());
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return readBytesFunction.apply(super.read(b, off, len));
  }

  @FunctionalInterface
  interface CalculationFunction {
    int apply(int value);
  }
}
