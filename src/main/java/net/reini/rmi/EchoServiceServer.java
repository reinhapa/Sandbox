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
    try (ServerSocket serverSocket = new ServerSocket(0)){
      System.out.println(serverSocket.getLocalPort());

      Registry registry = initializeRegistry(serverSocket);
      registry.bind("echoService", new RemoteEchoServiceImpl(EchoServiceServer::echoValue));
      Process process = new ProcessBuilder(System.getProperty("java.home") + "/bin/java", "-cp",
          System.getProperty("java.class.path"), "net.reini.rmi.EchoServiceClient",
          String.valueOf(serverSocket.getLocalPort()), UUID.randomUUID().toString()).start();
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
