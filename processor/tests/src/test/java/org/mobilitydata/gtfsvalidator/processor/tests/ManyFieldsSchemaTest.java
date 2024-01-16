package org.mobilitydata.gtfsvalidator.processor.tests;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ManyFieldsSchemaTest {
  @Test
  public void testHasField() {
    ManyFields manyFields = ManyFields.builder().setField2(2).setField8(9).build();

    assertThat(manyFields.hasField1()).isFalse();
    assertThat(manyFields.hasField2()).isTrue();
    assertThat(manyFields.hasField8()).isTrue();

    assertThat(manyFields.field1()).isEqualTo(0);
    assertThat(manyFields.field2()).isEqualTo(2);
    assertThat(manyFields.field8()).isEqualTo(9);
  }
}
