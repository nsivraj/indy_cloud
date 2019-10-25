package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.vcx.AlreadyInitializedException;
import com.evernym.sdk.vcx.vcx.VcxApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.self.indy.util.Misc;

public class Init extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    JsonNode vcxInitConfigNode = walletAction.getParameter("vcxInitConfig");
    String poolConfig = Misc.poolConfigToString(vcxInitConfigNode.get("config"));
    ((ObjectNode)vcxInitConfigNode).put("config", poolConfig);

    try {
      int retCode = VcxApi.initSovToken();
      if(retCode != 0) {
        throw new RuntimeException("Could not init sovtoken: " + retCode);
      } else {
        int result = VcxApi.vcxInitWithConfig(vcxInitConfigNode.toString()).get();
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