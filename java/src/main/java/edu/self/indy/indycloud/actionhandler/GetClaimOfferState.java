

package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.VcxException;
import com.evernym.sdk.vcx.connection.ConnectionApi;

public class GetSerializedConnection extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int connectionHandle = walletAction.getParameter("connectionHandle").asInt();
    String result = ConnectionApi.connectionSerialize(connectionHandle).get();
    return result;
  }
}


        try {
            CredentialApi.credentialGetState(credentialHandle).exceptionally((t) -> {
                Log.e(TAG, "getClaimOfferState: ", t);
                promise.reject("FutureException", t.getMessage());
                return -1;
            }).thenAccept(result -> {
                if (result != -1) {
                    BridgeUtils.resolveIfValid(promise, result);
                }
            });
        } catch (VcxException e) {
            promise.reject("VCXException", e.getMessage());
            e.printStackTrace();
        }

//WalletAction: id: 289 :: actionName: getClaimOfferState :: actionParams: {"claimHandle":1143750361}
