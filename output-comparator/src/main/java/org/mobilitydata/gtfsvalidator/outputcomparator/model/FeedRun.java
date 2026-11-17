package org.mobilitydata.gtfsvalidator.outputcomparator.model;

import com.google.common.flogger.FluentLogger;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * How the acceptance tests validated one feed, as written by scripts/queue_runner.sh next to the
 * feed's reports. The validators do not know about this file.
 */
public class FeedRun {

  /** Name of the file in each feed's report directory. */
  public static final String FILE_NAME = "feed.json";

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final Gson GSON = new Gson();

  /** Size of the downloaded feed zip, or {@code null} when the download failed. */
  private Long zipBytes;

  /**
   * Whether the reference report was reused from an earlier run, on another machine, instead of
   * being produced in the same job as the latest report.
   */
  private boolean masterReused;

  /** The runner label of the job that validated the feed. */
  private String runner;

  /** The number of processors available to the job. */
  private Integer processors;

  /**
   * Reads the file from a feed's report directory. Without the file, as with reports from before it
   * existed, both reports are assumed to come from the same job.
   */
  public static FeedRun fromDirectory(Path reportDirectory) {
    Path path = reportDirectory.resolve(FILE_NAME);
    if (!Files.exists(path)) {
      return new FeedRun();
    }
    try (Reader reader = Files.newBufferedReader(path)) {
      FeedRun feedRun = GSON.fromJson(reader, FeedRun.class);
      return feedRun == null ? new FeedRun() : feedRun;
    } catch (IOException | JsonParseException e) {
      logger.atWarning().withCause(e).log("Error reading %s", path);
      return new FeedRun();
    }
  }

  public Long getZipBytes() {
    return zipBytes;
  }

  public boolean isMasterReused() {
    return masterReused;
  }

  public String getRunner() {
    return runner;
  }

  public Integer getProcessors() {
    return processors;
  }
}
