package edu.self.indy.indycloud;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
import edu.self.indy.util.MiscDB;

@Path("/author")
public class AuthorResource {

	@Autowired
  WalletRepository walletRepository;

  @GET
  @Path("createWallet/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
	public Response createAuthorWallet(@PathParam("walletId") long walletId) throws Exception {

		Wallet authorWallet = null;
    Response resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RuntimeException("author :: createWallet")).build();

		try {
			JsonNode walletConfigData = Misc.jsonMapper.readTree(Utils.AUTHOR_WALLET_CONFIG);
      String storageConfigPath = Misc.getStorageConfigPath(walletId);
      ((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
      String walletConfig = walletConfigData.toString();
      System.out.println("The walletConfig is: " + walletConfig);

			// 3. Create and Open Author Wallet
			Wallet.createWallet(walletConfig, Utils.AUTHOR_WALLET_CREDENTIALS).get();
			authorWallet = Wallet.openWallet(walletConfig, Utils.AUTHOR_WALLET_CREDENTIALS).get();

			// 5. Create Author DID
			CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(authorWallet, "{}").get();
			String authorDid = createMyDidResult.getDid();
			String authorVerkey = createMyDidResult.getVerkey();
			System.out.println("Author did: " + authorDid);
			System.out.println("Author verkey: " + authorVerkey);

			edu.self.indy.indycloud.jpa.Wallet dbWallet = MiscDB.findWalletById(walletRepository, walletId);
      dbWallet.walletDID = authorDid;
      dbWallet = walletRepository.save(dbWallet);

			resp = Response.ok( "{\"action\": \"author/createWallet\", \"authorDid\": \"" + authorDid + "\", \"authorVerkey\": \"" + authorVerkey + "\"}" ).build();

		} catch(Exception ex) {
			ex.printStackTrace();
      resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
    } finally {
      if(authorWallet != null) {
        authorWallet.closeWallet().get();
        //Wallet.deleteWallet(walletConfig, Utils.AUTHOR_WALLET_CREDENTIALS).get();
      }
    }

    return resp;
	}


  @POST
  @Path("createSchema/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
	public Response createSchema(
    @PathParam("walletId") long walletId,
    String schemaPayload) throws Exception {

		JsonNode schemaData = Misc.jsonMapper.readTree(schemaPayload);
		String endorserDid = schemaData.get("endorserDid").asText();
		String authorDid = schemaData.get("authorDid").asText();
		String schemaName = schemaData.get("schemaName").asText();
		String schemaVersion = schemaData.get("schemaVersion").asText();
		String schemaAttributes = schemaData.get("schemaAttributes").toString();

		JsonNode walletConfigData = Misc.jsonMapper.readTree(Utils.AUTHOR_WALLET_CONFIG);
		String storageConfigPath = Misc.getStorageConfigPath(walletId);
		((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
		String walletConfig = walletConfigData.toString();
		System.out.println("The walletConfig is: " + walletConfig);

	// System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
		// Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
		// Pool pool = Pool.openPoolLedger(Utils.AUTHOR_POOL_NAME, "{}").get();

		Wallet authorWallet = Wallet.openWallet(walletConfig, Utils.AUTHOR_WALLET_CREDENTIALS).get();
		// CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(authorWallet, "{}").get();
		// String authorDid = createMyDidResult.getDid();

		//String schemaName = "gvt";
		//String schemaVersion = "1.0";
		//String schemaAttributes = new JSONArray().put("name").put("age").put("sex").put("height").toString();
		AnoncredsResults.IssuerCreateSchemaResult createSchemaResult =
						issuerCreateSchema(authorDid, schemaName, schemaVersion, schemaAttributes).get();
		String schemaId = createSchemaResult.getSchemaId();
		String schemaJson = createSchemaResult.getSchemaJson();

		//  Transaction Author builds Schema Request
		String schemaRequest = buildSchemaRequest(authorDid, schemaJson).get();

		//  Transaction Author appends Endorser's DID into the request
		String schemaRequestWithEndorser = appendRequestEndorser(schemaRequest, endorserDid).get();

		//  Transaction Author signs the request with the added endorser field
		String schemaRequestWithEndorserSignedByAuthor =
						multiSignRequest(authorWallet, authorDid, schemaRequestWithEndorser).get();

		//pool.closePoolLedger().get();
		//Pool.deletePoolLedgerConfig(Utils.AUTHOR_POOL_NAME).get();

		authorWallet.closeWallet().get();
		//Wallet.deleteWallet(walletConfig, Utils.AUTHOR_WALLET_CREDENTIALS).get();

		return Response.ok( "{\"action\": \"author/createSchema\", \"signedSchemaRequest\": " + schemaRequestWithEndorserSignedByAuthor + ", \"schemaJson\": " + schemaJson + ", \"schemaId\": \"" + schemaId + "\"}" ).build();
	}


  @POST
  @Path("createCredDef/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
	public Response createCredentialDef(
    @PathParam("walletId") long walletId,
    String credDefPayload) throws Exception {

		JsonNode credDefData = Misc.jsonMapper.readTree(credDefPayload);
		String schemaJson = credDefData.get("schemaJson").toString();
		String authorDid = credDefData.get("authorDid").asText();

		JsonNode walletConfigData = Misc.jsonMapper.readTree(Utils.AUTHOR_WALLET_CONFIG);
		String storageConfigPath = Misc.getStorageConfigPath(walletId);
		((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
		String walletConfig = walletConfigData.toString();
		System.out.println("The walletConfig is: " + walletConfig);

		// System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
		// Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
		// Pool pool = Pool.openPoolLedger(Utils.AUTHOR_POOL_NAME, "{}").get();

		Wallet authorWallet = Wallet.openWallet(walletConfig, Utils.AUTHOR_WALLET_CREDENTIALS).get();
		// CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(authorWallet, "{}").get();
		// String authorDid = createMyDidResult.getDid();

		//12. Author create Credential Definition
		String credDefTag = "NameOfTheCred1";
		String credDefConfigJson = new JSONObject().put("support_revocation", false).toString();
		AnoncredsResults.IssuerCreateAndStoreCredentialDefResult createCredDefResult =
			 issuerCreateAndStoreCredentialDef(authorWallet, authorDid, schemaJson, credDefTag, null, credDefConfigJson).get();
		// AnoncredsResults.IssuerCreateAndStoreCredentialDefResult createCredDefResult =
		//     issuerCreateAndStoreCredentialDef(endorserWallet, endorserDid, schemaJson, credDefTag, null, credDefConfigJson).get();
		String credDefId = createCredDefResult.getCredDefId();
		String credDefJson = createCredDefResult.getCredDefJson();
		JSONObject credDefJsonObject = new JSONObject(credDefJson);
		//System.out.println("credDefJsonObject :: " + credDefJsonObject.get("tag"));
		assertTrue(credDefTag.equals(credDefJsonObject.get("tag")));

		// NOTE: sleep for 5 seconds to give ledger time to persist changes
		//Thread.sleep(5000);

		//pool.closePoolLedger().get();
		//Pool.deletePoolLedgerConfig(Utils.AUTHOR_POOL_NAME).get();

		authorWallet.closeWallet().get();
		//Wallet.deleteWallet(walletConfig, Utils.AUTHOR_WALLET_CREDENTIALS).get();

		return Response.ok( "{\"action\": \"author/createCredDef\", \"credDefId\": \"" + credDefId + "\", \"credDefJson\": " + credDefJson + "}" ).build();
	}


  @POST
  @Path("createCredOffer/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
	public Response createCredOffer(
    @PathParam("walletId") long walletId,
    String credOfferPayload) throws Exception {

		JsonNode credOfferData = Misc.jsonMapper.readTree(credOfferPayload);
		String credDefId = credOfferData.get("credDefId").asText();

		JsonNode walletConfigData = Misc.jsonMapper.readTree(Utils.AUTHOR_WALLET_CONFIG);
		String storageConfigPath = Misc.getStorageConfigPath(walletId);
		((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
		String walletConfig = walletConfigData.toString();
		System.out.println("The walletConfig is: " + walletConfig);

		// System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
		// Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
		// Pool pool = Pool.openPoolLedger(Utils.AUTHOR_POOL_NAME, "{}").get();

		Wallet authorWallet = Wallet.openWallet(walletConfig, Utils.AUTHOR_WALLET_CREDENTIALS).get();

		//14. Issuer Creates Credential Offer
		String credOffer = issuerCreateCredentialOffer(authorWallet, credDefId).get();
		// String credOffer = issuerCreateCredentialOffer(endorserWallet, credDefId).get();

		// NOTE: sleep for 5 seconds to give ledger time to persist changes
		//Thread.sleep(5000);

		//pool.closePoolLedger().get();
		//Pool.deletePoolLedgerConfig(Utils.AUTHOR_POOL_NAME).get();

		authorWallet.closeWallet().get();
		//Wallet.deleteWallet(walletConfig, Utils.AUTHOR_WALLET_CREDENTIALS).get();

		return Response.ok( "{\"action\": \"author/createCredOffer\", \"credOffer\": " + credOffer + "}" ).build();
	}


  @POST
  @Path("createCredential/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
	public Response createCredential(
    @PathParam("walletId") long walletId,
    String credentialPayload) throws Exception {

		JsonNode credentialData = Misc.jsonMapper.readTree(credentialPayload);
		String credOffer = credentialData.get("credOffer").toString();
		String credReqJson = credentialData.get("credReqJson").toString();

		JsonNode walletConfigData = Misc.jsonMapper.readTree(Utils.AUTHOR_WALLET_CONFIG);
		String storageConfigPath = Misc.getStorageConfigPath(walletId);
		((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
		String walletConfig = walletConfigData.toString();
		System.out.println("The walletConfig is: " + walletConfig);

		// System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
		// Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
		// Pool pool = Pool.openPoolLedger(Utils.AUTHOR_POOL_NAME, "{}").get();

		Wallet authorWallet = Wallet.openWallet(walletConfig, Utils.AUTHOR_WALLET_CREDENTIALS).get();

		//16. Issuer create Credential
		//   note that encoding is not standardized by Indy except that 32-bit integers are encoded as themselves. IS-786
		String credValuesJson = new JSONObject()
				.put("sex", new JSONObject().put("raw", "male").put("encoded", "594465709955896723921094925839488742869205008160769251991705001"))
				.put("name", new JSONObject().put("raw", "Alex").put("encoded", "1139481716457488690172217916278103335"))
				.put("height", new JSONObject().put("raw", "175").put("encoded", "175"))
				.put("age", new JSONObject().put("raw", "28").put("encoded", "28"))
		.toString();

		AnoncredsResults.IssuerCreateCredentialResult createCredentialResult =
				issuerCreateCredential(authorWallet, credOffer, credReqJson, credValuesJson, null, - 1).get();
		// AnoncredsResults.IssuerCreateCredentialResult createCredentialResult =
		//     issuerCreateCredential(endorserWallet, credOffer, credReqJson, credValuesJson, null, - 1).get();
		String credential = createCredentialResult.getCredentialJson();

		//pool.closePoolLedger().get();
		//Pool.deletePoolLedgerConfig(Utils.AUTHOR_POOL_NAME).get();

		authorWallet.closeWallet().get();
		//Wallet.deleteWallet(walletConfig, Utils.AUTHOR_WALLET_CREDENTIALS).get();

		return Response.ok( "{\"action\": \"author/createCredential\", \"credential\": " + credential + "}" ).build();
	}


	/*
  @GET
  @Path("step1/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response step1(@PathParam("walletId") long walletId) throws Exception {
		// 1.
		System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
		Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
		Pool.createPoolLedgerConfig(Utils.AUTHOR_POOL_NAME, Utils.SERVERONE_POOL_CONFIG).exceptionally((t) -> {
				t.printStackTrace();
				return null;
		}).get();

		// 2
		System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
		Pool pool = Pool.openPoolLedger(Utils.AUTHOR_POOL_NAME, "{}").get();

		// 3
		System.out.println("\n3. Creates a new secure wallet\n");
		Wallet.createWallet(Utils.AUTHOR_WALLET_CONFIG, Utils.AUTHOR_WALLET_CREDENTIALS).exceptionally((t) -> {
				t.printStackTrace();
				return null;
		}).get();

		// 4
		System.out.println("\n4. Open wallet and get the wallet handle from libindy\n");
		Wallet issuerWalletHandle = Wallet.openWallet(Utils.AUTHOR_WALLET_CONFIG, Utils.AUTHOR_WALLET_CREDENTIALS).get();

		// 5
		System.out.println("\n5. Generating and storing steward DID and Verkey\n");
		String did_json = "{\"seed\": \"" + Utils.STEWARD_SEED + "\"}";
		DidResults.CreateAndStoreMyDidResult stewardResult = Did.createAndStoreMyDid(issuerWalletHandle, did_json).get();
		String defaultStewardDid = stewardResult.getDid();
		System.out.println("Steward did: " + defaultStewardDid);
		System.out.println("Steward Verkey: " + stewardResult.getVerkey());

		// 6.
		System.out.println("\n6. Generating and storing Trust Anchor DID and Verkey\n");
		DidResults.CreateAndStoreMyDidResult trustAnchorResult = Did.createAndStoreMyDid(issuerWalletHandle, "{}").get();
		String trustAnchorDID = trustAnchorResult.getDid();
		String trustAnchorVerkey = trustAnchorResult.getVerkey();
		System.out.println("Trust anchor DID: " + trustAnchorDID);
		System.out.println("Trust anchor Verkey: " + trustAnchorVerkey);

		// 7
		System.out.println("\n7. Build NYM request to add Trust Anchor to the ledger\n");
		String nymRequest = buildNymRequest(defaultStewardDid, trustAnchorDID, trustAnchorVerkey, null, "TRUST_ANCHOR").get();
		System.out.println("NYM request JSON:\n" + nymRequest);

		// 8
		System.out.println("\n8. Sending the nym request to ledger\n");
		String nymResponseJson = signAndSubmitRequest(pool, issuerWalletHandle, defaultStewardDid, nymRequest).get();
		System.out.println("NYM transaction response:\n" + nymResponseJson);


		// 9
		System.out.println("\n9. Build the SCHEMA request to add new schema to the ledger as a Steward\n");
		//{"data":{"attrNames":["fname","lname","iid","phonenumber","dept"],"name":"Employee Info2","version":"1.02"},"paymentHandle":0,"sourceId":"EmpId2","schemaId":"J3DNGdWmn1W9U1NVa3ZRL8:2:Employee Info2:1.02"}

		String schemaName = "gvt";
		String schemaVersion = "1.0";
		//String attributes = "[\"age\", \"sex\", \"height\", \"name\"]";
		String schemaAttributes = new JSONArray().put("name").put("age").put("sex").put("height").toString();
		//String schemaDataJSON = "{\"name\":\"" + name + "\",\"version\":\"" + version + "\",\"attrNames\":" + attributes + ", id=\"id\", ver=\"1.0\"}";
		System.out.println("schemaAttributes: " + schemaAttributes);
		AnoncredsResults.IssuerCreateSchemaResult createSchemaResult =
						issuerCreateSchema(defaultStewardDid, schemaName, schemaVersion, schemaAttributes).get();
		String schemaId = createSchemaResult.getSchemaId();
		String schemaJson = createSchemaResult.getSchemaJson();
		String schemaRequest = buildSchemaRequest(defaultStewardDid, schemaJson).get();
		System.out.println("Schema request:\n" + schemaRequest);

		// String schemaName = "gvt";
		// String schemaVersion = "1.0";
		// String schemaAttributes = new JSONArray().put("name").put("age").put("sex").put("height").toString();
		// AnoncredsResults.IssuerCreateSchemaResult createSchemaResult =
		// 				issuerCreateSchema(authorDid, schemaName, schemaVersion, schemaAttributes).get();
		// String schemaId = createSchemaResult.getSchemaId();
		// String schemaJson = createSchemaResult.getSchemaJson();


		// 10
		System.out.println("\n10. Sending the SCHEMA request to the ledger\n");
		String schemaResponse = signAndSubmitRequest(pool, issuerWalletHandle, defaultStewardDid, schemaRequest).get();
		System.out.println("Schema response:\n" + schemaResponse);

		// 11
		System.out.println("\n11. Creating and storing CREDENTIAL DEFINITION using anoncreds as Trust Anchor, for the given Schema\n");
		//String schemaDataJSON = "{\"seqNo\": 1, \"dest\": \"" + defaultStewardDid + "\", \"data\": " + schemaJson + "}";
		System.out.println("Schema:\n" + schemaJson);
		//String claimDef = issuerCreateAndStoreCredentialDef(issuerWalletHandle, trustAnchorDID, schemaDataJSON, "CL", false).get();
		AnoncredsResults.IssuerCreateAndStoreCredentialDefResult createCredentialDefResult = issuerCreateAndStoreCredentialDef					(issuerWalletHandle, trustAnchorDID, schemaJson, "tag1", null, Utils.DEFAULT_REVOCATION_CONFIG).get();
		String credDefId = createCredentialDefResult.getCredDefId();
		String credDefJson = createCredentialDefResult.getCredDefJson();
		System.out.println("Credential Definition:\n" + credDefJson);

		System.out.println("\n21. Close wallet\n");
		issuerWalletHandle.closeWallet().get();
		//Wallet.deleteWallet(walletName, null).get();
		//proverWalletHandle.closeWallet().get();

		// 22
		System.out.println("\n22. Close pool\n");
		pool.closePoolLedger().get();

    return Response.ok( "{\"credDefId\": \"" + credDefId + "\", \"schemaId\": \"" + schemaId + "\", \"credDefJson\": " + credDefJson + ", \"schemaJson\": " + schemaJson + ", \"action\": \"step1\"}" ).build();
	}

	// public Response getCloudResponse(String cloudResp, String stepName) {
  //   String cloudResponse = "{\"cloudResponse\": " + cloudResp + ",\"action\": \"" + stepName + "\"}";
  //   System.out.println("cloudResponse: "+cloudResponse);
  //   return Response.ok( cloudResponse ).build();
  // }

	@GET
  @Path("step3/{walletId}/{credDefId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
	public Response step3(@PathParam("walletId") long walletId, @PathParam("credDefId") String credDefId) throws Exception {

		//System.out.println("step3: Using credDefId that was passed in... " + credDefId);

		Wallet issuerWalletHandle = Wallet.openWallet(Utils.AUTHOR_WALLET_CONFIG, Utils.AUTHOR_WALLET_CREDENTIALS).get();

		// 14
		System.out.println("\n14. Issuer (Trust Anchor) is creating a Credential Offer for Prover\n");
		//String claimOfferJSON = issuerCreateClaimOffer(issuerWalletHandle, schemaJson, trustAnchorDID, proverDID).get();
		String credOffer = issuerCreateCredentialOffer(issuerWalletHandle, credDefId).get();
		System.out.println("Credential Offer:\n" + credOffer);

		System.out.println("\n21. Close wallet\n");
		issuerWalletHandle.closeWallet().get();
		//Wallet.deleteWallet(walletName, null).get();
		//proverWalletHandle.closeWallet().get();

		return Response.ok(  "{\"credOffer\": " + credOffer + ", \"action\": \"step3\"}" ).build();
	}

  @POST
  @Path("step5/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
	public Response step5(
    @PathParam("walletId") long walletId,
    String credentialRequestJSON) throws Exception {
    JsonNode credentialOffer = Misc.jsonMapper.readTree(credentialRequestJSON);
    String credOffer = credentialOffer.get("credOffer").toString();
    String credReqJson = credentialOffer.get("credReqJson").toString();

		Wallet issuerWalletHandle = Wallet.openWallet(Utils.AUTHOR_WALLET_CONFIG, Utils.AUTHOR_WALLET_CREDENTIALS).get();

		// 16
		System.out.println("\n16. Issuer (Trust Anchor) creates Credential for Credential Request\n");
		//   note that encoding is not standardized by Indy except that 32-bit integers are encoded as themselves. IS-786
		String credValuesJson = new JSONObject()
				.put("sex", new JSONObject().put("raw", "male").put("encoded", "594465709955896723921094925839488742869205008160769251991705001"))
				.put("name", new JSONObject().put("raw", "Alex").put("encoded", "1139481716457488690172217916278103335"))
				.put("height", new JSONObject().put("raw", "175").put("encoded", "175"))
				.put("age", new JSONObject().put("raw", "28").put("encoded", "28"))
		.toString();

		AnoncredsResults.IssuerCreateCredentialResult createCredentialResult =
				issuerCreateCredential(issuerWalletHandle, credOffer, credReqJson, credValuesJson, null, - 1).get();
		String credential = createCredentialResult.getCredentialJson();
		// Encoded value of non-integer attribute is SHA256 converted to decimal
		// note that encoding is not standardized by Indy except that 32-bit integers are encoded as themselves. IS-786
		// String credAttribsJson = "{\n" +
		// "               \"sex\":[\"male\",\"5944657099558967239210949258394887428692050081607692519917050011144233115103\"],\n" +
		// "               \"name\":[\"Alex\",\"99262857098057710338306967609588410025648622308394250666849665532448612202874\"],\n" +
		// "               \"height\":[\"175\",\"175\"],\n" +
		// "               \"age\":[\"28\",\"28\"]\n" +
		// "        }";
		// AnoncredsResults.IssuerCreateClaimResult createClaimResult = issuerCreateClaim(issuerWalletHandle, claimRequestJSON,
		// 		credAttribsJson, - 1).get();
		// String claimJSON = createClaimResult.getClaimJson();
		// System.out.println("Claim:\n" + claimJSON);

		System.out.println("\n21. Close wallet\n");
		issuerWalletHandle.closeWallet().get();
		//Wallet.deleteWallet(walletName, null).get();
		//proverWalletHandle.closeWallet().get();

		return Response.ok(  "{\"credential\": " + credential + ", \"action\": \"step5\"}" ).build();
	}
	*/
}
