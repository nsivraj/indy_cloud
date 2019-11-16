package edu.self.indy.indycloud.actionhandler;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Base64.Encoder;

public class GenerateThumbprint extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String data = walletAction.getParameter("data").asText();
    String base64EncodingOption = walletAction.getParameter("base64EncodingOption").asText();
    Encoder base64Encoder = base64EncodingOption.equalsIgnoreCase("NO_WRAP") ? Base64.getEncoder() : Base64.getUrlEncoder();
    MessageDigest hash = MessageDigest.getInstance("SHA-256");
    hash.update(data.getBytes(StandardCharsets.UTF_8));
    byte[] digest = hash.digest();
    String base64EncodedThumbprint = base64Encoder.encodeToString(digest);
    return base64EncodedThumbprint;
  }
}