package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.proof.DisclosedProofApi;

import edu.self.indy.util.Misc;

public class ProofDeserialize extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String serializedProofRequest = walletAction.getParameter("serializedProofRequest").asText();
    int result = DisclosedProofApi.proofDeserialize(serializedProofRequest).get();
    return String.valueOf(result);
  }
}