package edu.self.indy.indycloud;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.self.indy.util.WSResponse;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hyperledger.indy.sdk.wallet.WalletExistsException;
import org.springframework.beans.factory.annotation.Autowired;

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

import edu.self.indy.indycloud.jpa.JPAWallet;
import edu.self.indy.indycloud.jpa.JPAWalletRepository;
import edu.self.indy.util.Misc;
import edu.self.indy.util.MiscDB;

@Path("/wallets")
public class WalletResource
{
  @Autowired
  JPAWalletRepository walletRepository;

  @GET
  @Path("{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response getWalletByID(@PathParam("walletId") long walletId) {
    if (!MiscDB.isWalletIdValid(walletRepository, walletId)) {
      return Response
        .status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity( "{\"action\": \"wallets/" + walletId + "\", \"error\": \"wallet with id " + walletId + " does not exist!!\"}" )
        .build();
    }
    return Response
        .ok( "{\"action\": \"wallets/" + walletId + "\", \"walletId\": " + walletId + ", \"success\": \"wallet with id " + walletId + " does exist!!\"}" )
        .build();
  }

  @POST
  @Path("signupForWallet")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response signupForWallet(String signupPayload) throws Exception {
    JPAWallet wallet = signupForWallet(signupPayload, false, walletRepository);
    return Response.ok( "{\"action\": \"wallets/signupForWallet\", \"walletId\": " + wallet.getId() + "}" ).build();
  }


  public static final JPAWallet signupForWallet(String signupPayload, boolean forTrustee, JPAWalletRepository walletRepository) throws Exception {

    // TODO: add code to restrict the call to signupForWallet to users
    // whose account does not yet have a wallet associated with it yet,
    // if we don't do this then we will get overwhelmed by someone who
    // wants to just create empty wallets!!!! Our site will be hacked!!


    JsonNode signupData = Misc.jsonMapper.readTree(signupPayload);
    boolean secure = signupData.get("secure").asBoolean();
    JPAWallet wallet = null;

    if(forTrustee) {
      wallet = MiscDB.findTrusteeWallet(walletRepository);
      if(wallet == null) {
        wallet = walletRepository.save(new JPAWallet("tmp_placeholder_name", secure, Role.TRUSTEE.toString()));
      }
    } else {
      wallet = walletRepository.save(new JPAWallet("tmp_placeholder_name", secure));
    }

    if(secure) {
      // TODO: If they want a secure wallet then additional security requirements will be enforced
      throw new UnsupportedOperationException("A secure wallet is comming soon. Please try again in a few weeks.");
    } else {
      // TODO: add code here for unsecure wallets
    }

    return wallet;
  }


  @POST
  @Path("createWallet/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
	public Response createWallet(
    @PathParam("walletId") long walletId,
    String newWalletPayload) throws Exception {

      WSResponse wsResp = createWallet(walletId, newWalletPayload, null, walletRepository);
      return Response.status(wsResp.status).entity(wsResp.entity).build();
	}


  public static final WSResponse createWallet(
    long walletId,
    String newWalletPayload,
    String trusteeSeed,
    JPAWalletRepository walletRepository) throws Exception {

		Wallet newWallet = null;
    //Response resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RuntimeException("wallets :: createWallet")).build();
    WSResponse wsResp = new WSResponse(Response.Status.INTERNAL_SERVER_ERROR, new RuntimeException("wallets :: createWallet"));
    JPAWallet jpaWallet = MiscDB.findWalletById(walletRepository, walletId);
    JsonNode newWalletData = Misc.jsonMapper.readTree(newWalletPayload);
    String walletConfig = newWalletData.get("walletConfig").toString();
    String walletCred = newWalletData.get("walletCredential").toString();

    try {
			JsonNode walletConfigData = Misc.jsonMapper.readTree(walletConfig);
      String storageConfigPath = Misc.getStorageConfigPath(walletId);
      ((ObjectNode)walletConfigData).set("storage_config", Misc.jsonMapper.readTree("{\"path\": \"" + storageConfigPath + "\"}"));
      walletConfig = walletConfigData.toString();

			// 3. Create and Open new Wallet
			Wallet.createWallet(walletConfig, walletCred).get();
			newWallet = Wallet.openWallet(walletConfig, walletCred).get();

      String walletDid = null;
      String walletVerkey = null;
      String createWalletResponseJSON = null;
      if(trusteeSeed == null) {
        // 5. Create new DID
        CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(newWallet, "{}").get();
        walletDid = createMyDidResult.getDid();
        walletVerkey = createMyDidResult.getVerkey();
        System.out.println("New Wallet did: " + walletDid + " with wallet id: " + walletId);
        System.out.println("New Wallet verkey: " + walletVerkey);
        System.out.println("The walletConfig is: " + walletConfig);
        createWalletResponseJSON = "{\"action\": \"wallets/createWallet\", \"walletDid\": \"" + walletDid + "\", \"walletVerkey\": \"" + walletVerkey + "\"}";
      } else {
        // 4. Create Trustee DID
        DidJSONParameters.CreateAndStoreMyDidJSONParameter trusteeDidJson =
                new DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, trusteeSeed, null, null);
        CreateAndStoreMyDidResult createTheirDidResult = Did.createAndStoreMyDid(newWallet, trusteeDidJson.toJson()).get();
        walletDid = createTheirDidResult.getDid();
        walletVerkey = createTheirDidResult.getVerkey();
        System.out.println("Trustee did: " + walletDid + " with wallet id: " + walletId);
        System.out.println("Trustee verkey: " + walletVerkey);
        System.out.println("The walletConfig is: " + walletConfig);
        createWalletResponseJSON = "{\"action\": \"wallets/createWallet\", \"walletDid\": \"" + walletDid + "\"}";
      }

      jpaWallet.walletDID = walletDid;
      jpaWallet = walletRepository.save(jpaWallet);

			//resp = Response.ok( createWalletResponseJSON ).build();
      wsResp.status = Response.Status.OK;
      wsResp.entity = createWalletResponseJSON;

		} catch(Exception ex) {
			if(ex.getCause() != null && WalletExistsException.class.equals(ex.getCause().getClass())) {
        wsResp.status = Response.Status.OK;
        newWallet = Wallet.openWallet(walletConfig, walletCred).get();
        String walletVerkey = Did.keyForLocalDid(newWallet, jpaWallet.getWalletDID()).get();
        wsResp.entity = "{\"action\": \"wallets/createWallet\", \"walletDid\": \"" + jpaWallet.getWalletDID() + "\", \"walletVerkey\": \"" + walletVerkey + "\"}";
      } else {
        ex.printStackTrace();
        //resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
        wsResp.status = Response.Status.INTERNAL_SERVER_ERROR;
        wsResp.entity = ex;
      }
    } finally {
      if(newWallet != null) {
        newWallet.closeWallet().get();
        //Wallet.deleteWallet(walletConfig, walletCred).get();
      }
    }

    //return resp;
    return wsResp;
  }


}