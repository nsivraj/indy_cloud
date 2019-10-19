package edu.self.indy.util;

public class StrUtil {

  public static final String undoJSONStringify(String stringified) {
    String undone = stringified.replace("\\\"", "\"");
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
    String redo = json.replace("\"", "\\\"");
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
}