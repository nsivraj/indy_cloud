package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.credential.CredentialApi;

import edu.self.indy.util.Misc;

public class GetClaimVcx extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int claimHandle = walletAction.getParameter("claimHandle").asInt();
    String result = CredentialApi.getCredential(claimHandle).get();
    return result;
  }
}