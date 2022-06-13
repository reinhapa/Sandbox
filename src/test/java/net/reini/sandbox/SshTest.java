package net.reini.sandbox;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.junit.jupiter.api.Test;

class SshTest {

  @Test
  void simpleSshConnection() {
    try (SshClient client = SshClient.setUpDefaultClient()) {
      client.start();
      try (ClientSession session =
          client.connect("user", "somehost.somedomain", 22).verify(1, TimeUnit.SECONDS).getSession()) {
        session.addPasswordIdentity("myGreatSecret"); // for password-based authentication
        session.auth().verify(1, TimeUnit.SECONDS);
        try (ChannelExec execChannel = session.createExecChannel("ls -l")) {
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          ByteArrayOutputStream err = new ByteArrayOutputStream();
          execChannel.setOut(out);
          execChannel.setErr(err);
          execChannel.open().await(1, TimeUnit.SECONDS);
          Set<ClientChannelEvent> waitMask = execChannel.waitFor(EnumSet.of(ClientChannelEvent.EXIT_SIGNAL), 5000);
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
}
