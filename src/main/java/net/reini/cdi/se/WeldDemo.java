/**
 * File Name: WeldDemo.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se;

import javax.enterprise.inject.se.SeContainer;

import org.jboss.weld.bootstrap.api.CDI11Bootstrap;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.resources.spi.ResourceLoader;

public class WeldDemo {
  public static void main(String[] args) {
    Weld weld = new Weld() {
      @Override
      protected Deployment createDeployment(ResourceLoader resourceLoader,
          CDI11Bootstrap bootstrap) {
        return super.createDeployment(resourceLoader, bootstrap);
      }
    };
    try (SeContainer container = weld.initialize()) {
      System.out.println(container.select(TestApplication.class).get().helloWorld());
    }
  }
}
