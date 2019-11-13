package edu.self.indy.indycloud;

import java.io.IOException;
import java.io.InputStream;
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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import edu.self.indy.indycloud.jpa.Wallet;
import edu.self.indy.indycloud.jpa.WalletRepository;
import edu.self.indy.util.Misc;
@Path("/v1/wallets")
public class WalletResource
{
  @Autowired
  WalletRepository walletRepository;

  @GET
  @Path("{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  public Response getWalletByID(@PathParam("id") long id) {
    if (!isWalletIdValid(id)) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity( "" ).build();
    }

    return Response.ok( id ).build();
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
  @Path("{id}/getColor")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response uploadImage(
    @PathParam("id") long id,
    @FormDataParam("get-color-image") InputStream uploadedInputStream,
    @FormDataParam("get-color-image") FormDataContentDisposition fileDetails) {

    String uploadedFileLocation = Misc.getColorImagePath(id, fileDetails.getFileName());
    Misc.writeToFile(uploadedInputStream, uploadedFileLocation);
    System.out.println("get-color-images uploaded to : " + uploadedFileLocation);

    try {
      String imageColor = Misc.getColor(uploadedFileLocation);
      return getCloudResponse(Misc.addQuotes(imageColor), "getColor");
    } catch(Exception ex) {
      ex.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
    }
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
      if("writeToVcxLog".equalsIgnoreCase(actionName)) {
        System.out.println("WalletAction: " + wAction.toStringWithoutActionParams());
      } else {
        System.out.println("WalletAction: " + wAction);
      }
      String walletActionResponse = wAction.getActionHandler(walletRepository).execute();
      return getCloudResponse(walletActionResponse, actionName);
    } catch(Exception ex) {
      ex.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
    }
  }

  public Response getCloudResponse(String cloudResp, String actionName) {
    String cloudResponse = "{\"cloudResponse\": " + cloudResp + ",\"action\": \"" + actionName + "\"}";
    System.out.println("cloudResponse: "+cloudResponse);
    return Response.ok( cloudResponse ).build();
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
