package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.TestUtils;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestEntity;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestEntityValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestFileValidator;
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
          GtfsTestEntityValidator.class, GtfsTestFileValidator.class, WholeFeedValidator.class);

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

    assertThat(loader.getMultiFileValidatorsWithParsingErrors())
        .containsExactly(WholeFeedValidator.class);
  }
}
