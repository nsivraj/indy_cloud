package edu.self.indy.agency;

import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;

import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults;
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.hyperledger.indy.sdk.anoncreds.Anoncreds;
import static org.hyperledger.indy.sdk.anoncreds.Anoncreds.*;
import static org.hyperledger.indy.sdk.ledger.Ledger.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import edu.self.indy.howto.Utils;
import edu.self.indy.indycloud.jpa.WalletRepository;
import edu.self.indy.util.Misc;

@Path("/prover")
public class ProverResource {
  @Autowired
  WalletRepository walletRepository;

  @GET
  @Path("step2/{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response step2(@PathParam("id") long id) throws Exception {

		// 1.
		System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
		Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
		Pool.createPoolLedgerConfig(Utils.PROVER_POOL_NAME, Utils.SERVERONE_POOL_CONFIG).exceptionally((t) -> {
				t.printStackTrace();
				return null;
		}).get();

		// 2
		System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
		Pool pool = Pool.openPoolLedger(Utils.PROVER_POOL_NAME, "{}").get();

    // 12
		System.out.println("\n12. Creating Prover wallet and opening it to get the handle\n");
		//String proverDID = "VsKV7grR1BUE29mG2Fm2kX";
		//String proverWalletName = "prover_wallet";
		//Wallet.createWallet(poolName, proverWalletName, null, null, null);
		Wallet.createWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).exceptionally((t) -> {
				t.printStackTrace();
				return null;
		}).get();
		//Wallet proverWalletHandle = Wallet.openWallet(proverWalletName, null, null).get();
		Wallet proverWalletHandle = Wallet.openWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();

		// 13
		System.out.println("\n13. Prover is creating Master Secret\n");
		// Anoncreds.proverCreateMasterSecret(proverWalletHandle, Utils.PROVER_MASTER_SECRET).get();
    String proverMasterSecretId = proverCreateMasterSecret(proverWalletHandle, null).get();
    edu.self.indy.indycloud.jpa.Wallet dbWallet = new edu.self.indy.indycloud.jpa.Wallet(Utils.PROVER_WALLET_NAME, false);
    dbWallet.masterSecretId = proverMasterSecretId;
    dbWallet = walletRepository.save(dbWallet);

    System.out.println("\n21. Close wallet\n");
		//issuerWalletHandle.closeWallet().get();
		//Wallet.deleteWallet(walletName, null).get();
		proverWalletHandle.closeWallet().get();

		// 22
		System.out.println("\n22. Close pool\n");
		pool.closePoolLedger().get();

    return Response.ok( "{\"msg\": \"prover: step2 is done\", \"walletId\": " + dbWallet.id + "}" ).build();
  }

  @POST
  @Path("step4/{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response step4(
    @PathParam("id") long id,
    String credentialOfferJSON) throws Exception {
    //System.out.println("Received credentialOfferJSON... " + credentialOfferJSON);
    edu.self.indy.indycloud.jpa.Wallet dbWallet = findWalletById(id);

    JsonNode credentialOffer = Misc.jsonMapper.readTree(credentialOfferJSON);

    //System.out.println("Parsed into credentialOffer ... " + credentialOffer);
    //System.out.println("Parsed into credentialOffer.fieldNames() ... " + credentialOffer.fieldNames());

    String credOffer = credentialOffer.get("credOffer").toString();
    String credDefJson = credentialOffer.get("credDefJson").toString();
    Wallet proverWalletHandle = Wallet.openWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();

    // 15
		System.out.println("\n15. Prover creates Credential Request\n");
		DidResults.CreateAndStoreMyDidResult proverDidResult = Did.createAndStoreMyDid(proverWalletHandle, "{}").get();
		String proverDid = proverDidResult.getDid();
		String proverVerkey = proverDidResult.getVerkey();
    String proverMasterSecretId = dbWallet.masterSecretId;

    //System.out.println("Parsed into credOffer... " + credOffer);
    //System.out.println("Parsed into credDefJson... " + credDefJson);
    //System.out.println("Using proverMasterSecretId... " + proverMasterSecretId);
    System.out.println("Using proverDid... " + proverDid);
    //RXEtZWEmiKgeoQGbbVCyH4

		AnoncredsResults.ProverCreateCredentialRequestResult createCredReqResult =
				proverCreateCredentialReq(proverWalletHandle, proverDid, credOffer, credDefJson, proverMasterSecretId).get();
		String credReqJson = createCredReqResult.getCredentialRequestJson();
		String credReqMetadataJson = createCredReqResult.getCredentialRequestMetadataJson();

		//String claimRequestJSON = proverCreateAndStoreClaimReq(proverWalletHandle, proverDID, claimOfferJSON,
		//		claimDef, Utils.PROVER_MASTER_SECRET).get();
		//System.out.println("Claim Request:\n" + claimRequestJSON);

    System.out.println("\n21. Close wallet\n");
		//issuerWalletHandle.closeWallet().get();
		//Wallet.deleteWallet(walletName, null).get();
		proverWalletHandle.closeWallet().get();

    //return Response.ok( "{\"msg\": \"prover: step4 is done\"}" ).build();
    return Response.ok(  "{\"credReqJson\": " + credReqJson + ", \"credReqMetadataJson\": " + credReqMetadataJson + ", \"step\": \"step4\"}" ).build();
  }


  @POST
  @Path("step6/{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response step6(
    @PathParam("id") long id,
    String credentialJSON) throws Exception {

    JsonNode credentialData = Misc.jsonMapper.readTree(credentialJSON);
    String credDefJson = credentialData.get("credDefJson").toString();
    String credReqMetadataJson = credentialData.get("credReqMetadataJson").toString();
    String credential = credentialData.get("credential").toString();

    Wallet proverWalletHandle = Wallet.openWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();

    // 17
		System.out.println("\n17. Prover processes and stores Credential\n");
		String credentialId = proverStoreCredential(proverWalletHandle, null, credReqMetadataJson, credential, credDefJson, null).get();
		//Anoncreds.proverStoreClaim(proverWalletHandle, claimJSON, null).get();

    System.out.println("\n21. Close wallet\n");
		//issuerWalletHandle.closeWallet().get();
		//Wallet.deleteWallet(walletName, null).get();
		proverWalletHandle.closeWallet().get();

    return Response.ok( "{\"msg\": \"prover: step6 is done\", \"credentialId\": \"" + credentialId + "\"}" ).build();
  }


  public edu.self.indy.indycloud.jpa.Wallet findWalletById(long id) {
    edu.self.indy.indycloud.jpa.Wallet wallet = walletRepository.findById(id);
    return wallet;
  }
}