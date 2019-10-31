package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.utils.UtilsApi;

import edu.self.indy.util.Misc;

public class VcxGetAgentMessages extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String messageStatus = walletAction.getParameter("messageStatus").asText();
    String uid_s = walletAction.getParameter("uid_s").asText();
    String result = UtilsApi.vcxGetAgentMessages(messageStatus, uid_s).get();
    return result;
  }
}