package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.wallet.WalletApi;

import edu.self.indy.util.Misc;

public class UpdateWalletBackupStateWithMessage extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int walletBackupHandle = walletAction.getParameter("walletBackupHandle").asInt();
    String message = walletAction.getParameter("message").asText();
    int result = WalletApi.updateWalletBackupStateWithMessage(walletBackupHandle, message).get();
    return String.valueOf(result);
  }
}