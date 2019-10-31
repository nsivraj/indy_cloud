package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.wallet.WalletApi;

import edu.self.indy.util.Misc;

public class UpdateWalletBackupState extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int walletBackupHandle = walletAction.getParameter("walletBackupHandle").asInt();
    int result = WalletApi.updateWalletBackupState(walletBackupHandle).get();
    return String.valueOf(result);
  }
}