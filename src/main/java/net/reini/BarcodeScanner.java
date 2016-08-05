package net.reini;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.oned.EAN13Reader;

public class BarcodeScanner {
  public static void main(String[] args) throws Exception {
    for (Webcam webcam : Webcam.getWebcams()) {
      System.out.println(webcam.getName());
    }

    ImageIcon imageIcon = new ImageIcon();
    JLabel imageLabel = new JLabel(imageIcon);
    Webcam webcam = Webcam.getDefault();
    imageLabel.setPreferredSize(webcam.getViewSize());
    webcam.addWebcamListener(new WebcamListener() {
      @Override
      public void webcamOpen(WebcamEvent we) {}

      @Override
      public void webcamImageObtained(WebcamEvent we) {
        BufferedImage image = we.getImage();
        imageIcon.setImage(image);
        imageLabel.repaint();

        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "GBK");
        try {
          Reader reader = new EAN13Reader();
          Result result = reader.decode(bitmap, hints);
          System.out.println(result.getText());
        } catch (NotFoundException | ChecksumException | FormatException e) {
        }
      }

      @Override
      public void webcamDisposed(WebcamEvent we) {}

      @Override
      public void webcamClosed(WebcamEvent we) {}
    });
    webcam.open(true);

    JFrame testFrame = new JFrame("Test");
    testFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    testFrame.add(imageLabel, BorderLayout.CENTER);
    testFrame.pack();
    testFrame.setVisible(true);
  }
}
