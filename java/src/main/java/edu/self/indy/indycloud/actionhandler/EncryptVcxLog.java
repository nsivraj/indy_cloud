package edu.self.indy.indycloud.actionhandler;

import edu.self.indy.util.Misc;

public class EncryptVcxLog extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String logFilePath = walletAction.getParameter("logFilePath").toString();
    logFilePath = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(logFilePath));
    // String encryptionKey = walletAction.getParameter("encryptionKey").toString();
    // encryptionKey = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(encryptionKey));

    // RandomAccessFile logFile = new RandomAccessFile(logFilePath, "r");
    // byte[] fileBytes = new byte[(int)logFile.length()];
    // logFile.readFully(fileBytes);
    // logFile.close();

    // byte[] result = IndyApi.anonCrypt(encryptionKey, fileBytes).get();
    // String encryptedLogFilePath = Misc.getEncryptedLogFilePath(walletAction.id);
    // RandomAccessFile encLogFile = new RandomAccessFile(encryptedLogFilePath, "rw");
    // encLogFile.write(result, 0, result.length);
    // encLogFile.close();

    // return encryptedLogFilePath;
    return logFilePath;
  }
}