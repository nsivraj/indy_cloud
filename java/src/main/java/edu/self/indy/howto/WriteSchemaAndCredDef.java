package edu.self.indy.howto;

import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.json.JSONArray;

import static org.hyperledger.indy.sdk.anoncreds.Anoncreds.issuerCreateAndStoreCredentialDef;
import static org.hyperledger.indy.sdk.anoncreds.Anoncreds.issuerCreateSchema;
import static org.hyperledger.indy.sdk.ledger.Ledger.*;

//@SpringBootApplication
public class WriteSchemaAndCredDef {
	public static void main(String[] args) throws Exception {
		WriteSchemaAndCredDef.demo();
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
		Wallet walletHandle = Wallet.openWallet(Utils.ISSUER_WALLET_CONFIG, Utils.ISSUER_WALLET_CREDENTIALS).get();

		// 5
		System.out.println("\n5. Generating and storing steward DID and Verkey\n");
		String did_json = "{\"seed\": \"" + Utils.STEWARD_SEED + "\"}";
		DidResults.CreateAndStoreMyDidResult stewardResult = Did.createAndStoreMyDid(walletHandle, did_json).get();
		String defaultStewardDid = stewardResult.getDid();
		System.out.println("Steward DID: " + defaultStewardDid);
		System.out.println("Steward Verkey: " + stewardResult.getVerkey());

		// 6.
		System.out.println("\n6. Generating and storing Trust Anchor DID and Verkey\n");
		DidResults.CreateAndStoreMyDidResult trustAnchorResult = Did.createAndStoreMyDid(walletHandle, "{}").get();
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
		String nymResponseJson = signAndSubmitRequest(pool, walletHandle, defaultStewardDid, nymRequest).get();
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
		String schemaResponse = signAndSubmitRequest(pool, walletHandle, defaultStewardDid, schemaRequest).get();
		System.out.println("Schema response:\n" + schemaResponse);

		// 11
		System.out.println("\n11. Creating and storing CREDENTIAL DEFINITION using anoncreds as Trust Anchor, for the given Schema\n");
		//String schemaDataJSON = "{\"seqNo\": 1, \"dest\": \"" + defaultStewardDid + "\", \"data\": " + schemaJson + "}";
		System.out.println("Schema:\n" + schemaJson);
		//String claimDef = issuerCreateAndStoreCredentialDef(walletHandle, trustAnchorDID, schemaDataJSON, "CL", false).get();
		AnoncredsResults.IssuerCreateAndStoreCredentialDefResult createCredentialDefResult = issuerCreateAndStoreCredentialDef(walletHandle, trustAnchorDID, schemaJson, "tag1", null, Utils.DEFAULT_REVOCATION_CONFIG).get();
		String credDefId = createCredentialDefResult.getCredDefId();
		String credDefJson = createCredentialDefResult.getCredDefJson();
		System.out.println("Credential Definition:\n" + credDefJson);

		// 12
		System.out.println("\n12. Close wallet\n");
		walletHandle.closeWallet().get();
		//Wallet.deleteWallet(walletName, null).get();

		// 13
		System.out.println("\n13. Close pool\n");
		pool.closePoolLedger().get();

		// 14
		//System.out.println("\n14. Delete pool ledger config\n");
		//Pool.deletePoolLedgerConfig(poolName).get();
	}
}