package edu.self.indy.indycloud.actionhandler;

import edu.self.indy.util.Misc;

public class SetVcxLogger extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    return "\"" + Misc.getLogFilePath(walletAction.id) + "\"";
  }
}