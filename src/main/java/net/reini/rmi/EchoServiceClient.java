package net.reini.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class EchoServiceClient {

  public static void main(String[] args) {
    try {
      RemoteEchoService echo = (RemoteEchoService)Naming.lookup("rmi://localhost:" + args[0] + "/echoService");
      System.out.println(echo.echo(args[1]));
    } catch (MalformedURLException | RemoteException | NotBoundException e) {
      e.printStackTrace();
    }
  }

}
