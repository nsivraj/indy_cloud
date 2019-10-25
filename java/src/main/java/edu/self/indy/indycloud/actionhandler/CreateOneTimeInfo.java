package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.utils.UtilsApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.self.indy.util.Misc;

public class CreateOneTimeInfo extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    JsonNode agencyConfig = walletAction.getParameter("vcxProvisionConfig");
    String storageConfigPath = Misc.getStorageConfigPath(walletAction.id);

    ((ObjectNode)agencyConfig).put("storage_config", "{\"path\": \"" + storageConfigPath + "\"}");
    String result = UtilsApi.vcxAgentProvisionAsync(agencyConfig.toString()).get();
    return result;
  }
}