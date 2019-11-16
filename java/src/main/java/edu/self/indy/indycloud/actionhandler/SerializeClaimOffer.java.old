
package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.VcxException;
import com.evernym.sdk.vcx.credential.CredentialApi;

public class SerializeClaimOffer extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int claimHandle = walletAction.getParameter("claimHandle").asInt();
    String result = CredentialApi.credentialSerialize(claimHandle).get();
    return result;
  }
}
