package edu.self.indy.indycloud;

import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.self.indy.util.Const;
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults;
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq;
import org.hyperledger.indy.sdk.wallet.Wallet;

import static org.hyperledger.indy.sdk.anoncreds.Anoncreds.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import edu.self.indy.indycloud.jpa.JPAWalletRepository;
import edu.self.indy.util.Misc;

@Path("/prover")
public class ProverResource {

  public static final int MAX_ATTRS_PER_CRED = 100;

  @Autowired
  JPAWalletRepository walletRepository;

  // @GET
  // @Path("createWallet/{walletId}")
  // @Produces({ MediaType.APPLICATION_JSON })
  // @Consumes({ MediaType.APPLICATION_JSON })
	// public Response createProverWallet(@PathParam("walletId") long walletId) throws Exception {

  //   Wallet proverWallet = null;
  //   Response resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RuntimeException("prover :: createWallet")).build();

  //   try {

  //     JsonNode walletConfigData = Misc.jsonMapper.readTree(Const.PROVER_WALLET_CONFIG);
  //     String storageConfigPath = Misc.getStorageConfigPath(walletId);
  //     ((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
  //     String walletConfig = walletConfigData.toString();
  //     System.out.println("The walletConfig is: " + walletConfig);

  //     // 3. Create and Open Prover Wallet
  //     Wallet.createWallet(walletConfig, Const.PROVER_WALLET_CREDENTIALS).get();
  //     proverWallet = Wallet.openWallet(walletConfig, Const.PROVER_WALLET_CREDENTIALS).get();

  //     // 5. Create Prover DID
  //     CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(proverWallet, "{}").get();
  //     String proverDid = createMyDidResult.getDid();
  //     String proverVerkey = createMyDidResult.getVerkey();
  //     System.out.println("Prover did: " + proverDid);
  //     System.out.println("Prover verkey: " + proverVerkey);

  //     edu.self.indy.indycloud.jpa.Wallet dbWallet = MiscDB.findWalletById(walletRepository, walletId);
  //     dbWallet.walletDID = proverDid;
  //     dbWallet = walletRepository.save(dbWallet);

  //     resp = Response.ok( "{\"action\": \"prover/createWallet\", \"proverDid\": \"" + proverDid + "\", \"proverVerkey\": \"" + proverVerkey + "\"}" ).build();

  //   } catch(Exception ex) {
  //     ex.printStackTrace();
  //     resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
  //   } finally {
  //     if(proverWallet != null) {
  //       proverWallet.closeWallet().get();
  //       //Wallet.deleteWallet(walletConfig, Const.PROVER_WALLET_CREDENTIALS).get();
  //     }
  //   }

  //   return resp;
  // }


