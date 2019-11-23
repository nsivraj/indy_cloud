package edu.self.indy.indycloud;

import com.fasterxml.jackson.databind.JsonNode;

import edu.self.indy.indycloud.jpa.JPAWallet;
import edu.self.indy.indycloud.jpa.JPAWalletRepository;
import edu.self.indy.util.Misc;
import edu.self.indy.util.Utils;
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
        String newWalletPayload = "{\"walletConfig\":" + Utils.TRUSTEE_WALLET_CONFIG + ",\"walletCredential\":" + Utils.TRUSTEE_WALLET_CREDENTIALS + "}";
        WSResponse createWalletRes = WalletResource.createWallet(walletId, newWalletPayload, defaultTrusteeSeed, walletRepository);
        String createWalletJSON = (createWalletRes.entity instanceof String) ? (String) createWalletRes.entity : null;
        JsonNode authorData = Misc.jsonMapper.readTree(createWalletJSON);
        this.DID = authorData.get("walletDid").asText();
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
