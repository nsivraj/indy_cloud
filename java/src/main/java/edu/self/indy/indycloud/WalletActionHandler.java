package edu.self.indy.indycloud;

import java.io.File;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.Base64;

import com.evernym.sdk.vcx.utils.UtilsApi;
import com.evernym.sdk.vcx.vcx.AlreadyInitializedException;
import com.evernym.sdk.vcx.vcx.VcxApi;
import com.evernym.sdk.vcx.wallet.WalletApi;

import edu.self.indy.util.StrUtil;

// [INFO] aqueduct: POST /wallets/-1/action/setVcxLogger/params/%7B%22logLevel%22:%22debug%22,%22uniqueId%22:%2270c385f1-cbbc-2f12-8cb5-8df71ef3799b%22,%22MAX_ALLOWED_FILE_BYTES%22:10000000%7D 76ms 200
// [INFO] aqueduct: POST /wallets/-1/action/createWalletKey/params/%7B%22lengthOfKey%22:64%7D 93ms 200
// [INFO] aqueduct: POST /wallets/-1/action/shutdownVcx/params/%7B%22deletePool%22:false%7D 1ms 200


public class WalletActionHandler {
  public static final String execute(WalletAction wAction) throws Exception {
    if(wAction.actionName.equalsIgnoreCase("setVcxLogger")) {
      // ContextWrapper cw = new ContextWrapper(reactContext);
      // RNIndyStaticData.MAX_ALLOWED_FILE_BYTES = MAX_ALLOWED_FILE_BYTES;
      // RNIndyStaticData.LOG_FILE_PATH = cw.getFilesDir().getAbsolutePath() +
      //         "/connectme.rotating." + uniqueIdentifier + ".log";
      // RNIndyStaticData.ENCRYPTED_LOG_FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +
      //         "/connectme.rotating." + uniqueIdentifier + ".log.enc";
      // //get the documents directory:
      // Log.d(TAG, "Setting vcx logger to: " + RNIndyStaticData.LOG_FILE_PATH);

      // if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      //     RNIndyStaticData.initLoggerFile(cw);
      // }

      return "\"0\"";
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
      agencyConfig = StrUtil.undoJSONStringify(agencyConfig);
      String result = UtilsApi.vcxAgentProvisionAsync(agencyConfig).get();
      return StrUtil.redoJSONStringify(result);
    }
    else if(wAction.actionName.equalsIgnoreCase("getGenesisPathWithConfig")) {
      String fileName = wAction.getParameter("fileName").toString();
      fileName = StrUtil.undoJSONStringify(fileName);
      String poolConfig = wAction.getParameter("poolConfig").toString();
      poolConfig = StrUtil.undoJSONStringify(poolConfig);

      File genFile = new File(System.getProperty("user.home") + File.separator + ".indy_client" + File.separator + "genesis_" + fileName + ".txn");
      genFile.getParentFile().mkdirs();
      if (genFile.exists()) {
          genFile.delete();
      }

      FileOutputStream fos = new FileOutputStream(genFile);
      fos.write(poolConfig.getBytes());
      fos.flush();
      fos.close();

      return StrUtil.redoJSONStringify(genFile.getAbsolutePath());
    }
    else if(wAction.actionName.equalsIgnoreCase("init")) {
      String poolConfig = wAction.getParameter("vcxInitConfig").toString();
      //poolConfig = StrUtil.undoJSONStringify(poolConfig);

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
      messageStatus = StrUtil.undoJSONStringify(messageStatus);
      String uid_s = wAction.getParameter("uid_s").toString();
      uid_s = StrUtil.returnNullForEmptyOrNull(StrUtil.undoJSONStringify(uid_s));
      String pwdids = wAction.getParameter("pwdids").toString();
      pwdids = StrUtil.returnNullForEmptyOrNull(StrUtil.undoJSONStringify(pwdids));

      String result = UtilsApi.vcxGetMessages(messageStatus, uid_s, pwdids).get();
      return result;
    }
    else if(wAction.actionName.equalsIgnoreCase("setWalletItem")) {
      String key = wAction.getParameter("key").toString();
      key = StrUtil.returnNullForEmptyOrNull(StrUtil.undoJSONStringify(key));
      String value = wAction.getParameter("value").toString();
      value = StrUtil.returnNullForEmptyOrNull(StrUtil.undoJSONStringify(value));
      if(key == null || value == null) {
        return "-1";
      } else {
        int result = WalletApi.addRecordWallet("record_type", key, value).get();
        return String.valueOf(result);
      }
    }
    else if(wAction.actionName.equalsIgnoreCase("exportWallet")) {
      //Log.d(TAG, "exportWallet() called with: exportPath = [" + exportPath + "], encryptionKey = [" + encryptionKey
      //          + "], promise = [" + promise + "]");
      String exportPath = wAction.getParameter("exportPath").toString();
      exportPath = StrUtil.returnNullForEmptyOrNull(StrUtil.undoJSONStringify(exportPath));
      String encryptionKey = wAction.getParameter("encryptionKey").toString();
      encryptionKey = StrUtil.returnNullForEmptyOrNull(StrUtil.undoJSONStringify(encryptionKey));

      if(exportPath == null || encryptionKey == null) {
        return "-1";
      } else {
        int result = WalletApi.exportWallet(exportPath, encryptionKey).get();
        return String.valueOf(result);
      }
    }
    else if(wAction.actionName.equalsIgnoreCase("createBrandNewWallet")) {
      boolean secure = Boolean.valueOf(wAction.getParameter("secure").toString());
      String walletId = "1";
      if(secure) {
        // TODO: If they want a secure wallet then additional security requirements will be enforced
        walletId = "11";
      } else {
        walletId = "10";
      }

      return walletId;
    }
    else {
      return "-267896";
    }
  }
}

