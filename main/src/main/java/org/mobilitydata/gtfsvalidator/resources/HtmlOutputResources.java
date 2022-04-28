package org.mobilitydata.gtfsvalidator.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class HtmlOutputResources {
  private static final String JQUERY_FILE = "META-INF/resources/webjars/jquery/3.6.0/jquery.min.js";

  public static String styleCss() {
    return "body {\n"
        + "  font-family: Helvetica, Arial, sans-serif;\n"
        + "  font-size: 14px;\n"
        + "  min-width: 800px;\n"
        + "}\n"
        + "\n"
        + ".error:before {\n"
        + "  content: \"\\1F534  \";\n"
        + "}\n"
        + "\n"
        + ".warning:before {\n"
        + "  content: \"\\1F7E0  \";\n"
        + "}\n"
        + "\n"
        + ".info:before {\n"
        + "  content: \"\\26AA  \";\n"
        + "}\n"
        + "\n"
        + "* {\n"
        + "  box-sizing: border-box;\n"
        + "}\n"
        + "\n"
        + "body {\n"
        + "  padding: 1em 2em;\n"
        + "}\n"
        + "\n"
        + "table {\n"
        + "  width: 100%;\n"
        + "}\n"
        + "table th {\n"
        + "  text-align: left;\n"
        + "  border-bottom: 2px solid #000;\n"
        + "  padding: 0.5em;\n"
        + "}\n"
        + "table td {\n"
        + "  border-bottom: 1px solid #ddd;\n"
        + "  padding: 0.5em;\n"
        + "}\n"
        + "\n"
        + "table.accordion > tbody > tr.notice td, table.accordion > tbody > tr.view th {\n"
        + "  cursor: auto;\n"
        + "}\n"
        + "table.accordion > tbody > tr.notice td:first-child,\n"
        + "table.accordion > tbody > tr.notice th:first-child {\n"
        + "  position: relative;\n"
        + "  padding-left: 20px;\n"
        + "}\n"
        + "table.accordion > tbody > tr.notice td:first-child:before,\n"
        + "table.accordion > tbody > tr.notice th:first-child:before {\n"
        + "  position: absolute;\n"
        + "  top: 50%;\n"
        + "  left: 5px;\n"
        + "  width: 9px;\n"
        + "  height: 16px;\n"
        + "  margin-top: -8px;\n"
        + "  color: #000;\n"
        + "  content: \"+\";\n"
        + "}\n"
        + "table.accordion > tbody > tr.notice:hover {\n"
        + "  background: #ddd;\n"
        + "}\n"
        + "table.accordion > tbody > tr.notice.open {\n"
        + "  background: #ddd;\n"
        + "  color: black;\n"
        + "}\n"
        + "table.accordion > tbody > tr.description {\n"
        + "  display: none;\n"
        + "}\n"
        + "table.accordion > tbody > tr.description.open {\n"
        + "  display: table-row;\n"
        + "}\n"
        + "\n"
        + ".desc-content {\n"
        + "  padding: 0.5em;\n"
        + "  border-bottom: 5px solid #000;\n"
        + "  border-top: 5px solid #000\n"
        + "}\n"
        + ".desc-content h3 {\n"
        + "  margin-top: 0;\n"
        + "}";
  }

  public static String scriptJs() {
    return "$(function(){\n"
        + "  $(\".accordion tr.notice\").on(\"click\", function(){\n"
        + "    $(this).toggleClass(\"open\").next(\".description\").toggleClass(\"open\");\n"
        + "  });\n"
        + "});";
  }

  public static String jqueryDependency() {
    String script = "";
    try (InputStream stream =
        HtmlOutputResources.class.getClassLoader().getResourceAsStream(JQUERY_FILE)) {
      Scanner scanner = new Scanner(stream).useDelimiter("\\A");
      script = scanner.hasNext() ? scanner.next() : "";
    } catch (IOException e) {
      System.out.println("The jQuery dependency script was not found at \"" + JQUERY_FILE + "\".");
    }
    return script;
  }

  public static String outputHeader(int total, int errors, int warnings, int infos) {
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

  public static String outputFooter() {
    return "  </tbody>\n"
        + "</table>\n"
        + "<p>This validation report was generated using the <a href=\"https://github.com/MobilityData/gtfs-validator\">GTFS Schedule validator</a>.</p>\n";
  }

  public static String outputFrame(
      int totalQty, int totalErrors, int totalWarnings, int totalInfos, String outputNotices) {
    return "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<head>\n"
        + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
        + "<script>\n"
        + jqueryDependency()
        + "</script>\n"
        + "<style>\n"
        + styleCss()
        + "</style>\n"
        + "</head>\n"
        + "<body>\n"
        + outputHeader(totalQty, totalErrors, totalWarnings, totalInfos)
        + outputNotices
        + outputFooter()
        + "<script>\n"
        + scriptJs()
        + "</script>\n"
        + "</body>\n"
        + "</html>\n";
  }

  public static String noticeFrame(
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
