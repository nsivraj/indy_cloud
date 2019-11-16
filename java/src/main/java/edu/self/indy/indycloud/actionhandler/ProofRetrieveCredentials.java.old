package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.proof.DisclosedProofApi;

import edu.self.indy.util.Misc;

public class ProofRetrieveCredentials extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int proofHandle = walletAction.getParameter("proofHandle").asInt();
    String result = DisclosedProofApi.proofRetrieveCredentials(proofHandle).get();
    return result;
  }
}