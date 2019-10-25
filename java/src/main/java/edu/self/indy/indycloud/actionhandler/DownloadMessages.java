package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.utils.UtilsApi;

import edu.self.indy.util.Misc;

public class DownloadMessages extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String messageStatus = walletAction.getParameter("messageStatus").asText();
    String uid_s = walletAction.getParameter("uid_s").asText();
    uid_s = Misc.returnNullForEmptyOrNull(uid_s);
    String pwdids = walletAction.getParameter("pwdids").asText();
    pwdids = Misc.returnNullForEmptyOrNull(pwdids);

    String result = UtilsApi.vcxGetMessages(messageStatus, uid_s, pwdids).get();
    return result;
  }
}