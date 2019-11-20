package edu.self.indy.indycloud;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import edu.self.indy.indycloud.jpa.JPAWalletRepository;
import edu.self.indy.util.MiscDB;

@Provider
public class WalletFilter implements ContainerRequestFilter {

  @Autowired
  JPAWalletRepository walletRepository;

  @Override
  public void filter(ContainerRequestContext ctx) throws IOException {
    String uriPath = ctx.getUriInfo().getRequestUri().getPath();
    if(uriPath.contains("signupForWallet")) {
      return;
    }

    MultivaluedMap<String, String> pathparam = ctx.getUriInfo().getPathParameters();
    Long walletId = null;
    try {
      walletId = new Long(pathparam.getFirst("walletId"));
    } catch(Exception ex) {
      ex.printStackTrace();
    }

    if (walletId == null || !MiscDB.isWalletIdValid(walletRepository, walletId)) {
      ctx.abortWith( Response
        .status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity( "{\"action\": \"" + uriPath + "\", \"error\": \"wallet with id " + walletId + " does not exist!!\"}" )
        .build());
    }
  }
}