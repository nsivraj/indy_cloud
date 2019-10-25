package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.vcx.VcxApi;

public class ShutdownVcx extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    int result = VcxApi.vcxShutdown(walletAction.getParameter("deletePool").asBoolean());
    // return 0 for success and -1 for error
    return String.valueOf(result);
  }
}