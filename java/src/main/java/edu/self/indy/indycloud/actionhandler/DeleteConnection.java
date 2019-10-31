package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.connection.ConnectionApi;

import edu.self.indy.util.Misc;

public class DeleteConnection extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int connectionHandle = walletAction.getParameter("connectionHandle").asInt();
    int result = ConnectionApi.deleteConnection(connectionHandle).get();
    return String.valueOf(result);
  }
}