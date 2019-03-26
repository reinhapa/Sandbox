/**
 * File Name: InjectionAnnotation.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se.simple;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

public final class InjectionAnnotation extends AnnotationLiteral<Inject> {
  private static final long serialVersionUID = 1L;

  static final InjectionAnnotation INSTANCE = new InjectionAnnotation();

  private InjectionAnnotation() {
  }
}
