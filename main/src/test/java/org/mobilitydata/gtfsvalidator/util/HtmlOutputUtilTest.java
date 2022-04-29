package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class HtmlOutputUtilTest {
  private static final String noticeField1 = "noticeField1";
  private static final String noticeField2 = "noticeField2";
  private static final ArrayList<String> noticeFields =
      new ArrayList<>(List.of(noticeField1, noticeField2));
  private static final ArrayList<String> emptyNoticeFields = new ArrayList<>();
  private static final JsonObject sample = new JsonObject();
  private static final JsonArray sampleNotices = new JsonArray();

  @Test
  public void noticeColumnsBuilderValid() {
    String noticeColumns = HtmlOutputUtil.noticeColumnsBuilder(noticeFields);
    assertThat(noticeColumns)
        .isEqualTo("                <th>noticeField1</th><th>noticeField2</th>\n");
  }

  @Test
  public void noticeColumnsBuilderEmpty() {
    String noticeColumns = HtmlOutputUtil.noticeColumnsBuilder(emptyNoticeFields);
    assertThat(noticeColumns).isEqualTo("                \n");
  }

  @Test
  public void noticeRowsBuilderValid() {
    sample.addProperty(noticeField1, "value1");
    sample.addProperty(noticeField2, "value2");
    sampleNotices.add(sample);
    String noticeRows = HtmlOutputUtil.noticeRowsBuilder(noticeFields, sampleNotices);
    assertThat(noticeRows)
        .isEqualTo(
            "              <tr>\n"
                + "                <td>"
                + "\"value1\""
                + "</td>\n"
                + "                <td>"
                + "\"value2\""
                + "</td>\n"
                + "              </tr>\n");
  }

  @Test
  public void noticeRowsBuilderEmpty() {
    String noticeRows = HtmlOutputUtil.noticeRowsBuilder(noticeFields, sampleNotices);
    assertThat(noticeRows).isEqualTo("");
  }

  @Test
  public void outputHeaderTest() {
    String outputHeader = HtmlOutputUtil.outputHeader(0, 0, 0, 0);
    assertThat(outputHeader)
        .isEqualTo(
            "<h1>VALIDATION REPORT</h1>\n"
                + "<h2>"
                + "0"
                + " notices reported ("
                + "0"
                + " errors, "
                + "0"
                + " warnings, "
                + "0"
                + " infos)"
                + "</h2>\n"
                + "<p>Please visit <a href=\"https://github.com/MobilityData/gtfs-validator/blob/master/docs/NOTICES.md\">NOTICES.md</a> and <a href=\"https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md\">RULES.md</a>.</p>\n"
                + "<table class=\"accordion\">\n"
                + "  <thead>\n"
                + "    <tr>\n"
                + "      <th>Code</th><th>Severity</th><th>Total</th>\n"
                + "    </tr>\n"
                + "  </thead>\n"
                + "  <tbody>\n");
  }

  @Test
  public void outputFooterTest() {
    String outputFooter = HtmlOutputUtil.outputFooter();
    assertThat(outputFooter)
        .isEqualTo(
            "  </tbody>\n"
                + "</table>\n"
                + "<p>This validation report was generated using the <a href=\"https://github.com/MobilityData/gtfs-validator\">GTFS Schedule validator</a>.</p>\n");
  }

  @Test
  public void outputFrameTest() {
    String jqueryDependency = "jqueryDependencyTest";
    String styleCss = "styleCssTest";
    String scriptJs = "scriptJsTest";
    String outputHeader = "outputHeaderTest";
    String outputNotices = "outputNoticesTest";
    String outputFooter = "outputFooterTest";
    String outputFrame =
        HtmlOutputUtil.outputFrame(
            jqueryDependency, styleCss, scriptJs, outputHeader, outputNotices, outputFooter);
    assertThat(outputFrame)
        .isEqualTo(
            "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
                + "<script>\n"
                + "jqueryDependencyTest"
                + "</script>\n"
                + "<style>\n"
                + "styleCssTest"
                + "</style>\n"
                + "</head>\n"
                + "<body>\n"
                + "outputHeaderTest"
                + "outputNoticesTest"
                + "outputFooterTest"
                + "<script>\n"
                + "scriptJsTest"
                + "</script>\n"
                + "</body>\n"
                + "</html>\n");
  }

  @Test
  public void noticeFrameTest() {
    String noticeCode = "noticeCodeTest";
    String noticeSeverity = "TEST";
    int totalNotices = 0;
    String sampleColumns = "sampleColumnsTest";
    String sampleRows = "sampleRowsTest";
    String noticeFrame =
        HtmlOutputUtil.noticeFrame(
            noticeCode, noticeSeverity, totalNotices, sampleColumns, sampleRows);
    assertThat(noticeFrame)
        .isEqualTo(
            "    <tr class=\"notice\">\n"
                + "      <td>"
                + "noticeCodeTest"
                + "</td>\n"
                + "      <td class=\""
                + "test"
                + "\">"
                + "TEST"
                + "</td>\n"
                + "      <td>"
                + "0"
                + "</td>\n"
                + "    </tr>\n"
                + "    <tr class=\"description\">\n"
                + "      <td colspan=\"3\">\n"
                + "        <div class=\"desc-content\">\n"
                + "          <h3>"
                + "noticeCodeTest"
                + "</h3>\n"
                + "          <table>\n"
                + "            <thead>\n"
                + "              <tr>\n"
                + "sampleColumnsTest"
                + "              </tr>\n"
                + "            </thead>\n"
                + "            <tbody>\n"
                + "              <tr>\n"
                + "sampleRowsTest"
                + "            </tbody>\n"
                + "          </table>\n"
                + "        </div>\n"
                + "      </td>\n"
                + "    </tr>\n");
  }

  @Test
  public void outputBuilderTest() {
    String testReport =
        "{\"notices\":[{\"code\":\"missing_required_field\",\"severity\":\"ERROR\",\"totalNotices\":2,\"sampleNotices\":[{\"filename\":\"transfers.txt\",\"csvRowNumber\":2.0,\"fieldName\":\"transfer_type\"},{\"filename\":\"transfers.txt\",\"csvRowNumber\":3.0,\"fieldName\":\"transfer_type\"}]}]}";
    JsonObject noticesJson = new Gson().fromJson(testReport, JsonObject.class);
    String jqueryDependency = "jqueryDependencyTest";
    String styleCss = "styleCssTest";
    String scriptJs = "scriptJsTest";
    String outputResult =
        HtmlOutputUtil.outputBuilder(noticesJson, jqueryDependency, styleCss, scriptJs);
    assertThat(outputResult)
        .isEqualTo(
            "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
                + "<script>\n"
                + "jqueryDependencyTest"
                + "</script>\n"
                + "<style>\n"
                + "styleCssTest"
                + "</style>\n"
                + "</head>\n"
                + "<body>\n"
                + "<h1>VALIDATION REPORT</h1>\n"
                + "<h2>2 notices reported (2 errors, 0 warnings, 0 infos)</h2>\n"
                + "<p>Please visit <a href=\"https://github.com/MobilityData/gtfs-validator/blob/master/docs/NOTICES.md\">NOTICES.md</a> and <a href=\"https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md\">RULES.md</a>.</p>\n"
                + "<table class=\"accordion\">\n"
                + "  <thead>\n"
                + "    <tr>\n"
                + "      <th>Code</th><th>Severity</th><th>Total</th>\n"
                + "    </tr>\n"
                + "  </thead>\n"
                + "  <tbody>\n"
                + "    <tr class=\"notice\">\n"
                + "      <td>missing_required_field</td>\n"
                + "      <td class=\"error\">ERROR</td>\n"
                + "      <td>2</td>\n"
                + "    </tr>\n"
                + "    <tr class=\"description\">\n"
                + "      <td colspan=\"3\">\n"
                + "        <div class=\"desc-content\">\n"
                + "          <h3>missing_required_field</h3>\n"
                + "          <table>\n"
                + "            <thead>\n"
                + "              <tr>\n"
                + "                <th>filename</th><th>csvRowNumber</th><th>fieldName</th>\n"
                + "              </tr>\n"
                + "            </thead>\n"
                + "            <tbody>\n"
                + "              <tr>\n"
                + "              <tr>\n"
                + "                <td>\"transfers.txt\"</td>\n"
                + "                <td>2.0</td>\n"
                + "                <td>\"transfer_type\"</td>\n"
                + "              </tr>\n"
                + "              <tr>\n"
                + "                <td>\"transfers.txt\"</td>\n"
                + "                <td>3.0</td>\n"
                + "                <td>\"transfer_type\"</td>\n"
                + "              </tr>\n"
                + "            </tbody>\n"
                + "          </table>\n"
                + "        </div>\n"
                + "      </td>\n"
                + "    </tr>\n"
                + "  </tbody>\n"
                + "</table>\n"
                + "<p>This validation report was generated using the <a href=\"https://github.com/MobilityData/gtfs-validator\">GTFS Schedule validator</a>.</p>\n"
                + "<script>\n"
                + "scriptJsTest"
                + "</script>\n"
                + "</body>\n"
                + "</html>\n");
  }

  @Test
  public void resourceLoaderTest() {
    String resource = HtmlOutputUtil.resourceLoader(HtmlOutputUtil.SCRIPT_JS);
    assertThat(resource)
        .isEqualTo(
            "$(function(){\n"
                + "  $(\".accordion tr.notice\").on(\"click\", function(){\n"
                + "    $(this).toggleClass(\"open\").next(\".description\").toggleClass(\"open\");\n"
                + "  });\n"
                + "});");
  }
}
