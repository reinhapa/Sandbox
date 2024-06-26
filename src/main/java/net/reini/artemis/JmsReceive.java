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

package net.reini.artemis;

import jakarta.jms.Connection;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;

import org.apache.activemq.artemis.jms.client.ActiveMQDestination;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsReceive {
  private static final Logger logger = LoggerFactory.getLogger(JmsReceive.class);

  public static void main(String[] args) throws Exception {
    logger.info("Receive Setup...");
    Destination destination = ActiveMQDestination.fromPrefixedName("queue://TestQueue");
    try (ActiveMQJMSConnectionFactory factory =
            new ActiveMQJMSConnectionFactory("tcp://localhost:61616", "artemis", "simetraehcapa");
        Connection conn = factory.createConnection()) {
      Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
      MessageConsumer consumer = session.createConsumer(destination);
      consumer.setMessageListener(JmsReceive::onMessage);
      conn.start();
      Thread.sleep(1000000);
    }
  }

  private static void onMessage(Message message) {
    logger.info("Received Message: ");
    try {
      Order order = message.getBody(Order.class);
      logger.info("Received {}", order);
    } catch (JMSException e) {
      throw new IllegalStateException(e);
    }
  }
}
