package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.proof.DisclosedProofApi;

import edu.self.indy.util.Misc;

public class ProofGenerate extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int proofHandle = walletAction.getParameter("proofHandle").asInt();
    String selectedCredentials = walletAction.getParameter("selectedCredentials").asText();
    String selfAttestedAttributes = walletAction.getParameter("selfAttestedAttributes").asText();
    int result = DisclosedProofApi.proofGenerate(proofHandle, selectedCredentials, selfAttestedAttributes).get();
    return String.valueOf(result);
  }
}