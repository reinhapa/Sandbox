/**
 * File Name: TestEvent.java
 * 
 * Copyright (c) 2017 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.sandbox;

/**
 * Test event.
 *
 * @author Patrick Reinhart
 */
public final class ValueEvent {
  private String event;

  public static ValueEvent create(String value) {
    return new ValueEvent(value);
  }

  private ValueEvent(String event) {
    this.event = event;
  }

  /**
   * @return the event
   */
  public String getEvent() {
    return event;
  }

  @Override
  public String toString() {
    return String.format("%s(%s)", getClass().getSimpleName(), event);
  }
}
