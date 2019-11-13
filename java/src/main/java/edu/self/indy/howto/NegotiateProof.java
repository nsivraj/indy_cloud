package edu.self.indy.howto;

import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults;
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.hyperledger.indy.sdk.anoncreds.Anoncreds;

import org.json.JSONArray;
import org.json.JSONObject;
import static org.junit.Assert.*;

import static org.hyperledger.indy.sdk.anoncreds.Anoncreds.*;
import static org.hyperledger.indy.sdk.ledger.Ledger.*;

//@SpringBootApplication
public class NegotiateProof {

	public static void main(String[] args) throws Exception {
		NegotiateProof.demo();
	}

	static void demo() throws Exception {
		// 1.
		System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
		Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
		Pool.createPoolLedgerConfig(Utils.ISSUER_POOL_NAME, Utils.SERVERONE_POOL_CONFIG).exceptionally((t) -> {
				t.printStackTrace();
				return null;
		}).get();

		// 2
		System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
		Pool pool = Pool.openPoolLedger(Utils.ISSUER_POOL_NAME, "{}").get();

		// 3
		System.out.println("\n3. Creates a new secure wallet\n");
		Wallet.createWallet(Utils.ISSUER_WALLET_CONFIG, Utils.ISSUER_WALLET_CREDENTIALS).exceptionally((t) -> {
				t.printStackTrace();
				return null;
		}).get();

		// 4
		System.out.println("\n4. Open wallet and get the wallet handle from libindy\n");
		Wallet issuerWalletHandle = Wallet.openWallet(Utils.ISSUER_WALLET_CONFIG, Utils.ISSUER_WALLET_CREDENTIALS).get();

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
		AnoncredsResults.IssuerCreateAndStoreCredentialDefResult createCredentialDefResult = issuerCreateAndStoreCredentialDef(issuerWalletHandle, trustAnchorDID, schemaJson, "tag1", null, Utils.DEFAULT_REVOCATION_CONFIG).get();
		String credDefId = createCredentialDefResult.getCredDefId();
		String credDefJson = createCredentialDefResult.getCredDefJson();
		System.out.println("Credential Definition:\n" + credDefJson);


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

		// 14
		System.out.println("\n14. Issuer (Trust Anchor) is creating a Credential Offer for Prover\n");
		//String claimOfferJSON = issuerCreateClaimOffer(issuerWalletHandle, schemaJson, trustAnchorDID, proverDID).get();
		String credOffer = issuerCreateCredentialOffer(issuerWalletHandle, credDefId).get();
		System.out.println("Credential Offer:\n" + credOffer);

		// 15
		System.out.println("\n15. Prover creates Credential Request\n");
		DidResults.CreateAndStoreMyDidResult proverDidResult = Did.createAndStoreMyDid(proverWalletHandle, "{}").get();
		String proverDid = proverDidResult.getDid();
		String proverVerkey = proverDidResult.getVerkey();
		AnoncredsResults.ProverCreateCredentialRequestResult createCredReqResult =
				proverCreateCredentialReq(proverWalletHandle, proverDid, credOffer, credDefJson, proverMasterSecretId).get();
		String credReqJson = createCredReqResult.getCredentialRequestJson();
		String credReqMetadataJson = createCredReqResult.getCredentialRequestMetadataJson();

		//String claimRequestJSON = proverCreateAndStoreClaimReq(proverWalletHandle, proverDID, claimOfferJSON,
		//		claimDef, Utils.PROVER_MASTER_SECRET).get();
		//System.out.println("Claim Request:\n" + claimRequestJSON);


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


		// 17
		System.out.println("\n17. Prover processes and stores Credential\n");
		proverStoreCredential(proverWalletHandle, null, credReqMetadataJson, credential, credDefJson, null).get();
		//Anoncreds.proverStoreClaim(proverWalletHandle, claimJSON, null).get();




    // 18
    System.out.println("\n18. Prover Gets Credentials for Proof Request\n");
    String nonce = generateNonce().get();
    // NOTE: the proofRequestJson MUST come from the verifier
		String proofRequestJson = new JSONObject()
				.put("nonce", nonce)
				.put("name", "proof_req_1")
				.put("version", "0.1")
				.put("requested_attributes", new JSONObject()
						.put("attr1_referent", new JSONObject().put("name", "name"))
						.put("attr2_referent", new JSONObject().put("name", "sex"))
						.put("attr3_referent", new JSONObject().put("name", "phone"))
				)
				.put("requested_predicates", new JSONObject()
						.put("predicate1_referent", new JSONObject()
								.put("name", "age")
								.put("p_type", ">=")
								.put("p_value", 18)
						)
				)
				.toString();

		CredentialsSearchForProofReq credentialsSearch = CredentialsSearchForProofReq.open(proverWalletHandle, proofRequestJson, null).get();

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

		String proofJson = "";
		try {
			proofJson = proverCreateProof(proverWalletHandle, proofRequestJson, requestedCredentialsJson,
        proverMasterSecretId, schemas, credentialDefs, revocStates).get();
		} catch (Exception e){
			System.out.println("");
		}

    System.out.println("The proofJson is:");
    System.out.println(proofJson);
		JSONObject proof = new JSONObject(proofJson);



    // 20
    System.out.println("\n20. Verifier verify Proof\n");
		JSONObject revealedAttr1 = proof.getJSONObject("requested_proof").getJSONObject("revealed_attrs").getJSONObject("attr1_referent");
		assertEquals("Alex", revealedAttr1.getString("raw"));

		assertNotNull(proof.getJSONObject("requested_proof").getJSONObject("unrevealed_attrs").getJSONObject("attr2_referent").getInt("sub_proof_index"));

		assertEquals(selfAttestedValue, proof.getJSONObject("requested_proof").getJSONObject("self_attested_attrs").getString("attr3_referent"));

		String revocRegDefs = new JSONObject().toString();
		String revocRegs = new JSONObject().toString();

		Boolean valid = verifierVerifyProof(proofRequestJson, proofJson, schemas, credentialDefs, revocRegDefs, revocRegs).get();
		assertTrue(valid);











		// 21
		System.out.println("\n21. Close wallet\n");
		issuerWalletHandle.closeWallet().get();
		//Wallet.deleteWallet(walletName, null).get();
		proverWalletHandle.closeWallet().get();

		// 22
		System.out.println("\n22. Close pool\n");
		pool.closePoolLedger().get();

		// 23
		//System.out.println("\n20. Delete pool ledger config\n");
		//Pool.deletePoolLedgerConfig(poolName).get();
	}
}
