package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.wallet.WalletApi;

import edu.self.indy.util.Misc;

public class SetWalletItem extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String key = walletAction.getParameter("key").asText();
    key = Misc.returnNullForEmptyOrNull(key);
    String value = walletAction.getParameter("value").asText();
    value = Misc.returnNullForEmptyOrNull(value);
    if(key == null || value == null) {
      return "-1";
    } else {
      int result = WalletApi.addRecordWallet("record_type", key, value).get();
      return String.valueOf(result);
    }
  }
}