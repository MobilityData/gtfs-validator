package org.mobilitydata.gtfsvalidator.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;

public class HtmlOutputUtil {
  public static final String JQUERY_FILE = "META-INF/resources/webjars/jquery/3.6.0/jquery.min.js";
  public static final String STYLE_CSS = "style.css";
  public static final String SCRIPT_JS = "script.js";
  private static final String ERROR = SeverityLevel.ERROR.name();
  private static final String WARNING = SeverityLevel.WARNING.name();
  private static final String INFO = SeverityLevel.INFO.name();
  private static final String NOTICES = "notices";
  private static final String CODE = "code";
  private static final String SEVERITY = "severity";
  private static final String TOTAL_NOTICES = "totalNotices";
  private static final String SAMPLE_NOTICES = "sampleNotices";

  public static String outputBuilder(
      JsonObject noticesJson, String jqueryDependency, String styleCss, String scriptJs) {
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
          noticeFrame(noticeCode, noticeSeverity, totalNotices, sampleColumns, sampleRows));
    }
    String outputHeader =
        outputHeader(
            (noticesQty.get(ERROR) + noticesQty.get(WARNING) + noticesQty.get(INFO)),
            noticesQty.get(ERROR),
            noticesQty.get(WARNING),
            noticesQty.get(INFO));
    return outputFrame(
        jqueryDependency, styleCss, scriptJs, outputHeader, notices.toString(), outputFooter());
  }

  public static String resourceLoader(String resourcePath) {
    String content = "";
    try (InputStream stream =
        HtmlOutputUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
      Scanner scanner = new Scanner(stream).useDelimiter("\\A");
      content = scanner.hasNext() ? scanner.next() : "";
    } catch (IOException e) {
      System.out.println("The resource was not found at \"" + resourcePath + "\".");
    }
    return content;
  }

  protected static String noticeColumnsBuilder(ArrayList<String> noticeFields) {
    StringBuilder columns = new StringBuilder("                ");
    for (String field : noticeFields) {
      columns.append("<th>").append(field).append("</th>");
    }
    columns.append("\n");
    return columns.toString();
  }

  protected static String noticeRowsBuilder(
      ArrayList<String> noticeFields, JsonArray sampleNotices) {
    StringBuilder rows = new StringBuilder();
    for (JsonElement notice : sampleNotices) {
      JsonObject noticeJson = notice.getAsJsonObject();
      rows.append("              <tr>\n");
      for (String field : noticeFields) {
        rows.append("                <td>")
            .append(noticeJson.get(field).toString())
            .append("</td>\n");
      }
      rows.append("              </tr>\n");
    }
    return rows.toString();
  }

  protected static String outputHeader(int total, int errors, int warnings, int infos) {
    return "<h1>VALIDATION REPORT</h1>\n"
        + "<h2>"
        + total
        + " notices reported ("
        + errors
        + " errors, "
        + warnings
        + " warnings, "
        + infos
        + " infos)"
        + "</h2>\n"
        + "<p>Please visit <a href=\"https://github.com/MobilityData/gtfs-validator/blob/master/docs/NOTICES.md\">NOTICES.md</a> and <a href=\"https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md\">RULES.md</a>.</p>\n"
        + "<table class=\"accordion\">\n"
        + "  <thead>\n"
        + "    <tr>\n"
        + "      <th>Code</th><th>Severity</th><th>Total</th>\n"
        + "    </tr>\n"
        + "  </thead>\n"
        + "  <tbody>\n";
  }

  protected static String outputFooter() {
    return "  </tbody>\n"
        + "</table>\n"
        + "<p>This validation report was generated using the <a href=\"https://github.com/MobilityData/gtfs-validator\">GTFS Schedule validator</a>.</p>\n";
  }

  protected static String outputFrame(
      String jqueryDependency,
      String styleCss,
      String scriptJs,
      String outputHeader,
      String outputNotices,
      String outputFooter) {
    return "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<head>\n"
        + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
        + "<script>\n"
        + jqueryDependency
        + "</script>\n"
        + "<style>\n"
        + styleCss
        + "</style>\n"
        + "</head>\n"
        + "<body>\n"
        + outputHeader
        + outputNotices
        + outputFooter
        + "<script>\n"
        + scriptJs
        + "</script>\n"
        + "</body>\n"
        + "</html>\n";
  }

  protected static String noticeFrame(
      String noticeCode,
      String noticeSeverity,
      int totalNotices,
      String sampleColumns,
      String sampleRows) {
    return "    <tr class=\"notice\">\n"
        + "      <td>"
        + noticeCode
        + "</td>\n"
        + "      <td class=\""
        + noticeSeverity.toLowerCase()
        + "\">"
        + noticeSeverity
        + "</td>\n"
        + "      <td>"
        + totalNotices
        + "</td>\n"
        + "    </tr>\n"
        + "    <tr class=\"description\">\n"
        + "      <td colspan=\"3\">\n"
        + "        <div class=\"desc-content\">\n"
        + "          <h3>"
        + noticeCode
        + "</h3>\n"
        + "          <table>\n"
        + "            <thead>\n"
        + "              <tr>\n"
        + sampleColumns
        + "              </tr>\n"
        + "            </thead>\n"
        + "            <tbody>\n"
        + "              <tr>\n"
        + sampleRows
        + "            </tbody>\n"
        + "          </table>\n"
        + "        </div>\n"
        + "      </td>\n"
        + "    </tr>\n";
  }
}
