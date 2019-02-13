/**
 * File Name: SampleJUnit5Test.java
 * 
 * Copyright (c) 2016 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.junit5;

import static org.junit.Assert.assertNotNull;

import java.nio.file.Path;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import net.reini.Context;
import net.reini.junit4.MyRule;

@ExtendWith(TestExtension.class)
@EnableRuleMigrationSupport
public class SampleJUnit5Test {
  @Rule
  public MyRule rule = new MyRule();

  @BeforeEach
  public void setUpWithParam(Context context) throws Exception {
    System.out.println("before junit 5 test method with parameter:" + context);
  }

  @BeforeEach
  public void setUp() throws Exception {
    System.out.println("before junit 5 test method");
  }

  @AfterEach
  public void tearDown() throws Exception {
    System.out.println("after junit 5 test method");
  }

  @Test
  public void testOne() {
    System.out.println("junit 5 test method one");
  }

  @Test
  public void testTwo(@TempDir Path tempDirectory) {
    System.out.println("junit 5 test method two using a temporary directory " + tempDirectory);
  }

  @Test
  public void testWithParam(Context context) {
    assertNotNull(context);
    System.out.println("junit 5 test method with parameter: " + context);
  }

  @Test
  @Disabled("disabled test for demonstration reason")
  public void ignoredTest() {}
}
