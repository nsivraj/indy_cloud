package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.utils.UtilsApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.self.indy.util.Misc;

public class CreateOneTimeInfo extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String agencyConfig = walletAction.getParameter("vcxProvisionConfig").toString();
    agencyConfig = Misc.undoJSONStringify(agencyConfig);
    String storageConfigPath = Misc.getStorageConfigPath(walletAction.id);

    ObjectMapper mapper = new ObjectMapper();
    JsonNode configJSONNode = mapper.readTree(agencyConfig);
    ((ObjectNode)configJSONNode).put("storage_config", "{\"path\": \"" + storageConfigPath + "\"}");
    String result = UtilsApi.vcxAgentProvisionAsync(configJSONNode.toString()).get();
    return Misc.redoJSONStringify(result);
  }
}