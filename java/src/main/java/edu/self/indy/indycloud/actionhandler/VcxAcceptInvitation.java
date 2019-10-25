package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.connection.ConnectionApi;

import edu.self.indy.util.Misc;

public class VcxAcceptInvitation extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int connectionHandle = walletAction.getParameter("connectionHandle").asInt();
    String connectionOptions = walletAction.getParameter("connectionOptions").asText();
    String result = ConnectionApi.vcxAcceptInvitation(connectionHandle, connectionOptions).get();
    return result;
  }
}

