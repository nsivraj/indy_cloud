package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.vcx.VcxApi;

public class ShutdownVcx extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    VcxApi.vcxShutdown(new Boolean(walletAction.getParameter("deletePool").toString()));
    // return 0 for success and -1 for error
    return "0";
  }
}