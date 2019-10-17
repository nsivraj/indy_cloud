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
  @Path("{id}/action/{actionName}/params/{actionParams}")
  @Consumes("application/json")
  @Produces("application/json")
  public Response operateOnWallet(
    @PathParam("id") int id,
    @PathParam("actionName") String actionName,
    @PathParam("actionParams") String actionParams) throws URISyntaxException
  {
    // final Wallet wallet = await findWalletById(id);
    // if (wallet == null) {
    //   return Response.notFound();
    // }

    return Response.status(200).entity("{\"id\": "+id+"}").build();
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


// [INFO] aqueduct: POST /wallets/-1/action/setVcxLogger/params/%7B%22logLevel%22:%22debug%22,%22uniqueId%22:%2270c385f1-cbbc-2f12-8cb5-8df71ef3799b%22,%22MAX_ALLOWED_FILE_BYTES%22:10000000%7D 76ms 200
// [INFO] aqueduct: POST /wallets/-1/action/createWalletKey/params/%7B%22lengthOfKey%22:64%7D 93ms 200
// [INFO] aqueduct: POST /wallets/-1/action/shutdownVcx/params/%7B%22deletePool%22:false%7D 1ms 200
