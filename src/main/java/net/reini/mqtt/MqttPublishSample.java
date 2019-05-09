package net.reini.mqtt;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttPublishSample {

  public static void main(String[] args) {
    Properties systemProperties = System.getProperties();
    String topic = "MQTT Examples";
    String content = "Message from MqttPublishSample";
    String clientId = "JavaSample";
    int qos = 2;
    // String broker = "tcp://iot.eclipse.org:1883";
    String broker = systemProperties.getProperty("mqtt.url", "tcp://127.0.0.1:1883");
    MemoryPersistence persistence = new MemoryPersistence();


    try (MqttClient sampleClient = new MqttClient(broker, clientId, persistence); BufferedInputStream console = new BufferedInputStream(System.in)) {
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
      Optional.ofNullable(systemProperties.getProperty("mqtt.username"))
          .ifPresent(connOpts::setUserName);
      Optional.ofNullable(systemProperties.getProperty("mqtt.password"))
          .ifPresent(pw -> connOpts.setPassword(pw.toCharArray()));

      System.out.println("Connecting to broker: " + broker);
      sampleClient.connect(connOpts);
      System.out.println("Connected");

      System.out.println("Subscribing to OwnTracks events");
      sampleClient.subscribe("owntracks/#",
          (t, m) -> System.out.format("OwnTracks: topic: %s, message: %s%n", t,
              new String(m.getPayload())));

      System.out.println("Subscribing to OctoPrint events");
      sampleClient.subscribe("octoprint/#",
          (t, m) -> System.out.format("OctoPrint: topic: %s, message: %s%n", t,
              new String(m.getPayload())));

      System.out.println("Subscribing to '" + topic + "' events");
      sampleClient.subscribe(topic,
          (t, m) -> System.out.format("All Messages: topic: %s, message: %s%n", t,
              new String(m.getPayload())));

      System.out.println("Publishing message: " + content);
      MqttMessage message = new MqttMessage(content.getBytes());
      message.setQos(qos);
      sampleClient.publish(topic, message);
      System.out.println("Message published");

      for (;;) {
        if ('q' == console.read()) {
          sampleClient.disconnect();
          System.out.println("Disconnected");
          System.exit(0);
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
}
