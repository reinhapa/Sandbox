package net.reini.mqtt;

import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttPublishSample {

  public static void main(String[] args) {

    String topic = "MQTT Examples";
    String content = "Message from MqttPublishSample";
    int qos = 2;
    // String broker = "tcp://iot.eclipse.org:1883";
    String broker = "tcp://127.0.0.1:1883";
    String clientId = "JavaSample";
    MemoryPersistence persistence = new MemoryPersistence();

    Properties systemProperties = System.getProperties();

    try (MqttClient sampleClient = new MqttClient(broker, clientId, persistence)) {
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

      System.out.println("Subscribing to '" + topic + "' events");
      sampleClient.subscribe(topic,
          (t, m) -> System.out.format("All Messages: topic: %s, message: %s%n", t,
              new String(m.getPayload())));

      System.out.println("Publishing message: " + content);
      MqttMessage message = new MqttMessage(content.getBytes());
      message.setQos(qos);
      sampleClient.publish(topic, message);
      System.out.println("Message published");

      TimeUnit.MINUTES.sleep(1);
      sampleClient.disconnect();
      System.out.println("Disconnected");
      System.exit(0);
    } catch (MqttException me) {
      System.out.println("reason " + me.getReasonCode());
      System.out.println("msg " + me.getMessage());
      System.out.println("loc " + me.getLocalizedMessage());
      System.out.println("cause " + me.getCause());
      System.out.println("excep " + me);
      me.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
