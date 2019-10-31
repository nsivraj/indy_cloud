package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.wallet.WalletApi;

import edu.self.indy.util.Misc;

public class SerializeBackupWallet extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int walletBackupHandle = walletAction.getParameter("walletBackupHandle").asInt();
    int result = WalletApi.serializeBackupWallet(walletBackupHandle).get();
    return String.valueOf(result);
  }
}