package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.connection.ConnectionApi;

import edu.self.indy.util.Misc;

public class GetSerializedConnection extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int connectionHandle = walletAction.getParameter("connectionHandle").asInt();
    String result = ConnectionApi.connectionSerialize(connectionHandle).get();
    return result;
  }
}
