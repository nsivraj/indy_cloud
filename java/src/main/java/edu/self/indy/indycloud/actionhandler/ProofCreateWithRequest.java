package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.proof.DisclosedProofApi;

import edu.self.indy.util.Misc;

public class ProofCreateWithRequest extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String sourceId = walletAction.getParameter("sourceId").asText();
    String proofRequest = walletAction.getParameter("proofRequest").asText();
    int result = DisclosedProofApi.proofCreateWithRequest(sourceId, proofRequest).get();
    return String.valueOf(result);
  }
}