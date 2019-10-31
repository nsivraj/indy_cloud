package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.utils.UtilsApi;

import edu.self.indy.util.Misc;

public class UpdateMessages extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String messageStatus = walletAction.getParameter("messageStatus").asText();
    String pwdidsJson = walletAction.getParameter("pwdidsJson").asText();
    int result = UtilsApi.vcxUpdateMessages(messageStatus, pwdidsJson).get();
    return String.valueOf(result);
  }
}