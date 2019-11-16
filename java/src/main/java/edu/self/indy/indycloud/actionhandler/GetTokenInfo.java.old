package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.token.TokenApi;

import edu.self.indy.util.Misc;

public class GetTokenInfo extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int paymentHandle = walletAction.getParameter("paymentHandle").asInt();
    String result = TokenApi.getTokenInfo(paymentHandle).get();
    return result;
  }
}