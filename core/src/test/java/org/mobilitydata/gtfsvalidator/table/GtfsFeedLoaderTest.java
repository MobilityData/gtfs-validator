package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.TestUtils;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ThreadExecutionError;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestEntity;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestEntityValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestSingleFileValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestTableContainer;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestTableDescriptor;
import org.mobilitydata.gtfsvalidator.testgtfs.WholeFeedValidator;
import org.mobilitydata.gtfsvalidator.testing.MockGtfs;
import org.mobilitydata.gtfsvalidator.validator.DefaultValidatorProvider;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;

@RunWith(JUnit4.class)
public class GtfsFeedLoaderTest {
  private static final ImmutableList<Class<?>> VALIDATOR_CLASSES =
      ImmutableList.of(
          GtfsTestEntityValidator.class,
          GtfsTestSingleFileValidator.class,
          WholeFeedValidator.class);

  private MockGtfs mockGtfs;
  private NoticeContainer noticeContainer = new NoticeContainer();

  @Before
  public void before() throws IOException {
    mockGtfs = MockGtfs.create();
  }

  @Test
  public void testEndToEnd() throws Exception {
    mockGtfs.putFileFromLines(GtfsTestEntity.FILENAME, "id,code", "1,alpha");
    GtfsInput input = GtfsInput.createFromPath(mockGtfs.getPath(), noticeContainer);

    ValidatorProvider provider =
        new DefaultValidatorProvider(
            TestUtils.contextForTest(), ValidatorLoader.createForClasses(VALIDATOR_CLASSES));
    NoticeContainer notices = new NoticeContainer();

    GtfsFeedLoader loader = new GtfsFeedLoader(ImmutableList.of(GtfsTestTableDescriptor.class));
    GtfsFeedContainer feedContainer = loader.loadAndValidate(input, provider, notices);

    GtfsTestTableContainer container = feedContainer.getTable(GtfsTestTableContainer.class);
    assertThat(container.getEntities()).hasSize(1);

    GtfsTestEntity entity = container.getEntities().get(0);
    assertThat(entity.id()).isEqualTo("1");
    assertThat(entity.code()).isEqualTo("alpha");
  }

  @Test
  public void testInvalidDataInTable() throws Exception {
    // Missing `id` value in table, which is required.
    mockGtfs.putFileFromLines(GtfsTestEntity.FILENAME, "id,code", ",alpha");
    GtfsInput input = GtfsInput.createFromPath(mockGtfs.getPath(), noticeContainer);

    ValidatorProvider provider =
        new DefaultValidatorProvider(
            TestUtils.contextForTest(), ValidatorLoader.createForClasses(VALIDATOR_CLASSES));
    NoticeContainer notices = new NoticeContainer();

    GtfsFeedLoader loader = new GtfsFeedLoader(ImmutableList.of(GtfsTestTableDescriptor.class));
    GtfsFeedContainer feedContainer = loader.loadAndValidate(input, provider, notices);

    GtfsTestTableContainer container = feedContainer.getTable(GtfsTestTableContainer.class);
    assertThat(container.getEntities()).isEmpty();

    assertThat(
            loader
                .getSkippedValidators()
                .get(GtfsFeedLoader.SkippedValidatorReason.MULTI_FILE_VALIDATORS_WITH_ERROR))
        .containsExactly(WholeFeedValidator.class);
  }

  @Test
  public void loadAndValidate_ioExceptionOnPresentFile_stopsBeforeMultiFileValidators()
      throws Exception {
    assertLoadingFailureStopsValidation(new IOException("cannot read"));
  }

  @Test
  public void loadAndValidate_errorOnPresentFile_stopsBeforeMultiFileValidators() throws Exception {
    assertLoadingFailureStopsValidation(new OutOfMemoryError("Java heap space"));
  }

  @Test
  public void loadAndValidate_absentFile_continuesValidation() throws Exception {
    ValidatorProvider provider =
        spy(
            new DefaultValidatorProvider(
                TestUtils.contextForTest(), ValidatorLoader.createForClasses(VALIDATOR_CLASSES)));
    GtfsFeedLoader loader = new GtfsFeedLoader(ImmutableList.of(GtfsTestTableDescriptor.class));

    GtfsFeedContainer feedContainer =
        loader.loadAndValidate(
            new FailingGtfsInput(ImmutableSet.of(), null), provider, new NoticeContainer());

    assertThat(feedContainer.getTable(GtfsTestTableContainer.class).isMissingFile()).isTrue();
    verify(provider).createMultiFileValidators(any(), any());
  }

  /**
   * A present file whose loader task fails outside {@code load()} gets no table container, so the
   * loader must stop instead of running the multi-file validators with a {@code null} table.
   */
  private void assertLoadingFailureStopsValidation(Throwable failure) throws Exception {
    ValidatorProvider provider =
        spy(
            new DefaultValidatorProvider(
                TestUtils.contextForTest(), ValidatorLoader.createForClasses(VALIDATOR_CLASSES)));
    NoticeContainer notices = new NoticeContainer();
    GtfsFeedLoader loader = new GtfsFeedLoader(ImmutableList.of(GtfsTestTableDescriptor.class));
    GtfsInput input = new FailingGtfsInput(ImmutableSet.of(GtfsTestEntity.FILENAME), failure);

    FeedLoadingException e =
        assertThrows(
            FeedLoadingException.class, () -> loader.loadAndValidate(input, provider, notices));

    assertThat(e.getFailedFiles()).containsExactly(GtfsTestEntity.FILENAME);
    assertThat(e).hasCauseThat().isSameInstanceAs(failure);
    assertThat(notices.getSystemErrors()).hasSize(1);
    assertThat(notices.getSystemErrors().get(0)).isInstanceOf(ThreadExecutionError.class);
    verify(provider, never()).createMultiFileValidators(any(), any());
  }

  /** A {@link GtfsInput} listing the given files, whose streams fail with {@code failure}. */
  private static class FailingGtfsInput extends GtfsInput {
    private final ImmutableSet<String> filenames;
    private final Throwable failure;

    FailingGtfsInput(ImmutableSet<String> filenames, Throwable failure) {
      this.filenames = filenames;
      this.failure = failure;
    }

    @Override
    public ImmutableSet<String> getFilenames() {
      return filenames;
    }

    @Override
    public InputStream getFile(String filename) throws IOException {
      if (failure instanceof IOException) {
        throw (IOException) failure;
      }
      throw (Error) failure;
    }

    @Override
    public void close() {}
  }
}
