package edu.self.indy.indycloud;

import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults;
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq;
import org.hyperledger.indy.sdk.did.DidResults.CreateAndStoreMyDidResult;
import org.hyperledger.indy.sdk.did.DidJSONParameters;
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

@Path("/trustee")
public class TrusteeResource {

	@Autowired
  WalletRepository walletRepository;

  private static String trusteeSeed = "000000000000000000000000Trustee1";

  @GET
  @Path("createWallet/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response createTrusteeWallet(@PathParam("walletId") long walletId) throws Exception {

    Wallet trusteeWallet = null;
    Response resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RuntimeException("trustee :: createWallet")).build();

    try {

      JsonNode walletConfigData = Misc.jsonMapper.readTree(Utils.TRUSTEE_WALLET_CONFIG);
      String storageConfigPath = Misc.getStorageConfigPath(walletId);
      ((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
      String walletConfig = walletConfigData.toString();
      System.out.println("The walletConfig is: " + walletConfig);

      // 3. Create and Open Trustee Wallet
      Wallet.createWallet(walletConfig, Utils.TRUSTEE_WALLET_CREDENTIALS).get();
      trusteeWallet = Wallet.openWallet(walletConfig, Utils.TRUSTEE_WALLET_CREDENTIALS).get();

      // 4. Create Trustee DID
      DidJSONParameters.CreateAndStoreMyDidJSONParameter theirDidJson =
              new DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, trusteeSeed, null, null);
      CreateAndStoreMyDidResult createTheirDidResult = Did.createAndStoreMyDid(trusteeWallet, theirDidJson.toJson()).get();
      String trusteeDid = createTheirDidResult.getDid();
      String trusteeVerkey = createTheirDidResult.getVerkey();
      System.out.println("Trustee did: " + trusteeDid);
      System.out.println("Trustee verkey: " + trusteeVerkey);

      edu.self.indy.indycloud.jpa.Wallet dbWallet = MiscDB.findWalletById(walletRepository, walletId);
      dbWallet.walletDID = trusteeDid;
      dbWallet = walletRepository.save(dbWallet);

      resp = Response.ok( "{\"action\": \"trustee/createWallet\", \"trusteeDid\": \"" + trusteeDid + "\"}" ).build();

    } catch(Exception ex) {
      ex.printStackTrace();
      resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
    } finally {
      if(trusteeWallet != null) {
        trusteeWallet.closeWallet().get();
        //Wallet.deleteWallet(walletConfig, Utils.TRUSTEE_WALLET_CREDENTIALS).get();
      }
    }

    return resp;
  }

  @POST
  @Path("addAuthor/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response addAuthorToLedger(
    @PathParam("walletId") long walletId,
    String authorPayload) throws Exception {

    Wallet trusteeWallet = null;
    Pool pool = null;
    Response resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RuntimeException("trustee :: addAuthor")).build();

    try {

      JsonNode authorData = Misc.jsonMapper.readTree(authorPayload);
      String trusteeDid = authorData.get("trusteeDid").asText();
      String authorDid = authorData.get("authorDid").asText();
      String authorVerkey = authorData.get("authorVerkey").asText();

      // 2
      System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
      Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
      Pool.createPoolLedgerConfig(Utils.TRUSTEE_POOL_NAME, Utils.SERVERONE_POOL_CONFIG).get();

      System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
      pool = Pool.openPoolLedger(Utils.TRUSTEE_POOL_NAME, "{}").get();

      JsonNode walletConfigData = Misc.jsonMapper.readTree(Utils.TRUSTEE_WALLET_CONFIG);
      String storageConfigPath = Misc.getStorageConfigPath(walletId);
      ((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
      String walletConfig = walletConfigData.toString();
      System.out.println("The walletConfig is: " + walletConfig);

      // 3. Open Trustee Wallet
      trusteeWallet = Wallet.openWallet(walletConfig, Utils.TRUSTEE_WALLET_CREDENTIALS).get();

      // 7. Build Author Nym Request
      String nymRequest = buildNymRequest(trusteeDid, authorDid, authorVerkey, null, null).get();

      // 8. Trustee Sign Author Nym Request
      String nymResult = signAndSubmitRequest(pool, trusteeWallet, trusteeDid, nymRequest).get();
      System.out.println("addAuthor result: " + nymResult);
      JSONObject nymResultJson = new JSONObject(nymResult);
      assertEquals("REPLY", nymResultJson.getString("op"));

      resp = Response.ok( "{\"action\": \"trustee/addauthor\"}" ).build();
    } catch(Exception ex) {
      ex.printStackTrace();
      resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
    } finally {
      if(trusteeWallet != null) {
        trusteeWallet.closeWallet().get();
        //Wallet.deleteWallet(walletConfig, Utils.TRUSTEE_WALLET_CREDENTIALS).get();
      }
      if(pool != null) {
        pool.closePoolLedger().get();
        Pool.deletePoolLedgerConfig(Utils.TRUSTEE_POOL_NAME).get();
      }
    }

    return resp;

  }


  @POST
  @Path("addEndorser/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response addEndorserToLedger(
    @PathParam("walletId") long walletId,
    String endorserPayload) throws Exception {

    Wallet trusteeWallet = null;
    Pool pool = null;
    Response resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RuntimeException("trustee :: addAuthor")).build();

    try {

      JsonNode endorserData = Misc.jsonMapper.readTree(endorserPayload);
      String trusteeDid = endorserData.get("trusteeDid").asText();
      String endorserDid = endorserData.get("endorserDid").asText();
      String endorserVerkey = endorserData.get("endorserVerkey").asText();

      // 2
      System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
      Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
      Pool.createPoolLedgerConfig(Utils.TRUSTEE_POOL_NAME, Utils.SERVERONE_POOL_CONFIG).get();

      System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
      pool = Pool.openPoolLedger(Utils.TRUSTEE_POOL_NAME, "{}").get();

      JsonNode walletConfigData = Misc.jsonMapper.readTree(Utils.TRUSTEE_WALLET_CONFIG);
      String storageConfigPath = Misc.getStorageConfigPath(walletId);
      ((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
      String walletConfig = walletConfigData.toString();
      System.out.println("The walletConfig is: " + walletConfig);

      // 3. Open Trustee Wallet
      trusteeWallet = Wallet.openWallet(walletConfig, Utils.TRUSTEE_WALLET_CREDENTIALS).get();

      // 7. Build Author Nym Request
      String nymRequest = buildNymRequest(trusteeDid, endorserDid, endorserVerkey, null, "ENDORSER").get();

      // 8. Trustee Sign Author Nym Request
      String nymResult = signAndSubmitRequest(pool, trusteeWallet, trusteeDid, nymRequest).get();
      System.out.println("addEndorser result: " + nymResult);
      JSONObject nymResultJson = new JSONObject(nymResult);
      assertEquals("REPLY", nymResultJson.getString("op"));

      resp = Response.ok( "{\"action\": \"trustee/addendorser\"}" ).build();

    } catch(Exception ex) {
      ex.printStackTrace();
      resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
    } finally {
      if(trusteeWallet != null) {
        trusteeWallet.closeWallet().get();
        //Wallet.deleteWallet(walletConfig, Utils.TRUSTEE_WALLET_CREDENTIALS).get();
      }
      if(pool != null) {
        pool.closePoolLedger().get();
        Pool.deletePoolLedgerConfig(Utils.TRUSTEE_POOL_NAME).get();
      }
    }

    return resp;

  }
}