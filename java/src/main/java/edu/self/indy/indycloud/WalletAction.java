package edu.self.indy.indycloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.self.indy.indycloud.actionhandler.ActionHandler;
import edu.self.indy.indycloud.jpa.WalletRepository;

public class WalletAction {

  public long id;
  public String actionName;
  private JsonNode actionParams;

  public WalletAction(long id, String actionName, String actionParams)
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
    return toStringWithoutActionParams() + " :: actionParams: " + actionParams;
  }

  public String toStringWithoutActionParams() {
    return "id: " + id + " :: actionName: " + actionName;
  }

  public ActionHandler getActionHandler(WalletRepository walletRepository)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    String actionHandlerName = actionName.substring(0, 1).toUpperCase() + actionName.substring(1);
    ActionHandler handler = (ActionHandler)Class.forName(ActionHandler.class.getPackage().getName() + "." + actionHandlerName).newInstance();
    handler.setWalletRepository(walletRepository);
    handler.setWalletAction(this);
    return handler;
  }
}