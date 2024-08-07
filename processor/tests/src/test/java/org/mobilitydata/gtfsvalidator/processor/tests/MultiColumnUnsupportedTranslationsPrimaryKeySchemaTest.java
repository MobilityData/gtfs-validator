package org.mobilitydata.gtfsvalidator.processor.tests;

import static com.google.common.truth.Truth8.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

@RunWith(JUnit4.class)
public class MultiColumnUnsupportedTranslationsPrimaryKeySchemaTest {
  private MultiColumnUnsupportedTranslationsPrimaryKeyTableDescriptor tableDescriptor;

  private LoadingHelper helper;

  @Before
  public void setup() {
    tableDescriptor = new MultiColumnUnsupportedTranslationsPrimaryKeyTableDescriptor();
    helper = new LoadingHelper();
  }

  @Test
  public void testNoTranslationSupport() throws ValidatorLoaderException {
    MultiColumnUnsupportedTranslationsPrimaryKeyTableContainer container =
        helper.load(tableDescriptor, "id_a,id_b,id_c", "a1,b1,c1", ",,");

    // byTranslationKey should return empty for all these calls because none of the primary keys
    // have translation record id annotations.
    assertThat(container.byTranslationKey("a1", "b1")).isEmpty();
    assertThat(container.byTranslationKey("", "")).isEmpty();
  }
}
