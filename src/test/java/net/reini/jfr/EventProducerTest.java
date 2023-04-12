package net.reini.jfr;

import org.junit.jupiter.api.Test;

class EventProducerTest {
  @Test
  void produceJfrEvents() throws InterruptedException {
    for (int count = 0; count < 100; count++) {
      var event = new MyEvent();
      event.theId = "id" + count;
      event.someValue = "value" + count;
      event.begin();
      event.commit();
      Thread.sleep(300);
    }
  }
}
