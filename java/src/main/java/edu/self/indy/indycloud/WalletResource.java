package edu.self.indy.indycloud;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import edu.self.indy.indycloud.jpa.Wallet;
import edu.self.indy.indycloud.jpa.WalletRepository;

@Path("/api/v1/wallets")
public class WalletResource
{
  @Autowired
  WalletActionHandler walletActionHandler;

  @GET
  @Path("{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  public String getWalletByID(@PathParam("id") int id) {
    if (!isWalletIdValid(id)) {
      return null;
    }

    return String.valueOf(id);
  }

  @POST
  @Path("createBrandNewWallet")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response createBrandNewWallet(String actionParams) {
    return operateOnWallet(-1, "createBrandNewWallet", actionParams);
  }

  @POST
  @Path("{id}/action/{actionName}")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces({ MediaType.APPLICATION_JSON })
  public Response operateOnWallet(
    @PathParam("id") int id,
    @PathParam("actionName") String actionName,
    String actionParams)
  {
    try {
      if(!"createBrandNewWallet".equalsIgnoreCase(actionName) && !isWalletIdValid(id)) {
        throw new RuntimeException("Incorrect wallet id: " + id + ". You must get a valid wallet id!!!!");
      }
      WalletAction wAction = new WalletAction(id, actionName, actionParams);
      System.out.println("WalletAction: "+wAction);
      String walletActionResponse = walletActionHandler.execute(wAction);
      String cloudResponse = "{\"cloudResponse\": " + walletActionResponse + "}";
      System.out.println("cloudResponse: "+cloudResponse);
      return Response.ok( cloudResponse ).build();
    } catch(Exception ex) {
      ex.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
    }
  }

  public Wallet findWalletById(int id) {
    // TODO: find the wallet with id
    Wallet wallet = null;
    return wallet;
  }

  public boolean isWalletIdValid(int id) {
    // TODO: see if the wallet with id really exists
    return id != -1;
  }


}
