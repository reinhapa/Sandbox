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
