package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;

@RunWith(JUnit4.class)
public final class ValidationContextTest {
  private static final CountryCode COUNTRY_CODE = CountryCode.forStringOrUnknown("AU");
  private static final CurrentDateTime CURRENT_DATE_TIME =
      new CurrentDateTime(ZonedDateTime.of(2021, 1, 1, 14, 30, 0, 0, ZoneOffset.UTC));
  private static final ValidationContext VALIDATION_CONTEXT =
      ValidationContext.builder()
          .setCountryCode(COUNTRY_CODE)
          .setCurrentDateTime(CURRENT_DATE_TIME)
          .build();

  @Test
  public void get_countryCode_successful() {
    assertThat(VALIDATION_CONTEXT.get(CountryCode.class)).isEqualTo(COUNTRY_CODE);
  }

  @Test
  public void get_currentDateTime_successful() {
    assertThat(VALIDATION_CONTEXT.get(CurrentDateTime.class)).isEqualTo(CURRENT_DATE_TIME);
  }

  @Test
  public void get_unsupported_throws() {
    assertThrows(
        IllegalArgumentException.class, () -> VALIDATION_CONTEXT.get(ChildCurrentDateTime.class));
  }

  private static class ChildCurrentDateTime extends CurrentDateTime {

    public ChildCurrentDateTime(ZonedDateTime now) {
      super(now);
    }
  }
}
