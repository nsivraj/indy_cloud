package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.credential.CredentialApi;

import edu.self.indy.util.Misc;

public class SendClaimRequest extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int claimHandle = walletAction.getParameter("claimHandle").asInt();
    int connectionHandle = walletAction.getParameter("connectionHandle").asInt();
    int paymentHandle = walletAction.getParameter("paymentHandle").asInt();
    String result = CredentialApi.credentialSendRequest(claimHandle, connectionHandle, paymentHandle).get();
    return result;
  }
}