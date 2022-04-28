package org.mobilitydata.gtfsvalidator.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.resources.HtmlOutputResources;

public class HtmlOutputUtil {
  private static final String ERROR = SeverityLevel.ERROR.name();
  private static final String WARNING = SeverityLevel.WARNING.name();
  private static final String INFO = SeverityLevel.INFO.name();
  private static final String NOTICES = "notices";
  private static final String CODE = "code";
  private static final String SEVERITY = "severity";
  private static final String TOTAL_NOTICES = "totalNotices";
  private static final String SAMPLE_NOTICES = "sampleNotices";

  public static String noticeColumnsBuilder(ArrayList<String> noticeFields) {
    StringBuilder columns = new StringBuilder("                ");
    for (String field : noticeFields) {
      columns.append("<th>").append(field).append("</th>");
    }
    columns.append("\n");
    return columns.toString();
  }

  public static String noticeRowsBuilder(ArrayList<String> noticeFields, JsonArray sampleNotices) {
    StringBuilder rows = new StringBuilder();
    for (JsonElement notice : sampleNotices) {
      JsonObject noticeJson = notice.getAsJsonObject();
      rows.append("              <tr>\n");
      for (String field : noticeFields) {
        rows.append("                <td>")
            .append(noticeJson.get(field).toString())
            .append("</td>");
      }
      rows.append("              </tr>\n");
    }
    return rows.toString();
  }

  public static String outputBuilder(JsonObject noticesJson) {
    StringBuilder notices = new StringBuilder();
    HashMap<String, Integer> noticesQty =
        new HashMap<>() {
          {
            put(ERROR, 0);
            put(WARNING, 0);
            put(INFO, 0);
          }
        };
    JsonArray noticesArray = noticesJson.getAsJsonArray(NOTICES);
    for (JsonElement notice : noticesArray) {
      JsonObject noticeJson = notice.getAsJsonObject();
      String noticeCode = noticeJson.get(CODE).getAsString();
      String noticeSeverity = noticeJson.get(SEVERITY).getAsString();
      int totalNotices = noticeJson.get(TOTAL_NOTICES).getAsInt();
      noticesQty.put(noticeSeverity, noticesQty.get(noticeSeverity) + totalNotices);
      JsonArray sampleNotices = noticeJson.getAsJsonArray(SAMPLE_NOTICES);
      JsonElement sampleNotice = sampleNotices.get(sampleNotices.size() - 1);
      JsonObject sampleJson = sampleNotice.getAsJsonObject();
      ArrayList<String> noticeFields = new ArrayList<>();
      Set<Map.Entry<String, JsonElement>> sampleEntries = sampleJson.entrySet();
      for (Map.Entry<String, JsonElement> entry : sampleEntries) {
        noticeFields.add(entry.getKey());
      }
      String sampleColumns = noticeColumnsBuilder(noticeFields);
      String sampleRows = noticeRowsBuilder(noticeFields, sampleNotices);
      notices.append(
          HtmlOutputResources.noticeFrame(
              noticeCode, noticeSeverity, totalNotices, sampleColumns, sampleRows));
    }
    return HtmlOutputResources.outputFrame(
        (noticesQty.get(ERROR) + noticesQty.get(WARNING) + noticesQty.get(INFO)),
        noticesQty.get(ERROR),
        noticesQty.get(WARNING),
        noticesQty.get(INFO),
        notices.toString());
  }
}
