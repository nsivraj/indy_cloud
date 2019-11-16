package edu.self.indy.indycloud.actionhandler;

import org.springframework.beans.factory.annotation.Autowired;

import edu.self.indy.indycloud.jpa.Wallet;
import edu.self.indy.indycloud.jpa.WalletRepository;

public class CreateBrandNewWallet extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    boolean secure = walletAction.getParameter("secure").asBoolean();
    Wallet wallet = walletRepository.save(new Wallet("wallet_name", secure));
    if(secure) {
      // TODO: If they want a secure wallet then additional security requirements will be enforced
      throw new UnsupportedOperationException("A secure wallet is comming soon. Please try again in a few weeks.");
    } else {

    }

    return String.valueOf(wallet.getId());
  }
}