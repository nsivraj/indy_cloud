package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.connection.ConnectionApi;

import edu.self.indy.util.Misc;

public class GetSerializedConnection extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int connectionHandle = new Integer(walletAction.getParameter("connectionHandle").toString());
    //connectionHandle = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(connectionHandle));

    //try {
        String result = ConnectionApi.connectionSerialize(connectionHandle).get();

        // exceptionally((t) -> {
        //     Log.e(TAG, "getSerializedConnection: ", t);
        //     promise.reject("FutureException", t.getMessage());
        //     return null;
        // }).thenAccept(result -> {
        //     BridgeUtils.resolveIfValid(promise, result);
        // });
    // } catch (VcxException e) {
    //     promise.reject("VCXException", e.getMessage());
    //     e.printStackTrace();
    // }

    return Misc.redoJSONStringify(result);
  }
}

// WalletAction: id: 257 :: actionName: getSerializedConnection :: actionParams: {"connectionHandle":-267896}
// cloudResponse: {"cloudResponse": -267896}
