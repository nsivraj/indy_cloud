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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import edu.self.indy.indycloud.jpa.Wallet;
import edu.self.indy.indycloud.jpa.WalletRepository;
import edu.self.indy.util.Misc;
import edu.self.indy.util.MiscDB;

@Path("/wallets")
public class WalletResource
{
  @Autowired
  WalletRepository walletRepository;

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
  @Path("createBrandNewWallet")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response createBrandNewWallet(String walletPayload) throws Exception {

    // TODO: add code to restrict the call to createBrandNewWallet to users
    // whose account does not yet have a wallet associated with it yet,
    // if we don't do this then we will get overwhelmed by someone who
    // wants to just create empty wallets!!!! Our site will be hacked!!

    JsonNode walletData = Misc.jsonMapper.readTree(walletPayload);
    boolean secure = walletData.get("secure").asBoolean();

    Wallet wallet = walletRepository.save(new Wallet("tmp_placeholder_name", secure));
    if(secure) {
      // TODO: If they want a secure wallet then additional security requirements will be enforced
      throw new UnsupportedOperationException("A secure wallet is comming soon. Please try again in a few weeks.");
    } else {
      // TODO: add code here for unsecure wallets
    }

    return Response.ok( "{\"action\": \"wallets/createBrandNewWallet\", \"walletId\": " + wallet.getId() + "}" ).build();
  }

}