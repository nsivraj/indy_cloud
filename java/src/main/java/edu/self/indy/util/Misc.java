package edu.self.indy.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Misc {
  public static final ObjectMapper jsonMapper = new ObjectMapper();

  // public static final String undoJSONStringify(String stringified) {
  //   // String undone = stringified.replace("\\\"", "\"").replace("\\\\", "\\");
  //   // if(undone.startsWith("\"") && undone.endsWith("\"")) {
  //   //   undone = undone.substring(1, undone.length() - 1);
  //   // } else if(undone.startsWith("\"")) {
  //   //   undone = undone.substring(1);
  //   // } else if(undone.endsWith("\"")) {
  //   //   undone = undone.substring(0, undone.length() - 1);
  //   // }
  //   // //System.out.println("1) undone: " + undone);
  //   // //JsonNode config = Misc.jsonMapper.readValue(agencyConfig, JsonNode.class);
  //   // //System.out.println("2) agencyConfig: " + config);
  //   // return undone;
  //   return stringified;
  // }

  // public static final String redoJSONStringify(String json) {
  //   // String redo = json.replace("\\", "\\\\").replace("\"", "\\\"");
  //   // redo = "\"" + redo + "\"";
  //   // return redo;
  //   return json;
  // }

  public static final String addQuotes(String toChange) {
    return "\"" + toChange + "\"";
  }

  // public static final String removeQuotes(String toChange) {
  //   if(toChange == null) {
  //     return toChange;
  //   } else if(toChange.startsWith("\"") && toChange.endsWith("\"")) {
  //     return toChange.substring(1, toChange.length() - 1);
  //   } else if(toChange.startsWith("\"")) {
  //     return toChange.substring(1);
  //   } else if(toChange.endsWith("\"")) {
  //     return toChange.substring(0, toChange.length() - 1);
  //   } else {
  //     return toChange;
  //   }
  // }

  public static final String returnNullForEmptyOrNull(String checkMe) {
    if(checkMe == null || "null".equalsIgnoreCase(checkMe.toLowerCase().trim()) || "".equals(checkMe.trim())) {
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

  public static final String getLogFilePath(long walletId) {
    return getFullFilePath(walletId, "connectme.rotating." + walletId + ".log");
  }

  public static final String getEncryptedLogFilePath(long walletId) {
    return getFullFilePath(walletId, "connectme.rotating." + walletId + ".log.enc");
  }

  public static final String getFullFilePath(long walletId, String fileName) {
    return getStorageConfigPath(walletId) + File.separator + fileName;
  }

  public static final String getVcxInitConfigFilePath(long walletId) {
    return getFullFilePath(walletId, "vcxInitConfig.json");
  }
  public static final String getVcxInitConfigJson(long walletId) throws IOException {
    File vcxConfigFile = new File(getVcxInitConfigFilePath(walletId));
    return readInFileAsString(vcxConfigFile);
  }

  public static void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
    try {
      File uploadedFile = new File(uploadedFileLocation);
      uploadedFile.getParentFile().mkdirs();

      int read = 0;
      byte[] bytes = new byte[1024];
      OutputStream out = new FileOutputStream(uploadedFile);
      while ((read = uploadedInputStream.read(bytes)) != -1) {
        out.write(bytes, 0, read);
      }
      out.flush();
      out.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  public static final String getColorImagePath(long walletId, String fileName) {
    return getFullFilePath(walletId, fileName);
  }

  public static final String getColor(String colorImage) throws IOException {
    BufferedImage image = ImageIO.read(new File(colorImage));

    int w = image.getWidth();
    int h = image.getHeight();

    int[] dataBuffInt = image.getRGB(0, 0, w, h, null, 0, w);
    int r=0,g=0,b=0;
    for(int i=0; i<dataBuffInt.length; ++i) {
      Color c = new Color(dataBuffInt[i]);
      r += c.getRed() * c.getRed();
      g += c.getGreen() * c.getGreen();
      b += c.getBlue() * c.getBlue();
    }

    return "["+(int)(Math.sqrt(r/dataBuffInt.length))+","+
               (int)(Math.sqrt(g/dataBuffInt.length))+","+
               (int)(Math.sqrt(b/dataBuffInt.length))+"]";
  }

  public static final String poolConfigToString(JsonNode poolConfigNode) {
    StringBuilder poolConfig = new StringBuilder();
    for(JsonNode poolEntry : poolConfigNode) {
      poolConfig.append(poolEntry.toString());
      poolConfig.append("\n");
    }
    poolConfig.setLength(Math.max(poolConfig.length() - 1, 0));
    return poolConfig.toString();
  }

  public static final String readInFileAsString(String fullpath) throws IOException {
    return readInFileAsString(new File(fullpath));
  }

  public static final String readInFileAsString(File file) throws IOException {
    String content = new String ( Files.readAllBytes( Paths.get(file.toURI()) ) );
    return content;
  }

  public static boolean isInteger(String strNum) {
    if (strNum == null) {
      return false;
    }
    try {
      Integer.parseInt(strNum);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }
}