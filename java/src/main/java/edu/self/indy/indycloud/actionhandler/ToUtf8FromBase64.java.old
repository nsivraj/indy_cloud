package edu.self.indy.indycloud.actionhandler;

import java.util.Base64;
import java.util.Base64.Decoder;

public class ToUtf8FromBase64 extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String data = walletAction.getParameter("data").asText();
    String base64EncodingOption = walletAction.getParameter("base64EncodingOption").asText();
    Decoder base64Decoder = base64EncodingOption.equalsIgnoreCase("NO_WRAP") ? Base64.getDecoder() : Base64.getUrlDecoder();
    String decodedUtf8 = new String(base64Decoder.decode(data));
    return decodedUtf8;
  }
}