package net.reini.sandbox;

import java.util.UUID;

public class TestEvent {
  private UUID id = UUID.randomUUID();

  @Override
  public String toString() {
    return id.toString();
  }
}
