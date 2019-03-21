/**
 * File Name: TestResourceInjectionServices.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se;

import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.weld.injection.spi.ResourceInjectionServices;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;

/**
 * TD2:rep Auto-generated comment for class
 *
 * @author rep
 */
public class TestResourceInjectionServices implements ResourceInjectionServices {

  public TestResourceInjectionServices() {
    System.out.println("init net.reini.cdi.se.TestResourceInjectionServices");
  }

  @Override
  public void cleanup() {
  }

  @Override
  public ResourceReferenceFactory<Object> registerResourceInjectionPoint(
      InjectionPoint injectionPoint) {
    return null;
  }

  @Override
  public ResourceReferenceFactory<Object> registerResourceInjectionPoint(String jndiName,
      String mappedName) {
    return null;
  }

}
