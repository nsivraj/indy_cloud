package edu.self.indy.indycloud;

import com.fasterxml.jackson.databind.JsonNode;

import edu.self.indy.indycloud.jpa.JPAWallet;
import edu.self.indy.indycloud.jpa.JPAWalletRepository;
import edu.self.indy.util.Misc;
import edu.self.indy.util.Const;
import edu.self.indy.util.WSResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Trustee {

  @Autowired
  JPAWalletRepository walletRepository;

  private String DID;
  private Long walletId;

  @PostConstruct
  public void initTrustee() {
    try {
      JPAWallet wallet = WalletResource.signupForWallet("{\"secure\": false}", true, walletRepository);
      this.walletId = wallet.getId();
      this.DID = wallet.getWalletDID();
      if(this.DID == null) {
        String defaultTrusteeSeed = "000000000000000000000000Trustee1";
        String newWalletPayload = "{\"walletConfig\":" + Const.TRUSTEE_WALLET_CONFIG + ",\"walletCredential\":" + Const.TRUSTEE_WALLET_CREDENTIALS + "}";
        WSResponse createWalletRes = WalletResource.createWallet(walletId, newWalletPayload, defaultTrusteeSeed, walletRepository);
        String createWalletJSON = (createWalletRes.entity instanceof String) ? (String) createWalletRes.entity : null;
        JsonNode createWalletData = Misc.jsonMapper.readTree(createWalletJSON);
        this.DID = createWalletData.get("walletDid").asText();
        System.out.println("Created new trustee wallet with wallet id: " + this.walletId + " and DID: " + this.DID);
      } else {
        System.out.println("Found existing trustee wallet with wallet id: " + this.walletId + " and DID: " + this.DID);
      }
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public String getDID() {
    return DID;
  }

  public void setDID(String DID) {
    this.DID = DID;
  }

  public Long getWalletId() {
    return walletId;
  }

  public void setWalletId(Long walletId) {
    this.walletId = walletId;
  }

}
