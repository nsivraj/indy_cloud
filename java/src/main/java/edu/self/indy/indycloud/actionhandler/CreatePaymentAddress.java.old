package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.token.TokenApi;

import edu.self.indy.util.Misc;

public class CreatePaymentAddress extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String seed = Misc.returnNullForEmptyOrNull(walletAction.getParameter("seed").asText());
    String result = TokenApi.createPaymentAddress(seed).get();
    return Misc.addQuotes(result);
  }
}