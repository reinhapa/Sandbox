/**
 * File Name: MockitoTest.java
 * 
 * Copyright (c) 2018 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.junit5;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.tools.Tool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("boxing")
@ExtendWith(MockitoExtension.class)
public class MockitoTest {
  @Mock
  Tool tool;

  @Test
  public void demoVerifyTest() {
    InputStream in = new ByteArrayInputStream(new byte[0]);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayOutputStream err = new ByteArrayOutputStream();

    tool.run(in, out, err, "a", "b", "c");

     verify(tool).run(any(), any(), any(), eq("a"), eq("b"), eq("c"));
  }

  @Test
  public void demoStubTest() {
    InputStream in = new ByteArrayInputStream(new byte[0]);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayOutputStream err = new ByteArrayOutputStream();

    when(tool.run(any(), argThat(MockitoTest::replayOutput), any(), eq("a"), eq("b"), eq("c")))
        .thenReturn(1);

    tool.run(in, out, err, "a", "b", "c");

    assertArrayEquals("gugus".getBytes(), out.toByteArray());
  }

  static boolean replayOutput(OutputStream out) {
    try {
      out.write("gugus".getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }
}
