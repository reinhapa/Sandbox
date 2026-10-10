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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;

class InMemoryJavaFileManagerTest {

  @Test
  void testNullConstructor() {
    assertThrows(NullPointerException.class, () -> new InMemoryJavaFileManager(null));
  }

  @Test
  void testGetJavaFileForOutputAndInput() throws IOException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);
    try (InMemoryJavaFileManager fileManager = new InMemoryJavaFileManager(standardFileManager)) {
      String className = "net.reini.scripting.DynamicClass";

      JavaFileObject fileForOutput =
          fileManager.getJavaFileForOutput(
              StandardLocation.CLASS_OUTPUT, className, Kind.CLASS, null);

      assertInstanceOf(ByteArrayJavaClassFileObject.class, fileForOutput);
      try (OutputStream out = fileForOutput.openOutputStream()) {
        out.write(new byte[] {0x1, 0x2, 0x3});
      }

      JavaFileObject fileForInput =
          fileManager.getJavaFileForInput(StandardLocation.CLASS_OUTPUT, className, Kind.CLASS);
      assertSame(fileForOutput, fileForInput);

      // Non CLASS_OUTPUT location should delegate to super (returning null for in-memory only
      // class)
      JavaFileObject nonClassOutputInput =
          fileManager.getJavaFileForInput(StandardLocation.CLASS_PATH, className, Kind.CLASS);
      assertNull(nonClassOutputInput);

      Map<String, byte[]> allByteCodes = fileManager.getAllByteCodes();
      assertEquals(1, allByteCodes.size());
      assertArrayEquals(new byte[] {0x1, 0x2, 0x3}, allByteCodes.get(className));

      assertThrows(
          UnsupportedOperationException.class, () -> allByteCodes.put("other", new byte[] {0x1}));
    }
  }

  @Test
  void testGetJavaFileForOutputRejectsNonClassOutput() throws IOException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);
    try (InMemoryJavaFileManager fileManager = new InMemoryJavaFileManager(standardFileManager)) {
      assertThrows(
          UnsupportedOperationException.class,
          () ->
              fileManager.getJavaFileForOutput(
                  StandardLocation.SOURCE_OUTPUT, "net.reini.Generated", Kind.SOURCE, null));
      assertThrows(
          UnsupportedOperationException.class,
          () ->
              fileManager.getJavaFileForOutput(
                  StandardLocation.NATIVE_HEADER_OUTPUT, "net.reini.Header", Kind.OTHER, null));
    }
  }

  @Test
  void testGetFileForOutputRejectsResourceAndNativeHeaderOutput() throws IOException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);
    try (InMemoryJavaFileManager fileManager = new InMemoryJavaFileManager(standardFileManager)) {
      assertThrows(
          UnsupportedOperationException.class,
          () ->
              fileManager.getFileForOutput(
                  StandardLocation.CLASS_OUTPUT, "net.reini", "script.properties", null));
      assertThrows(
          UnsupportedOperationException.class,
          () ->
              fileManager.getFileForOutput(
                  StandardLocation.NATIVE_HEADER_OUTPUT, "net.reini", "Script.h", null));
    }
  }

  @Test
  void testGetJavaFileForInputWhenAbsent() throws IOException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);
    try (InMemoryJavaFileManager fileManager = new InMemoryJavaFileManager(standardFileManager)) {
      JavaFileObject fileForInput =
          fileManager.getJavaFileForInput(
              StandardLocation.CLASS_OUTPUT, "net.reini.NonExistent", Kind.CLASS);
      assertNull(fileForInput);
    }
  }
}
