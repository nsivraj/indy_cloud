package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.connection.ConnectionApi;

import edu.self.indy.util.Misc;

public class ConnectionSendMessage extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int connectionHandle = walletAction.getParameter("connectionHandle").asInt();
    String message = walletAction.getParameter("message").asText();
    String messageOptions = walletAction.getParameter("messageOptions").asText();
    String result = ConnectionApi.connectionSendMessage(connectionHandle, message, messageOptions).get();
    return result;
  }
}