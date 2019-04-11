package net.reini.cdi.se.simple;

import javax.enterprise.util.AnnotationLiteral;

import net.reini.cdi.se.EjbTransactional;

class EjbTransactionalAnnotation extends AnnotationLiteral<EjbTransactional>
    implements EjbTransactional {
  private static final long serialVersionUID = 1L;

  public static final EjbTransactionalAnnotation INSTANCE = new EjbTransactionalAnnotation();

  private EjbTransactionalAnnotation() {
  }
}
