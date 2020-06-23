/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.reini.mqtt;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.newInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttPublishSample {

  public static void main(String[] args) {
    Properties mqttProperties = new Properties(System.getProperties());
    URL mqttPropertiesUrl =
        ClassLoader.getSystemClassLoader().getResource("META-INF/mqtt.properties");
    if (mqttPropertiesUrl == null) {
      System.err.println("No META-INF/mqtt.properties found in classpath");
    } else {
      try (InputStream in = mqttPropertiesUrl.openStream()) {
        mqttProperties.load(in);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    int qos = 2;
    String clientId = mqttProperties.getProperty("mqtt.client.id", "JavaSample");
    String broker = mqttProperties.getProperty("mqtt.url", "tcp://127.0.0.1:1883");

    try (MemoryPersistence persistence = new MemoryPersistence();
        MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
        BufferedInputStream console = new BufferedInputStream(System.in)) {
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
      Optional.ofNullable(mqttProperties.getProperty("mqtt.username"))
          .ifPresent(connOpts::setUserName);
      Optional.ofNullable(mqttProperties.getProperty("mqtt.password"))
          .ifPresent(pw -> connOpts.setPassword(pw.toCharArray()));

      System.out.println("Connecting to broker: " + broker + " connection options: " + connOpts);
      sampleClient.connect(connOpts);
      System.out.println("Connected");

      Path subscriptions = Paths.get(mqttProperties.getProperty("mqtt.subscriptions.file",
          System.getProperty("user.home") + "/mqttsubscriptions"));
      if (exists(subscriptions)) {
        try (InputStream in = newInputStream(subscriptions);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
          reader.lines().filter(line -> !line.isBlank() && !line.startsWith("#"))
              .forEach(topicFilter -> subscribe(sampleClient, topicFilter));
        }
      } else {
        System.err.println(subscriptions + " does not exist");
      }

      for (;;) {
        switch (console.read()) {
          case 'q':
            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
            break;
          case 's':
            System.out.println("Publishing stop message");
            MqttMessage message = new MqttMessage("stop".getBytes(StandardCharsets.UTF_8));
            message.setQos(qos);
            sampleClient.publish("tweetwall/action/tweetwall-3", message);
            System.out.println("Message published");
            break;
          default:
        }
      }
    } catch (MqttException me) {
      System.out.println("reason " + me.getReasonCode());
      System.out.println("msg " + me.getMessage());
      System.out.println("loc " + me.getLocalizedMessage());
      System.out.println("cause " + me.getCause());
      System.out.println("excep " + me);
      me.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void subscribe(MqttClient mqttClient, String topicFilter) {
    try {
      System.out.println("Subscribing to " + topicFilter);
      mqttClient.subscribe(topicFilter,
          (t, m) -> System.out.format("topic: %s, message: %s%n", t, new String(m.getPayload())));
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }
}
