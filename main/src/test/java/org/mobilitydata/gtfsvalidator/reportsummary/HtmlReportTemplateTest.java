package org.mobilitydata.gtfsvalidator.reportsummary;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import org.junit.Test;

public class HtmlReportTemplateTest {

  @Test
  public void reportUsesOriginalGtfsSourceWhenAvailable() throws Exception {
    String template;
    try (var stream = getClass().getClassLoader().getResourceAsStream("report.html")) {
      if (stream == null) {
        throw new IllegalStateException("report.html not found");
      }
      template = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }

    assertTrue(template.contains("${config.displayGtfsSource}"));
    assertFalse(template.contains("${config.gtfsSource}"));
  }
}
