package edu.self.indy.indycloud.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface WalletRepository extends CrudRepository<Wallet, Long> {

  List<Wallet> findByWalletName(String walletName);

  List<Wallet> findByWalletDID(String walletDID);

	Wallet findById(long walletId);
}