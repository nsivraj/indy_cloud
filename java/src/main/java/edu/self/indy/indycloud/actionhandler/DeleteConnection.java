package edu.self.indy.indycloud.actionhandler;

import edu.self.indy.util.Misc;

public class DeleteConnection extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    return Misc.addQuotes(Misc.getLogFilePath(walletAction.id));
  }
}