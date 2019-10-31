package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.indy.IndyApi;

import edu.self.indy.util.Misc;

public class GetAcceptanceMechanisms extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String submitterDid = walletAction.getParameter("submitterDid").asText();
    long timestamp = walletAction.getParameter("timestamp").asLong();
    String version = walletAction.getParameter("version").asText();
    String result = IndyApi.getAcceptanceMechanisms(submitterDid, timestamp, version).get();
    return result;
  }
}