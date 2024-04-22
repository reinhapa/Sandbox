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

package net.reini.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HandleTest {
  Map<String, ObjectStreamClass> mappings;

  @BeforeEach
  void prepare() {
    mappings = new HashMap<>();
    mappings.put(Handle.class.getName(), ObjectStreamClass.lookup(HandleReader.class));
    // mappings.put(HandleSerializationProxy.class.getName(), handleReader);
  }

  @Test
  void readOldVersion() throws IOException, ClassNotFoundException {
    String base64Data =
        "rO0ABXNyAB5uZXQucmVpbmkuc2VyaWFsaXphdGlvbi5IYW5kbGUAAAAAAAAAAQIAAUwAA19pZHQAEkxqYXZhL2xhbmcvU3RyaW5nO3hwdAAFdGhlSWQ=";
    try (ObjectInputStream oi = createObjectInputStream(base64Data)) {
      Handle handle = (Handle) oi.readObject();
      assertEquals("theId", handle.getId());
      assertEquals("xx", handle.toString());
    }
  }

  @Test
  void readSerializationProxy() throws IOException, ClassNotFoundException {
    try (ObjectInputStream oi = createObjectInputStream()) {
      Handle handle1 = (Handle) oi.readObject();
      assertEquals("theNewId1", handle1.getId());
      assertEquals("xx", handle1.toString());
      Handle handle2 = (Handle) oi.readObject();
      assertEquals("theNewId2", handle2.getId());
      assertEquals("xx", handle2.toString());
    }
  }

  private ObjectInputStream createObjectInputStream() throws IOException {
    ByteArrayOutputStream bo = new ByteArrayOutputStream();
    try (ObjectOutputStream oo = new OldObjectOutputStream(bo)) {
      oo.writeObject(new Handle("theNewId1"));
      oo.writeObject(new Handle("theNewId2"));
    }
    System.out.println(Base64.getEncoder().encodeToString(bo.toByteArray()));
    return new OldObjectInputStream(new ByteArrayInputStream(bo.toByteArray()), mappings);
  }

  private ObjectInputStream createObjectInputStream(String base64Data) throws IOException {
    byte[] data = Base64.getDecoder().decode(base64Data);
    return new OldObjectInputStream(new ByteArrayInputStream(data), mappings);
  }

  static class OldObjectInputStream extends ObjectInputStream {
    private final Map<String, ObjectStreamClass> mappings;

    protected OldObjectInputStream(InputStream in, Map<String, ObjectStreamClass> mappings)
        throws IOException {
      super(in);
      this.mappings = mappings;
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
      ObjectStreamClass readClassDescriptor = super.readClassDescriptor();
      return mappings.getOrDefault(readClassDescriptor.getName(), readClassDescriptor);
    }
  }

  static class OldObjectOutputStream extends ObjectOutputStream {
    public OldObjectOutputStream(OutputStream out) throws IOException {
      super(out);
      enableReplaceObject(true);
    }

    @Override
    protected Object replaceObject(Object obj) throws IOException {
      if (obj instanceof Handle) {
        return new HandleSerializationProxy((Handle) obj);
      }
      return super.replaceObject(obj);
    }
  }

  static class HandleReader implements Serializable {
    private static final ObjectStreamField[] serialPersistentFields = { //
      new ObjectStreamField("_id", String.class), //
    };
    private static final long serialVersionUID = 1L;

    private transient Handle state;

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      final GetField readFields = in.readFields();
      state = new Handle((String) readFields.get("_id", ""));
    }

    Object readResolve() {
      return state;
    }
  }

  static class HandleSerializationProxy implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient Handle state;

    HandleSerializationProxy(Handle state) {
      this.state = state;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
      out.writeUTF(state.getId());
    }

    private void readObject(ObjectInputStream in) throws IOException {
      state = new Handle(in.readUTF());
    }

    Object readResolve() {
      return state;
    }
  }
}
