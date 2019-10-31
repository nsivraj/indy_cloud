package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.indy.IndyApi;

import edu.self.indy.util.Misc;

public class AppendTxnAuthorAgreement extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String requestJson = walletAction.getParameter("requestJson").asText();
    String text = walletAction.getParameter("text").asText();
    String version = walletAction.getParameter("version").asText();
    String taaDigest = walletAction.getParameter("taaDigest").asText();
    String mechanism = walletAction.getParameter("mechanism").asText();
    long timestamp = walletAction.getParameter("timestamp").asLong();
    String result = IndyApi.appendTxnAuthorAgreement(requestJson, text, version, taaDigest, mechanism, timestamp).get();
    return result;
  }
}