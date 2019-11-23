package edu.self.indy.indycloud;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.self.indy.util.Utils;
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;

import static org.hyperledger.indy.sdk.anoncreds.Anoncreds.*;
import static org.hyperledger.indy.sdk.ledger.Ledger.*;

import org.springframework.beans.factory.annotation.Autowired;

import edu.self.indy.indycloud.jpa.JPAWalletRepository;
import edu.self.indy.util.Misc;

@Path("/endorser")
public class EndorserResource {

  @Autowired
  JPAWalletRepository walletRepository;

  // @GET
  // @Path("createWallet/{walletId}")
  // @Produces({ MediaType.APPLICATION_JSON })
  // @Consumes({ MediaType.APPLICATION_JSON })
	// public Response createEndorserWallet(@PathParam("walletId") long walletId) throws Exception {

  //   Wallet endorserWallet = null;
  //   Response resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RuntimeException("endorser :: createWallet")).build();

  //   try {

  //     JsonNode walletConfigData = Misc.jsonMapper.readTree(Utils.ENDORSER_WALLET_CONFIG);
  //     String storageConfigPath = Misc.getStorageConfigPath(walletId);
  //     ((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
  //     String walletConfig = walletConfigData.toString();
  //     System.out.println("The walletConfig is: " + walletConfig);

  //     // 3. Create and Open Endorser Wallet
  //     Wallet.createWallet(walletConfig, Utils.ENDORSER_WALLET_CREDENTIALS).get();
  //     endorserWallet = Wallet.openWallet(walletConfig, Utils.ENDORSER_WALLET_CREDENTIALS).get();

  //     // 5. Create Endorser DID
  //     CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(endorserWallet, "{}").get();
  //     String endorserDid = createMyDidResult.getDid();
  //     String endorserVerkey = createMyDidResult.getVerkey();
  //     System.out.println("Endorser did: " + endorserDid);
  //     System.out.println("Endorser verkey: " + endorserVerkey);

  //     edu.self.indy.indycloud.jpa.Wallet dbWallet = MiscDB.findWalletById(walletRepository, walletId);
  //     dbWallet.walletDID = endorserDid;
  //     dbWallet = walletRepository.save(dbWallet);

  //     resp = Response.ok( "{\"action\": \"endorser/createWallet\", \"endorserDid\": \"" + endorserDid + "\", \"endorserVerkey\": \"" + endorserVerkey + "\"}" ).build();

  //   } catch(Exception ex) {
  //     ex.printStackTrace();
  //     resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
  //   } finally {
  //     if(endorserWallet != null) {
  //       endorserWallet.closeWallet().get();
  //       //Wallet.deleteWallet(walletConfig, Utils.ENDORSER_WALLET_CREDENTIALS).get();
  //     }
  //   }

  //   return resp;
  // }


