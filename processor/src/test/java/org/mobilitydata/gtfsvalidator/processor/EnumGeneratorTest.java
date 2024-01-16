package org.mobilitydata.gtfsvalidator.processor;

import static com.google.common.truth.Truth.assertThat;

import com.squareup.javapoet.TypeName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class EnumGeneratorTest {
  @Test
  public void testGetMinMaxValue() {
    GtfsEnumDescriptor.Builder builder =
        GtfsEnumDescriptor.builder()
            .setName("MyEnum")
            .setPackageName("")
            .setFieldType(TypeName.INT);
    builder
        .valuesBuilder()
        .add(GtfsEnumValueDescriptor.create("STOP", 0))
        .add(GtfsEnumValueDescriptor.create("ENTRANCE", 2))
        .add(GtfsEnumValueDescriptor.create("STATION", 1));
    GtfsEnumDescriptor enumDescriptor = builder.build();

    assertThat(EnumGenerator.getMinUnrecognizedValue(enumDescriptor)).isEqualTo(-1);
    assertThat(EnumGenerator.getMaxValue(enumDescriptor)).isEqualTo(2);
  }
}
