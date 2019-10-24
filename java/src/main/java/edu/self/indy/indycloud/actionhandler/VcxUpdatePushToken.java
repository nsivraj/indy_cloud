package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.utils.UtilsApi;

import edu.self.indy.util.Misc;

public class VcxUpdatePushToken extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String pushTokenConfig = walletAction.getParameter("pushTokenConfig").toString();
    pushTokenConfig = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(pushTokenConfig));

    //try {
        int result = UtilsApi.vcxUpdateAgentInfo(pushTokenConfig).get();

        // exceptionally((t) -> {
        //     Log.e(TAG, "vcxUpdatePushToken: ", t);
        //     promise.reject("FutureException", t.getMessage());
        //     return -1;
        // }).thenAccept(result -> {
        //     if (result != -1) {
        //         BridgeUtils.resolveIfValid(promise, result);
        //     }
        // });
    // } catch (VcxException e) {
    //     e.printStackTrace();
    //     promise.reject(e);
    // }

    return String.valueOf(result);
  }
}

// WalletAction: id: 257 :: actionName: vcxUpdatePushToken :: actionParams: {"pushTokenConfig":"{\"id\":\"42074375-81ae-8554-4b63-dda90b83839a\",\"value\":\"FCM:eDx2mGXbP6o:APA91bFsUriYYzmu84miQ7Y8bOOoS5_yeqkqt66VD3MWqjV6YkIxz1EVgv2wWWw58J4YfENMajI6N7Esqt5WGXVaxNkv4Z2fZI_ggx6EbrLSoKeqXsqHzsbQGUwXdUBv0O8MvFyrSOoe\"}"}
// cloudResponse: {"cloudResponse": -267896}
