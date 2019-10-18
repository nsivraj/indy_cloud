package edu.self.indy.indycloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WalletAction {

  public int id;
  public String actionName;
  private JsonNode actionParams;

  public WalletAction(int id, String actionName, String actionParams)
      throws JsonMappingException, JsonProcessingException {
    this.id = id;
    this.actionName = actionName;
    ObjectMapper mapper = new ObjectMapper();
    this.actionParams = mapper.readTree(actionParams);
	}

  public JsonNode getParameter(String name) {
    return actionParams.get(name);
  }
  
  @Override
  public String toString() {
    return "id: " + id + " :: actionName: " + actionName + " :: actionParams: " + actionParams;
  }
}