package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.connection.ConnectionApi;

import edu.self.indy.util.Misc;

public class DeserializeConnection extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String serializedConnection = walletAction.getParameter("serializedConnection").asText();
    if(serializedConnection == null) {
      return "-1";
    } else {
      int result = ConnectionApi.connectionDeserialize(serializedConnection).get();
      return String.valueOf(result);
    }
  }
}
