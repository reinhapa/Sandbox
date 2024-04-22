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

package net.reini.swissqr;

import static java.nio.file.Files.newOutputStream;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class SwissQR {
  private static final String DEMO_PAYLOAD =
      "SPC\n0001\n1\nCH4431999123000889012\nRobert Schneider AG\nVia Casa Postale\n1268/2/22\n2501\nBiel\nCH\nRobert Schneider Services Switzerland AG\nVia Casa Postale\n1268/3/1\n2501\nBiel\nCH\n123949.75\nCHF\n2017-10-30\nPia-Maria Rutschmann-Schnyder\nGrosse Marktgasse\n28/5\n9400\nRorschach\nCH\nQRR\n210000000003139471430009017\nBeachten sie unsere Sonderangebotswoche bis 23.02.2017!##SR1;/CINV/AbC123456789012/ / 20170213/RFB/CHE-102.673.831/PUR/ORD/TXT/123457688\n1;1.1;1278564;1A-2F-43-AC-9B-33-21-B0-CC-D4-28-56;TCXVMKC22;2017-02-10T15:12:39;2017-02-10T15:18:16\n2;2a-2.2r;_R1-CH2_ConradCH-2074-1_3350_2017-03-13T10:23:47_16,99_0,00_0,00_0,00_0,00_+8FADt/DQ=_1==";

  public static void main(String[] args) {
    String payload;
    if (args.length < 1) {
      payload = DEMO_PAYLOAD;
    } else {
      payload = args[0];
    }

    try {
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      Map<EncodeHintType, Object> hints = new HashMap<>();
      hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
      hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.ISO_8859_1.name());

      BitMatrix bitMatrix = qrCodeWriter.encode(payload, BarcodeFormat.QR_CODE, 1265, 1265, hints);
      BufferedImage baseQrCode = MatrixToImageWriter.toBufferedImage(bitMatrix);

      drawSwissCross(baseQrCode);

      try (OutputStream out = newOutputStream(Paths.get("swissqrcode.png"))) {
        ImageIO.write(baseQrCode, "PNG", out);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private static void drawSwissCross(BufferedImage baseQrCode) {
    Graphics g = baseQrCode.getGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(550, 550, 164, 164);
    g.setColor(Color.BLACK);
    g.fillRect(561, 561, 142, 142);
    g.setColor(Color.WHITE);
    g.fillRect(617, 587, 30, 90);
    g.fillRect(587, 617, 90, 30);
  }
}
