/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016, 2025 Patrick Reinhart
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

import java.util.Map;

import jakarta.json.bind.annotation.JsonbTypeDeserializer;

import net.reini.json.MapDeserializer.FloatMapDeserializer;
import net.reini.json.MapDeserializer.IntegerMapDeserializer;

/** Simple PoJo object used to test the Json serialization. */
public class JsonDataObject {
  private Map<Byte, String> byteValues;
  private Map<Double, String> doubleValues;

  @JsonbTypeDeserializer(FloatMapDeserializer.class)
  private Map<Float, SubDataObject> floatValues;

  @JsonbTypeDeserializer(IntegerMapDeserializer.class)
  private Map<Integer, String> integerValues;

  private Map<Long, String> longValues;
  private Map<Short, String> shortValues;

  /** Default constructor. */
  public JsonDataObject() {}

  /**
   * Returns the map content.
   *
   * @return the map content
   */
  public final Map<Byte, String> getByteValues() {
    return byteValues;
  }

  /**
   * Sets the map content.
   *
   * @param byteValues the map content
   */
  public final void setByteValues(Map<Byte, String> byteValues) {
    this.byteValues = byteValues;
  }

  /**
   * Returns the map content.
   *
   * @return the map content
   */
  public final Map<Double, String> getDoubleValues() {
    return doubleValues;
  }

  /**
   * Sets the map content.
   *
   * @param doubleValues the map content
   */
  public final void setDoubleValues(Map<Double, String> doubleValues) {
    this.doubleValues = doubleValues;
  }

  /**
   * Returns the map content.
   *
   * @return the map content
   */
  public final Map<Integer, String> getIntegerValues() {
    return integerValues;
  }

  /**
   * Sets the map content.
   *
   * @param integerValues the map content
   */
  public final void setIntegerValues(Map<Integer, String> integerValues) {
    this.integerValues = integerValues;
  }

  /**
   * Returns the map content.
   *
   * @return the map content
   */
  public final Map<Float, SubDataObject> getFloatValues() {
    return floatValues;
  }

  /**
   * Sets the map content.
   *
   * @param floatValues the map content
   */
  public final void setFloatValues(Map<Float, SubDataObject> floatValues) {
    this.floatValues = floatValues;
  }

  /**
   * Returns the map content.
   *
   * @return the map content
   */
  public final Map<Long, String> getLongValues() {
    return longValues;
  }

  /**
   * Sets the map content.
   *
   * @param longValues the map content
   */
  public final void setLongValues(Map<Long, String> longValues) {
    this.longValues = longValues;
  }

  /**
   * Returns the map content.
   *
   * @return the map content
   */
  public final Map<Short, String> getShortValues() {
    return shortValues;
  }

  /**
   * Sets the map content.
   *
   * @param shortValues the map content
   */
  public final void setShortValues(Map<Short, String> shortValues) {
    this.shortValues = shortValues;
  }
}
