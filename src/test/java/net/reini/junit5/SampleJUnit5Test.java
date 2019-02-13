/**
 * File Name: SampleJUnit5Test.java
 * 
 * Copyright (c) 2016 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.junit5;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

  @TempDir
  Path tempFolder;

  @BeforeAll
  static void setUpForClass() {
    System.err.println("before class");
  }

  @BeforeEach
  void setUpWithParam(Context context) throws Exception {
    System.out.println("before junit 5 test method with parameter:" + context);
  }

  @BeforeEach
  void setUp() throws Exception {
    System.out.println("before junit 5 test method");
  }

  @AfterEach
  void tearDown() throws Exception {
    System.out.println("after junit 5 test method");
  }

  @AfterAll
  static void tearDownClass() {
    System.out.println("after class");
  }

  @Test
  @DisplayName("test(Sting) argument null")
  void testOne() {
    System.out.println("junit 5 test method one");
  }

  @Test
  void testTwo(@TempDir Path tempDirectory) {
    System.out.println("junit 5 test method two using a temporary directory " + tempDirectory);
  }

  @Test
  void testWithParam(Context context) {
    assertNotNull(context);
    System.out.println("junit 5 test method with parameter: " + context);
  }

  @Test
  @Disabled("disabled test for demonstration reason")
  void ignoredTest() {}

  @Nested
  class SubTest {
    @Test
    void testForSubmethods() {
      IOException tested = assertThrows(IOException.class, () -> {
        throw new IOException("some error");
      });
      assertEquals("some error", tested.getMessage());
    }
  }
}
