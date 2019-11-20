package edu.self.indy.indycloud.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface JPAWalletRepository extends CrudRepository<JPAWallet, Long> {

  List<JPAWallet> findByWalletName(String walletName);

  List<JPAWallet> findByWalletDID(String walletDID);

	JPAWallet findById(long walletId);
}