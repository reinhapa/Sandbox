package net.reini.cdi.se.simple;

import javax.annotation.Resource;
import javax.enterprise.util.AnnotationLiteral;

class ResourceQualifierAnnotation extends AnnotationLiteral<ResourceQualifier>
    implements ResourceQualifier {
  private static final long serialVersionUID = 1L;

  public static ResourceQualifierAnnotation of(Resource resource) {
    return new ResourceQualifierAnnotation(resource.name(), resource.mappedName(),
        resource.lookup());
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
    return String.format("ResourceQualifier{name='%s' , mappedName='%s' , lookup='%s'}", name,
        mappedName, lookup);
  }
}
