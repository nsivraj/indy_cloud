package edu.self.indy.indycloud;

import java.io.File;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.Base64;

import com.evernym.sdk.vcx.utils.UtilsApi;
import com.evernym.sdk.vcx.vcx.AlreadyInitializedException;
import com.evernym.sdk.vcx.vcx.VcxApi;
import com.evernym.sdk.vcx.wallet.WalletApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.self.indy.indycloud.jpa.Wallet;
import edu.self.indy.indycloud.jpa.WalletRepository;
import edu.self.indy.util.Misc;

// [INFO] aqueduct: POST /wallets/-1/action/setVcxLogger/params/%7B%22logLevel%22:%22debug%22,%22uniqueId%22:%2270c385f1-cbbc-2f12-8cb5-8df71ef3799b%22,%22MAX_ALLOWED_FILE_BYTES%22:10000000%7D 76ms 200
// [INFO] aqueduct: POST /wallets/-1/action/createWalletKey/params/%7B%22lengthOfKey%22:64%7D 93ms 200
// [INFO] aqueduct: POST /wallets/-1/action/shutdownVcx/params/%7B%22deletePool%22:false%7D 1ms 200

@Component
public class WalletActionHandler {

  @Autowired
  WalletRepository walletRepository;

  public String execute(WalletAction wAction) throws Exception {
    if(wAction.actionName.equalsIgnoreCase("setVcxLogger")) {
      return "\"" + Misc.getLogFilePath(wAction.id) + "\"";
    }
    else if(wAction.actionName.equalsIgnoreCase("createWalletKey")) {
      SecureRandom random = new SecureRandom();
      byte bytes[] = new byte[new Integer(wAction.getParameter("lengthOfKey").toString())];
      random.nextBytes(bytes);
      return "\"" + Base64.getEncoder().encodeToString(bytes) + "\"";
    }
    else if(wAction.actionName.equalsIgnoreCase("shutdownVcx")) {
      VcxApi.vcxShutdown(new Boolean(wAction.getParameter("deletePool").toString()));
      // return 0 for success and -1 for error
      return "0";
    }
    else if(wAction.actionName.equalsIgnoreCase("createOneTimeInfo")) {
      String agencyConfig = wAction.getParameter("vcxProvisionConfig").toString();
      agencyConfig = Misc.undoJSONStringify(agencyConfig);
      String storageConfigPath = Misc.getStorageConfigPath(wAction.id);

      ObjectMapper mapper = new ObjectMapper();
      JsonNode configJSONNode = mapper.readTree(agencyConfig);
      ((ObjectNode)configJSONNode).put("storage_config", "{\"path\": \"" + storageConfigPath + "\"}");
      String result = UtilsApi.vcxAgentProvisionAsync(configJSONNode.toString()).get();
      return Misc.redoJSONStringify(result);
    }
    else if(wAction.actionName.equalsIgnoreCase("getGenesisPathWithConfig")) {
      String genesisFileName = wAction.getParameter("fileName").toString();
      genesisFileName = Misc.undoJSONStringify(genesisFileName);
      String poolConfig = wAction.getParameter("poolConfig").toString();
      poolConfig = Misc.undoJSONStringify(poolConfig);

      File genFile = new File(Misc.getGenesisPath(wAction.id, genesisFileName));
      genFile.getParentFile().mkdirs();
      if (genFile.exists()) {
          genFile.delete();
      }

      FileOutputStream fos = new FileOutputStream(genFile);
      fos.write(poolConfig.getBytes());
      fos.flush();
      fos.close();

      Wallet wallet = walletRepository.findById(wAction.id);
      wallet.setGenesisPath(genFile.getAbsolutePath());
      walletRepository.save(wallet);
      return Misc.redoJSONStringify(wallet.genesisPath);
    }
    else if(wAction.actionName.equalsIgnoreCase("init")) {
      String poolConfig = wAction.getParameter("vcxInitConfig").toString();
      //poolConfig = Misc.undoJSONStringify(poolConfig);

      try {
        int retCode = VcxApi.initSovToken();
        if(retCode != 0) {
          throw new RuntimeException("Could not init sovtoken: " + retCode);
        } else {
          int result = VcxApi.vcxInitWithConfig(poolConfig).get();
          if (result != -1) {
            return "true";
          } else {
            return "false";
          }
        }
      } catch (AlreadyInitializedException e) {
        // even if we get already initialized exception
        // then also we will resolve promise, because we don't care if vcx is already
        // initialized
        return "true";
      }
    }
    else if(wAction.actionName.equalsIgnoreCase("downloadMessages")) {
      String messageStatus = wAction.getParameter("messageStatus").toString();
      messageStatus = Misc.undoJSONStringify(messageStatus);
      String uid_s = wAction.getParameter("uid_s").toString();
      uid_s = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(uid_s));
      String pwdids = wAction.getParameter("pwdids").toString();
      pwdids = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(pwdids));

