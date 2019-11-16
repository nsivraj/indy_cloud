package edu.self.indy.indycloud.actionhandler;

import java.io.File;
import java.io.FileOutputStream;

import edu.self.indy.indycloud.jpa.Wallet;
import edu.self.indy.util.Misc;

public class GetGenesisPathWithConfig extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String genesisFileName = walletAction.getParameter("fileName").asText();
    String poolConfig = Misc.poolConfigToString(walletAction.getParameter("poolConfig"));

    File genFile = new File(Misc.getGenesisPath(walletAction.id, genesisFileName));
    genFile.getParentFile().mkdirs();
    if (genFile.exists()) {
        genFile.delete();
    }

    FileOutputStream fos = new FileOutputStream(genFile);
    fos.write(poolConfig.getBytes());
    fos.flush();
    fos.close();

    Wallet wallet = walletRepository.findById(walletAction.id);
    wallet.setGenesisPath(genFile.getAbsolutePath());
    walletRepository.save(wallet);

    //return Misc.redoJSONStringify(wallet.genesisPath);
    return Misc.addQuotes(wallet.genesisPath);
  }
}