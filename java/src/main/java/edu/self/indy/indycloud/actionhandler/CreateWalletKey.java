package edu.self.indy.indycloud.actionhandler;

import java.security.SecureRandom;
import java.util.Base64;

public class CreateWalletKey extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    SecureRandom random = new SecureRandom();
    byte bytes[] = new byte[new Integer(walletAction.getParameter("lengthOfKey").toString())];
    random.nextBytes(bytes);
    return "\"" + Base64.getEncoder().encodeToString(bytes) + "\"";
  }
}