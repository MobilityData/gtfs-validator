package org.mobilitydata.gtfsvalidator.processor.tests;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Locale;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.columns.GtfsColumnStore;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

public class ManyTypesColumnBasedTest {

  private final GtfsColumnStore store = new GtfsColumnStore();

  @Test
  public void testBuilder() {
    ManyTypesColumnBased.Builder builder = new ManyTypesColumnBased.Builder(store);

    {
      builder.clear();
      ManyTypesColumnBased entity =
          builder
              .setCsvRowNumber(1)
              .setIntValue(2)
              .setDoubleValue(3.14)
              .setBigDecimalValue(BigDecimal.TEN)
              .setCurrencyValue(Currency.getInstance("USD"))
              .setColorValue(GtfsColor.fromString("ff00ff"))
              .setDateValue(GtfsDate.fromString("20240416"))
              .setTimeValue(GtfsTime.fromString("05:34:00"))
              .setLocaleValue(Locale.CANADA)
              .setZoneIdValue(ZoneId.of("America/New_York"))
              .build();

      assertThat(entity.csvRowNumber()).isEqualTo(1);
      assertThat(entity.intValue()).isEqualTo(2);
      assertThat(entity.doubleValue()).isEqualTo(3.14);
      assertThat(entity.bigDecimalValue()).isEqualTo(BigDecimal.TEN);
      assertThat(entity.currencyValue()).isEqualTo(Currency.getInstance("USD"));
      assertThat(entity.colorValue()).isEqualTo(GtfsColor.fromString("ff00ff"));
      assertThat(entity.dateValue()).isEqualTo(GtfsDate.fromString("20240416"));
      assertThat(entity.timeValue()).isEqualTo(GtfsTime.fromString("05:34:00"));
      assertThat(entity.localeValue()).isEqualTo(Locale.CANADA);
      assertThat(entity.zoneIdValue()).isEqualTo(ZoneId.of("America/New_York"));
    }

    {
      builder.clear();
      ManyTypesColumnBased entity =
          builder
              .setCsvRowNumber(2)
              .setIntValue(3)
              .setDoubleValue(1.5)
              .setBigDecimalValue(BigDecimal.ZERO)
              .setCurrencyValue(Currency.getInstance("CAD"))
              .setColorValue(GtfsColor.fromString("00ff00"))
              .setDateValue(GtfsDate.fromString("20240401"))
              .setTimeValue(GtfsTime.fromString("06:34:00"))
              .setLocaleValue(Locale.FRANCE)
              .setZoneIdValue(ZoneId.of("America/Los_Angeles"))
              .build();

      assertThat(entity.csvRowNumber()).isEqualTo(2);
      assertThat(entity.intValue()).isEqualTo(3);
      assertThat(entity.doubleValue()).isEqualTo(1.5);
      assertThat(entity.bigDecimalValue()).isEqualTo(BigDecimal.ZERO);
      assertThat(entity.currencyValue()).isEqualTo(Currency.getInstance("CAD"));
      assertThat(entity.colorValue()).isEqualTo(GtfsColor.fromString("00ff00"));
      assertThat(entity.dateValue()).isEqualTo(GtfsDate.fromString("20240401"));
      assertThat(entity.timeValue()).isEqualTo(GtfsTime.fromString("06:34:00"));
      assertThat(entity.localeValue()).isEqualTo(Locale.FRANCE);
      assertThat(entity.zoneIdValue()).isEqualTo(ZoneId.of("America/Los_Angeles"));
    }

    {
      ManyTypesColumnBased entity = ManyTypesColumnBased.create(store, builder.getAssignments(), 0);
      assertThat(entity.intValue()).isEqualTo(2);
    }

    {
      ManyTypesColumnBased entity = ManyTypesColumnBased.create(store, builder.getAssignments(), 1);
      assertThat(entity.intValue()).isEqualTo(3);
    }
  }

  @Test
  public void testHasValue() {
    ManyTypesColumnBased.Builder builder = new ManyTypesColumnBased.Builder(store);

    {
      //  No fields set.
      builder.clear();
      ManyTypesColumnBased entity = builder.build();

      assertThat(entity.hasIntValue()).isFalse();
      assertThat(entity.hasDoubleValue()).isFalse();
      assertThat(entity.hasBigDecimalValue()).isFalse();
      assertThat(entity.hasCurrencyValue()).isFalse();
      assertThat(entity.hasColorValue()).isFalse();
      assertThat(entity.hasDateValue()).isFalse();
      assertThat(entity.hasTimeValue()).isFalse();
      assertThat(entity.hasLocaleValue()).isFalse();
      assertThat(entity.hasZoneIdValue()).isFalse();
    }

    {
      // Some fields set.
      builder.clear();
      builder.setDoubleValue(1.0);
      builder.setCurrencyValue(Currency.getInstance("USD"));
      builder.setDateValue(GtfsDate.fromString("20240416"));
      builder.setLocaleValue(Locale.CANADA);
      ManyTypesColumnBased entity = builder.build();

      assertThat(entity.hasIntValue()).isFalse();
      assertThat(entity.hasDoubleValue()).isTrue();
      assertThat(entity.hasBigDecimalValue()).isFalse();
      assertThat(entity.hasCurrencyValue()).isTrue();
      assertThat(entity.hasColorValue()).isFalse();
      assertThat(entity.hasDateValue()).isTrue();
      assertThat(entity.hasTimeValue()).isFalse();
      assertThat(entity.hasLocaleValue()).isTrue();
      assertThat(entity.hasZoneIdValue()).isFalse();
    }
  }
}
