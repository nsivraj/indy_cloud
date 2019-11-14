package edu.self.indy.sample;

import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults;
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidJSONParameters;
import org.hyperledger.indy.sdk.did.DidResults.CreateAndStoreMyDidResult;
import org.hyperledger.indy.sdk.LibIndy;
import org.hyperledger.indy.sdk.IndyException;

import static org.hyperledger.indy.sdk.ledger.Ledger.buildNymRequest;
import static org.hyperledger.indy.sdk.ledger.Ledger.buildSchemaRequest;
import static org.hyperledger.indy.sdk.ledger.Ledger.signAndSubmitRequest;
import static org.hyperledger.indy.sdk.ledger.Ledger.submitRequest;
import static org.hyperledger.indy.sdk.ledger.Ledger.multiSignRequest;
import static org.hyperledger.indy.sdk.ledger.Ledger.appendRequestEndorser;
import static org.hyperledger.indy.sdk.anoncreds.Anoncreds.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import edu.self.indy.howto.Utils;
import edu.self.indy.sample.utils.PoolUtils;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

//@SpringBootApplication
public class Endorser {

  public static void main(String[] args) throws Exception {
    LibIndy.setRuntimeConfig("{\"collect_backtrace\": true}");
		Endorser.demo();
  }

  static void demo() throws Exception {

        System.out.println("Endorser sample -> started");
        String trusteeSeed = "000000000000000000000000Trustee1";
        //String trusteeSeed = "100000000000000000000000Trustee1";

        // Set protocol version 2 to work with Indy Node 1.4
        //Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get();

        // 1. Create and Open Pool
        String poolName = PoolUtils.createPoolLedgerConfig();
        Pool pool = Pool.openPoolLedger(poolName, "{}").get();

        // 2. Create and Open Author Wallet
        String authorWalletConfig = new JSONObject().put("id", "authorWallet").toString();
        String authorWalletCredentials = new JSONObject().put("key", "author_wallet_key").toString();
        Wallet.createWallet(authorWalletConfig, authorWalletCredentials).exceptionally((t) -> {
          t.printStackTrace();
          return null;
        }).get();
        Wallet authorWallet = Wallet.openWallet(authorWalletConfig, authorWalletCredentials).get();

        // 3. Create and Open Endorser Wallet
        String endorserWalletConfig = new JSONObject().put("id", "endorserWallet").toString();
        String endorserWalletCredentials = new JSONObject().put("key", "endorser_wallet_key").toString();
        Wallet.createWallet(endorserWalletConfig, endorserWalletCredentials).exceptionally((t) -> {
          t.printStackTrace();
          return null;
        }).get();
        Wallet endorserWallet = Wallet.openWallet(endorserWalletConfig, endorserWalletCredentials).get();

        // 3. Create and Open Trustee Wallet
        String trusteeWalletConfig = new JSONObject().put("id", "trusteeWallet").toString();
        String trusteeWalletCredentials = new JSONObject().put("key", "trustee_wallet_key").toString();
        Wallet.createWallet(trusteeWalletConfig, trusteeWalletCredentials).exceptionally((t) -> {
          t.printStackTrace();
          return null;
        }).get();
        Wallet trusteeWallet = Wallet.openWallet(trusteeWalletConfig, trusteeWalletCredentials).get();

        // 3.5. Create and Open Prover Wallet
        String proverWalletConfig = new JSONObject().put("id", "proverWallet").toString();
        String proverWalletCredentials = new JSONObject().put("key", "prover_wallet_key").toString();
        Wallet.createWallet(proverWalletConfig, proverWalletCredentials).exceptionally((t) -> {
          t.printStackTrace();
          return null;
        }).get();
        Wallet proverWallet = Wallet.openWallet(proverWalletConfig, proverWalletCredentials).get();

        // 4. Create Trustee DID
        DidJSONParameters.CreateAndStoreMyDidJSONParameter theirDidJson =
                new DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, trusteeSeed, null, null);
        CreateAndStoreMyDidResult createTheirDidResult = Did.createAndStoreMyDid(trusteeWallet, theirDidJson.toJson()).get();
        String trusteeDid = createTheirDidResult.getDid();
        String trusteeVerkey = createTheirDidResult.getVerkey();
        System.out.println("Trustee did: " + trusteeDid);
        System.out.println("Trustee verkey: " + trusteeVerkey);

