package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.wallet.WalletApi;

import edu.self.indy.util.Misc;

public class DeleteWalletItem extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String key = walletAction.getParameter("key").asText();
    key = Misc.returnNullForEmptyOrNull(key);
    if(key == null) {
      return "-1";
    } else {
      int result = WalletApi.deleteRecordWallet("record_type", key).get();
      return String.valueOf(result);
    }
  }
}