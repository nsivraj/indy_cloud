package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.vcx.AlreadyInitializedException;
import com.evernym.sdk.vcx.vcx.VcxApi;

import edu.self.indy.util.Misc;

public class Init extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    //String vcxInitConfig = walletAction.getParameter("vcxInitConfig").toString();
    String vcxInitConfig = walletAction.getParameter("vcxInitConfig").toString();
    vcxInitConfig = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(vcxInitConfig));

    try {
      int retCode = VcxApi.initSovToken();
      if(retCode != 0) {
        throw new RuntimeException("Could not init sovtoken: " + retCode);
      } else {
        int result = VcxApi.vcxInitWithConfig(vcxInitConfig).get();
        if (result != -1) {
          return "true";
        } else {
          return "false";
        }
      }
    } catch (AlreadyInitializedException e) {
      // even if we get already initialized exception
      // then also we will resolve promise, because we don't care if vcx is already
      // initialized
      return "true";
    }
  }
}