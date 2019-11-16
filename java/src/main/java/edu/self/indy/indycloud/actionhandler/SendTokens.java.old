package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.token.TokenApi;

import edu.self.indy.util.Misc;

public class SendTokens extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int paymentHandle = walletAction.getParameter("paymentHandle").asInt();
    String sovrinAtoms = walletAction.getParameter("sovrinAtoms").asText();
    String recipientWalletAddress = walletAction.getParameter("recipientWalletAddress").asText();
    String result = TokenApi.sendTokens(paymentHandle, sovrinAtoms, recipientWalletAddress).get();
    return result;
  }
}

