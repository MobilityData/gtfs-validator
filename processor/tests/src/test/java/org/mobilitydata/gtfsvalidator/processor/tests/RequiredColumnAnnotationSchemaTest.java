package org.mobilitydata.gtfsvalidator.processor.tests;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredColumnNotice;
import org.mobilitydata.gtfsvalidator.table.RequiredColumnAnnotationTableDescriptor;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

@RunWith(JUnit4.class)
public class RequiredColumnAnnotationSchemaTest {
  private RequiredColumnAnnotationTableDescriptor tableDescriptor;
  private LoadingHelper helper;

  @Before
  public void setup() throws ValidatorLoaderException {
    tableDescriptor = new RequiredColumnAnnotationTableDescriptor();
    helper = new LoadingHelper();
  }

  @Test
  public void includingRequiredColumnHeaderWithValueShouldNotGenerateNotice()
      throws ValidatorLoaderException {

    helper.load(tableDescriptor, "value_required,column_required", "value,value2");

    assertThat(helper.getValidationNotices()).isEmpty();
  }

  @Test
  public void includingRequiredColumnHeaderWithoutValueShouldNotGenerateNotice()
      throws ValidatorLoaderException {

    helper.load(tableDescriptor, "value_required,column_required", "value,");

    assertThat(helper.getValidationNotices()).isEmpty();
  }

  @Test
  public void missingRequiredColumnHeaderShouldGenerateNotice() throws ValidatorLoaderException {

    helper.load(tableDescriptor, "value_required", "value");
    assertThat(helper.getValidationNotices())
        .containsExactly(new MissingRequiredColumnNotice("required_column.txt", "column_required"));
  }
}
