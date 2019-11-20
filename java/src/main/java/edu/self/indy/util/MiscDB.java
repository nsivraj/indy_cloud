package edu.self.indy.util;

import edu.self.indy.indycloud.jpa.JPAWallet;
import edu.self.indy.indycloud.jpa.JPAWalletRepository;

public class MiscDB {

  public static final JPAWallet findWalletById(JPAWalletRepository walletRepository, long walletId) {
    JPAWallet wallet = walletRepository.findById(walletId);
    return wallet;
  }

  public static final boolean isWalletIdValid(JPAWalletRepository walletRepository, long walletId) {
    JPAWallet wallet = findWalletById(walletRepository, walletId);
    return wallet == null ? false : true;
  }
}