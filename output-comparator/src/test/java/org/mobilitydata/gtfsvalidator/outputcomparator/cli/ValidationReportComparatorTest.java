package org.mobilitydata.gtfsvalidator.outputcomparator.cli;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.outputcomparator.cli.TestHelper.writeFile;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.outputcomparator.cli.ValidationReportComparator.Result;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.SourceUrlContainer;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.AcceptanceReport;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.AffectedSource;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.ChangedNotice;
import org.mobilitydata.gtfsvalidator.outputcomparator.model.report.CorruptedSources;

public class ValidationReportComparatorTest {

  private static final String REFERENCE_REPORT_NAME = "reference.json";
  private static final String LATEST_REPORT_NAME = "latest.json";

  @Rule public final TemporaryFolder reportsDir = new TemporaryFolder();

  @Test
  public void acceptanceReport() throws IOException {
    Arguments args = defaultArgs();

    List<File> reportDirs =
        ImmutableList.of(
            // Drops a warning and adds an error.
            constructBeforeAndAfterReports(
                "feed-id-a",
                ImmutableList.of(new MissingRecommendedFileNotice("b.txt")),
                ImmutableList.of(new MissingRequiredFileNotice("a.txt"))),
            // Drops two errors and adds a warning.
            constructBeforeAndAfterReports(
                "feed-id-b",
                ImmutableList.of(
                    new MissingRequiredFileNotice("a.txt"), new MissingRequiredFileNotice("c.txt")),
                ImmutableList.of(new MissingRecommendedFileNotice("b.txt"))),
            // A "corrupted" source directory with no feeds.
            new File(reportsDir.getRoot(), "feed-id-c"));

    // Note that we don't include feed-id-c here.
    SourceUrlContainer sourceUrlContainer =
        new SourceUrlContainer(
            ImmutableMap.of("feed-id-a", "url-a", "feed-id-b", "url-b", "feed-id-c", "url-c"));

    ValidationReportComparator comparator = new ValidationReportComparator();
    Result result = comparator.compareValidationRuns(args, reportDirs, sourceUrlContainer);

    AcceptanceReport report = result.report();

    assertThat(report.newErrors())
        .containsExactly(
            new ChangedNotice("missing_required_file")
                .addAffectedSource(AffectedSource.create("feed-id-a", "url-a", 1)));
    assertThat(report.droppedErrors())
        .containsExactly(
            new ChangedNotice("missing_required_file")
                .addAffectedSource(AffectedSource.create("feed-id-b", "url-b", 2)));
    assertThat(report.newWarnings())
        .containsExactly(
            new ChangedNotice("missing_recommended_file")
                .addAffectedSource(AffectedSource.create("feed-id-b", "url-b", 1)));
    assertThat(report.droppedWarnings())
        .containsExactly(
            new ChangedNotice("missing_recommended_file")
                .addAffectedSource(AffectedSource.create("feed-id-a", "url-a", 1)));

    CorruptedSources corruptedSources = report.corruptedSources();
    assertThat(corruptedSources.sourceIdCount()).isEqualTo(3);
    assertThat(corruptedSources.corruptedSourcesCount()).isEqualTo(1);
    assertThat(corruptedSources.corruptedSources()).containsExactly("feed-id-c");
  }

  @Test
  public void addedErrorNotice_summaryString() throws Exception {
    Arguments args = defaultArgs();
    args.setNewErrorThreshold(1);
    args.setPercentInvalidDatasetsThreshold(25);

    List<File> reportDirs =
        ImmutableList.of(
            constructBeforeAndAfterReports(
                "feed-id-a",
                ImmutableList.of(new MissingRequiredFileNotice("a.txt")),
                ImmutableList.of(new MissingRequiredFileNotice("a.txt"))),
            // We have a single additional error notice in feed b.
            constructBeforeAndAfterReports(
                "feed-id-b",
                ImmutableList.of(),
                ImmutableList.of(new MissingRequiredFileNotice("a.txt"))));

    ValidationReportComparator comparator = new ValidationReportComparator();
    Result result = comparator.compareValidationRuns(args, reportDirs, constructSourceUrls());

    assertThat(result.reportSummary())
        .isEqualTo(
            "❌ Invalid acceptance test.\n"
                + "New Errors: 1 out of 2 datasets (~50%) are invalid due to code change, which is above the provided threshold of 25%.\n"
                + "Dropped Errors: 0 out of 2 datasets (~0%) are invalid due to code change, which is less than the provided threshold of 25%.\n"
                + "New Warnings: 0 out of 2 datasets (~0%) are invalid due to code change, which is less than the provided threshold of 25%.\n"
                + "Dropped Warnings: 0 out of 2 datasets (~0%) are invalid due to code change, which is less than the provided threshold of 25%.\n"
                + "0 out of 2 sources (~0 %) are corrupted.\n"
                + "❌ Invalid acceptance test.\n");
  }

