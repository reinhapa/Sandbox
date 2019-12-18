package net.reini.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteEchoServiceImpl extends UnicastRemoteObject implements RemoteEchoService {
  private static final long serialVersionUID = 1L;

  private final EchoService echoDelegate;

  protected RemoteEchoServiceImpl(EchoService echoDelegate) throws RemoteException {
    super();
    this.echoDelegate = echoDelegate;
  }

  @Override
  public String echo(String input) {
    return echoDelegate.echo(input);
  }

}
