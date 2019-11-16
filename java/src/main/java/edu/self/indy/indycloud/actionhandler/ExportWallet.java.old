package edu.self.indy.indycloud.actionhandler;

import java.io.File;

import com.evernym.sdk.vcx.wallet.WalletApi;

import edu.self.indy.indycloud.jpa.Wallet;
import edu.self.indy.util.Misc;

public class ExportWallet extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String walletPath = walletAction.getParameter("walletPath").asText();
    String walletEncryptionKey = walletAction.getParameter("walletEncryptionKey").asText();

    if(walletPath == null || walletEncryptionKey == null) {
      return "-1";
    } else {
      String exportFileName = walletPath.substring(walletPath.lastIndexOf('/') + 1);
      String exportFilePath = Misc.getExportPath(walletAction.id, exportFileName);
      File exportFile = new File(exportFilePath);
      exportFile.delete();
      int result = WalletApi.exportWallet(exportFilePath, walletEncryptionKey).get();

      Wallet wallet = walletRepository.findById(walletAction.id);
      wallet.setExportPath(exportFilePath);
      walletRepository.save(wallet);

      return String.valueOf(result);
    }
  }
}