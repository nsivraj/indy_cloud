package edu.self.indy.indycloud.actionhandler;

import edu.self.indy.indycloud.WalletAction;
import edu.self.indy.indycloud.jpa.WalletRepository;

public abstract class AbstractActionHandler implements ActionHandler {
  public WalletAction walletAction;
  public WalletRepository walletRepository;

	public void setWalletAction(WalletAction walletAction) {
    this.walletAction = walletAction;
  }

  public void setWalletRepository(WalletRepository walletRepository) {
    this.walletRepository = walletRepository;
  }
}