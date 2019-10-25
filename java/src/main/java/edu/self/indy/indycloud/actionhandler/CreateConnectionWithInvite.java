package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.connection.ConnectionApi;

import edu.self.indy.util.Misc;

public class CreateConnectionWithInvite extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String invitationRequestId = walletAction.getParameter("invitationRequestId").asText();
    String inviteDetails = walletAction.getParameter("inviteDetails").asText();
    int connectionHandle = ConnectionApi.vcxCreateConnectionWithInvite(invitationRequestId, inviteDetails).get();
    return String.valueOf(connectionHandle);
  }
}
