package edu.self.indy.agency;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/agency")
public class AgencyResource
{
  @POST
  @Path("msg")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response handleMsg(String msg) {
    System.out.println("The message is: " + msg);
    return Response.ok( "hi mom" ).build();
  }
}