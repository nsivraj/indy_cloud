package edu.self.indy.agency;

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
import static org.junit.Assert.*;

import edu.self.indy.howto.Utils;
import edu.self.indy.util.Misc;

@Path("/prover")
public class ProverResource {
  private String proverMasterSecretId = null;

  @GET
  @Path("step2")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response step2() throws Exception {

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
	  proverMasterSecretId = proverCreateMasterSecret(proverWalletHandle, null).get();

    System.out.println("\n21. Close wallet\n");
		//issuerWalletHandle.closeWallet().get();
		//Wallet.deleteWallet(walletName, null).get();
		proverWalletHandle.closeWallet().get();

		// 22
		System.out.println("\n22. Close pool\n");
		pool.closePoolLedger().get();

    return Response.ok( "{\"msg\": \"prover: step2 is done\"}" ).build();
  }


  @POST
  @Path("step4")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response step4(String credentialOfferJSON) throws Exception {
    //System.out.println("Received credentialOfferJSON... " + credentialOfferJSON);

    JsonNode credentialOffer = Misc.jsonMapper.readTree(credentialOfferJSON);

    //System.out.println("Parsed into credentialOffer... " + credentialOffer);

    String credOffer = credentialOffer.get("credOffer").asText();
    String credDefJson = credentialOffer.get("credDefJson").asText();
    Wallet proverWalletHandle = Wallet.openWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();

    // 15
		System.out.println("\n15. Prover creates Credential Request\n");
		DidResults.CreateAndStoreMyDidResult proverDidResult = Did.createAndStoreMyDid(proverWalletHandle, "{}").get();
		String proverDid = proverDidResult.getDid();
		String proverVerkey = proverDidResult.getVerkey();

    System.out.println("Parsed into credOffer... " + credOffer);
    System.out.println("Parsed into credDefJson... " + credDefJson);
    System.out.println("Parsed into proverMasterSecretId... " + proverMasterSecretId);
    System.out.println("Parsed into proverDid... " + proverDid);

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

    return Response.ok( "{\"msg\": \"prover: step4 is done\"}" ).build();
  }
}