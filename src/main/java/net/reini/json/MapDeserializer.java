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

package net.reini.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

public abstract class MapDeserializer<K, V> implements JsonbDeserializer<Map<K, V>> {
  @Override
  public Map<K, V> deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
    final Map<K, V> result = new HashMap<>();
    parser.getObjectStream().forEach(e -> result.put(toKey(e.getKey()), toValue((e.getValue()))));
    return result;
  }

  abstract K toKey(String key);

  abstract V toValue(JsonValue value);

  public static class IntegerMapDeserializer extends MapDeserializer<Integer, String> {
    @Override
    Integer toKey(String key) {
      return Integer.valueOf(key);
    }

    @Override
    String toValue(JsonValue value) {
      return ((JsonString) value).getString();
    }
  }

  public static class FloatMapDeserializer extends MapDeserializer<Float, SubDataObject> {
    @Override
    Float toKey(String key) {
      return Float.valueOf(key);
    }

    @Override
    SubDataObject toValue(JsonValue value) {
      return null;
    }
  }
}
