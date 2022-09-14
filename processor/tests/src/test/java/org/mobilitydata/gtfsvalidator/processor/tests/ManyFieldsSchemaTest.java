package org.mobilitydata.gtfsvalidator.processor.tests;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.table.ManyFields;

@RunWith(JUnit4.class)
public class ManyFieldsSchemaTest {
  @Test
  public void testHasField() {
    ManyFields manyFields = new ManyFields.Builder().setField2(2).setField9(9).build();

    assertThat(manyFields.hasField1()).isFalse();
    assertThat(manyFields.hasField2()).isTrue();
    assertThat(manyFields.hasField9()).isTrue();
    assertThat(manyFields.hasField10()).isFalse();

    assertThat(manyFields.field1()).isEqualTo(0);
    assertThat(manyFields.field2()).isEqualTo(2);
    assertThat(manyFields.field9()).isEqualTo(9);
    assertThat(manyFields.field10()).isEqualTo(0);
  }
}
