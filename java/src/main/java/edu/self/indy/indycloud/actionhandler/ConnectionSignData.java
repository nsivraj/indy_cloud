package edu.self.indy.indycloud.actionhandler;

import java.util.Base64;
import java.util.Base64.Encoder;

import com.evernym.sdk.vcx.connection.ConnectionApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.self.indy.util.Misc;

public class ConnectionSignData extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int connectionHandle = walletAction.getParameter("connectionHandle").asInt();
    String data = walletAction.getParameter("data").asText();
    String base64EncodingOption = walletAction.getParameter("base64EncodingOption").asText();
    boolean encodeBeforeSigning = walletAction.getParameter("encodeBeforeSigning").asBoolean();

    Encoder base64Encoder = base64EncodingOption.equalsIgnoreCase("NO_WRAP") ? Base64.getEncoder() : Base64.getUrlEncoder();
    byte[] dataToSign = encodeBeforeSigning ? base64Encoder.encode(data.getBytes()) : data.getBytes();
    byte[] signedData = ConnectionApi.connectionSignData(connectionHandle, dataToSign, dataToSign.length).get();
    if (signedData != null) {
      ObjectNode signResponse = Misc.jsonMapper.createObjectNode();
      signResponse.set("data",  Misc.jsonMapper.convertValue(new String(dataToSign), JsonNode.class));
      signResponse.set("signature", Misc.jsonMapper.convertValue(Base64.getEncoder().encodeToString(signedData), JsonNode.class));
      return signResponse.toString();
    } else {
      throw new RuntimeException("NULL-VALUE: Null value was received as result from libvcx");
    }
  }
}