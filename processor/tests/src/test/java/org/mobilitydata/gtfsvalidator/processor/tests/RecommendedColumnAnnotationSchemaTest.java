package org.mobilitydata.gtfsvalidator.processor.tests;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredColumnNotice;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

@RunWith(JUnit4.class)
public class RecommendedColumnAnnotationSchemaTest {
  private RecommendedColumnAnnotationTableDescriptor tableDescriptor;
  private LoadingHelper helper;

  @Before
  public void setup() throws ValidatorLoaderException {
    tableDescriptor = new RecommendedColumnAnnotationTableDescriptor();
    helper = new LoadingHelper();
  }

  @Test
  public void includingRecommendedColumnHeaderWithoutValueShouldNotGenerateNotice()
      throws ValidatorLoaderException {

    helper.load(tableDescriptor, "some_column,column_recommended", "value,");

    assertThat(
        !helper
            .getValidationNotices()
            .contains(
                new MissingRequiredColumnNotice("recommended_column.txt", "column_recommended")));
  }

  @Test
  public void missingRecommendedColumnHeaderShouldGenerateNotice() throws ValidatorLoaderException {

    helper.load(tableDescriptor, "column", "value");
    // Since we use an unknown column ("column") we have to expect at least one unknown_column
    // notice along with the
    // missing_recommended_column notice.
    assertThat(
        helper
            .getValidationNotices()
            .contains(
                new MissingRequiredColumnNotice("recommended_column.txt", "column_recommended")));
  }
}
