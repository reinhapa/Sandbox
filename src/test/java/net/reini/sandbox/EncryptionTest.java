/**
 * File Name: EncryptionTest.java
 * 
 * Copyright (c) 2022 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.sandbox;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;

import org.junit.jupiter.api.Test;

class EncryptionTest {

  @Test
  void testli() {
    byte[] hash = Base64.getDecoder().decode("KE1ENSkrTzNQWmhJYys4WVE5NGxYc285a21RPT0=");
    System.out.println(new String(hash, StandardCharsets.UTF_8));
    hash = Base64.getDecoder().decode("+O3PZhIc+8YQ94lXso9kmQ==");
    System.out.println(HexFormat.of().formatHex(hash));
  }

  @Test
  void diffieHellmanKeyTest() {

  }

}
