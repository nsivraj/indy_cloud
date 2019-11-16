package edu.self.indy.indycloud;

import java.lang.reflect.Method;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import edu.self.indy.indycloud.actionhandler.ActionHandler;
import edu.self.indy.indycloud.annotation.VcxInit;
import edu.self.indy.indycloud.annotation.VcxInitActionWrapper;
import edu.self.indy.indycloud.jpa.WalletRepository;
import edu.self.indy.util.Misc;

public class WalletAction {

  public long id;
  public String actionName;
  private JsonNode actionParams;

  public WalletAction(long id, String actionName, String actionParams)
      throws JsonMappingException, JsonProcessingException {
    this.id = id;
    this.actionName = actionName;
    this.actionParams = Misc.jsonMapper.readTree(actionParams);
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
      throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException,
      SecurityException {
    String actionHandlerName = actionName.substring(0, 1).toUpperCase() + actionName.substring(1);
    ActionHandler handler = (ActionHandler)Class.forName(ActionHandler.class.getPackage().getName() + "." + actionHandlerName).newInstance();
    handler.setWalletRepository(walletRepository);
    handler.setWalletAction(this);
    return WalletAction.wrapWithVcxInit(handler);
  }

  public static final ActionHandler wrapWithVcxInit(ActionHandler handler)
      throws NoSuchMethodException, SecurityException {
    // Check if the execute method of the handler is annotated with @VcxInit
    // If it is then wrap it with a VcxInitActionWrapper
    Method method = handler.getClass().getMethod("execute", new Class[]{});
    VcxInit annotation = method.getAnnotation(VcxInit.class);
    if(annotation != null) {
      return new VcxInitActionWrapper(handler);
    } else {
      return handler;
    }
  }
}