  @Test
  public void noChangeInNotices_noFailure() throws Exception {
    Arguments args = defaultArgs();
    args.setNewErrorThreshold(1);
    args.setPercentInvalidDatasetsThreshold(25);

    List<File> reportDirs =
        ImmutableList.of(
            constructBeforeAndAfterReports(
                "feed-id-a",
                ImmutableList.of(new MissingRequiredFileNotice("a.txt")),
                ImmutableList.of(new MissingRequiredFileNotice("a.txt"))),
            constructBeforeAndAfterReports("feed-id-b", ImmutableList.of(), ImmutableList.of()));

    ValidationReportComparator comparator = new ValidationReportComparator();
    Result result = comparator.compareValidationRuns(args, reportDirs, constructSourceUrls());

    assertThat(result.failure()).isFalse();
  }

  @Test
  public void addedErrorNotice_aboveThresholds_failure() throws Exception {
    Arguments args = defaultArgs();
    args.setNewErrorThreshold(1);
    args.setPercentInvalidDatasetsThreshold(25);

    List<File> reportDirs =
        ImmutableList.of(
            constructBeforeAndAfterReports(
                "feed-id-a",
                ImmutableList.of(new MissingRequiredFileNotice("a.txt")),
                ImmutableList.of(new MissingRequiredFileNotice("a.txt"))),
            // We have a single additional error notice in feed b.
            constructBeforeAndAfterReports(
                "feed-id-b",
                ImmutableList.of(),
                ImmutableList.of(new MissingRequiredFileNotice("a.txt"))));

    ValidationReportComparator comparator = new ValidationReportComparator();
    Result result = comparator.compareValidationRuns(args, reportDirs, constructSourceUrls());

    // 1 additional error notice is enough to invalidate one dataset, for an invalid dataset
    // threshold of 50%.
    assertThat(result.failure()).isTrue();

    assertThat(result.report().newErrors())
        .containsExactly(
            new ChangedNotice("missing_required_file")
                .addAffectedSource(AffectedSource.create("feed-id-b", "url", 1)));
    assertThat(result.report().droppedErrors()).isEmpty();
    assertThat(result.report().newWarnings()).isEmpty();
    assertThat(result.report().droppedWarnings()).isEmpty();
  }

  @Test
  public void addedErrorNotice_belowNewNoticeThreshold_noFailure() throws Exception {
    Arguments args = defaultArgs();
    args.setNewErrorThreshold(2);
    args.setPercentInvalidDatasetsThreshold(25);

    List<File> reportDirs =
        ImmutableList.of(
            constructBeforeAndAfterReports(
                "feed-id-a",
                ImmutableList.of(new MissingRequiredFileNotice("a.txt")),
                ImmutableList.of(new MissingRequiredFileNotice("a.txt"))),
            // We have a single additional error notice in feed b.
            constructBeforeAndAfterReports(
                "feed-id-b",
                ImmutableList.of(),
                ImmutableList.of(new MissingRequiredFileNotice("a.txt"))));

    ValidationReportComparator comparator = new ValidationReportComparator();
    Result result = comparator.compareValidationRuns(args, reportDirs, constructSourceUrls());

    // 1 additional error notice is NOT enough to invalidate one dataset, for an invalid dataset
    // threshold of 0%.
    assertThat(result.failure()).isFalse();
  }

  @Test
  public void addedErrorNotice_belowInvalidDatasetThreshold_noFailure() throws Exception {
    Arguments args = defaultArgs();
    args.setNewErrorThreshold(1);
    args.setPercentInvalidDatasetsThreshold(75);

    List<File> reportDirs =
        ImmutableList.of(
            constructBeforeAndAfterReports(
                "feed-id-a",
                ImmutableList.of(new MissingRequiredFileNotice("a.txt")),
                ImmutableList.of(new MissingRequiredFileNotice("a.txt"))),
            // We have a single additional error notice in feed b.
            constructBeforeAndAfterReports(
                "feed-id-b",
                ImmutableList.of(),
                ImmutableList.of(new MissingRequiredFileNotice("a.txt"))));

    ValidationReportComparator comparator = new ValidationReportComparator();
    Result result = comparator.compareValidationRuns(args, reportDirs, constructSourceUrls());

    // 1 additional error notice is enough to invalidate one dataset, for an invalid dataset
    // threshold of 50%, which is NOT above the invalid dataset threshold.
    assertThat(result.failure()).isFalse();
  }

  @Test
  public void droppedErrorNotice_aboveThresholds_failure() throws Exception {
    Arguments args = defaultArgs();
    args.setNewErrorThreshold(1);
    args.setPercentInvalidDatasetsThreshold(25);

    List<File> reportDirs =
        ImmutableList.of(
            constructBeforeAndAfterReports(
                "feed-id-a",
                ImmutableList.of(new MissingRequiredFileNotice("a.txt")),
                ImmutableList.of(new MissingRequiredFileNotice("a.txt"))),
            // We have a single dropped error notice in feed b.
            constructBeforeAndAfterReports(
                "feed-id-b",
                ImmutableList.of(new MissingRequiredFileNotice("a.txt")),
                ImmutableList.of()));

    ValidationReportComparator comparator = new ValidationReportComparator();
    Result result = comparator.compareValidationRuns(args, reportDirs, constructSourceUrls());

    // 1 dropped error notice is enough to invalidate one dataset, for an invalid dataset
    // threshold of 50%.
    assertThat(result.failure()).isTrue();
  }

