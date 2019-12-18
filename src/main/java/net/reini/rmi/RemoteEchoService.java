package net.reini.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteEchoService extends Remote {
  public String echo(String input) throws RemoteException;
}
