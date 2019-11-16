package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.proof.CreateProofMsgIdResult;
import com.evernym.sdk.vcx.proof.DisclosedProofApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.self.indy.util.Misc;

public class ProofCreateWithMsgId extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String sourceId = walletAction.getParameter("sourceId").asText();
    int connectionHandle = walletAction.getParameter("connectionHandle").asInt();
    String messageId = walletAction.getParameter("messageId").asText();
    CreateProofMsgIdResult typedResult = (CreateProofMsgIdResult)DisclosedProofApi.proofCreateWithMsgId(sourceId, connectionHandle, messageId).get();
    ObjectNode vcxProofCreateResult = Misc.jsonMapper.createObjectNode();
    vcxProofCreateResult.set("proofHandle", Misc.jsonMapper.convertValue(typedResult.proofHandle, JsonNode.class));
    vcxProofCreateResult.set("proofRequest", Misc.jsonMapper.readTree(typedResult.proofRequest));
    return vcxProofCreateResult.toString();
  }
}
