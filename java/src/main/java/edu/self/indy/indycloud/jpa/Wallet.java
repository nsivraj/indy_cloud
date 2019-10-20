package edu.self.indy.indycloud.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Wallet {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  private String walletName;
  private Boolean secure;

  protected Wallet() {}

  public Wallet(String walletName, Boolean secure) {
      this.walletName = walletName;
      this.secure = secure;
  }

  @Override
  public String toString() {
    return String.format(
        "Wallet[id=%d, walletName='%s', secure='%s']",
        id, walletName, secure);
  }

  public Long getId() {
		return id;
	}

	public String getWalletName() {
		return walletName;
	}

	public Boolean getSecure() {
		return secure;
	}
}