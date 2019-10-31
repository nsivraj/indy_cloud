package edu.self.indy.indycloud.actionhandler;

import java.util.Base64;
import java.util.Base64.Encoder;

public class ToBase64FromUtf8 extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String data = walletAction.getParameter("data").asText();
    String base64EncodingOption = walletAction.getParameter("base64EncodingOption").asText();
    Encoder base64Encoder = base64EncodingOption.equalsIgnoreCase("NO_WRAP") ? Base64.getEncoder() : Base64.getUrlEncoder();
    return base64Encoder.encodeToString(data.getBytes());
  }
}