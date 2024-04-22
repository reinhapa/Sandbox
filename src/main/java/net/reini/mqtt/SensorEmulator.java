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

package net.reini.mqtt;

import static java.lang.String.format;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorEmulator {
  private static final Logger LOGGER = LoggerFactory.getLogger(SensorEmulator.class);
  private static final String T_TEMPLATE =
      "{\"Env_Filamente\":{\"Device\":\"0xAFFE\",\"Name\":\"Env_Test\",\"Temperature\":%.2f,\"BatteryPercentage\":42,\"Endpoint\":1,\"LinkQuality\":92}}";
  private static final String H_TEMPLATE =
      "{\"Env_Filamente\":{\"Device\":\"0xAFFE\",\"Name\":\"Env_Test\",\"Humidity\":%.2f,\"BatteryPercentage\":42,\"Endpoint\":1,\"LinkQuality\":92}}";

  private final Random random;

  private double temperature;
  private double humidity;

  public SensorEmulator(ExecutorService executorService) {
    random = new SecureRandom();
    nextHumidity();
    nextTemparature();
  }

  public static void main(String[] args) {
    ExecutorService executorService = Executors.newCachedThreadPool();
    try {
      new SensorEmulator(executorService).run();
    } finally {
      try {
        executorService.shutdown();
        if (executorService.awaitTermination(5, TimeUnit.SECONDS)) {
          executorService.shutdownNow();
        }
      } catch (InterruptedException e) {
        LOGGER.error("Interrupted while await termination", e);
      }
    }
  }

  public int run() {
    Properties mqttProperties = new Properties(System.getProperties());
    URL mqttPropertiesUrl =
        ClassLoader.getSystemClassLoader().getResource("META-INF/mqtt.properties");
    if (mqttPropertiesUrl == null) {
      System.err.println("No META-INF/mqtt.properties found in classpath");
    } else {
      try (InputStream in = mqttPropertiesUrl.openStream()) {
        mqttProperties.load(in);
      } catch (IOException e) {
        LOGGER.error("Failed to read mqtt.properties", e);
      }
    }

    MqttConnectOptions connOpts = new MqttConnectOptions();
    connOpts.setCleanSession(true);
    Optional.ofNullable(mqttProperties.getProperty("mqtt.username"))
        .ifPresent(connOpts::setUserName);
    Optional.ofNullable(mqttProperties.getProperty("mqtt.password"))
        .ifPresent(pw -> connOpts.setPassword(pw.toCharArray()));
    String broker = mqttProperties.getProperty("mqtt.url", "tcp://127.0.0.1:1883");
    String clientId = mqttProperties.getProperty("mqtt.client.id", "JavaSample");
    System.out.println(
        "Connecting to broker: "
            + broker
            + " with client id "
            + clientId
            + " connection options: "
            + connOpts);

    try (MemoryPersistence persistence = new MemoryPersistence();
        MqttClient client = new MqttClient(broker, clientId, persistence);
        BufferedInputStream console = new BufferedInputStream(System.in)) {
      client.connect(connOpts);
      System.out.println("Connected");
      StringBuilder commandBuffer = new StringBuilder();
      for (; ; ) {
        int chr = console.read();
        if (chr == '\n' || chr == '\r') {
          if (executeCommand(commandBuffer.toString(), client)) {
            client.disconnect();
            System.out.println("Disconnected");
            break;
          } else {
            commandBuffer.setLength(0);
          }
        } else if (chr > -1) {
          commandBuffer.append((char) chr);
        }
      }
      return 0;
    } catch (MqttException me) {
      logFailure(me);
      return 1;
    } catch (IOException e) {
      LOGGER.error("Failed to read from console", e);
      return 2;
    }
  }

  private boolean executeCommand(String command, MqttClient client) {
    switch (command) {
      case "q":
      case "exit":
      case "quit":
        System.out.println("Exit command received");
        return true;
      case "t":
        nextTemparature();
        sendSensorMessage(client, T_TEMPLATE, Double.valueOf(temperature));
        return false;
      case "h":
        nextHumidity();
        sendSensorMessage(client, H_TEMPLATE, Double.valueOf(humidity));
        return false;
      default:
        if (command.startsWith("s") && command.length() > 1) {}
        return false;
    }
  }

  private double nextHumidity() {
    return humidity = random.nextDouble(40);
  }

  private double nextTemparature() {
    return temperature = random.nextDouble(40);
  }

  private void sendSensorMessage(MqttClient client, String template, Double value) {
    try {
      MqttMessage message = new MqttMessage();
      message.setPayload(format(template, value).getBytes(Charset.defaultCharset()));
      message.setQos(2);
      client.publish("tele/tasmota_04D7D4/SENSOR", message);
      System.out.println("Message published");
    } catch (MqttException me) {
      logFailure(me);
    }
  }

  private void logFailure(MqttException me) {
    System.out.println("reason " + me.getReasonCode());
    System.out.println("msg " + me.getMessage());
    System.out.println("loc " + me.getLocalizedMessage());
    System.out.println("cause " + me.getCause());
    System.out.println("excep " + me);
    me.printStackTrace();
  }
}
