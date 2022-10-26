package org.mobilitydata.gtfsvalidator.processor.tests;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.table.ByteGtfs;
import org.mobilitydata.gtfsvalidator.table.EnumSizes;
import org.mobilitydata.gtfsvalidator.table.IntGtfs;
import org.mobilitydata.gtfsvalidator.table.ShortGtfs;

@RunWith(JUnit4.class)
public class EnumSizesSchemaTest {

  @Test
  public void testByteEnumValid() {
    assertThat(new EnumSizes.Builder().setByteEnum(ByteGtfs.ONE).build().byteEnum())
        .isEqualTo(ByteGtfs.ONE);
    assertThat(new EnumSizes.Builder().setByteEnum(ByteGtfs.MAX_BYTE).build().byteEnum())
        .isEqualTo(ByteGtfs.MAX_BYTE);
    assertThat(new EnumSizes.Builder().setByteEnum(ByteGtfs.MIN_BYTE).build().byteEnum())
        .isEqualTo(ByteGtfs.MIN_BYTE);
  }

  @Test
  public void testByteEnumInvalid() {
    EnumSizes enumSizes = new EnumSizes.Builder().setByteEnum(10).build();
    // 10 is not a valid constant for ByteGtfs.
    assertThat(enumSizes.byteEnum()).isEqualTo(ByteGtfs.UNRECOGNIZED);
  }

  @Test
  public void testShortEnumValid() {
    assertThat(new EnumSizes.Builder().setShortEnum(ShortGtfs.ONE).build().shortEnum())
        .isEqualTo(ShortGtfs.ONE);
    assertThat(new EnumSizes.Builder().setShortEnum(ShortGtfs.MAX_SHORT).build().shortEnum())
        .isEqualTo(ShortGtfs.MAX_SHORT);
    assertThat(new EnumSizes.Builder().setShortEnum(ShortGtfs.MIN_SHORT).build().shortEnum())
        .isEqualTo(ShortGtfs.MIN_SHORT);
  }

  @Test
  public void testShortEnumInvalid() {
    EnumSizes enumSizes = new EnumSizes.Builder().setShortEnum(10).build();
    // 10 is not a valid constant for ShortGtfs.
    assertThat(enumSizes.shortEnum()).isEqualTo(ShortGtfs.UNRECOGNIZED);
  }

  @Test
  public void testIntEnumValid() {
    assertThat(new EnumSizes.Builder().setIntEnum(IntGtfs.ONE).build().intEnum())
        .isEqualTo(IntGtfs.ONE);
    assertThat(new EnumSizes.Builder().setIntEnum(IntGtfs.MAX_INT).build().intEnum())
        .isEqualTo(IntGtfs.MAX_INT);
    assertThat(new EnumSizes.Builder().setIntEnum(IntGtfs.MIN_INT).build().intEnum())
        .isEqualTo(IntGtfs.MIN_INT);
  }

  @Test
  public void testIntEnumInvalid() {
    EnumSizes enumSizes = new EnumSizes.Builder().setIntEnum(10).build();
    // 10 is not a valid constant for ShortGtfs.
    assertThat(enumSizes.intEnum()).isEqualTo(IntGtfs.UNRECOGNIZED);
  }
}
