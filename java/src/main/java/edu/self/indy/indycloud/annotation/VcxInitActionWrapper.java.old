package edu.self.indy.indycloud.annotation;

import com.evernym.sdk.vcx.LibVcx;
import com.evernym.sdk.vcx.vcx.VcxApi;

import edu.self.indy.indycloud.WalletAction;
import edu.self.indy.indycloud.actionhandler.AbstractActionHandler;
import edu.self.indy.indycloud.actionhandler.ActionHandler;
import edu.self.indy.indycloud.actionhandler.Init;
import edu.self.indy.util.Misc;

public class VcxInitActionWrapper extends AbstractActionHandler {
  public ActionHandler handler;

  public VcxInitActionWrapper(ActionHandler handler) {
    this.handler = handler;
    this.setWalletRepository(handler.getWalletRepository());
    this.setWalletAction(handler.getWalletAction());
  }

  @Override
  public String execute() throws Exception {
    String result = null;
    synchronized(LibVcx.api) {
      int shutdownResult = VcxApi.vcxShutdown(false);

      ActionHandler initHandler = new Init();
      String vcxInitConfig = Misc.getVcxInitConfigJson(walletAction.id);
      WalletAction initWalletAction = new WalletAction(walletAction.id, walletAction.actionName, vcxInitConfig);
      initHandler.setWalletAction(initWalletAction);
      initHandler.setWalletRepository(walletRepository);
      String initResult = initHandler.execute();

      result = handler.execute();
    }

    return result;
  }
}