package org.mobilitydata.gtfsvalidator.processor.tests;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class EnumSizesSchemaTest {

  @Test
  public void testByteEnumValid() {
    assertThat(EnumSizes.builder().setByteEnum(ByteGtfs.ONE).build().byteEnum())
        .isEqualTo(ByteGtfs.ONE);
    assertThat(EnumSizes.builder().setByteEnum(ByteGtfs.MAX_BYTE).build().byteEnum())
        .isEqualTo(ByteGtfs.MAX_BYTE);
    assertThat(EnumSizes.builder().setByteEnum(ByteGtfs.MIN_BYTE).build().byteEnum())
        .isEqualTo(ByteGtfs.MIN_BYTE);
  }

  @Test
  public void testByteEnumInvalid() {
    EnumSizes enumSizes = EnumSizes.builder().setByteEnum(10).build();
    // 10 is not a valid constant for ByteGtfs.
    assertThat(enumSizes.byteEnum()).isEqualTo(ByteGtfs.UNRECOGNIZED);
  }

  @Test
  public void testShortEnumValid() {
    assertThat(EnumSizes.builder().setShortEnum(ShortGtfs.ONE).build().shortEnum())
        .isEqualTo(ShortGtfs.ONE);
    assertThat(EnumSizes.builder().setShortEnum(ShortGtfs.MAX_SHORT).build().shortEnum())
        .isEqualTo(ShortGtfs.MAX_SHORT);
    assertThat(EnumSizes.builder().setShortEnum(ShortGtfs.MIN_SHORT).build().shortEnum())
        .isEqualTo(ShortGtfs.MIN_SHORT);
  }

  @Test
  public void testShortEnumInvalid() {
    EnumSizes enumSizes = EnumSizes.builder().setShortEnum(10).build();
    // 10 is not a valid constant for ShortGtfs.
    assertThat(enumSizes.shortEnum()).isEqualTo(ShortGtfs.UNRECOGNIZED);
  }

  @Test
  public void testIntEnumValid() {
    assertThat(EnumSizes.builder().setIntEnum(IntGtfs.ONE).build().intEnum())
        .isEqualTo(IntGtfs.ONE);
    assertThat(EnumSizes.builder().setIntEnum(IntGtfs.MAX_INT).build().intEnum())
        .isEqualTo(IntGtfs.MAX_INT);
    assertThat(EnumSizes.builder().setIntEnum(IntGtfs.MIN_INT).build().intEnum())
        .isEqualTo(IntGtfs.MIN_INT);
  }

  @Test
  public void testIntEnumInvalid() {
    EnumSizes enumSizes = EnumSizes.builder().setIntEnum(10).build();
    // 10 is not a valid constant for ShortGtfs.
    assertThat(enumSizes.intEnum()).isEqualTo(IntGtfs.UNRECOGNIZED);
  }
}
