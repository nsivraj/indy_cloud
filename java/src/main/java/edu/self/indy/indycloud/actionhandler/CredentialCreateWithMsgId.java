package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.credential.CredentialApi;
import com.evernym.sdk.vcx.credential.GetCredentialCreateMsgidResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.self.indy.util.Misc;

public class CredentialCreateWithMsgId extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int connectionHandle = walletAction.getParameter("connectionHandle").asInt();
    String sourceId = walletAction.getParameter("sourceId").asText();
    String messageId = walletAction.getParameter("messageId").asText();
    GetCredentialCreateMsgidResult credential = (GetCredentialCreateMsgidResult)CredentialApi.credentialCreateWithMsgid(sourceId, connectionHandle, messageId).get();
    ObjectNode vcxCredentialCreateResult = Misc.jsonMapper.createObjectNode();
    vcxCredentialCreateResult.set("credential_handle", Misc.jsonMapper.convertValue(credential.getCredential_handle(), JsonNode.class));
    vcxCredentialCreateResult.set("credential_offer", Misc.jsonMapper.readTree(credential.getOffer()));
    return vcxCredentialCreateResult.toString();
  }
}

// WalletAction: id: 289 :: actionName: credentialCreateWithMsgId :: actionParams: {"sourceId":"ZjgxYTg","connectionHandle":1091877102,"messageId":"ZjgxYTg"}