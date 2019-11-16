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
import org.hyperledger.indy.sdk.did.DidResults.CreateAndStoreMyDidResult;
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
  @Path("createWallet/{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
	public Response createProverWallet(@PathParam("id") long id) throws Exception {

		// 1.
		// System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
		// Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
		// Pool.createPoolLedgerConfig(Utils.PROVER_POOL_NAME, Utils.SERVERONE_POOL_CONFIG).exceptionally((t) -> {
		// 		t.printStackTrace();
		// 		return null;
		// }).get();

		// 2
		// System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
		// Pool pool = Pool.openPoolLedger(Utils.PROVER_POOL_NAME, "{}").get();

    // 3. Create and Open Prover Wallet
		Wallet.createWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).exceptionally((t) -> {
			t.printStackTrace();
			return null;
		}).get();
		Wallet proverWallet = Wallet.openWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();

		// 5. Create Prover DID
		CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(proverWallet, "{}").get();
		String proverDid = createMyDidResult.getDid();
		String proverVerkey = createMyDidResult.getVerkey();
    System.out.println("Prover did: " + proverDid);
    System.out.println("Prover verkey: " + proverVerkey);

    //pool.closePoolLedger().get();
    //Pool.deletePoolLedgerConfig(Utils.PROVER_POOL_NAME).get();

    proverWallet.closeWallet().get();
    //Wallet.deleteWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();

    return Response.ok( "{\"action\": \"prover/createWallet\", \"proverDid\": \"" + proverDid + "\", \"proverVerkey\": \"" + proverVerkey + "\"}" ).build();
  }


  @POST
  @Path("createMasterSecret/{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response createMasterSecret(
    @PathParam("id") long id,
    String masterSecretPayload) throws Exception {

    JsonNode masterSecretData = Misc.jsonMapper.readTree(masterSecretPayload);
    String masterSecretId = masterSecretData.get("masterSecretId").asText();

		// 1.
		// System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
		// Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
    // Pool pool = Pool.openPoolLedger(Utils.PROVER_POOL_NAME, "{}").get();
    Wallet proverWallet = Wallet.openWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();

    //13. Prover create Master Secret
    // String proverMasterSecretId = proverCreateMasterSecret(proverWallet, null).get();
    String proverMasterSecretId = proverCreateMasterSecret(proverWallet, masterSecretId).exceptionally((t) -> {
      t.printStackTrace();
      return masterSecretId;
    }).get();
    //System.out.println("proverMasterSecretId :: " + proverMasterSecretId);
    //System.out.println("Utils.PROVER_MASTER_SECRET_ID :: " + Utils.PROVER_MASTER_SECRET_ID);

    // NOTE: sleep for 5 seconds to give ledger time to persist changes
    //Thread.sleep(5000);

    //pool.closePoolLedger().get();
    //Pool.deletePoolLedgerConfig(Utils.PROVER_POOL_NAME).get();

    proverWallet.closeWallet().get();
    //Wallet.deleteWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();

    return Response.ok( "{\"action\": \"prover/createMasterSecret\", \"masterSecretId\": \"" + proverMasterSecretId + "\"}" ).build();
  }


  @POST
  @Path("createCredRequest/{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response createCredRequest(
    @PathParam("id") long id,
    String credRequestPayload) throws Exception {

    JsonNode credRequestData = Misc.jsonMapper.readTree(credRequestPayload);
    String credOffer = credRequestData.get("credOffer").toString();
    String credDefJson = credRequestData.get("credDefJson").toString();
    String proverDid = credRequestData.get("proverDid").asText();

    Wallet proverWallet = null;
    Response resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RuntimeException("prover :: createCredRequest")).build();

    try {
      // 1.
      // System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
      // Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
      // Pool pool = Pool.openPoolLedger(Utils.PROVER_POOL_NAME, "{}").get();
      proverWallet = Wallet.openWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();

      // CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(proverWallet, "{}").get();
      // String proverDid = createMyDidResult.getDid();

      //15. Prover Creates Credential Request
      AnoncredsResults.ProverCreateCredentialRequestResult createCredReqResult =
      proverCreateCredentialReq(proverWallet, proverDid, credOffer, credDefJson, Utils.PROVER_MASTER_SECRET_ID).get();
      String credReqJson = createCredReqResult.getCredentialRequestJson();
      String credReqMetadataJson = createCredReqResult.getCredentialRequestMetadataJson();

      //pool.closePoolLedger().get();
      //Pool.deletePoolLedgerConfig(Utils.PROVER_POOL_NAME).get();
      resp = Response.ok( "{\"action\": \"prover/createCredRequest\", \"credReqMetadataJson\": " + credReqMetadataJson + ", \"credReqJson\": " + credReqJson + "}" ).build();

    } catch(Exception ex) {
      ex.printStackTrace();
      resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
    } finally {
      if(proverWallet != null) {
        proverWallet.closeWallet().get();
        //Wallet.deleteWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();
      }
    }

    return resp;
  }


  @POST
  @Path("saveCredential/{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response saveCredential(
    @PathParam("id") long id,
    String credentialPayload) throws Exception {

    JsonNode credentialData = Misc.jsonMapper.readTree(credentialPayload);
    String credReqMetadataJson = credentialData.get("credReqMetadataJson").toString();
    String credDefJson = credentialData.get("credDefJson").toString();
    String credential = credentialData.get("credential").toString();

		// 1.
		// System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
		// Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
    // Pool pool = Pool.openPoolLedger(Utils.PROVER_POOL_NAME, "{}").get();
    Wallet proverWallet = Wallet.openWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();

    //17. Prover Stores Credential
    proverStoreCredential(proverWallet, null, credReqMetadataJson, credential, credDefJson, null).get();

    // NOTE: sleep for 5 seconds to give ledger time to persist changes
    Thread.sleep(5000);

    //pool.closePoolLedger().get();
    //Pool.deletePoolLedgerConfig(Utils.PROVER_POOL_NAME).get();

    proverWallet.closeWallet().get();
    //Wallet.deleteWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();

    return Response.ok( "{\"action\": \"prover/saveCredential\"}" ).build();
  }


  @POST
  @Path("createProof/{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response createProof(
    @PathParam("id") long id,
    String proofRequestPayload) throws Exception {

    JsonNode proofRequestData = Misc.jsonMapper.readTree(proofRequestPayload);
    String proofReqJson = proofRequestData.get("proofReqJson").toString();
    String credDefJson = proofRequestData.get("credDefJson").toString();
    String credDefId = proofRequestData.get("credDefId").asText();
    String schemaId = proofRequestData.get("schemaId").asText();
    String schemaJson = proofRequestData.get("schemaJson").toString();

		// 1.
		// System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
		// Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
    // Pool pool = Pool.openPoolLedger(Utils.PROVER_POOL_NAME, "{}").get();
    Wallet proverWallet = Wallet.openWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();

    CredentialsSearchForProofReq credentialsSearch = CredentialsSearchForProofReq.open(proverWallet, proofReqJson, null).get();

    JSONArray credentialsForAttribute1 = new JSONArray(credentialsSearch.fetchNextCredentials("attr1_referent", 100).get());
    String credentialIdForAttribute1 = credentialsForAttribute1.getJSONObject(0).getJSONObject("cred_info").getString("referent");

    JSONArray credentialsForAttribute2 = new JSONArray(credentialsSearch.fetchNextCredentials("attr2_referent", 100).get());
    String credentialIdForAttribute2 = credentialsForAttribute2.getJSONObject(0).getJSONObject("cred_info").getString("referent");

    JSONArray credentialsForAttribute3 = new JSONArray(credentialsSearch.fetchNextCredentials("attr3_referent", 100).get());
    assertEquals(0, credentialsForAttribute3.length());

    JSONArray credentialsForPredicate = new JSONArray(credentialsSearch.fetchNextCredentials("predicate1_referent", 100).get());
    String credentialIdForPredicate = credentialsForPredicate.getJSONObject(0).getJSONObject("cred_info").getString("referent");

    credentialsSearch.close();


    //Thread.sleep(1000);



    //12. Prover Creates Proof
    String selfAttestedValue = "8-800-300";
    String requestedCredentialsJson = new JSONObject()
        .put("self_attested_attributes", new JSONObject().put("attr3_referent", selfAttestedValue))
        .put("requested_attributes", new JSONObject()
            .put("attr1_referent", new JSONObject()
                .put("cred_id", credentialIdForAttribute1)
                .put("revealed", true)
            )
            .put("attr2_referent", new JSONObject()
                .put("cred_id", credentialIdForAttribute2)
                .put("revealed", false)
            )
        )
        .put("requested_predicates", new JSONObject()
            .put("predicate1_referent", new JSONObject()
                .put("cred_id",credentialIdForPredicate)
            )
        )
        .toString();

    String schemas = new JSONObject().put(schemaId, new JSONObject(schemaJson)).toString();
    String credentialDefs = new JSONObject().put(credDefId,  new JSONObject(credDefJson)).toString();
    String revocStates = new JSONObject().toString();

    String proofJson = null;
    int attemptToCreateProof = 0;
    do {
      attemptToCreateProof++;
      try {
        // System.out.println("****** proofRequestJson: " + proofRequestJson);
        // System.out.println("****** schemas: " + schemas);
        // System.out.println("****** credentialDefs: " + credentialDefs);
        // System.out.println("****** revocStates: " + revocStates);
        // System.out.println("****** requestedCredentialsJson: " + requestedCredentialsJson);
        // System.out.println("****** Utils.PROVER_MASTER_SECRET_ID: " + Utils.PROVER_MASTER_SECRET_ID);
        // System.out.println("****** ");

        proofJson = proverCreateProof(proverWallet, proofReqJson, requestedCredentialsJson,
          Utils.PROVER_MASTER_SECRET_ID, schemas, credentialDefs, revocStates).get();
        // proofJson = proverCreateProof(proverWallet, proofRequestJson, requestedCredentialsJson,
        //   Utils.PROVER_MASTER_SECRET_ID, schemas, credentialDefs, revocStates).exceptionally((t) -> {
        //     t.printStackTrace();
        //     if(t instanceof IndyException) {
        //       System.out.println("t.getSdkErrorCode() :: " + ((IndyException)t).getSdkErrorCode());
        //       System.out.println("t.getMessage() :: " + ((IndyException)t).getMessage());
        //       System.out.println("t.getSdkBacktrace() :: " + ((IndyException)t).getSdkBacktrace());
        //     }
        //     return null;
        //   }).get();
      } catch (Exception e) {
        e.printStackTrace();
        Thread.sleep(1500);
        //System.out.println("");
      }
    } while(proofJson == null && attemptToCreateProof < 3);

    System.out.println("The proofJson is:");
    System.out.println(proofJson);
    //JSONObject proof = new JSONObject(proofJson);

    //pool.closePoolLedger().get();
    //Pool.deletePoolLedgerConfig(Utils.PROVER_POOL_NAME).get();

    proverWallet.closeWallet().get();
    //Wallet.deleteWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();

    return Response.ok( "{\"action\": \"prover/createProof\", \"proofJson\": " + proofJson + ", \"credentialDefs\": " + credentialDefs + ", \"schemas\": " + schemas + "}" ).build();
  }

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
		String proverMasterSecretId = Anoncreds.proverCreateMasterSecret(proverWalletHandle, Utils.PROVER_MASTER_SECRET_ID).get();
    //String proverMasterSecretId = proverCreateMasterSecret(proverWalletHandle, null).get();
    edu.self.indy.indycloud.jpa.Wallet dbWallet = new edu.self.indy.indycloud.jpa.Wallet(Utils.PROVER_WALLET_NAME, false);
    dbWallet.masterSecretId = proverMasterSecretId;
    dbWallet = walletRepository.save(dbWallet);

    System.out.println("\n21. Close wallet\n");
		//issuerWalletHandle.closeWallet().get();
		//Wallet.deleteWallet(walletName, null).get();
		proverWalletHandle.closeWallet().get();

		// 22
		//System.out.println("\n22. Close pool\n");
		//pool.closePoolLedger().get();

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
		//		claimDef, Utils.PROVER_MASTER_SECRET_ID).get();
		//System.out.println("Claim Request:\n" + claimRequestJSON);

    System.out.println("\n21. Close wallet\n");
		//issuerWalletHandle.closeWallet().get();
		//Wallet.deleteWallet(walletName, null).get();
		proverWalletHandle.closeWallet().get();

    //return Response.ok( "{\"msg\": \"prover: step4 is done\"}" ).build();
    return Response.ok(  "{\"credReqJson\": " + credReqJson + ", \"credReqMetadataJson\": " + credReqMetadataJson + ", \"action\": \"step4\"}" ).build();
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

    return Response.ok( "{\"action\": \"step6\", \"credentialId\": \"" + credentialId + "\"}" ).build();
  }


  public edu.self.indy.indycloud.jpa.Wallet findWalletById(long id) {
    edu.self.indy.indycloud.jpa.Wallet wallet = walletRepository.findById(id);
    return wallet;
  }


  @POST
  @Path("step8/{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response step8(
    @PathParam("id") long id,
    String proofRequestJSON) throws Exception {

      edu.self.indy.indycloud.jpa.Wallet dbWallet = findWalletById(id);

      JsonNode proofRequestData = Misc.jsonMapper.readTree(proofRequestJSON);
      String proofReqJson = proofRequestData.get("proofReqJson").toString();
      String credDefJson = proofRequestData.get("credDefJson").toString();
      String credDefId = proofRequestData.get("credDefId").asText();
      String schemaId = proofRequestData.get("schemaId").asText();
      String schemaJson = proofRequestData.get("schemaJson").toString();

      Wallet proverWalletHandle = Wallet.openWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();

    // 18
    System.out.println("\n18. Prover Gets Credentials for Proof Request\n");
		CredentialsSearchForProofReq credentialsSearch = CredentialsSearchForProofReq.open(proverWalletHandle, proofReqJson, null).get();

		JSONArray credentialsForAttribute1 = new JSONArray(credentialsSearch.fetchNextCredentials("attr1_referent", 100).get());
		String credentialIdForAttribute1 = credentialsForAttribute1.getJSONObject(0).getJSONObject("cred_info").getString("referent");

		JSONArray credentialsForAttribute2 = new JSONArray(credentialsSearch.fetchNextCredentials("attr2_referent", 100).get());
		String credentialIdForAttribute2 = credentialsForAttribute2.getJSONObject(0).getJSONObject("cred_info").getString("referent");

		JSONArray credentialsForAttribute3 = new JSONArray(credentialsSearch.fetchNextCredentials("attr3_referent", 100).get());
		assertEquals(0, credentialsForAttribute3.length());

		JSONArray credentialsForPredicate = new JSONArray(credentialsSearch.fetchNextCredentials("predicate1_referent", 100).get());
		String credentialIdForPredicate = credentialsForPredicate.getJSONObject(0).getJSONObject("cred_info").getString("referent");

		credentialsSearch.close();

    // 19
    System.out.println("\n19. Prover Creates Proof\n");
		String selfAttestedValue = "8-800-300";
		String requestedCredentialsJson = new JSONObject()
				.put("self_attested_attributes", new JSONObject().put("attr3_referent", selfAttestedValue))
				.put("requested_attributes", new JSONObject()
						.put("attr1_referent", new JSONObject()
								.put("cred_id", credentialIdForAttribute1)
								.put("revealed", true)
						)
						.put("attr2_referent", new JSONObject()
								.put("cred_id", credentialIdForAttribute2)
								.put("revealed", false)
						)
				)
				.put("requested_predicates", new JSONObject()
						.put("predicate1_referent", new JSONObject()
								.put("cred_id",credentialIdForPredicate)
						)
				)
				.toString();

		String schemas = new JSONObject().put(schemaId, new JSONObject(schemaJson)).toString();
		String credentialDefs = new JSONObject().put(credDefId,  new JSONObject(credDefJson)).toString();
		String revocStates = new JSONObject().toString();

    String proverMasterSecretId = dbWallet.masterSecretId;

    System.out.println("proofReqJson: " + proofReqJson);
    System.out.println("schemas: " + schemas);
    System.out.println("credentialDefs: " + credentialDefs);
    System.out.println("revocStates: " + revocStates);
    System.out.println("requestedCredentialsJson: " + requestedCredentialsJson);

		String proofJson = proverCreateProof(proverWalletHandle, proofReqJson, requestedCredentialsJson,
        proverMasterSecretId, schemas, credentialDefs, revocStates).get();

    System.out.println("The proofJson is:");
    System.out.println(proofJson);
    //JSONObject proof = new JSONObject(proofJson);


    return Response.ok( "{\"action\": \"step8\", \"proof\": " + proofJson + "}" ).build();
  }
}