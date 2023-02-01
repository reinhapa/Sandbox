package net.reini.cdi.se;

import java.util.UUID;

import jakarta.enterprise.inject.Vetoed;

@Vetoed
public class TestData {
  private UUID uuid;

  TestData(UUID uuid) {
    this.uuid = uuid;
  }
  
  @Override
  public String toString() {
    return uuid.toString();
  }
}
