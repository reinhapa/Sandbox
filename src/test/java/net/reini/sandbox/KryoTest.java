/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.reini.sandbox;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.nio.file.Path;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoTest {

  @Test
  void kryo(@TempDir Path tmpDir) throws Exception {
    Kryo kryo = new Kryo();
    kryo.register(SomeClass.class);
    kryo.register(SomeOtherClass.class);

    SomeClass object = new SomeClass();
    object.value = "Hello Kryo!";
    object.otherClass = new SomeOtherClass();
    object.otherClass.value = "Hello others";

    Path binFile = tmpDir.resolve("file.bin");
    try (Output output = new Output(newOutputStream(binFile))) {
      kryo.writeObject(output, object);
    }

    try (Input input = new Input(newInputStream(binFile))) {
      SomeClass object2 = kryo.readObject(input, SomeClass.class);
      assertNotSame(object, object2);
      assertEquals(object, object2);
    }
  }

  static public class SomeClass {
    String value;
    SomeOtherClass otherClass;

    @Override
    public int hashCode() {
      return Objects.hash(value, otherClass);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof SomeClass)) {
        return false;
      }
      SomeClass other = (SomeClass) obj;
      return Objects.equals(value, other.value) && Objects.equals(otherClass, other.otherClass);
    }
  }

  static class SomeOtherClass {
    String value;

    @Override
    public int hashCode() {
      return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof SomeOtherClass)) {
        return false;
      }
      SomeOtherClass other = (SomeOtherClass) obj;
      return Objects.equals(value, other.value);
    }
  }
}
