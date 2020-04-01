/**
 * File Name: CdiDemo.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.reini.cdi.se.simple.ResourceExtension;
import net.reini.cdi.se.simple.StartupBean;

public class CdiDemo {

  @Test
  @Disabled
  void testInjection() {
    SeContainerInitializer initializer = SeContainerInitializer.newInstance();
    List<StartupBean> startupBeans = new ArrayList<>();
    initializer.addExtensions(new ResourceExtension(startupBeans::add));
    // initializer.addExtensions(new EjbExtension());
    try (SeContainer container = initializer.initialize()) {
      // provoke initialization for startup beans
      startupBeans.forEach(startupBean -> startupBean.start(container));
      // invoke method on main application
      System.err.println(container.select(TestApplication.class).get().helloWorld());
    }
  }

}
