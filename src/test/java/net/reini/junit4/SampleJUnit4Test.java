/**
 * File Name: SampleJUnit4Test.java
 * 
 * Copyright (c) 2016 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.junit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SampleJUnit4Test {

  @Before
  public void setUp() throws Exception {
    System.out.println("before junit 4 test method");
  }

  @After
  public void tearDown() throws Exception {
    System.out.println("after junit 4 test method");
  }

  @Test
  public void testOne() {
    System.out.println("junit 4 test method one");
  }

  @Test
  public void testTwo() {
    System.out.println("junit 4 test method two");
  }

  @Test
  @Ignore("disabled test for demonstration reason")
  public void ignoredTest() {}
}
