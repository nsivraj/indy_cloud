package edu.self.indy.howto;

import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.hyperledger.indy.sdk.anoncreds.Anoncreds;

import static org.hyperledger.indy.sdk.anoncreds.Anoncreds.*;
import static org.hyperledger.indy.sdk.ledger.Ledger.*;

@SpringBootApplication
public class IssueCredential {

	public static void main(String[] args) throws Exception {
		IssueCredential.demo();
	}

	static void demo() throws Exception {
		// 1.
		System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
		Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
		Pool.createPoolLedgerConfig(Utils.POOL_NAME, Utils.POOL_CONFIG).exceptionally((t) -> {
				t.printStackTrace();
				return null;
		}).thenAccept(result -> {
				// Need to put this logic in every accept because that is how ugly Java's
				// promise API is
				// even if exceptionally is called, then also thenAccept block will be called
				// we either need to switch to complete method and pass two callbacks as
				// parameter
				// till we change to that API, we have to live with this IF condition
				// also reason to add this if condition is because we already rejected promise
				// in
				// exceptionally block, if we call promise.resolve now, then it `thenAccept`
				// block
				// would throw an exception that would not be caught here, because this is an
				// async
				// block and above try catch would not catch this exception
				System.out.println("The result from createPoolLedgerConfig is: " + result);
				//return null;
		});

		// 2
		System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
		Pool pool = Pool.openPoolLedger(Utils.POOL_NAME, "{}").get();

		// 3
		System.out.println("\n3. Creates a new secure wallet\n");
		Wallet.createWallet(Utils.WALLET_CONFIG, Utils.WALLET_CREDENTIALS).exceptionally((t) -> {
				t.printStackTrace();
				return null;
		}).thenAccept(result -> {
				// Need to put this logic in every accept because that is how ugly Java's
				// promise API is
				// even if exceptionally is called, then also thenAccept block will be called
				// we either need to switch to complete method and pass two callbacks as
				// parameter
				// till we change to that API, we have to live with this IF condition
				// also reason to add this if condition is because we already rejected promise
				// in
				// exceptionally block, if we call promise.resolve now, then it `thenAccept`
				// block
				// would throw an exception that would not be caught here, because this is an
				// async
				// block and above try catch would not catch this exception
				System.out.println("The result from createWallet is: " + result);
				//return null;
		});

		// 4
		System.out.println("\n4. Open wallet and get the wallet handle from libindy\n");
		Wallet walletHandle = Wallet.openWallet(Utils.WALLET_CONFIG, Utils.WALLET_CREDENTIALS).get();

		// 5
		System.out.println("\n5. Generating and storing steward DID and Verkey\n");
		String did_json = "{\"seed\": \"" + stewardSeed + "\"}";
		DidResults.CreateAndStoreMyDidResult stewardResult = Did.createAndStoreMyDid(walletHandle, did_json).get();
		String defaultStewardDid = stewardResult.getDid();
		System.out.println("Steward did: " + defaultStewardDid);

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
		String name = "gvt";
		String version = "1.0";
		String attributes = "[\"age\", \"sex\", \"height\", \"name\"]";
		String schemaDataJSON = "{\"name\":\"" + name + "\",\"version\":\"" + version + "\",\"attr_names\":" + attributes + "}";
		System.out.println("Schema: " + schemaDataJSON);
		String schemaRequest = buildSchemaRequest(defaultStewardDid, schemaDataJSON).get();
		System.out.println("Schema request:\n" + schemaRequest);

		// 10
		System.out.println("\n10. Sending the SCHEMA request to the ledger\n");
		String schemaResponse = signAndSubmitRequest(pool, walletHandle, defaultStewardDid, schemaRequest).get();
		System.out.println("Schema response:\n" + schemaResponse);

		// 11
		System.out.println("\n11. Creating and storing CLAIM DEFINITION using anoncreds as Trust Anchor, for the given Schema\n");
		String schemaJSON = "{\"seqNo\": 1, \"dest\": \"" + defaultStewardDid + "\", \"data\": " + schemaDataJSON + "}";
		System.out.println("Schema:\n" + schemaJSON);
		String claimDef = issuerCreateAndStoreClaimDef(walletHandle, trustAnchorDID, schemaJSON, "CL", false).get();
		System.out.println("Claim Definition:\n" + claimDef);

		// 12
		System.out.println("\n12. Creating Prover wallet and opening it to get the handle\n");
		String proverDID = "VsKV7grR1BUE29mG2Fm2kX";
		String proverWalletName = "prover_wallet";
		Wallet.createWallet(poolName, proverWalletName, null, null, null);
		Wallet proverWalletHandle = Wallet.openWallet(proverWalletName, null, null).get();

		// 13
		System.out.println("\n13. Prover is creating Master Secret\n");
		String masterSecretName = "master_secret";
		Anoncreds.proverCreateMasterSecret(proverWalletHandle, masterSecretName).get();

		// 14
		System.out.println("\n14. Issuer (Trust Anchor) is creating a Claim Offer for Prover\n");
		String claimOfferJSON = issuerCreateClaimOffer(walletHandle, schemaJSON, trustAnchorDID, proverDID).get();
		System.out.println("Claim Offer:\n" + claimOfferJSON);

		// 15
		System.out.println("\n15. Prover creates Claim Request\n");
		String claimRequestJSON = proverCreateAndStoreClaimReq(proverWalletHandle, proverDID, claimOfferJSON,
				claimDef, masterSecretName).get();
		System.out.println("Claim Request:\n" + claimRequestJSON);

		// 16
		System.out.println("\n16. Issuer (Trust Anchor) creates Claim for Claim Request\n");
		// Encoded value of non-integer attribute is SHA256 converted to decimal
		// note that encoding is not standardized by Indy except that 32-bit integers are encoded as themselves. IS-786
                String credAttribsJson = "{\n" +
                "               \"sex\":[\"male\",\"5944657099558967239210949258394887428692050081607692519917050011144233115103\"],\n" +
                "               \"name\":[\"Alex\",\"99262857098057710338306967609588410025648622308394250666849665532448612202874\"],\n" +
                "               \"height\":[\"175\",\"175\"],\n" +
                "               \"age\":[\"28\",\"28\"]\n" +
                "        }";
		AnoncredsResults.IssuerCreateClaimResult createClaimResult = issuerCreateClaim(walletHandle, claimRequestJSON,
				credAttribsJson, - 1).get();
		String claimJSON = createClaimResult.getClaimJson();
		System.out.println("Claim:\n" + claimJSON);

		// 17
		System.out.println("\n17. Prover processes and stores Claim\n");
		Anoncreds.proverStoreClaim(proverWalletHandle, claimJSON, null).get();

		// 18
		System.out.println("\n18. Close wallet\n");
		walletHandle.closeWallet().get();
		//Wallet.deleteWallet(walletName, null).get();

		// 19
		System.out.println("\n19. Close pool\n");
		pool.closePoolLedger().get();

		// 20
		//System.out.println("\n20. Delete pool ledger config\n");
		//Pool.deletePoolLedgerConfig(poolName).get();
	}
}