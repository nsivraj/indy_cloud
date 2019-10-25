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

    //String result = ConnectionApi.connectionSerialize(connectionHandle).get();
    //try {
        GetCredentialCreateMsgidResult credential = (GetCredentialCreateMsgidResult)CredentialApi.credentialCreateWithMsgid(sourceId, connectionHandle, messageId).get();
        ObjectNode vcxCredentialCreateResult = Misc.jsonMapper.createObjectNode();
        vcxCredentialCreateResult.set("credential_handle", Misc.jsonMapper.convertValue(credential.getCredential_handle(), JsonNode.class));
        //vcxCredentialCreateResult.set("credential_offer", Misc.jsonMapper.convertValue(credential.getOffer(), JsonNode.class));
        vcxCredentialCreateResult.set("credential_offer", Misc.jsonMapper.readTree(credential.getOffer()));
        // vcxCredentialCreateResult.set("bool", mapper.convertValue(true, JsonNode.class));
        // vcxCredentialCreateResult.set("array", mapper.convertValue(Arrays.asList("a", "b", "c"), JsonNode.class));

        //.exceptionally((t) -> {
        //    Log.e(TAG, "credentialCreateWithMsgId: ", t);
        //    promise.reject("FutureException", t.getMessage());
        //    return null;
        //}).thenAccept(result -> {
            // GetCredentialCreateMsgidResult typedResult =  result;
            // WritableMap vcxCredentialCreateResult = Arguments.createMap();
            // vcxCredentialCreateResult.putInt("credential_handle", typedResult.getCredential_handle());
            // vcxCredentialCreateResult.putString("credential_offer", typedResult.getOffer());
            // BridgeUtils.resolveIfValid(promise, vcxCredentialCreateResult);
        //});
    // } catch (VcxException e) {
    //     promise.reject("VCXException", e.getMessage());
    //     e.printStackTrace();
    // }

    return vcxCredentialCreateResult.toString();
  }
}

// WalletAction: id: 289 :: actionName: credentialCreateWithMsgId :: actionParams: {"sourceId":"ZjgxYTg","connectionHandle":1091877102,"messageId":"ZjgxYTg"}