        // 5. Create Author DID
        CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(authorWallet, "{}").get();
        String authorDid = createMyDidResult.getDid();
        String authorVerkey = createMyDidResult.getVerkey();

        // 6. Create Endorser DID
        createMyDidResult = Did.createAndStoreMyDid(endorserWallet, "{}").get();
        String endorserDid = createMyDidResult.getDid();
        String endorserVerkey = createMyDidResult.getVerkey();

        // 6.5. Create Prover DID
        createMyDidResult = Did.createAndStoreMyDid(proverWallet, "{}").get();
        String proverDid = createMyDidResult.getDid();
        String proverVerkey = createMyDidResult.getVerkey();

        // 7. Build Author Nym Request
        String nymRequest = buildNymRequest(trusteeDid, authorDid, authorVerkey, null, null).get();

        // 8. Trustee Sign Author Nym Request
        signAndSubmitRequest(pool, trusteeWallet, trusteeDid, nymRequest).get();

        // 9. Build Endorser Nym Request
        nymRequest = buildNymRequest(trusteeDid, endorserDid, endorserVerkey, null, "ENDORSER").get();

        // 10. Trustee Sign Endorser Nym Request
        signAndSubmitRequest(pool, trusteeWallet, trusteeDid, nymRequest).get();

        // 11. Create schema with endorser

        String schemaName = "gvt";
        String schemaVersion = "1.0";
        String schemaAttributes = new JSONArray().put("name").put("age").put("sex").put("height").toString();
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

        //  Transaction Endorser signs the request
        String schemaRequestSignedByEndorser =
                multiSignRequest(endorserWallet, endorserDid, schemaRequestWithEndorserSignedByAuthor).get();

        //  Transaction Endorser sends the request
        String response = submitRequest(pool, schemaRequestSignedByEndorser).get();
        JSONObject responseJson = new JSONObject(response);
        assertEquals("REPLY", responseJson.getString("op"));
        //System.out.println("responseJson.getJSONObject(\"result\").getJSONObject(\"txnMetadata\") :: " + responseJson.getJSONObject("result").getJSONObject("txnMetadata"));
        assertFalse(responseJson.getJSONObject("result").getJSONObject("txnMetadata").isNull("seqNo"));



        //12. Author create Credential Definition
        String credDefTag = "NameOfTheCred1";
        String credDefConfigJson = new JSONObject().put("support_revocation", false).toString();
        AnoncredsResults.IssuerCreateAndStoreCredentialDefResult createCredDefResult =
            issuerCreateAndStoreCredentialDef(authorWallet, authorDid, schemaJson, credDefTag, null, credDefConfigJson).get();
        String credDefId = createCredDefResult.getCredDefId();
        String credDefJson = createCredDefResult.getCredDefJson();
        JSONObject credDefJsonObject = new JSONObject(credDefJson);
        //System.out.println("credDefJsonObject :: " + credDefJsonObject.get("tag"));
        assertTrue(credDefTag.equals(credDefJsonObject.get("tag")));

        //13. Prover create Master Secret
        // String proverMasterSecretId = proverCreateMasterSecret(proverWallet, null).get();
        String proverMasterSecretId = proverCreateMasterSecret(proverWallet, Utils.PROVER_MASTER_SECRET).exceptionally((t) -> {
          t.printStackTrace();
          return Utils.PROVER_MASTER_SECRET;
        }).get();
        //System.out.println("proverMasterSecretId :: " + proverMasterSecretId);
        //System.out.println("Utils.PROVER_MASTER_SECRET :: " + Utils.PROVER_MASTER_SECRET);

        //14. Issuer Creates Credential Offer
        String credOffer = issuerCreateCredentialOffer(authorWallet, credDefId).get();



        //15. Prover Creates Credential Request
        AnoncredsResults.ProverCreateCredentialRequestResult createCredReqResult =
            proverCreateCredentialReq(proverWallet, proverDid, credOffer, credDefJson, Utils.PROVER_MASTER_SECRET).get();
        String credReqJson = createCredReqResult.getCredentialRequestJson();
        String credReqMetadataJson = createCredReqResult.getCredentialRequestMetadataJson();

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
        String credential = createCredentialResult.getCredentialJson();

