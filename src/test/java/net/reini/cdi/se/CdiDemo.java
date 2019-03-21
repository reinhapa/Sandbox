/**
 * File Name: CdiDemo.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CdiDemo {

  @Disabled
  @Test
  void testInjection() {
    SeContainerInitializer initializer = SeContainerInitializer.newInstance();
    initializer.addExtensions(new EJBExtension());
    try (SeContainer container = initializer.initialize()) {
      System.out.println(container.select(TestApplication.class).get().helloWorld());
    }
  }

}
