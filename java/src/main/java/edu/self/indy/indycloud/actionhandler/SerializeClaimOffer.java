
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
            CredentialApi.credentialSerialize(credentialHandle).exceptionally((t) -> {
                Log.e(TAG, "serializeClaimOffer: ", t);
                promise.reject("FutureException", t.getMessage());
                return null;
            }).thenAccept(result -> {
                BridgeUtils.resolveIfValid(promise, result);
            });
        } catch (VcxException e) {
            promise.reject("VCXException", e.getMessage());
            e.printStackTrace();
        }



//WalletAction: id: 289 :: actionName: serializeClaimOffer :: actionParams: {"claimHandle":1143750361}
