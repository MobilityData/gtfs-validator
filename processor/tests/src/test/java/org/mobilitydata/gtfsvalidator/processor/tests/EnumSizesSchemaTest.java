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
    // However, 10 can be still retrieved using byteEnumValue method.
    assertThat(enumSizes.byteEnumValue()).isEqualTo(10);
  }

  @Test
  public void testByteEnumOutOfBounds() {
    // Short.MIN_VALUE is out of bounds for byte. It cannot be retrieved with byteEnumValue.
    assertThat(new EnumSizes.Builder().setByteEnum((int) Short.MIN_VALUE).build().byteEnumValue())
        .isEqualTo(0);
    // Short.MAX_VALUE is out of bounds for byte. It cannot be retrieved with byteEnumValue.
    assertThat(new EnumSizes.Builder().setByteEnum((int) Short.MAX_VALUE).build().byteEnumValue())
        .isEqualTo(-1);
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
    // However, 10 can be still retrieved using shortEnumValue method.
    assertThat(enumSizes.shortEnumValue()).isEqualTo(10);
  }

  @Test
  public void testShortEnumOutOfBounds() {
    // Integer.MIN_VALUE is out of bounds for short. It cannot be retrieved with ShortEnumValue.
    assertThat(new EnumSizes.Builder().setShortEnum(Integer.MIN_VALUE).build().shortEnumValue())
        .isEqualTo(0);
    // Integer.MAX_VALUE is out of bounds for short. It cannot be retrieved with ShortEnumValue.
    assertThat(new EnumSizes.Builder().setShortEnum(Integer.MAX_VALUE).build().shortEnumValue())
        .isEqualTo(-1);
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
    // However, 10 can be still retrieved using shortEnumValue method.
    assertThat(enumSizes.intEnumValue()).isEqualTo(10);
  }
}
