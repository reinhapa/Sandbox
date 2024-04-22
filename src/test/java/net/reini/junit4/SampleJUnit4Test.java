/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016, 2024 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.reini.junit4;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SampleJUnit4Test {
  @Rule public MyRule rule = new MyRule();
  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  @BeforeClass
  public static void setUpForClass() {
    System.err.println("before class");
  }

  @Before
  public void setUp() throws Exception {
    System.out.println("before junit 4 test method");
  }

  @After
  public void tearDown() throws Exception {
    System.out.println("after junit 4 test method");
  }

  @AfterClass
  public static void tearDownClass() {
    System.out.println("after class");
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
