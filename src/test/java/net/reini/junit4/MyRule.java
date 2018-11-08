package net.reini.junit4;

import org.junit.rules.ExternalResource;

/**
 * Tests the limited support of rules of JUnit 4
 */
public class MyRule extends ExternalResource {
  private String value;

  @Override
  protected void before() throws Throwable {
    System.out.println("before MyRule");
    value = "theValue";
  }

  @Override
  protected void after() {
    value = null;
    System.out.println("afer MyRule");
  }

  public String getValue() {
    return value;
  }
}
