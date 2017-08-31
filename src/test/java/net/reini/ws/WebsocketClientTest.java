/**
 * File Name: WebsocketClientTest.java
 * 
 * Copyright (c) 2017 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.ws;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.junit.Test;

/**
 * Websocket integration test.
 *
 * @author Patrick Reinhart
 */
public class WebsocketClientTest {

  @Test
  public void testNonSSL() throws DeploymentException, IOException {
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    Session session = container.connectToServer(new WebsocketClient(), URI.create("ws://echo.websocket.org/"));
    assertTrue(session.isOpen());
    session.getBasicRemote().sendText("hallo");
    session.close();
  }

  @Test
  public void testSSLconnect() throws DeploymentException, IOException {
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    Session session = container.connectToServer(new WebsocketClient(), URI.create("wss://echo.websocket.org/"));
    assertTrue(session.isOpen());
    session.getBasicRemote().sendText("hallo");
    session.close();
  }
}
