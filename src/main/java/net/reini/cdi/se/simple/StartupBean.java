/**
 * File Name: StartupBean.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se.simple;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.enterprise.inject.Instance;

public final class StartupBean {
  private final Class<?> javaClass;
  private final List<Annotation> annotations;

  public static StartupBean create(Class<?> javaClass, List<Annotation> annotations) {
    return new StartupBean(javaClass, annotations);
  }

  private StartupBean(Class<?> javaClass, List<Annotation> annotations) {
    this.javaClass = javaClass;
    this.annotations = annotations;
  }

  public void start(Instance<Object> cdi) {
    cdi.select(javaClass, annotations.toArray(Annotation[]::new)).get().toString();
  }

  @Override
  public String toString() {
    return String.format("%s %s", javaClass.getName(), annotations);
  }
}
