package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.utils.UtilsApi;

import edu.self.indy.util.Misc;

public class GetTxnAuthorAgreement extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String result = UtilsApi.getLedgerAuthorAgreement().get();
    return result;
  }
}