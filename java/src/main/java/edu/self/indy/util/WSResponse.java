package edu.self.indy.util;

import javax.ws.rs.core.Response;

public class WSResponse {

  public Response.Status status;
  public Object entity;

  public WSResponse(Response.Status status, Object entity) {
    this.status = status;
    this.entity = entity;
  }
}
