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

package net.reini.rmi;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

public class EchoServiceServer {

  public static void main(String[] args) {
    try (ServerSocket serverSocket = new ServerSocket(0)) {
      System.out.println(serverSocket.getLocalPort());

      Registry registry = initializeRegistry(serverSocket);
      registry.bind("echoService", new RemoteEchoServiceImpl(EchoServiceServer::echoValue));
      Process process =
          new ProcessBuilder(
                  System.getProperty("java.home") + "/bin/java",
                  "-cp",
                  System.getProperty("java.class.path"),
                  "net.reini.rmi.EchoServiceClient",
                  String.valueOf(serverSocket.getLocalPort()),
                  UUID.randomUUID().toString())
              .start();
      System.out.println("Process ended with: " + process.waitFor());

      System.exit(0);
    } catch (AlreadyBoundException | IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static String echoValue(String value) {
    System.out.println("echoing " + value);
    return ">" + value;
  }

  private static Registry initializeRegistry(ServerSocket serverSocket) throws RemoteException {
    return LocateRegistry.createRegistry(0, null, port -> serverSocket);
  }
}
