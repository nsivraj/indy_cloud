package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.wallet.WalletApi;

import edu.self.indy.indycloud.annotation.VcxInit;
import edu.self.indy.util.Misc;

public class BackupWalletBackup extends AbstractActionHandler {

  //@VcxInit
  @Override
  public String execute() throws Exception {
    int walletBackupHandle = walletAction.getParameter("walletBackupHandle").asInt();
    String path = walletAction.getParameter("path").asText();
    int result = WalletApi.backupWalletBackup(walletBackupHandle, path).get();
    return String.valueOf(result);
  }
}