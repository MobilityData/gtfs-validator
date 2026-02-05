package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopAccess;

@RunWith(JUnit4.class)
public class StopAccessValidatorTest {
  private static List<ValidationNotice> generateNotices(GtfsStop stop) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopAccessValidator().validate(stop, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void emptyStopAccess_noNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("S0")
                    .setLocationType(GtfsLocationType.STOP)
                    .setStopName("stop name")
                    .setStopAccess(GtfsStopAccess.ACCESSIBLE_VIA_PATHWAYS)
                    .build()))
        .isEmpty();
  }

  @Test
  public void stopLocationWithoutParentStation_generatesNotice() {
    GtfsStop stop =
        new GtfsStop.Builder()
            .setCsvRowNumber(7)
            .setStopId("S1")
            .setLocationType(GtfsLocationType.STOP)
            .setStopName("Stop 1")
            .setStopAccess(GtfsStopAccess.NOT_ACCESSIBLE_VIA_PATHWAYS)
            .build();

    assertThat(generateNotices(stop))
        .containsExactly(
            new StopAccessValidator.StopAccessSpecifiedForStopWithNoParentStationNotice(
                7,
                "S1",
                "Stop 1",
                GtfsStopAccess.NOT_ACCESSIBLE_VIA_PATHWAYS,
                GtfsLocationType.STOP));
  }

  @Test
  public void nonStopLocationWithStopAccess_generatesIncorrectLocationNotice() {
    GtfsStop stop =
        new GtfsStop.Builder()
            .setCsvRowNumber(9)
            .setStopId("S2")
            .setLocationType(GtfsLocationType.STATION)
            .setStopName("Stop 2")
            .setStopAccess(GtfsStopAccess.NOT_ACCESSIBLE_VIA_PATHWAYS)
            .build();

    assertThat(generateNotices(stop))
        .containsExactly(
            new StopAccessValidator.StopAccessSpecifiedForIncorrectLocationNotice(
                9,
                "S2",
                "Stop 2",
                GtfsStopAccess.NOT_ACCESSIBLE_VIA_PATHWAYS,
                GtfsLocationType.STATION));
  }
}
