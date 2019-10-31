package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.proof.DisclosedProofApi;

import edu.self.indy.util.Misc;

public class ProofSend extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int proofHandle = walletAction.getParameter("proofHandle").asInt();
    int connectionHandle = walletAction.getParameter("connectionHandle").asInt();
    int result = DisclosedProofApi.proofSend(proofHandle, connectionHandle).get();
    return String.valueOf(result);
  }
}