  @Test
  public void addedWarningNotice_aboveThresholds_failure() throws Exception {
    Arguments args = defaultArgs();
    args.setNewErrorThreshold(1);
    args.setPercentInvalidDatasetsThreshold(25);

    List<File> reportDirs =
        ImmutableList.of(
            constructBeforeAndAfterReports(
                "feed-id-a",
                ImmutableList.of(new MissingRequiredFileNotice("a.txt")),
                ImmutableList.of(new MissingRequiredFileNotice("a.txt"))),
            // We have a single dropped error notice in feed b.
            constructBeforeAndAfterReports(
                "feed-id-b",
                ImmutableList.of(),
                ImmutableList.of(new MissingRecommendedFieldNotice("a.txt", 1, "field"))));

    ValidationReportComparator comparator = new ValidationReportComparator();
    Result result = comparator.compareValidationRuns(args, reportDirs, constructSourceUrls());

    // 1 added warning notice is enough to invalidate one dataset, for an invalid dataset
    // threshold of 50%.
    assertThat(result.failure()).isTrue();
  }

  @Test
  public void droppedWarningNotice_aboveThresholds_failure() throws Exception {
    Arguments args = defaultArgs();
    args.setNewErrorThreshold(1);
    args.setPercentInvalidDatasetsThreshold(25);

    List<File> reportDirs =
        ImmutableList.of(
            constructBeforeAndAfterReports(
                "feed-id-a",
                ImmutableList.of(new MissingRequiredFileNotice("a.txt")),
                ImmutableList.of(new MissingRequiredFileNotice("a.txt"))),
            // We have a single dropped error notice in feed b.
            constructBeforeAndAfterReports(
                "feed-id-b",
                ImmutableList.of(new MissingRecommendedFieldNotice("a.txt", 1, "field")),
                ImmutableList.of()));

    ValidationReportComparator comparator = new ValidationReportComparator();
    Result result = comparator.compareValidationRuns(args, reportDirs, constructSourceUrls());

    // 1 dropped warning notice is enough to invalidate one dataset, for an invalid dataset
    // threshold of 50%.
    assertThat(result.failure()).isTrue();
  }

  @Test
  public void corruptedSource_aboveThresholds_failure() throws Exception {
    Arguments args = defaultArgs();
    args.setPercentCorruptedSourcesThreshold(25);

    List<File> reportDirs =
        ImmutableList.of(
            constructBeforeAndAfterReports(
                "feed-id-a",
                ImmutableList.of(new MissingRequiredFileNotice("a.txt")),
                ImmutableList.of(new MissingRequiredFileNotice("a.txt"))),
            // A "corrupted" source directory with no feeds.
            new File(reportsDir.getRoot(), "feed-id-b"));

    SourceUrlContainer sourceUrlContainer =
        new SourceUrlContainer(ImmutableMap.of("feed-id-a", "url-a", "feed-id-b", "url-b"));

    ValidationReportComparator comparator = new ValidationReportComparator();
    Result result = comparator.compareValidationRuns(args, reportDirs, sourceUrlContainer);

    // 1 corrupted source out of 2 = 50% corrupted = above threshold.
    assertThat(result.failure()).isTrue();
  }

  private static Arguments defaultArgs() {
    Arguments args = new Arguments();
    args.setReferenceValidationReportName(REFERENCE_REPORT_NAME);
    args.setLatestValidationReportName(LATEST_REPORT_NAME);
    args.setPercentCorruptedSourcesThreshold(25);
    args.setPercentInvalidDatasetsThreshold(25);
    args.setNewErrorThreshold(1);
    return args;
  }

  private SourceUrlContainer constructSourceUrls() {
    ImmutableMap.Builder<String, String> sourceUrlMapping = ImmutableMap.builder();
    for (File path : reportsDir.getRoot().listFiles()) {
      sourceUrlMapping.put(path.getName(), "url");
    }
    return new SourceUrlContainer(sourceUrlMapping.build());
  }

  private File constructBeforeAndAfterReports(
      String sourceId,
      ImmutableList<ValidationNotice> beforeNotices,
      ImmutableList<ValidationNotice> afterNotices)
      throws IOException {
    Path reportDir = reportsDir.getRoot().toPath().resolve(sourceId);
    writeFile(
        new NoticeContainer().addValidationNotices(beforeNotices).exportValidationNotices(),
        reportDir.resolve(REFERENCE_REPORT_NAME));
    writeFile(
        new NoticeContainer().addValidationNotices(afterNotices).exportValidationNotices(),
        reportDir.resolve(LATEST_REPORT_NAME));
    return reportDir.toFile();
  }
}
