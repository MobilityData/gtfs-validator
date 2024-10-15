package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader.SkippedValidatorReason.*;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.TestUtils;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader;
import org.mobilitydata.gtfsvalidator.table.TableStatus;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestEntity;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestEntitySkippedValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestEntityValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestMultiFileSkippedValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestMultiFileValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestSingleFileSkippedValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestSingleFileValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestTableContainer;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestTableContainer2;
import org.mobilitydata.gtfsvalidator.testgtfs.WholeFeedValidator;

@RunWith(JUnit4.class)
public class DefaultValidatorProviderTest {

  @Test
  public void testCreateValidators() throws ValidatorLoaderException {
    DefaultValidatorProvider provider =
        new DefaultValidatorProvider(
            TestUtils.contextForTest(),
            ValidatorLoader.createForClasses(
                ImmutableList.of(
                    GtfsTestEntityValidator.class,
                    GtfsTestSingleFileValidator.class,
                    WholeFeedValidator.class)));

    Multimap<GtfsFeedLoader.SkippedValidatorReason, Class<?>> skippedValidators =
        ArrayListMultimap.create();
    GtfsTestTableContainer tableContainer =
        new GtfsTestTableContainer(TableStatus.PARSABLE_HEADERS_AND_ROWS);
    GtfsFeedContainer feedContainer = new GtfsFeedContainer(ImmutableList.of(tableContainer));
    assertThat(
            provider
                .createSingleEntityValidators(GtfsTestEntity.class, null, skippedValidators)
                .stream()
                .map(Object::getClass))
        .containsExactly(GtfsTestEntityValidator.class);
    assertThat(skippedValidators.get(SINGLE_ENTITY_VALIDATORS_WITH_ERROR)).isEmpty();
    assertThat(
            provider.createSingleFileValidators(tableContainer, skippedValidators).stream()
                .map(Object::getClass))
        .containsExactly(GtfsTestSingleFileValidator.class);
    assertThat(skippedValidators.get(SINGLE_FILE_VALIDATORS_WITH_ERROR)).isEmpty();
    assertThat(
            provider.createMultiFileValidators(feedContainer, skippedValidators).stream()
                .map(Object::getClass))
        .containsExactly(WholeFeedValidator.class);
    assertThat(skippedValidators.get(MULTI_FILE_VALIDATORS_WITH_ERROR)).isEmpty();
  }

  @Test
  public void testCreateValidators_skippedValidatorsWithErrors() throws ValidatorLoaderException {
    DefaultValidatorProvider provider =
        new DefaultValidatorProvider(
            TestUtils.contextForTest(),
            ValidatorLoader.createForClasses(
                ImmutableList.of(
                    GtfsTestEntityValidator.class,
                    GtfsTestMultiFileValidator.class,
                    GtfsTestSingleFileValidator.class,
                    WholeFeedValidator.class)));

    Multimap<GtfsFeedLoader.SkippedValidatorReason, Class<?>> skippedValidators =
        ArrayListMultimap.create();
    // Create 2 tables, one with errors and the other not.
    // This will let us test the multi-file validator.
    GtfsTestTableContainer tableContainer = new GtfsTestTableContainer(TableStatus.UNPARSABLE_ROWS);
    GtfsTestTableContainer2 tableContainer2 =
        new GtfsTestTableContainer2(TableStatus.PARSABLE_HEADERS_AND_ROWS);

    GtfsFeedContainer feedContainer =
        new GtfsFeedContainer(ImmutableList.of(tableContainer, tableContainer2));

    // First test the multi file validators. Apparently the FeedContainerValidator is considered a
    // multi-file validator.
    // We should not be able to create any validator since the dependant file container has parsing
    // errors. For the WholeFeedValidator the feedContainer is also in error since one of its
    // file is in error.
    assertThat(provider.createMultiFileValidators(feedContainer, skippedValidators)).isEmpty();
    // And the 2 validators should be skipped
    assertThat(skippedValidators.get(MULTI_FILE_VALIDATORS_WITH_ERROR))
        .containsExactly(WholeFeedValidator.class, GtfsTestMultiFileValidator.class);

    skippedValidators.clear();
    // Try with the single file validator.  We should not be able to build any validator since the
    // file has errors.
    assertThat(provider.createSingleFileValidators(tableContainer, skippedValidators)).isEmpty();
    // And it should tell us that the single file validator was skipped
    assertThat(skippedValidators.get(SINGLE_FILE_VALIDATORS_WITH_ERROR))
        .containsExactly(GtfsTestSingleFileValidator.class);
  }

  @Test
  public void testCreateValidators_skippedNotRun() throws ValidatorLoaderException {
    DefaultValidatorProvider provider =
        new DefaultValidatorProvider(
            TestUtils.contextForTest(),
            ValidatorLoader.createForClasses(
                ImmutableList.of(
                    GtfsTestEntitySkippedValidator.class,
                    GtfsTestSingleFileSkippedValidator.class,
                    GtfsTestMultiFileSkippedValidator.class)));

    Multimap<GtfsFeedLoader.SkippedValidatorReason, Class<?>> skippedValidators =
        ArrayListMultimap.create();
    GtfsTestTableContainer tableContainer =
        new GtfsTestTableContainer(TableStatus.PARSABLE_HEADERS_AND_ROWS);
    GtfsFeedContainer feedContainer = new GtfsFeedContainer(ImmutableList.of(tableContainer));
    assertThat(provider.createSingleEntityValidators(GtfsTestEntity.class, null, skippedValidators))
        .isEmpty();
    assertThat(skippedValidators.get(VALIDATORS_NO_NEED_TO_RUN))
        .containsExactly(GtfsTestEntitySkippedValidator.class);
    skippedValidators.clear();
    assertThat(provider.createSingleFileValidators(tableContainer, skippedValidators)).isEmpty();
    assertThat(skippedValidators.get(VALIDATORS_NO_NEED_TO_RUN))
        .containsExactly(GtfsTestSingleFileSkippedValidator.class);
    skippedValidators.clear();
    assertThat(provider.createMultiFileValidators(feedContainer, skippedValidators)).isEmpty();
    assertThat(skippedValidators.get(VALIDATORS_NO_NEED_TO_RUN))
        .containsExactly(GtfsTestMultiFileSkippedValidator.class);
  }
}
