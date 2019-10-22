package edu.self.indy.indycloud;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

import org.springframework.beans.factory.annotation.Autowired;

import edu.self.indy.indycloud.jpa.Wallet;
import edu.self.indy.indycloud.jpa.WalletRepository;

@Path("/api/v1/wallets")
public class WalletResource
{
  @Autowired
  WalletRepository walletRepository;

  @Autowired
  WalletActionHandler walletActionHandler;

  @GET
  @Path("{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  public String getWalletByID(@PathParam("id") long id) {
    if (!isWalletIdValid(id)) {
      return null;
    }

    return String.valueOf(id);
  }

  @GET
  @Path("{id}/downloadExport")
  public Response downloadExport(@PathParam("id") long id) {
    Wallet wallet = findWalletById(id);
    if (wallet == null) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("The wallet id '" + id + "' is not a correct wallet id!!!").build();
    }

    System.out.println("downloadExport file: " + wallet.getExportPath());
    StreamingOutput downloadStream =  new StreamingOutput()
    {
        @Override
        public void write(java.io.OutputStream output) throws IOException, WebApplicationException
        {
            try
            {
              java.nio.file.Path path = Paths.get(wallet.getExportPath());
              byte[] data = Files.readAllBytes(path);
              output.write(data);
              output.flush();
            }
            catch (Exception e)
            {
                throw new WebApplicationException("File Not Found !!");
            }
        }
    };

    return Response
        .ok(downloadStream, MediaType.APPLICATION_OCTET_STREAM)
        .header("content-disposition","attachment; filename = " + wallet.getExportPath())
        .build();
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
    @PathParam("id") long id,
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

  public Wallet findWalletById(long id) {
    Wallet wallet = walletRepository.findById(id);
    return wallet;
  }

  public boolean isWalletIdValid(long id) {
    Wallet wallet = findWalletById(id);
    return wallet == null ? false : true;
  }


}
