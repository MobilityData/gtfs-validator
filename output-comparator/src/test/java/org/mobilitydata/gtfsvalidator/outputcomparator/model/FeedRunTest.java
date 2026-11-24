package org.mobilitydata.gtfsvalidator.outputcomparator.model;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FeedRunTest {

  @Rule public final TemporaryFolder reportDir = new TemporaryFolder();

  private Path writeFeedRun(String json) throws IOException {
    Path dir = reportDir.getRoot().toPath();
    Files.write(dir.resolve(FeedRun.FILE_NAME), json.getBytes(StandardCharsets.UTF_8));
    return dir;
  }

  @Test
  public void readsFileWrittenByQueueRunner() throws IOException {
    FeedRun feedRun =
        FeedRun.fromDirectory(
            writeFeedRun(
                "{\"zipBytes\":12345,\"masterReused\":true,\"runner\":\"ubuntu-24.04\","
                    + "\"processors\":4}"));

    assertThat(feedRun.getZipBytes()).isEqualTo(12345L);
    assertThat(feedRun.isMasterReused()).isTrue();
    assertThat(feedRun.getRunner()).isEqualTo("ubuntu-24.04");
    assertThat(feedRun.getProcessors()).isEqualTo(4);
  }

  @Test
  public void failedDownload_hasNoSize() throws IOException {
    FeedRun feedRun =
        FeedRun.fromDirectory(
            writeFeedRun(
                "{\"zipBytes\":null,\"masterReused\":false,\"runner\":\"\",\"processors\":4}"));

    assertThat(feedRun.getZipBytes()).isNull();
    assertThat(feedRun.isMasterReused()).isFalse();
  }

  @Test
  public void missingFile_assumesSameJob() {
    FeedRun feedRun = FeedRun.fromDirectory(reportDir.getRoot().toPath());

    assertThat(feedRun.isMasterReused()).isFalse();
    assertThat(feedRun.getZipBytes()).isNull();
  }

  @Test
  public void malformedFile_assumesSameJob() throws IOException {
    FeedRun feedRun = FeedRun.fromDirectory(writeFeedRun("{not json"));

    assertThat(feedRun.isMasterReused()).isFalse();
  }
}