        //17. Prover Stores Credential
        proverStoreCredential(proverWallet, null, credReqMetadataJson, credential, credDefJson, null).get();




        //11. Prover Gets Credentials for Proof Request
        String nonce = generateNonce().get();
        String proofRequestJson = new JSONObject()
            .put("nonce", nonce)
            .put("name", "proof_req_1")
            .put("version", "0.2")
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

        CredentialsSearchForProofReq credentialsSearch = CredentialsSearchForProofReq.open(proverWallet, proofRequestJson, null).get();

        JSONArray credentialsForAttribute1 = new JSONArray(credentialsSearch.fetchNextCredentials("attr1_referent", 100).get());
        String credentialIdForAttribute1 = credentialsForAttribute1.getJSONObject(0).getJSONObject("cred_info").getString("referent");

        JSONArray credentialsForAttribute2 = new JSONArray(credentialsSearch.fetchNextCredentials("attr2_referent", 100).get());
        String credentialIdForAttribute2 = credentialsForAttribute2.getJSONObject(0).getJSONObject("cred_info").getString("referent");

        JSONArray credentialsForAttribute3 = new JSONArray(credentialsSearch.fetchNextCredentials("attr3_referent", 100).get());
        assertEquals(0, credentialsForAttribute3.length());

        JSONArray credentialsForPredicate = new JSONArray(credentialsSearch.fetchNextCredentials("predicate1_referent", 100).get());
        String credentialIdForPredicate = credentialsForPredicate.getJSONObject(0).getJSONObject("cred_info").getString("referent");

        credentialsSearch.close();





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
            // System.out.println("****** Utils.PROVER_MASTER_SECRET: " + Utils.PROVER_MASTER_SECRET);
            // System.out.println("****** ");

            proofJson = proverCreateProof(proverWallet, proofRequestJson, requestedCredentialsJson,
              Utils.PROVER_MASTER_SECRET, schemas, credentialDefs, revocStates).get();
            // proofJson = proverCreateProof(proverWallet, proofRequestJson, requestedCredentialsJson,
            //   Utils.PROVER_MASTER_SECRET, schemas, credentialDefs, revocStates).exceptionally((t) -> {
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
            Thread.sleep(5000);
            //System.out.println("");
          }
        } while(proofJson == null && attemptToCreateProof < 3);

        System.out.println("proofJson: " + proofJson);
        JSONObject proof = new JSONObject(proofJson);




        //13. Verifier verify Proof
        JSONObject revealedAttr1 = proof.getJSONObject("requested_proof").getJSONObject("revealed_attrs").getJSONObject("attr1_referent");
        assertEquals("Alex", revealedAttr1.getString("raw"));

        assertNotNull(proof.getJSONObject("requested_proof").getJSONObject("unrevealed_attrs").getJSONObject("attr2_referent").getInt("sub_proof_index"));

        assertEquals(selfAttestedValue, proof.getJSONObject("requested_proof").getJSONObject("self_attested_attrs").getString("attr3_referent"));

        String revocRegDefs = new JSONObject().toString();
        String revocRegs = new JSONObject().toString();

        Boolean valid = verifierVerifyProof(proofRequestJson, proofJson, schemas, credentialDefs, revocRegDefs, revocRegs).get();
        assertTrue(valid);




        pool.closePoolLedger().get();
        //Pool.deletePoolLedgerConfig(poolName).get();

        trusteeWallet.closeWallet().get();
        //Wallet.deleteWallet(trusteeWalletConfig, trusteeWalletCredentials).get();

        authorWallet.closeWallet().get();
        //Wallet.deleteWallet(authorWalletConfig, authorWalletCredentials).get();

        endorserWallet.closeWallet().get();
        //Wallet.deleteWallet(endorserWalletConfig, endorserWalletCredentials).get();

        proverWallet.closeWallet().get();
        //Wallet.deleteWallet(proverWalletConfig, proverWalletCredentials).get();

        System.out.println("Endorser sample -> completed");
  }
}