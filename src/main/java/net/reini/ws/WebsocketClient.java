
package net.reini.ws;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple websocket client.
 * 
 * <h4>Dependencies</h4> <pre>
 * compile 'javax.websocket:javax.websocket-client-api:1.1'
 * runtime 'org.glassfish.tyrus.bundles:tyrus-standalone-client-jdk:1.12'
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