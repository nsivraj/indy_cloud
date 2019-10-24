package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.connection.ConnectionApi;

import edu.self.indy.util.Misc;

public class VcxAcceptInvitation extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int connectionHandle = new Integer(walletAction.getParameter("connectionHandle").toString());
    //connectionHandle = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(connectionHandle));
    String connectionOptions = walletAction.getParameter("connectionOptions").toString();
    connectionOptions = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(connectionOptions));

    //Log.d(TAG, "acceptInvitation() called with: connectionHandle = [" + connectionHandle + "], connectionOptions = ["
    //            + connectionOptions + "], promise = [" + promise + "]");
    //try {
        String result = ConnectionApi.vcxAcceptInvitation(connectionHandle, connectionOptions).get();

        // .exceptionally((t) -> {
        //     Log.e(TAG, "vcxAcceptInvitation: ", t);
        //     promise.reject("FutureException", t.getMessage());
        //     return null;
        // }).thenAccept(result -> BridgeUtils.resolveIfValid(promise, result));
    // } catch (VcxException e) {
    //     e.printStackTrace();
    //     promise.reject(e);
    // }
    return result;
  }
}

// WalletAction: id: 257 :: actionName: vcxAcceptInvitation :: actionParams: {"connectionHandle":-267896,"connectionOptions":"{\"connection_type\":\"QR\",\"phone\":\"\"}"}
// cloudResponse: {"cloudResponse": -267896}
