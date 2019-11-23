package edu.self.indy.indycloud.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Id;

@Entity
public class JPAWallet {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  public Long id;
  public String walletName;
  public Boolean secure;
  public String genesisPath;
  public String exportPath;
  //public String masterSecretId;
  @Column(name = "WALLET_DID", unique=true)
  public String walletDID;
  public String roleList;

  protected JPAWallet() {
    this.roleList = "";
  }

  public JPAWallet(String walletName, Boolean secure) {
    this();
    this.walletName = walletName;
    this.secure = secure;
  }

  public JPAWallet(String walletName, Boolean secure, String roleList) {
    this.roleList = roleList;
    this.walletName = walletName;
    this.secure = secure;
  }

//  public JPAWallet(String walletName, Boolean secure, String genesisPath) {
//    this(walletName, secure);
//    this.genesisPath = genesisPath;
//  }

  @Override
  public String toString() {
    return String.format(
        "Wallet[id=%d, walletName='%s', secure='%s']",
        id, walletName, secure, genesisPath);
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

  public String getWalletDID() {
    return walletDID;
  }

  public void setWalletDID(String walletDID) {
    this.walletDID = walletDID;
  }

  public String getRoleList() {
    return roleList;
  }

  public void setRoleList(String roleList) {
    if(roleList != null) {
      this.roleList = roleList;
    }
  }

  public boolean hasRole(String role) {
    if(role == null) {
      return false;
    } else {
      return roleList.toLowerCase().contains(role.toLowerCase());
    }
  }

  // public String getMasterSecretId() {
  //   return masterSecretId;
  // }

  // public void setMasterSecretId(String masterSecretId) {
  //   this.masterSecretId = masterSecretId;
  // }

  public String getGenesisPath() {
    return genesisPath;
  }

  public void setGenesisPath(String genesisPath) {
    this.genesisPath = genesisPath;
  }

  public String getExportPath() {
    return exportPath;
  }

  public void setExportPath(String exportPath) {
    this.exportPath = exportPath;
  }
}