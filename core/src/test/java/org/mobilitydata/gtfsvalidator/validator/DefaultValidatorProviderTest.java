package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.TestUtils;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer.TableStatus;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestEntity;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestEntityValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestMultiFileValidator;
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

    GtfsTestTableContainer tableContainer =
        new GtfsTestTableContainer(TableStatus.PARSABLE_HEADERS_AND_ROWS);
    GtfsFeedContainer feedContainer = new GtfsFeedContainer(ImmutableList.of(tableContainer));
    List<Class<? extends SingleEntityValidator>> singleEntityValidatorsWithParsingErrors =
        new ArrayList<>();
    assertThat(
            provider
                .createSingleEntityValidators(
                    GtfsTestEntity.class, singleEntityValidatorsWithParsingErrors::add)
                .stream()
                .map(Object::getClass))
        .containsExactly(GtfsTestEntityValidator.class);

    List<Class<? extends FileValidator>> singleFileValidatorsWithParsingErrors = new ArrayList<>();
    assertThat(
            provider
                .createSingleFileValidators(
                    tableContainer, singleFileValidatorsWithParsingErrors::add)
                .stream()
                .map(Object::getClass))
        .containsExactly(GtfsTestSingleFileValidator.class);

    List<Class<? extends FileValidator>> skippedValidators = new ArrayList<>();
    assertThat(
            provider.createMultiFileValidators(feedContainer, skippedValidators::add).stream()
                .map(Object::getClass))
        .containsExactly(WholeFeedValidator.class);
    assertThat(skippedValidators).isEmpty();
  }

  @Test
  public void testCreateValidators_skippedValidators() throws ValidatorLoaderException {
    DefaultValidatorProvider provider =
        new DefaultValidatorProvider(
            TestUtils.contextForTest(),
            ValidatorLoader.createForClasses(
                ImmutableList.of(
                    GtfsTestEntityValidator.class,
                    GtfsTestMultiFileValidator.class,
                    GtfsTestSingleFileValidator.class,
                    WholeFeedValidator.class)));

    // Create 2 tables, one with errors and the other not.
    // This will let us test the multi-file validator.
    GtfsTestTableContainer tableContainer = new GtfsTestTableContainer(TableStatus.UNPARSABLE_ROWS);
    GtfsTestTableContainer2 tableContainer2 =
        new GtfsTestTableContainer2(TableStatus.PARSABLE_HEADERS_AND_ROWS);

    GtfsFeedContainer feedContainer =
        new GtfsFeedContainer(ImmutableList.of(tableContainer, tableContainer2));

    List<Class<? extends FileValidator>> skippedValidators = new ArrayList<>();
    // First test the multi file validators. Apparently the FeedContainerValidator is considered a
    // multi-file validator.
    // We should not be able to create any validator since the dependant file container has parsing
    // errors. For the WholeFeedValidator the feedContainer is also in error since one of its
    // file is in error.
    assertThat(provider.createMultiFileValidators(feedContainer, skippedValidators::add)).isEmpty();
    // And the 2 validators should be skipped
    assertThat(skippedValidators)
        .containsExactly(WholeFeedValidator.class, GtfsTestMultiFileValidator.class);

    skippedValidators.clear();
    // Try with the single file validator.  We should not be able to build any validator since the
    // file has errors.
    assertThat(provider.createSingleFileValidators(tableContainer, skippedValidators::add))
        .isEmpty();
    // And it should tell us that the single file validator was skipped
    assertThat(skippedValidators).containsExactly(GtfsTestSingleFileValidator.class);
  }
}
