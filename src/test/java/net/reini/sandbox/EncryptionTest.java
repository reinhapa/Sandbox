/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 Patrick Reinhart
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

package net.reini.sandbox;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HexFormat;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;

class EncryptionTest {
  private static final HexFormat HEX_FORMAT = HexFormat.of();

  @Test
  void testli() {
    byte[] hash = Base64.getDecoder().decode("KE1ENSkrTzNQWmhJYys4WVE5NGxYc285a21RPT0=");
    System.out.println(new String(hash, UTF_8));
    hash = Base64.getDecoder().decode("+O3PZhIc+8YQ94lXso9kmQ==");
    System.out.println(HEX_FORMAT.formatHex(hash));
  }

  @Test
  void diffieHellmanKeyTest() throws GeneralSecurityException {
    var keyAgreementAlgorithm = "ECDH";
    var keySpecAlgorithm = "AES";
    var cipherAlgorithm = "AES/CBC/PKCS5Padding";

    KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("EC");
    ECGenParameterSpec ecsp = new ECGenParameterSpec("secp256r1");
    keyGenerator.initialize(ecsp);

    KeyPair globalKeyPair = keyGenerator.generateKeyPair();
    PrivateKey globalPrivateKey = globalKeyPair.getPrivate();
    System.out.println("globalPrivateKey: " + HEX_FORMAT.formatHex(globalPrivateKey.getEncoded()));
    PublicKey globalPublicKey = globalKeyPair.getPublic();
    System.out.println("globalPublicKey:  " + HEX_FORMAT.formatHex(globalPublicKey.getEncoded()));

    KeyPair recordKeyPair = keyGenerator.generateKeyPair();
    PrivateKey recordPrivateKey = recordKeyPair.getPrivate();
    System.out.println("recordPrivateKey: " + HEX_FORMAT.formatHex(recordPrivateKey.getEncoded()));
    PublicKey recordPublicKey = recordKeyPair.getPublic();
    System.out.println("recordPublicKey:  " + HEX_FORMAT.formatHex(recordPublicKey.getEncoded()));

    KeyAgreement globalKeyAgreement = KeyAgreement.getInstance(keyAgreementAlgorithm);
    globalKeyAgreement.init(globalPrivateKey);
    globalKeyAgreement.doPhase(recordPublicKey, true);
    byte[] globalSecret = globalKeyAgreement.generateSecret();
    System.out.println("globalSecret:     " + HEX_FORMAT.formatHex(globalSecret));

    SecretKeySpec globalSecretKeySpec = new SecretKeySpec(globalSecret, keySpecAlgorithm);

    KeyAgreement recordKeyAgreement = KeyAgreement.getInstance(keyAgreementAlgorithm);
    recordKeyAgreement.init(recordPrivateKey);
    recordKeyAgreement.doPhase(globalPublicKey, true);
    byte[] recordSecret = recordKeyAgreement.generateSecret();
    System.out.println("recordSecret:     " + HEX_FORMAT.formatHex(recordSecret));

    SecretKeySpec recordSecretKeySpec = new SecretKeySpec(recordSecret, keySpecAlgorithm);

    byte[] initVector = new SecureRandom().generateSeed(16);
    System.out.println("initVector:       " + HEX_FORMAT.formatHex(initVector));

    Cipher encryptCipher = Cipher.getInstance(cipherAlgorithm);
    encryptCipher.init(Cipher.ENCRYPT_MODE, globalSecretKeySpec, new IvParameterSpec(initVector));

    byte[] encryptedData = encryptCipher.doFinal("Hello".getBytes());
    System.out.println("Encrypted Data:   " + HEX_FORMAT.formatHex(encryptedData));

    Cipher decryptCipher = Cipher.getInstance(cipherAlgorithm);
    decryptCipher.init(Cipher.DECRYPT_MODE, recordSecretKeySpec, new IvParameterSpec(initVector));

    byte[] decryptedData = decryptCipher.doFinal(encryptedData);

    System.out.println("Decrypted Data:   " + new String(decryptedData));
  }

  @Test
  void decryptTest() throws GeneralSecurityException {
    var keyAgreementAlgorithm = "ECDH";
    var keySpecAlgorithm = "AES";
    var cipherAlgorithm = "AES/CBC/PKCS5Padding";

    var globalPrivateKey =
        "3041020100301306072a8648ce3d020106082a8648ce3d03010704273025020101042097cacc8666504dfc07e19dbc06358916b15ccdb0f022200c94fa65b52d362689";
    var initVector = "04477ffa9cda1a84512670906ccf92ff";
    var recordPublicKey =
        "3059301306072a8648ce3d020106082a8648ce3d0301070342000431b1a17958dcd56a063c73195cb967e949219fb9623e60aae14d30021a67e047a8ccc7fc8a73e6e34a5c056fac03f763f70c5529f87ef9b5ec1d6f5a25026521";
    var encryptedData = "d31c01c47188001d7002869a764836f1";

    KeyFactory keyFactory = KeyFactory.getInstance("EC");
    PrivateKey privateKey =
        keyFactory.generatePrivate(new PKCS8EncodedKeySpec(HEX_FORMAT.parseHex(globalPrivateKey)));
    PublicKey publicKey =
        keyFactory.generatePublic(new X509EncodedKeySpec(HEX_FORMAT.parseHex(recordPublicKey)));
    KeyAgreement keyAgreement = KeyAgreement.getInstance(keyAgreementAlgorithm);
    keyAgreement.init(privateKey);
    keyAgreement.doPhase(publicKey, true);
    SecretKeySpec secretKeySpec =
        new SecretKeySpec(keyAgreement.generateSecret(), keySpecAlgorithm);
    Cipher decryptCipher = Cipher.getInstance(cipherAlgorithm);
    decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec,
        new IvParameterSpec(HEX_FORMAT.parseHex(initVector)));

    byte[] decryptedData = decryptCipher.doFinal(HEX_FORMAT.parseHex(encryptedData));

    System.out.println("Decrypted Data:   " + new String(decryptedData));
  }
}
