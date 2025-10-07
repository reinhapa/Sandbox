/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Patrick Reinhart
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

import java.util.concurrent.StructuredTaskScope;

import org.junit.jupiter.api.Test;

class StructuredConcurrencyTest {
  private static final ScopedValue<String> NAME = ScopedValue.newInstance();

  @Test
  void scopedValue() {
    IO.println(NAME.orElse("unkown"));
    ScopedValue.where(NAME, "duke")
        .run(
            () -> {
              try (var scope = StructuredTaskScope.open()) {
                IO.println(NAME.orElse("unkown"));

                scope.fork(() -> childTask1());
                scope.fork(() -> childTask2());
                scope.fork(() -> childTask3());

                scope.join();

              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
              }
            });
    IO.println(NAME.orElse("unkown"));
  }

  void childTask1() {
    IO.println(NAME.orElse("unkown"));
  }

  void childTask2() {
    IO.println(NAME.orElse("unkown"));
  }

  void childTask3() {
    System.out.println(NAME.orElse("unkown"));
  }
}
