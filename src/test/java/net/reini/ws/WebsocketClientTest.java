/**
 * File Name: WebsocketClientTest.java
 * 
 * Copyright (c) 2017 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.ws;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;


/**
 * Websocket integration test.
 *
 * @author Patrick Reinhart
 */
public class WebsocketClientTest {

  @Disabled
  @DisabledIfEnvironmentVariable(named = "TRAVIS", matches = "true")
  @Test
  public void testNonSSL() throws DeploymentException, IOException {
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    try (Session session =
        container.connectToServer(new WebsocketClient(), URI.create("ws://echo.websocket.org/"))) {
      assertTrue(session.isOpen());
      session.getBasicRemote().sendText("hallo");
    }
  }

  @Disabled
  @DisabledIfEnvironmentVariable(named = "TRAVIS", matches = "true")
  @Test
  public void testSSLconnect() throws DeploymentException, IOException {
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    try (Session session =
        container.connectToServer(new WebsocketClient(), URI.create("wss://echo.websocket.org/"))) {
      assertTrue(session.isOpen());
      session.getBasicRemote().sendText("hallo");
    }
  }
}
