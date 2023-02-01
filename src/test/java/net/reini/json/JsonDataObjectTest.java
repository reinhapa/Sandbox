/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Patrick Reinhart
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

package net.reini.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

class JsonDataObjectTest {

  @Test
  @Disabled
  void test() throws Exception {
    try (Jsonb jsonb = JsonbBuilder.create()) {
      MapLikeObject expected = new MapLikeObject();
      expected.put("key1", new SubDataObject("a","b"));
      expected.put("key2", new SubDataObject("c","d"));

      String json = jsonb.toJson(expected);
      String expectedJson = "{\"key1\":{\"value1\":\"a\",\"value2\":\"b\"},\"key2\":{\"value1\":\"c\",\"value2\":\"d\"}}";
      assertEquals(expectedJson, json);
    }
  }

  @Test
  void test2() throws Exception {
    try (Jsonb jsonb = JsonbBuilder.create()) {
      JsonDataObject expected = new JsonDataObject();
      expected.setByteValues(Map.of());
      expected.setDoubleValues(Map.of());
      expected.setIntegerValues(Map.of(Integer.valueOf(1), "keyOne"));
      expected.setFloatValues(Map.of(1.0f, new SubDataObject()));
      expected.setLongValues(Map.of());
      expected.setShortValues(Map.of());

      String serializedForm = jsonb.toJson(expected);
      assertEquals(
          "{\"byteValues\":{},\"doubleValues\":{},\"floatValues\":{\"1.0\":{}},\"integerValues\":{\"1\":\"keyOne\"},\"longValues\":{},\"shortValues\":{}}",
          serializedForm);

      JsonDataObject tested = jsonb.fromJson(serializedForm, JsonDataObject.class);
      assertEquals(expected.getIntegerValues(), tested.getIntegerValues());
    }
  }

}
