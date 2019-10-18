package edu.self.indy.indycloud;

import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v1/wallets")
public class WalletResource
{
  // '/wallets/[:id/[action/[:actionName/[params/[:actionParams]]]]]'
  @GET
  @Path("{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  public String getWalletByID(@PathParam("id") int id) {
    String walletId = isWalletIdValid(id);

    if (walletId == null) {
      return null;
    }

    return walletId;
  }

  @POST
  @Path("{id}/action/{actionName}")
  @Consumes("application/json")
  @Produces("application/json")
  public Response operateOnWallet(
    @PathParam("id") int id,
    @PathParam("actionName") String actionName,
    String actionParams) throws URISyntaxException
  {
    try {
      WalletAction wAction = new WalletAction(id, actionName, actionParams);
      System.out.println("WalletAction: "+wAction);
      String walletActionResponse = WalletActionHandler.execute(wAction);
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

  public String isWalletIdValid(int id) {
    // TODO: see if the wallet with id really exists
    return String.valueOf(id);
  }


}
