package org.mobilitydata.gtfsvalidator.processor.tests;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredColumnNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

@RunWith(JUnit4.class)
public class RequiredAnnotationSchemaTest {

  private RequiredAnnotationTableDescriptor tableDescriptor;
  private LoadingHelper helper;

  @Before
  public void setup() throws ValidatorLoaderException {
    tableDescriptor = new RequiredAnnotationTableDescriptor();
    helper = new LoadingHelper();
  }

  @Test
  public void includingRequiredColumnHeaderWithValueShouldNotGenerateNotice()
      throws ValidatorLoaderException {

    helper.load(tableDescriptor, "value_required,value_not_required", "value1,value2");

    assertThat(helper.getValidationNotices()).isEmpty();
  }

  @Test
  public void includingRequiredColumnHeaderWithoutValueShouldGenerateNotice()
      throws ValidatorLoaderException {

    helper.load(tableDescriptor, "value_required,value_not_required", ",value2");
    assertThat(helper.getValidationNotices())
        .containsExactly(new MissingRequiredFieldNotice("required.txt", 2, "value_required"));
  }

  @Test
  public void missingColumnHeaderForRequiredColumnShouldGenerateNotice()
      throws ValidatorLoaderException {

    helper.load(tableDescriptor, "value_not_required", "value2");
    assertThat(helper.getValidationNotices())
        .containsExactly(new MissingRequiredColumnNotice("required.txt", "value_required"));
  }
}
