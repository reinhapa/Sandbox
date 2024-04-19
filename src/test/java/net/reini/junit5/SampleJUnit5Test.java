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

package net.reini.junit5;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import net.reini.Context;
import net.reini.junit4.MyRule;

@ExtendWith(TestExtension.class)
@EnableRuleMigrationSupport
public class SampleJUnit5Test {
  @Rule public MyRule rule = new MyRule();

  @TempDir Path tempFolder;

  @BeforeAll
  static void setUpForClass() {
    System.err.println("before class");
  }

  @BeforeEach
  void setUpWithParam(Context context) throws Exception {
    System.out.println("before junit 5 test method with parameter:" + context);
  }

  @BeforeEach
  void setUp() throws Exception {
    System.out.println("before junit 5 test method");
  }

  @AfterEach
  void tearDown() throws Exception {
    System.out.println("after junit 5 test method");
  }

  @AfterAll
  static void tearDownClass() {
    System.out.println("after class");
  }

  @Test
  @DisplayName("test(Sting) argument null")
  void testOne() {
    System.out.println("junit 5 test method one");
  }

  @Test
  void testTwo(@TempDir Path tempDirectory) {
    System.out.println("junit 5 test method two using a temporary directory " + tempDirectory);
  }

  @Test
  void testWithParam(Context context) {
    assertNotNull(context);
    System.out.println("junit 5 test method with parameter: " + context);
  }

  @Test
  @Disabled("disabled test for demonstration reason")
  void ignoredTest() {}

  @Nested
  class SubTest {
    @Test
    void testForSubmethods() {
      IOException tested =
          assertThrows(
              IOException.class,
              () -> {
                throw new IOException("some error");
              });
      assertEquals("some error", tested.getMessage());
    }
  }
}
