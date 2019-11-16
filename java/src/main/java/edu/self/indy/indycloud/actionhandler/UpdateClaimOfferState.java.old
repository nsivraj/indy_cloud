package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.credential.CredentialApi;

import edu.self.indy.util.Misc;

public class UpdateClaimOfferState extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int claimHandle = walletAction.getParameter("claimHandle").asInt();
    int result = CredentialApi.credentialUpdateState(claimHandle).get();
    return String.valueOf(result);
  }
}