      String result = UtilsApi.vcxGetMessages(messageStatus, uid_s, pwdids).get();
      return result;
    }
    else if(wAction.actionName.equalsIgnoreCase("setWalletItem")) {
      String key = wAction.getParameter("key").toString();
      key = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(key));
      String value = wAction.getParameter("value").toString();
      value = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(value));
      if(key == null || value == null) {
        return "-1";
      } else {
        int result = WalletApi.addRecordWallet("record_type", key, value).get();
        return String.valueOf(result);
      }
    }
    else if(wAction.actionName.equalsIgnoreCase("exportWallet")) {
      //Log.d(TAG, "exportWallet() called with: walletPath = [" + walletPath + "], walletEncryptionKey = [" + walletEncryptionKey
      //          + "], promise = [" + promise + "]");
      String walletPath = wAction.getParameter("walletPath").toString();
      walletPath = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(walletPath));
      String walletEncryptionKey = wAction.getParameter("walletEncryptionKey").toString();
      walletEncryptionKey = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(walletEncryptionKey));

      if(walletPath == null || walletEncryptionKey == null) {
        return "-1";
      } else {
        String exportFileName = walletPath.substring(walletPath.lastIndexOf('/') + 1);
        String exportFilePath = Misc.getExportPath(wAction.id, exportFileName);
        File exportFile = new File(exportFilePath);
        exportFile.delete();
        int result = WalletApi.exportWallet(exportFilePath, walletEncryptionKey).get();

        Wallet wallet = walletRepository.findById(wAction.id);
        wallet.setExportPath(exportFilePath);
        walletRepository.save(wallet);

        return String.valueOf(result);
      }
    }
    else if(wAction.actionName.equalsIgnoreCase("createBrandNewWallet")) {
      boolean secure = Boolean.valueOf(wAction.getParameter("secure").toString());
      Wallet wallet = walletRepository.save(new Wallet("wallet_name", secure));
      if(secure) {
        // TODO: If they want a secure wallet then additional security requirements will be enforced

      } else {

      }

      return String.valueOf(wallet.getId());
    }
    else if(wAction.actionName.equalsIgnoreCase("updateWalletItem")) {
      String key = wAction.getParameter("key").toString();
      key = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(key));
      String data = wAction.getParameter("data").toString();
      data = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(data));
      if(key == null || data == null) {
        return "-1";
      } else {
        int result = WalletApi.updateRecordWallet("record_type", key, data).get();
        return String.valueOf(result);
      }
    }
    else if(wAction.actionName.equalsIgnoreCase("deleteWalletItem")) {
      String key = wAction.getParameter("key").toString();
      key = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(key));
      if(key == null) {
        return "-1";
      } else {
        int result = WalletApi.deleteRecordWallet("record_type", key).get();
        return String.valueOf(result);
      }
    }
    else if(wAction.actionName.equalsIgnoreCase("getWalletItem")) {
      String key = wAction.getParameter("key").toString();
      key = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(key));
      if(key == null) {
        return "-1";
      } else {
        String result = WalletApi.getRecordWallet("record_type", key, "").get();
        return result;
      }
    }
    else if(wAction.actionName.equalsIgnoreCase("encryptVcxLog")) {
      String logFilePath = wAction.getParameter("logFilePath").toString();
      logFilePath = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(logFilePath));
      // String encryptionKey = wAction.getParameter("encryptionKey").toString();
      // encryptionKey = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(encryptionKey));

      // RandomAccessFile logFile = new RandomAccessFile(logFilePath, "r");
      // byte[] fileBytes = new byte[(int)logFile.length()];
      // logFile.readFully(fileBytes);
      // logFile.close();

      // byte[] result = IndyApi.anonCrypt(encryptionKey, fileBytes).get();
      // String encryptedLogFilePath = Misc.getEncryptedLogFilePath(wAction.id);
      // RandomAccessFile encLogFile = new RandomAccessFile(encryptedLogFilePath, "rw");
      // encLogFile.write(result, 0, result.length);
      // encLogFile.close();

      // return encryptedLogFilePath;
      return logFilePath;
    }
    else if(wAction.actionName.equalsIgnoreCase("writeToVcxLog")) {
      String logMessage = wAction.getParameter("logMessage").toString();
      logMessage = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(logMessage));

      /////////////////////////////////////////////////////////////////////////////////
      // NOTE: Comment out the line below if you do not want the logs
      //       cluttered with Connect.Me state logging statements
      /////////////////////////////////////////////////////////////////////////////////
      //System.out.println(logMessage);
      /////////////////////////////////////////////////////////////////////////////////

      return "0";
    }
    else {
      return "-267896";
    }
  }
}

