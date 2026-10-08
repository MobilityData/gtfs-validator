/*
 * Copyright 2026 MobilityData
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.reportsummary;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.reportsummary.model.FeedMetadata;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.table.GtfsEntityContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;

@RunWith(JUnit4.class)
public class HtmlReportGeneratorTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  /**
   * The page must report how many notices the feed produced, not how many the container kept: the
   * container drops notices of a type past its retention limit, and the JSON report generated from
   * the same run reports the exact number.
   */
  @Test
  public void generateReport_countsAreExactWhenTheContainerDroppedNotices() throws IOException {
    NoticeContainer noticeContainer = new NoticeContainer(1_000, 100, 100);
    for (int i = 1; i <= 300; i++) {
      noticeContainer.addValidationNotice(
          new MissingRequiredFieldNotice("test.txt", i, String.format("field_%03d", i)));
    }

    String report = visibleText(generateReport(noticeContainer));

    assertThat(noticeContainer.getValidationNotices()).hasSize(100);
    assertThat(report).contains("300 notices reported ( 300 errors, 0 warnings, 0 infos)");
    assertThat(report).contains("missing_required_field ERROR 300");
    assertThat(report).contains("Only the first 50 of 300 affected records are displayed below.");
  }

  /** Strips the markup so that assertions read like the rendered page. */
  private static String visibleText(String html) {
    return html.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ");
  }

  private String generateReport(NoticeContainer noticeContainer) throws IOException {
    GtfsFeedContainer feedContainer =
        new GtfsFeedContainer(
            ImmutableList.<GtfsEntityContainer<?, ?>>of(
                GtfsStopTimeTableContainer.forEntities(ImmutableList.of(), new NoticeContainer())));
    Path reportPath = temporaryFolder.newFile("report.html").toPath();
    ValidationRunnerConfig config =
        ValidationRunnerConfig.builder()
            .setGtfsSource(Path.of("feed.zip").toUri())
            .setOutputDirectory(reportPath.getParent())
            .setCountryCode(CountryCode.forStringOrUnknown(""))
            .build();

    new HtmlReportGenerator()
        .generateReport(
            FeedMetadata.from(feedContainer, ImmutableSet.of("stop_times.txt")),
            noticeContainer,
            config,
            VersionInfo.empty(),
            reportPath,
            "2026-01-01T00:00:00+00:00",
            false);

    return Files.readString(reportPath);
  }
}
