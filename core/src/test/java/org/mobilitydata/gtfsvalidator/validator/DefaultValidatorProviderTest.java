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
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestFileValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestTableContainer;
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
                    GtfsTestFileValidator.class,
                    WholeFeedValidator.class)));

    GtfsTestTableContainer tableContainer =
        new GtfsTestTableContainer(TableStatus.PARSABLE_HEADERS_AND_ROWS);
    GtfsFeedContainer feedContainer = new GtfsFeedContainer(ImmutableList.of(tableContainer));

    assertThat(
            provider.createSingleEntityValidators(GtfsTestEntity.class).stream()
                .map(Object::getClass))
        .containsExactly(GtfsTestEntityValidator.class);

    assertThat(provider.createSingleFileValidators(tableContainer).stream().map(Object::getClass))
        .containsExactly(GtfsTestFileValidator.class);

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
                    GtfsTestFileValidator.class,
                    WholeFeedValidator.class)));

    // Our table had invalid data!
    GtfsTestTableContainer tableContainer = new GtfsTestTableContainer(TableStatus.UNPARSABLE_ROWS);
    GtfsFeedContainer feedContainer = new GtfsFeedContainer(ImmutableList.of(tableContainer));

    List<Class<? extends FileValidator>> skippedValidators = new ArrayList<>();
    assertThat(provider.createMultiFileValidators(feedContainer, skippedValidators::add)).isEmpty();
    assertThat(skippedValidators).containsExactly(WholeFeedValidator.class);
  }
}
