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
import org.junit.jupiter.api.Test;

class SshTest {

  @Test
  void simpleSshConnection() {
    try (SshClient client = SshClient.setUpDefaultClient()) {
      KeyPair keyPair = loadKeyPair("/home/someUser/.ssh/id_rsa", "secretKeyPassword");
      client.start();
      try (ClientSession session = client.connect("hostUser", "somehost.somedomain", 22)
          .verify(1, TimeUnit.SECONDS).getSession()) {
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
      PEMEncryptedKeyPair encryptedKeyPair = (PEMEncryptedKeyPair)pemParser.readObject();
      PEMDecryptorProvider keyDecryptorProvider= new BcPEMDecryptorProvider(keyPass.toCharArray());
      return converter.getKeyPair(encryptedKeyPair.decryptKeyPair(keyDecryptorProvider));
    } catch (Exception e) {
      throw new RuntimeException("Could not load key pair", e);
    }
  }
}
