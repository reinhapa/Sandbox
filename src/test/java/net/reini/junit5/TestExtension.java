/**
 * File Name: TestExtension.java
 * 
 * Copyright (c) 2016 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.junit5;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestExtensionContext;

import net.reini.Context;

public class TestExtension implements ParameterResolver, AfterEachCallback {
  private TestContext testContext;

  @Override
  public boolean supports(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getType() == Context.class;
  }

  @Override
  public Object resolve(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    if (testContext == null) {
      testContext = new TestContext();
    }
    return testContext;
  }

  @Override
  public void afterEach(TestExtensionContext context) throws Exception {
    if (testContext != null) {
      testContext.cleanUp();
      testContext = null;
    }
  }

  static class TestContext implements Context {
    void cleanUp() {
      System.out.println("context clean up");
    }

    @Override
    public String toString() {
      return "test context";
    }
  }
}
