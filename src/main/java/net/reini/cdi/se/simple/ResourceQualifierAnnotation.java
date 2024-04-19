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

package net.reini.cdi.se.simple;

import jakarta.annotation.Resource;
import jakarta.enterprise.util.AnnotationLiteral;

@SuppressWarnings("all")
class ResourceQualifierAnnotation extends AnnotationLiteral<ResourceQualifier>
    implements ResourceQualifier {
  private static final long serialVersionUID = 1L;

  public static ResourceQualifierAnnotation of(Resource resource) {
    return new ResourceQualifierAnnotation(
        resource.name(), resource.mappedName(), resource.lookup());
  }

  private final String name;
  private final String mappedName;
  private final String lookup;

  private ResourceQualifierAnnotation(String name, String mappedName, String lookup) {
    this.name = name;
    this.mappedName = mappedName;
    this.lookup = lookup;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String lookup() {
    return lookup;
  }

  @Override
  public String mappedName() {
    return mappedName;
  }

  @Override
  public String toString() {
    return String.format(
        "ResourceQualifier{name='%s' , mappedName='%s' , lookup='%s'}", name, mappedName, lookup);
  }
}
