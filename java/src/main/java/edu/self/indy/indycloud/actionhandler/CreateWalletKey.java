package edu.self.indy.indycloud.actionhandler;

import java.security.SecureRandom;
import java.util.Base64;

import edu.self.indy.util.Misc;

public class CreateWalletKey extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    SecureRandom random = new SecureRandom();
    byte bytes[] = new byte[walletAction.getParameter("lengthOfKey").asInt()];
    random.nextBytes(bytes);
    return Misc.addQuotes(Base64.getEncoder().encodeToString(bytes));
  }
}