

package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.VcxException;
import com.evernym.sdk.vcx.credential.CredentialApi;

public class GetClaimOfferState extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int claimHandle = walletAction.getParameter("claimHandle").asInt();
    int result = CredentialApi.credentialGetState(claimHandle).get();
    return String.valueOf(result);
  }
}
