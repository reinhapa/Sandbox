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

package net.reini.sandbox;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.security.Security;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.bc.BcPEMDecryptorProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SshTest {

  @Test
  @Disabled
  void simpleSshConnection() {
    try (SshClient client = SshClient.setUpDefaultClient()) {
      KeyPair keyPair = loadKeyPair("/home/someUser/.ssh/id_rsa", "secretKeyPassword");
      client.start();
      try (ClientSession session =
          client
              .connect("hostUser", "somehost.somedomain", 22)
              .verify(1, TimeUnit.SECONDS)
              .getSession()) {
        session.addPublicKeyIdentity(keyPair);
        session.auth().verify(1, TimeUnit.SECONDS);
        try (ChannelExec execChannel = session.createExecChannel("ls -l")) {
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          ByteArrayOutputStream err = new ByteArrayOutputStream();
          execChannel.setOut(out);
          execChannel.setErr(err);
          execChannel.open().await(1, TimeUnit.SECONDS);
          Set<ClientChannelEvent> waitMask =
              execChannel.waitFor(EnumSet.of(ClientChannelEvent.EXIT_SIGNAL), 5000);
          waitMask.forEach(event -> System.out.println(event.name()));
          System.out.println(execChannel.getExitStatus());
          byte[] errBytes = err.toByteArray();
          byte[] outBytes = out.toByteArray();
          System.out.println(new String(outBytes, UTF_8));
          System.out.println(new String(errBytes, UTF_8));
        }
      } finally {
        client.stop();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static KeyPair loadKeyPair(String keyFile, String keyPass) {
    Security.addProvider(new BouncyCastleProvider());
    try (PEMParser pemParser = new PEMParser(new FileReader(keyFile))) {
      JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
      PEMEncryptedKeyPair encryptedKeyPair = (PEMEncryptedKeyPair) pemParser.readObject();
      PEMDecryptorProvider keyDecryptorProvider = new BcPEMDecryptorProvider(keyPass.toCharArray());
      return converter.getKeyPair(encryptedKeyPair.decryptKeyPair(keyDecryptorProvider));
    } catch (Exception e) {
      throw new RuntimeException("Could not load key pair", e);
    }
  }
}
