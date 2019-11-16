package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.wallet.WalletApi;

import edu.self.indy.util.Misc;

public class DeserializeBackupWallet extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String message = walletAction.getParameter("message").asText();
    int result = WalletApi.deserializeBackupWallet(message).get();
    return String.valueOf(result);
  }
}