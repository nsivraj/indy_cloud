package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.wallet.WalletApi;

import edu.self.indy.util.Misc;

public class UpdateWalletItem extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String key = walletAction.getParameter("key").toString();
    key = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(key));
    String data = walletAction.getParameter("data").toString();
    data = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(data));
    if(key == null || data == null) {
      return "-1";
    } else {
      int result = WalletApi.updateRecordWallet("record_type", key, data).get();
      return String.valueOf(result);
    }
  }
}