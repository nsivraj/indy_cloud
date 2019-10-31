package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.credential.CredentialApi;

import edu.self.indy.util.Misc;

public class DeserializeClaimOffer extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String serializedClaimOffer = walletAction.getParameter("serializedClaimOffer").asText();
    if(serializedClaimOffer == null) {
      return "-1";
    } else {
      int result = CredentialApi.credentialDeserialize(serializedClaimOffer).get();
      return String.valueOf(result);
    }
  }
}