package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.ForbiddenGeographyIdNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;

@RunWith(JUnit4.class)
public class StopTimeGeographyIdPresenceValidatorTest {

  @Test
  public void duplicateGeographyId_yieldsNotice() {
    // Test for duplicate locationId
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsStopTime stopTime =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setLocationId("locationId")
            .setLocationGroupId("locationGroupId")
            .build();
    StopTimesGeographyIdPresenceValidator validator = new StopTimesGeographyIdPresenceValidator();
    validator.validate(stopTime, noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new ForbiddenGeographyIdNotice(1, null, "locationGroupId", "locationId"));
  }

  @Test
  public void missingGeographyId_yieldsNotice() {
    // Test for missing locationId
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsStopTime stopTime = new GtfsStopTime.Builder().setCsvRowNumber(1).build();
    StopTimesGeographyIdPresenceValidator validator = new StopTimesGeographyIdPresenceValidator();
    validator.validate(stopTime, noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new MissingRequiredFieldNotice("stop_times.txt", 1, "stop_id"));
  }
}
