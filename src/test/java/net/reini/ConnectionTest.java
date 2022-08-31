package net.reini;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

class ConnectionTest {
  public static void main(String[] args) {
    Toolkit toolKit = Toolkit.getDefaultToolkit();
    List<Image> icons = new ArrayList<>();
    for (String iconFile : List.of("/home/pr/bison_jdbc.jpg"
    // "/opt/sublime_text/Icon/16x16/sublime-text.png",
    // "/opt/sublime_text/Icon/32x32/sublime-text.png",
    // "/opt/sublime_text/Icon/48x48/sublime-text.png",
    // "/opt/sublime_text/Icon/128x128/sublime-text.png",
    // "/opt/sublime_text/Icon/256x256/sublime-text.png"
    )) {
      icons.add(toolKit.createImage(iconFile));
    }
    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame("test");
      frame.setSize(500, 400);
      frame.setVisible(true);
      frame.setIconImages(icons);
    });
    System.setProperty("program.name", "Bison JDBC Query Editor");
    System.out.println();
  }

  @Test
  void testConnection() throws IOException {
    URL testUrl = new URL("https://rtc.to:80");
    HttpURLConnection connection = (HttpURLConnection) testUrl.openConnection();
    connection.setRequestMethod("HEAD");
    connection.setDoOutput(true);
    connection.setDoInput(true);
    connection.setRequestProperty("Method-Name", "");
    try (OutputStream out = connection.getOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(out)) {
      objOut.writeObject(new Object[0]);
    }
    try (InputStream in = connection.getInputStream()) {
      in.transferTo(System.out);
    }
  }
}
