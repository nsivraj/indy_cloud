package edu.self.indy.util;

import edu.self.indy.indycloud.jpa.Wallet;
import edu.self.indy.indycloud.jpa.WalletRepository;

public class MiscDB {

  public static final Wallet findWalletById(WalletRepository walletRepository, long walletId) {
    Wallet wallet = walletRepository.findById(walletId);
    return wallet;
  }

  public static final boolean isWalletIdValid(WalletRepository walletRepository, long walletId) {
    Wallet wallet = findWalletById(walletRepository, walletId);
    return wallet == null ? false : true;
  }
}