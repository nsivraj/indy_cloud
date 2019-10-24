package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.utils.UtilsApi;

import edu.self.indy.util.Misc;

public class DownloadMessages extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String messageStatus = walletAction.getParameter("messageStatus").toString();
    messageStatus = Misc.undoJSONStringify(messageStatus);
    String uid_s = walletAction.getParameter("uid_s").toString();
    uid_s = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(uid_s));
    String pwdids = walletAction.getParameter("pwdids").toString();
    pwdids = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(pwdids));

    String result = UtilsApi.vcxGetMessages(messageStatus, uid_s, pwdids).get();
    return result;
  }
}