  @POST
  @Path("createMasterSecret/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response createMasterSecret(
    @PathParam("walletId") long walletId,
    String masterSecretPayload) throws Exception {

    JsonNode masterSecretData = Misc.jsonMapper.readTree(masterSecretPayload);
    String masterSecretId = masterSecretData.get("masterSecretId").asText();

    JsonNode walletConfigData = Misc.jsonMapper.readTree(Const.PROVER_WALLET_CONFIG);
    String storageConfigPath = Misc.getStorageConfigPath(walletId);
    ((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
    String walletConfig = walletConfigData.toString();
    System.out.println("The walletConfig is: " + walletConfig);

    // 1.
		// System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
		// Pool.setProtocolVersion(Const.PROTOCOL_VERSION).get();
    // Pool pool = Pool.openPoolLedger(Const.PROVER_POOL_NAME, "{}").get();
    Wallet proverWallet = Wallet.openWallet(walletConfig, Const.PROVER_WALLET_CREDENTIALS).get();

    //13. Prover create Master Secret
    // String proverMasterSecretId = proverCreateMasterSecret(proverWallet, null).get();
    String proverMasterSecretId = proverCreateMasterSecret(proverWallet, masterSecretId).exceptionally((t) -> {
      t.printStackTrace();
      return masterSecretId;
    }).get();
    //System.out.println("proverMasterSecretId :: " + proverMasterSecretId);
    //System.out.println("Const.PROVER_MASTER_SECRET_ID :: " + Const.PROVER_MASTER_SECRET_ID);

    // NOTE: sleep for 5 seconds to give ledger time to persist changes
    //Thread.sleep(5000);

    //pool.closePoolLedger().get();
    //Pool.deletePoolLedgerConfig(Const.PROVER_POOL_NAME).get();

    proverWallet.closeWallet().get();
    //Wallet.deleteWallet(walletConfig, Const.PROVER_WALLET_CREDENTIALS).get();

    return Response.ok( "{\"action\": \"prover/createMasterSecret\", \"masterSecretId\": \"" + proverMasterSecretId + "\"}" ).build();
  }


  @POST
  @Path("createCredRequest/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response createCredRequest(
    @PathParam("walletId") long walletId,
    String credRequestPayload) throws Exception {

    JsonNode credRequestData = Misc.jsonMapper.readTree(credRequestPayload);
    String credOffer = credRequestData.get("credOffer").toString();
    String credDefJson = credRequestData.get("credDefJson").toString();
    String proverDid = credRequestData.get("proverDid").asText();

    Wallet proverWallet = null;
    Response resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RuntimeException("prover :: createCredRequest")).build();

    try {
      JsonNode walletConfigData = Misc.jsonMapper.readTree(Const.PROVER_WALLET_CONFIG);
      String storageConfigPath = Misc.getStorageConfigPath(walletId);
      ((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
      String walletConfig = walletConfigData.toString();
      System.out.println("The walletConfig is: " + walletConfig);

      // 1.
      // System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
      // Pool.setProtocolVersion(Const.PROTOCOL_VERSION).get();
      // Pool pool = Pool.openPoolLedger(Const.PROVER_POOL_NAME, "{}").get();
      proverWallet = Wallet.openWallet(walletConfig, Const.PROVER_WALLET_CREDENTIALS).get();

      // CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(proverWallet, "{}").get();
      // String proverDid = createMyDidResult.getDid();

      //15. Prover Creates Credential Request
      AnoncredsResults.ProverCreateCredentialRequestResult createCredReqResult =
      proverCreateCredentialReq(proverWallet, proverDid, credOffer, credDefJson, Const.PROVER_MASTER_SECRET_ID).get();
      String credReqJson = createCredReqResult.getCredentialRequestJson();
      String credReqMetadataJson = createCredReqResult.getCredentialRequestMetadataJson();

      //pool.closePoolLedger().get();
      //Pool.deletePoolLedgerConfig(Const.PROVER_POOL_NAME).get();
      resp = Response.ok( "{\"action\": \"prover/createCredRequest\", \"credReqMetadataJson\": " + credReqMetadataJson + ", \"credReqJson\": " + credReqJson + "}" ).build();

    } catch(Exception ex) {
      ex.printStackTrace();
      resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
    } finally {
      if(proverWallet != null) {
        proverWallet.closeWallet().get();
        //Wallet.deleteWallet(walletConfig, Const.PROVER_WALLET_CREDENTIALS).get();
      }
    }

    return resp;
  }


  @POST
  @Path("saveCredential/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response saveCredential(
    @PathParam("walletId") long walletId,
    String credentialPayload) throws Exception {

    JsonNode credentialData = Misc.jsonMapper.readTree(credentialPayload);
    String credReqMetadataJson = credentialData.get("credReqMetadataJson").toString();
    String credDefJson = credentialData.get("credDefJson").toString();
    String credential = credentialData.get("credential").toString();

    JsonNode walletConfigData = Misc.jsonMapper.readTree(Const.PROVER_WALLET_CONFIG);
    String storageConfigPath = Misc.getStorageConfigPath(walletId);
    ((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
    String walletConfig = walletConfigData.toString();
    System.out.println("The walletConfig is: " + walletConfig);

		// 1.
		// System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
		// Pool.setProtocolVersion(Const.PROTOCOL_VERSION).get();
    // Pool pool = Pool.openPoolLedger(Const.PROVER_POOL_NAME, "{}").get();
    Wallet proverWallet = Wallet.openWallet(walletConfig, Const.PROVER_WALLET_CREDENTIALS).get();

    //17. Prover Stores Credential
    proverStoreCredential(proverWallet, null, credReqMetadataJson, credential, credDefJson, null).get();

    // NOTE: sleep for 5 seconds to give ledger time to persist changes
    //Thread.sleep(5000);

    //pool.closePoolLedger().get();
    //Pool.deletePoolLedgerConfig(Const.PROVER_POOL_NAME).get();

    proverWallet.closeWallet().get();
    //Wallet.deleteWallet(walletConfig, Const.PROVER_WALLET_CREDENTIALS).get();

    return Response.ok( "{\"action\": \"prover/saveCredential\"}" ).build();
  }


  @POST
  @Path("findCredentials/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response findCredentials(
    @PathParam("walletId") long walletId,
    String findCredPayload) throws Exception {

    JsonNode findCredData = Misc.jsonMapper.readTree(findCredPayload);
    JsonNode proofReqData = findCredData.get("proofReqJson");

    Wallet proverWallet = null;
    //Pool pool = null;
    Response resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RuntimeException("prover :: findCredentials")).build();

    try {
      JsonNode walletConfigData = Misc.jsonMapper.readTree(Const.PROVER_WALLET_CONFIG);
      String storageConfigPath = Misc.getStorageConfigPath(walletId);
      ((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
      String walletConfig = walletConfigData.toString();
      System.out.println("The walletConfig is: " + walletConfig);

      // 1.
      // System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
      // Pool.setProtocolVersion(Const.PROTOCOL_VERSION).get();
      // Pool pool = Pool.openPoolLedger(Const.PROVER_POOL_NAME, "{}").get();
      proverWallet = Wallet.openWallet(walletConfig, Const.PROVER_WALLET_CREDENTIALS).get();

      CredentialsSearchForProofReq credentialsSearch = CredentialsSearchForProofReq.open(proverWallet, proofReqData.toString(), null).get();

      JSONObject matchingCredentials = new JSONObject();

      // loop over requested attributes...
      JsonNode proofReqAttrs = proofReqData.get("requested_attributes");
      JsonNode attrRef = null;
      for(int attrIndex = 0; (attrIndex == 0 || attrRef != null); ++attrIndex) {
        String attrRefName = "attr" + (attrIndex+1) + "_referent";
        attrRef = proofReqAttrs.get(attrRefName);
        if(attrRef != null) {
          try {
            JSONArray credsForAttr = new JSONArray(credentialsSearch.fetchNextCredentials(attrRefName, MAX_ATTRS_PER_CRED).get());
            matchingCredentials.put(attrRefName, credsForAttr);
          } catch(Exception ex) {
            ex.printStackTrace();
            matchingCredentials.put(attrRefName, new JSONArray());
          }
        }
      }

      //loop over requested predicates...
      JsonNode proofReqPreds = proofReqData.get("requested_predicates");
      JsonNode predRef = null;
      for(int predIndex = 0; (predIndex == 0 || predRef != null); ++predIndex) {
        String predRefName = "predicate" + (predIndex+1) + "_referent";
        predRef = proofReqPreds.get(predRefName);
        if(predRef != null) {
          try {
            JSONArray credsForPred = new JSONArray(credentialsSearch.fetchNextCredentials(predRefName, MAX_ATTRS_PER_CRED).get());
            matchingCredentials.put(predRefName, credsForPred);
          } catch(Exception ex) {
            ex.printStackTrace();
            matchingCredentials.put(predRefName, new JSONArray());
          }
        }
      }

      credentialsSearch.close();

      resp = Response.ok( "{\"action\": \"prover/findCredentials\", \"matchingCredentials\": " + matchingCredentials + "}" ).build();
    } catch(Exception ex) {
			ex.printStackTrace();
      resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
    } finally {
      if(proverWallet != null) {
        proverWallet.closeWallet().get();
        //Wallet.deleteWallet(walletConfig, Const.PROVER_WALLET_CREDENTIALS).get();
      }
      // if(pool != null) {
      //   //pool.closePoolLedger().get();
      //   //Pool.deletePoolLedgerConfig(Const.PROVER_POOL_NAME).get();
      // }
    }

    return resp;

  }


  @POST
  @Path("createProof/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response createProof(
    @PathParam("walletId") long walletId,
    String proofRequestPayload) throws Exception {

    JsonNode proofRequestData = Misc.jsonMapper.readTree(proofRequestPayload);
    String proofReqJson = proofRequestData.get("proofReqJson").toString();
    String credDefJson = proofRequestData.get("credDefJson").toString();
    String credDefId = proofRequestData.get("credDefId").asText();
    String schemaId = proofRequestData.get("schemaId").asText();
    String schemaJson = proofRequestData.get("schemaJson").toString();
    String requestedCredentialsJson = proofRequestData.get("requestedCredentialsJson").toString();

    Wallet proverWallet = null;
    //Pool pool = null;
    Response resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RuntimeException("prover :: createProof")).build();

    try {
      JsonNode walletConfigData = Misc.jsonMapper.readTree(Const.PROVER_WALLET_CONFIG);
      String storageConfigPath = Misc.getStorageConfigPath(walletId);
      ((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
      String walletConfig = walletConfigData.toString();
      System.out.println("The walletConfig is: " + walletConfig);

      // 1.
      // System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
      // Pool.setProtocolVersion(Const.PROTOCOL_VERSION).get();
      // Pool pool = Pool.openPoolLedger(Const.PROVER_POOL_NAME, "{}").get();
      proverWallet = Wallet.openWallet(walletConfig, Const.PROVER_WALLET_CREDENTIALS).get();

      //CredentialsSearchForProofReq credentialsSearch = CredentialsSearchForProofReq.open(proverWallet, proofReqJson, null).get();

//      build the requestedCredentialsJson by looping over proofReqJson taking
//      each attr referent and predicate referent to check the credentialsSearch
//      for that referent and if the attr referent does not exist then set it as
//      self attested and as the user for the values...
      

      // JSONArray credentialsForAttribute1 = new JSONArray(credentialsSearch.fetchNextCredentials("attr1_referent", MAX_ATTRS_PER_CRED).get());
      // String credentialIdForAttribute1 = credentialsForAttribute1.getJSONObject(0).getJSONObject("cred_info").getString("referent");

      // JSONArray credentialsForAttribute2 = new JSONArray(credentialsSearch.fetchNextCredentials("attr2_referent", MAX_ATTRS_PER_CRED).get());
      // String credentialIdForAttribute2 = credentialsForAttribute2.getJSONObject(0).getJSONObject("cred_info").getString("referent");

      // JSONArray credentialsForAttribute3 = new JSONArray(credentialsSearch.fetchNextCredentials("attr3_referent", MAX_ATTRS_PER_CRED).get());
      // //assertEquals(0, credentialsForAttribute3.length());

      // JSONArray credentialsForPredicate = new JSONArray(credentialsSearch.fetchNextCredentials("predicate1_referent", MAX_ATTRS_PER_CRED).get());
      // String credentialIdForPredicate = credentialsForPredicate.getJSONObject(0).getJSONObject("cred_info").getString("referent");

      // credentialsSearch.close();

      // //12. Prover Creates Proof
      // String selfAttestedValue = "8-800-300";
      // String requestedCredentialsJson = new JSONObject()
      //     .put("self_attested_attributes", new JSONObject().put("attr3_referent", selfAttestedValue))
      //     .put("requested_attributes", new JSONObject()
      //         .put("attr1_referent", new JSONObject()
      //             .put("cred_id", credentialIdForAttribute1)
      //             .put("revealed", true)
      //         )
      //         .put("attr2_referent", new JSONObject()
      //             .put("cred_id", credentialIdForAttribute2)
      //             .put("revealed", false)
      //         )
      //     )
      //     .put("requested_predicates", new JSONObject()
      //         .put("predicate1_referent", new JSONObject()
      //             .put("cred_id",credentialIdForPredicate)
      //         )
      //     )
      //     .toString();

      System.out.println("requestedCredentialsJson: " + requestedCredentialsJson);

      String schemas = new JSONObject().put(schemaId, new JSONObject(schemaJson)).toString();
      String credentialDefs = new JSONObject().put(credDefId,  new JSONObject(credDefJson)).toString();
      String revocStates = new JSONObject().toString();

      String proofJson = null;
      //int attemptToCreateProof = 0;
      //do {
      //  attemptToCreateProof++;
        //try {
          // System.out.println("****** proofRequestJson: " + proofRequestJson);
          // System.out.println("****** schemas: " + schemas);
          // System.out.println("****** credentialDefs: " + credentialDefs);
          // System.out.println("****** revocStates: " + revocStates);
          // System.out.println("****** requestedCredentialsJson: " + requestedCredentialsJson);
          // System.out.println("****** Const.PROVER_MASTER_SECRET_ID: " + Const.PROVER_MASTER_SECRET_ID);
          // System.out.println("****** ");

          proofJson = proverCreateProof(proverWallet, proofReqJson, requestedCredentialsJson,
            Const.PROVER_MASTER_SECRET_ID, schemas, credentialDefs, revocStates).get();
          // proofJson = proverCreateProof(proverWallet, proofRequestJson, requestedCredentialsJson,
          //   Const.PROVER_MASTER_SECRET_ID, schemas, credentialDefs, revocStates).exceptionally((t) -> {
          //     t.printStackTrace();
          //     if(t instanceof IndyException) {
          //       System.out.println("t.getSdkErrorCode() :: " + ((IndyException)t).getSdkErrorCode());
          //       System.out.println("t.getMessage() :: " + ((IndyException)t).getMessage());
          //       System.out.println("t.getSdkBacktrace() :: " + ((IndyException)t).getSdkBacktrace());
          //     }
          //     return null;
          //   }).get();
        // } catch (Exception e) {
        //   e.printStackTrace();
        //   //Thread.sleep(1500);
        //   //System.out.println("");
        // }
      //} while(proofJson == null && attemptToCreateProof < 3);

      System.out.println("The proofJson is:");
      System.out.println(proofJson);
      //JSONObject proof = new JSONObject(proofJson);

      resp = Response.ok( "{\"action\": \"prover/createProof\", \"proofJson\": " + proofJson + ", \"credentialDefs\": " + credentialDefs + ", \"schemas\": " + schemas + "}" ).build();
    } catch(Exception ex) {
			ex.printStackTrace();
      resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
    } finally {
      if(proverWallet != null) {
        proverWallet.closeWallet().get();
        //Wallet.deleteWallet(walletConfig, Const.PROVER_WALLET_CREDENTIALS).get();
      }
      // if(pool != null) {
      //   //pool.closePoolLedger().get();
      //   //Pool.deletePoolLedgerConfig(Const.PROVER_POOL_NAME).get();
      // }
    }

    return resp;
  }


  /*
  @GET
  @Path("step2/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response step2(@PathParam("walletId") long walletId) throws Exception {

		// 1.
		System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
		Pool.setProtocolVersion(Const.PROTOCOL_VERSION).get();
		Pool.createPoolLedgerConfig(Const.PROVER_POOL_NAME, Const.SERVERONE_POOL_CONFIG).exceptionally((t) -> {
				t.printStackTrace();
				return null;
		}).get();

		// 2
		System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
		Pool pool = Pool.openPoolLedger(Const.PROVER_POOL_NAME, "{}").get();

    // 12
		System.out.println("\n12. Creating Prover wallet and opening it to get the handle\n");
		//String proverDID = "VsKV7grR1BUE29mG2Fm2kX";
		//String proverWalletName = "prover_wallet";
		//Wallet.createWallet(poolName, proverWalletName, null, null, null);
		Wallet.createWallet(Const.PROVER_WALLET_CONFIG, Const.PROVER_WALLET_CREDENTIALS).exceptionally((t) -> {
				t.printStackTrace();
				return null;
		}).get();
		//Wallet proverWalletHandle = Wallet.openWallet(proverWalletName, null, null).get();
		Wallet proverWalletHandle = Wallet.openWallet(Const.PROVER_WALLET_CONFIG, Const.PROVER_WALLET_CREDENTIALS).get();

		// 13
		System.out.println("\n13. Prover is creating Master Secret\n");
		String proverMasterSecretId = Anoncreds.proverCreateMasterSecret(proverWalletHandle, Const.PROVER_MASTER_SECRET_ID).get();
    //String proverMasterSecretId = proverCreateMasterSecret(proverWalletHandle, null).get();
    edu.self.indy.indycloud.jpa.Wallet dbWallet = new edu.self.indy.indycloud.jpa.Wallet(Const.PROVER_WALLET_NAME, false);
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
  @Path("step4/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response step4(
    @PathParam("walletId") long walletId,
    String credentialOfferJSON) throws Exception {
    //System.out.println("Received credentialOfferJSON... " + credentialOfferJSON);
    edu.self.indy.indycloud.jpa.Wallet dbWallet = findWalletById(walletId);

    JsonNode credentialOffer = Misc.jsonMapper.readTree(credentialOfferJSON);

    //System.out.println("Parsed into credentialOffer ... " + credentialOffer);
    //System.out.println("Parsed into credentialOffer.fieldNames() ... " + credentialOffer.fieldNames());

    String credOffer = credentialOffer.get("credOffer").toString();
    String credDefJson = credentialOffer.get("credDefJson").toString();
    Wallet proverWalletHandle = Wallet.openWallet(Const.PROVER_WALLET_CONFIG, Const.PROVER_WALLET_CREDENTIALS).get();

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
		//		claimDef, Const.PROVER_MASTER_SECRET_ID).get();
		//System.out.println("Claim Request:\n" + claimRequestJSON);

    System.out.println("\n21. Close wallet\n");
		//issuerWalletHandle.closeWallet().get();
		//Wallet.deleteWallet(walletName, null).get();
		proverWalletHandle.closeWallet().get();

    //return Response.ok( "{\"msg\": \"prover: step4 is done\"}" ).build();
    return Response.ok(  "{\"credReqJson\": " + credReqJson + ", \"credReqMetadataJson\": " + credReqMetadataJson + ", \"action\": \"step4\"}" ).build();
  }


  @POST
  @Path("step6/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response step6(
    @PathParam("walletId") long walletId,
    String credentialJSON) throws Exception {

    JsonNode credentialData = Misc.jsonMapper.readTree(credentialJSON);
    String credDefJson = credentialData.get("credDefJson").toString();
    String credReqMetadataJson = credentialData.get("credReqMetadataJson").toString();
    String credential = credentialData.get("credential").toString();

    Wallet proverWalletHandle = Wallet.openWallet(Const.PROVER_WALLET_CONFIG, Const.PROVER_WALLET_CREDENTIALS).get();

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


  public edu.self.indy.indycloud.jpa.Wallet findWalletById(long walletId) {
    edu.self.indy.indycloud.jpa.Wallet wallet = walletRepository.findById(walletId);
    return wallet;
  }


  @POST
  @Path("step8/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response step8(
    @PathParam("walletId") long walletId,
    String proofRequestJSON) throws Exception {

      edu.self.indy.indycloud.jpa.Wallet dbWallet = findWalletById(walletId);

      JsonNode proofRequestData = Misc.jsonMapper.readTree(proofRequestJSON);
      String proofReqJson = proofRequestData.get("proofReqJson").toString();
      String credDefJson = proofRequestData.get("credDefJson").toString();
      String credDefId = proofRequestData.get("credDefId").asText();
      String schemaId = proofRequestData.get("schemaId").asText();
      String schemaJson = proofRequestData.get("schemaJson").toString();

      Wallet proverWalletHandle = Wallet.openWallet(Const.PROVER_WALLET_CONFIG, Const.PROVER_WALLET_CREDENTIALS).get();

    // 18
    System.out.println("\n18. Prover Gets Credentials for Proof Request\n");
		CredentialsSearchForProofReq credentialsSearch = CredentialsSearchForProofReq.open(proverWalletHandle, proofReqJson, null).get();

		JSONArray credentialsForAttribute1 = new JSONArray(credentialsSearch.fetchNextCredentials("attr1_referent", MAX_ATTRS_PER_CRED).get());
		String credentialIdForAttribute1 = credentialsForAttribute1.getJSONObject(0).getJSONObject("cred_info").getString("referent");

		JSONArray credentialsForAttribute2 = new JSONArray(credentialsSearch.fetchNextCredentials("attr2_referent", MAX_ATTRS_PER_CRED).get());
		String credentialIdForAttribute2 = credentialsForAttribute2.getJSONObject(0).getJSONObject("cred_info").getString("referent");

		JSONArray credentialsForAttribute3 = new JSONArray(credentialsSearch.fetchNextCredentials("attr3_referent", MAX_ATTRS_PER_CRED).get());
		assertEquals(0, credentialsForAttribute3.length());

		JSONArray credentialsForPredicate = new JSONArray(credentialsSearch.fetchNextCredentials("predicate1_referent", MAX_ATTRS_PER_CRED).get());
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

  */

}