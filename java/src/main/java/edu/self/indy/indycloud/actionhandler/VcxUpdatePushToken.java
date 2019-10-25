package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.utils.UtilsApi;

import edu.self.indy.util.Misc;

public class VcxUpdatePushToken extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String pushTokenConfig = walletAction.getParameter("pushTokenConfig").asText();
    int result = UtilsApi.vcxUpdateAgentInfo(pushTokenConfig).get();
    return String.valueOf(result);
  }
}
