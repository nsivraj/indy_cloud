package edu.self.indy.indycloud.actionhandler;

import java.util.Base64;

import com.evernym.sdk.vcx.connection.ConnectionApi;

public class ConnectionVerifySignature extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int connectionHandle = walletAction.getParameter("connectionHandle").asInt();
    String data = walletAction.getParameter("data").asText();
    String signature = walletAction.getParameter("signature").asText();
    byte[] dataToVerify = data.getBytes();
    byte[] signatureToVerify = Base64.getDecoder().decode(signature);
    boolean result = ConnectionApi.connectionVerifySignature(
            connectionHandle, dataToVerify, dataToVerify.length, signatureToVerify, signatureToVerify.length
    ).get();
    return String.valueOf(result);
  }
}