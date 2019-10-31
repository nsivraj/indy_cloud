package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.wallet.WalletApi;

import edu.self.indy.util.Misc;

public class CreateWalletBackup extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String sourceID = walletAction.getParameter("sourceID").asText();
    String backupKey = walletAction.getParameter("backupKey").asText();
    int result = WalletApi.createWalletBackup(sourceID, backupKey).get();
    return String.valueOf(result);
  }
}