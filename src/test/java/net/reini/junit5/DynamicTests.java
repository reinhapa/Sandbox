/**
 * File Name: DynamicTests.java
 * 
 * Copyright (c) 2016 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.junit5;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * Sample dynamic test factory
 */
public class DynamicTests {

  @TestFactory
  List<DynamicTest> dynamicTests() {
    return Arrays.asList(dynamicTest("dynamicTestOne", DynamicTests::noOp),
        dynamicTest("dynamicTestTwo", DynamicTests::noOp));
  }

  static void noOp() {
    System.out.println("dynamic test");
  }
}
