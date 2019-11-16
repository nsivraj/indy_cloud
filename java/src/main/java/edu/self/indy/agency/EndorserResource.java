package edu.self.indy.agency;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import static org.junit.Assert.*;

import edu.self.indy.howto.Utils;
import edu.self.indy.util.Misc;

@Path("/endorser")
public class EndorserResource {

  @GET
  @Path("createWallet/{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
	public Response createEndorserWallet(@PathParam("id") long id) throws Exception {

		// 1.
		// System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
		// Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
		// Pool.createPoolLedgerConfig(Utils.ENDORSER_POOL_NAME, Utils.SERVERONE_POOL_CONFIG).exceptionally((t) -> {
		// 		t.printStackTrace();
		// 		return null;
		// }).get();

		// 2
		// System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
		// Pool pool = Pool.openPoolLedger(Utils.ENDORSER_POOL_NAME, "{}").get();

    // 3. Create and Open Endorser Wallet
		Wallet.createWallet(Utils.ENDORSER_WALLET_CONFIG, Utils.ENDORSER_WALLET_CREDENTIALS).exceptionally((t) -> {
			t.printStackTrace();
			return null;
		}).get();
		Wallet endorserWallet = Wallet.openWallet(Utils.ENDORSER_WALLET_CONFIG, Utils.ENDORSER_WALLET_CREDENTIALS).get();

		// 5. Create Endorser DID
		CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(endorserWallet, "{}").get();
		String endorserDid = createMyDidResult.getDid();
		String endorserVerkey = createMyDidResult.getVerkey();
    System.out.println("Endorser did: " + endorserDid);
    System.out.println("Endorser verkey: " + endorserVerkey);

    //pool.closePoolLedger().get();
    //Pool.deletePoolLedgerConfig(Utils.ENDORSER_POOL_NAME).get();

    endorserWallet.closeWallet().get();
    //Wallet.deleteWallet(Utils.ENDORSER_WALLET_CONFIG, Utils.ENDORSER_WALLET_CREDENTIALS).get();

    return Response.ok( "{\"action\": \"endorser/createWallet\", \"endorserDid\": \"" + endorserDid + "\", \"endorserVerkey\": \"" + endorserVerkey + "\"}" ).build();
  }

  @POST
  @Path("signAndSubmitRequest/{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response signAndSubmitRequest(
    @PathParam("id") long id,
    String requestPayload) throws Exception {

		JsonNode requestData = Misc.jsonMapper.readTree(requestPayload);
		String requestSignedByAuthor = requestData.get("requestSignedByAuthor").toString();
		String endorserDid = requestData.get("endorserDid").asText();

    // 1.
    System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
    Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
    Pool.createPoolLedgerConfig(Utils.ENDORSER_POOL_NAME, Utils.SERVERONE_POOL_CONFIG).exceptionally((t) -> {
    		t.printStackTrace();
    		return null;
    }).get();

    // 2
    System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
    Pool pool = Pool.openPoolLedger(Utils.ENDORSER_POOL_NAME, "{}").get();

    Wallet endorserWallet = Wallet.openWallet(Utils.ENDORSER_WALLET_CONFIG, Utils.ENDORSER_WALLET_CREDENTIALS).get();

		// 5. Create Endorser DID
		// CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(endorserWallet, "{}").get();
		// String endorserDid = createMyDidResult.getDid();
		// String endorserVerkey = createMyDidResult.getVerkey();

    //  Transaction Endorser signs the request
    String requestSignedByEndorser =
            multiSignRequest(endorserWallet, endorserDid, requestSignedByAuthor).get();

    //  Transaction Endorser sends the request
    String response = submitRequest(pool, requestSignedByEndorser).get();
    System.out.println("signAndSubmitRequest response: " + response);
    JSONObject responseJson = new JSONObject(response);
    assertEquals("REPLY", responseJson.getString("op"));

    //System.out.println("responseJson.getJSONObject(\"result\").getJSONObject(\"txnMetadata\") :: " + responseJson.getJSONObject("result").getJSONObject("txnMetadata"));
    assertFalse(responseJson.getJSONObject("result").getJSONObject("txnMetadata").isNull("seqNo"));


    pool.closePoolLedger().get();
    Pool.deletePoolLedgerConfig(Utils.ENDORSER_POOL_NAME).get();

    endorserWallet.closeWallet().get();
    //Wallet.deleteWallet(Utils.ENDORSER_WALLET_CONFIG, Utils.ENDORSER_WALLET_CREDENTIALS).get();

    return Response.ok( "{\"action\": \"endorser/signAndSubmitRequest\"}" ).build();
  }

}