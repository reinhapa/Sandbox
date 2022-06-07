/**
 * File Name: EncryptionTest.java
 * 
 * Copyright (c) 2022 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.sandbox;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.HexFormat;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;

class EncryptionTest {

  @Test
  void testli() {
    byte[] hash = Base64.getDecoder().decode("KE1ENSkrTzNQWmhJYys4WVE5NGxYc285a21RPT0=");
    System.out.println(new String(hash, UTF_8));
    hash = Base64.getDecoder().decode("+O3PZhIc+8YQ94lXso9kmQ==");
    System.out.println(HexFormat.of().formatHex(hash));
  }

  @Test
  void diffieHellmanKeyTest() throws GeneralSecurityException {
    String keyAgreementAlgorithm = "ECDH";
    String keySpecAlgorithm = "AES";
    String cipherAlgorithm = "AES/CBC/PKCS5Padding";
    int keySize = 571;

    KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("EC");
    ECGenParameterSpec ecsp = new ECGenParameterSpec("secp256r1");
    keyGenerator.initialize(ecsp);

    KeyPair globalKeyPair = keyGenerator.generateKeyPair();
    PrivateKey globalPrivateKey = globalKeyPair.getPrivate();
    System.out.println("globalPrivateKey: " + HexFormat.of().formatHex(globalPrivateKey.getEncoded()));
    PublicKey globalPublicKey = globalKeyPair.getPublic();
    System.out.println("globalPublicKey:  " + HexFormat.of().formatHex(globalPublicKey.getEncoded()));

    KeyPair recordKeyPair = keyGenerator.generateKeyPair();
    PrivateKey recordPrivateKey = recordKeyPair.getPrivate();
    System.out.println("recordPrivateKey: " + HexFormat.of().formatHex(recordPrivateKey.getEncoded()));
    PublicKey recordPublicKey = recordKeyPair.getPublic();
    System.out.println("recordPublicKey:  " + HexFormat.of().formatHex(recordPublicKey.getEncoded()));

    KeyAgreement globalKeyAgreement = KeyAgreement.getInstance(keyAgreementAlgorithm);
    keyGenerator.initialize(keySize, new SecureRandom());
    globalKeyAgreement.init(globalPrivateKey);
    globalKeyAgreement.doPhase(recordPublicKey, true);
    byte[] globalSecret = globalKeyAgreement.generateSecret();
    System.out.println("globalSecret:     " + HexFormat.of().formatHex(globalSecret));

    SecretKeySpec globalSecretKeySpec = new SecretKeySpec(globalSecret, keySpecAlgorithm);

    KeyAgreement recordKeyAgreement = KeyAgreement.getInstance(keyAgreementAlgorithm);
    recordKeyAgreement.init(recordPrivateKey);
    recordKeyAgreement.doPhase(globalPublicKey, true);
    byte[] recordSecret = recordKeyAgreement.generateSecret();
    System.out.println("recordSecret:     " + HexFormat.of().formatHex(recordSecret));

    SecretKeySpec recordSecretKeySpec = new SecretKeySpec(recordSecret, keySpecAlgorithm);

    byte[] initVector = new SecureRandom().generateSeed(16);
    System.out.println("initVector:       " + HexFormat.of().formatHex(initVector));

    Cipher encryptCipher = Cipher.getInstance(cipherAlgorithm);
    encryptCipher.init(Cipher.ENCRYPT_MODE, globalSecretKeySpec, new IvParameterSpec(initVector));

    byte[] encryptedData = encryptCipher.doFinal("Hello".getBytes());


    Cipher decryptCipher = Cipher.getInstance(cipherAlgorithm);
    decryptCipher.init(Cipher.DECRYPT_MODE, recordSecretKeySpec, new IvParameterSpec(initVector));

    byte[] decryptedData = decryptCipher.doFinal(encryptedData);

    System.out.println("Decrypted Data:   " + new String(decryptedData));
  }

}
