/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 Patrick Reinhart
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

package net.reini.scripting.compiler;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.JavaFileObject.Kind;

import org.junit.jupiter.api.Test;

class ByteArrayJavaClassFileObjectTest {

  @Test
  void testConstructorAndGetters() {
    String className = "net.reini.scripting.TestScript";
    ByteArrayJavaClassFileObject fileObject = new ByteArrayJavaClassFileObject(className);

    assertEquals(URI.create("bytes:///net/reini/scripting/TestScript.class"), fileObject.toUri());
    assertEquals(Kind.CLASS, fileObject.getKind());
    assertArrayEquals(new byte[0], fileObject.getBytes());
  }

  @Test
  void testReadWrite() throws IOException {
    String className = "net.reini.scripting.TestScript";
    ByteArrayJavaClassFileObject fileObject = new ByteArrayJavaClassFileObject(className);

    byte[] testData = new byte[] {0x1, 0x2, 0x3, 0x4, 0x5};
    try (OutputStream out = fileObject.openOutputStream()) {
      assertNotNull(out);
      out.write(testData);
    }

    assertArrayEquals(testData, fileObject.getBytes());

    try (InputStream in = fileObject.openInputStream()) {
      assertNotNull(in);
      byte[] readBytes = in.readAllBytes();
      assertArrayEquals(testData, readBytes);
    }
  }

  @Test
  void testNullArgument() {
    assertThrows(NullPointerException.class, () -> new ByteArrayJavaClassFileObject(null));
  }
}
