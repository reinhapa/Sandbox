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

package net.reini.ws;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple websocket client.
 * 
 * <strong>Dependencies</strong>
 * 
 * <pre>
 * compile 'jakarta.websocket:jakarta.websocket-client-api:1.1.2'
 * runtime 'org.glassfish.tyrus.bundles:tyrus-standalone-client-jdk:2.0.0'
 * </pre>
 */
@ClientEndpoint
public final class WebsocketClient {
  private final Logger logger;

  WebsocketClient() {
    logger = LoggerFactory.getLogger(getClass());
  }

  /**
   * Callback hook for Connection open events.
   * 
   * @param userSession the userSession which is opened.
   */
  @OnOpen
  public void onOpen(Session userSession) {
    logger.info("onOpen(userSession={})", userSession);
  }

  /**
   * Callback hook for Connection close events.
   * 
   * @param userSession the userSession which is getting closed.
   * @param reason the reason for connection close
   */
  @OnClose
  public void onClose(Session userSession, CloseReason reason) {
    logger.info("onClose(userSession={}, reason={})", userSession, reason);
  }

  /**
   * Callback hook for Connection error events.
   * 
   * @param userSession the userSession which is getting closed.
   * @param cause the cause of the error
   */
  @OnError
  public void onError(Session userSession, Throwable cause) {
    logger.error("onError(userSession={})", userSession, cause);
  }

  /**
   * Callback hook for Message Events. This method will be invoked when a client send a message.
   * 
   * @param message The text message
   */
  @OnMessage
  public void onMessage(String message) {
    logger.info("onMessage(message={})", message);
  }
}