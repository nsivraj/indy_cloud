package edu.self.indy.util;

import java.io.File;

public class Misc {

  public static final String undoJSONStringify(String stringified) {
    String undone = stringified.replace("\\\"", "\"").replace("\\\\", "\\");
    if(undone.startsWith("\"") && undone.endsWith("\"")) {
      undone = undone.substring(1, undone.length() - 1);
    } else if(undone.startsWith("\"")) {
      undone = undone.substring(1);
    } else if(undone.endsWith("\"")) {
      undone = undone.substring(0, undone.length() - 1);
    }
    //System.out.println("1) undone: " + undone);
    //ObjectMapper mapper = new ObjectMapper();
    //JsonNode config = mapper.readValue(agencyConfig, JsonNode.class);
    //System.out.println("2) agencyConfig: " + config);
    return undone;
  }

  public static final String redoJSONStringify(String json) {
    String redo = json.replace("\\", "\\\\").replace("\"", "\\\"");
    redo = "\"" + redo + "\"";
    return redo;
  }

  public static final String returnNullForEmptyOrNull(String checkMe) {
    if("null".equalsIgnoreCase(checkMe) || "".equals(checkMe.trim())) {
      return null;
    } else {
      return checkMe;
    }
  }

  public static final String getStorageConfigPath(long walletId) {
    return System.getProperty("user.home") + File.separator + ".indy_client" + File.separator + "wallet" + File.separator + walletId;
  }
  public static final String getGenesisPath(long walletId, String fileName) {
    return getFullFilePath(walletId, "genesis_" + fileName + ".txn");
  }

  public static final String getExportPath(long walletId, String fileName) {
    return getFullFilePath(walletId, fileName);
  }

  public static final String getFullFilePath(long walletId, String fileName) {
    return getStorageConfigPath(walletId) + File.separator + fileName;
  }
}