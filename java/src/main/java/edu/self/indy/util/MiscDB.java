package edu.self.indy.util;

import edu.self.indy.indycloud.Role;
import edu.self.indy.indycloud.jpa.JPAWallet;
import edu.self.indy.indycloud.jpa.JPAWalletRepository;

import java.util.List;

public class MiscDB {

  public static final JPAWallet findWalletById(JPAWalletRepository walletRepository, long walletId) {
    JPAWallet wallet = walletRepository.findById(walletId);
    return wallet;
  }

  public static final JPAWallet findTrusteeWallet(JPAWalletRepository walletRepository) {
    List<JPAWallet> wallets = walletRepository.findByRoleList(Role.TRUSTEE.toString());
    return wallets.size() > 0 ? wallets.get(0) : null;
  }
  public static final boolean isWalletIdValid(JPAWalletRepository walletRepository, long walletId) {
    JPAWallet wallet = findWalletById(walletRepository, walletId);
    return wallet == null ? false : true;
  }
}