package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.wallet.WalletApi;

import edu.self.indy.util.Misc;

public class DecryptWalletFile extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String walletConfig = walletAction.getParameter("walletConfig").asText();
    int result = WalletApi.importWallet(walletConfig).get();
    return String.valueOf(result);
  }
}