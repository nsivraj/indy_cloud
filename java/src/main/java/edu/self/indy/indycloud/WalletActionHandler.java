package edu.self.indy.indycloud;

import java.security.SecureRandom;
import java.util.Base64;

import com.evernym.sdk.vcx.VcxException;
import com.evernym.sdk.vcx.vcx.VcxApi;


// http://10.4.1.73:9000/api/v1/wallets/-1/action/setVcxLogger/params/%7B%22logLevel%22:%22debug%22,%22uniqueId%22:%22453a52a7-7eb4-9dab-33c4-319210a539f4%22,%22MAX_ALLOWED_FILE_BYTES%22:10000000%7D
// http://10.4.1.73:9000/api/v1/wallets/-1/action/createWalletKey/params/%7B%22lengthOfKey%22:64%7D
// http://10.4.1.73:9000/api/v1/wallets/-1/action/shutdownVcx/params/%7B%22deletePool%22:false%7D

// [INFO] aqueduct: POST /wallets/-1/action/setVcxLogger/params/%7B%22logLevel%22:%22debug%22,%22uniqueId%22:%2270c385f1-cbbc-2f12-8cb5-8df71ef3799b%22,%22MAX_ALLOWED_FILE_BYTES%22:10000000%7D 76ms 200
// [INFO] aqueduct: POST /wallets/-1/action/createWalletKey/params/%7B%22lengthOfKey%22:64%7D 93ms 200
// [INFO] aqueduct: POST /wallets/-1/action/shutdownVcx/params/%7B%22deletePool%22:false%7D 1ms 200


public class WalletActionHandler {
  public static final String execute(WalletAction wAction) throws VcxException {
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
    else {
      return "-1";
    }
  }
}