package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.wallet.WalletApi;

import edu.self.indy.util.Misc;

public class GetWalletItem extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String key = walletAction.getParameter("key").asText();
    key = Misc.returnNullForEmptyOrNull(key);
    if(key == null) {
      return "-1";
    } else {
      String result = WalletApi.getRecordWallet("record_type", key, "").get();
      return result;
    }
  }
}