  @POST
  @Path("createSchema/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
	public Response createSchema(
    @PathParam("walletId") long walletId,
    String schemaPayload) throws Exception {

    Wallet endorserWallet = null;
    Pool pool = null;
    Response resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RuntimeException("endorser :: createSchema")).build();

    try {

      JsonNode schemaData = Misc.jsonMapper.readTree(schemaPayload);
      String endorserDid = schemaData.get("endorserDid").asText();
      //String authorDid = schemaData.get("authorDid").asText();
      String schemaName = schemaData.get("schemaName").asText();
      String schemaVersion = schemaData.get("schemaVersion").asText();
      String schemaAttributes = schemaData.get("schemaAttributes").toString();

      JsonNode walletConfigData = Misc.jsonMapper.readTree(Utils.ENDORSER_WALLET_CONFIG);
      String storageConfigPath = Misc.getStorageConfigPath(walletId);
      ((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
      String walletConfig = walletConfigData.toString();
      System.out.println("The walletConfig is: " + walletConfig);

      // 1.
      System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
      Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
      Pool.createPoolLedgerConfig(Utils.ENDORSER_POOL_NAME, Utils.SERVERONE_POOL_CONFIG).get();

      System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
      pool = Pool.openPoolLedger(Utils.ENDORSER_POOL_NAME, "{}").get();

      endorserWallet = Wallet.openWallet(walletConfig, Utils.ENDORSER_WALLET_CREDENTIALS).get();
      // CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(endorserWallet, "{}").get();
      // String endorserDid = createMyDidResult.getDid();

      //String schemaName = "gvt";
      //String schemaVersion = "1.0";
      //String schemaAttributes = new JSONArray().put("name").put("age").put("sex").put("height").toString();
      AnoncredsResults.IssuerCreateSchemaResult createSchemaResult =
              issuerCreateSchema(endorserDid, schemaName, schemaVersion, schemaAttributes).get();
      String schemaId = createSchemaResult.getSchemaId();
      String schemaJson = createSchemaResult.getSchemaJson();

      //  Transaction Endorser builds Schema Request
      String schemaRequest = buildSchemaRequest(endorserDid, schemaJson).get();

      // 10
      System.out.println("\n10. Sending the SCHEMA request to the ledger\n");
      String schemaResponse = signAndSubmitRequest(pool, endorserWallet, endorserDid, schemaRequest).get();
      System.out.println("Schema response:\n" + schemaResponse);

      resp = Response.ok( "{\"action\": \"endorser/createSchema\", \"schemaJson\": " + schemaJson + ", \"schemaId\": \"" + schemaId + "\"}" ).build();
    } catch(Exception ex) {
      ex.printStackTrace();
      resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
    } finally {
      if(endorserWallet != null) {
        endorserWallet.closeWallet().get();
        //Wallet.deleteWallet(walletConfig, Utils.ENDORSER_WALLET_CREDENTIALS).get();
      }
      if(pool != null) {
        pool.closePoolLedger().get();
        Pool.deletePoolLedgerConfig(Utils.ENDORSER_POOL_NAME).get();
      }
    }

    return resp;

  }


  // @POST
  // @Path("signAndSubmitRequest/{walletId}")
  // @Produces({ MediaType.APPLICATION_JSON })
  // @Consumes({ MediaType.APPLICATION_JSON })
  // public Response signAndSubmitRequest(
  //   @PathParam("walletId") long walletId,
  //   String requestPayload) throws Exception {

	// 	JsonNode requestData = Misc.jsonMapper.readTree(requestPayload);
	// 	String requestSignedByAuthor = requestData.get("requestSignedByAuthor").toString();
	// 	String endorserDid = requestData.get("endorserDid").asText();

  //   Wallet endorserWallet = null;
  //   Pool pool = null;
  //   Response resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RuntimeException("endorser :: signAndSubmitRequest")).build();

  //   try {
  //     JsonNode walletConfigData = Misc.jsonMapper.readTree(Utils.ENDORSER_WALLET_CONFIG);
  //     String storageConfigPath = Misc.getStorageConfigPath(walletId);
  //     ((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
  //     String walletConfig = walletConfigData.toString();
  //     System.out.println("The walletConfig is: " + walletConfig);

  //     // 1.
  //     System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
  //     Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
  //     Pool.createPoolLedgerConfig(Utils.ENDORSER_POOL_NAME, Utils.SERVERONE_POOL_CONFIG).get();

  //     // 2
  //     System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
  //     pool = Pool.openPoolLedger(Utils.ENDORSER_POOL_NAME, "{}").get();

  //     endorserWallet = Wallet.openWallet(walletConfig, Utils.ENDORSER_WALLET_CREDENTIALS).get();

  //     // 5. Create Endorser DID
  //     // CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(endorserWallet, "{}").get();
  //     // String endorserDid = createMyDidResult.getDid();
  //     // String endorserVerkey = createMyDidResult.getVerkey();

  //     //  Transaction Endorser signs the request
  //     String requestSignedByEndorser =
  //             multiSignRequest(endorserWallet, endorserDid, requestSignedByAuthor).get();

  //     //  Transaction Endorser sends the request
  //     String response = submitRequest(pool, requestSignedByEndorser).get();
  //     System.out.println("signAndSubmitRequest response: " + response);
  //     JSONObject responseJson = new JSONObject(response);
  //     //assertEquals("REPLY", responseJson.getString("op"));

  //     //System.out.println("responseJson.getJSONObject(\"result\").getJSONObject(\"txnMetadata\") :: " + responseJson.getJSONObject("result").getJSONObject("txnMetadata"));
  //     //assertFalse(responseJson.getJSONObject("result").getJSONObject("txnMetadata").isNull("seqNo"));
  //     if("REPLY".equals(responseJson.getString("op"))) {
  //       resp = Response.ok( "{\"action\": \"endorser/signAndSubmitRequest\"}" ).build();
  //     } else {
  //       resp = Response.ok( "{\"action\": \"endorser/signAndSubmitRequest\", \"result\": " + responseJson + "}" ).build();
  //     }
  //   } catch(Exception ex) {
	// 		ex.printStackTrace();
  //     resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
  //   } finally {
  //     if(endorserWallet != null) {
  //       endorserWallet.closeWallet().get();
  //       //Wallet.deleteWallet(Utils.PROVER_WALLET_CONFIG, Utils.PROVER_WALLET_CREDENTIALS).get();
  //     }
  //     if(pool != null) {
  //       pool.closePoolLedger().get();
  //       Pool.deletePoolLedgerConfig(Utils.ENDORSER_POOL_NAME).get();
  //     }
  //   }

  //   return resp;
  // }

}