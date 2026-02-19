package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class PathwayStopAccessValidatorTest {

  private static List<ValidationNotice> generateNotices(
          List<GtfsPathway> pathways, List<GtfsStop> stops) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new PathwayStopAccessValidator(
            GtfsPathwayTableContainer.forEntities(pathways, noticeContainer),
            GtfsStopTableContainer.forEntities(stops, noticeContainer))
            .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  static GtfsStop createStop(int csvRowNumber, GtfsStopAccess stopAccess, String platformCode) {
    return new GtfsStop.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setStopId(rowToStopId(csvRowNumber))
            .setStopAccess(stopAccess)
            .setPlatformCode(platformCode)
            .build();
  }

  static String rowToStopId(int csvRowNumber) {
    return "stop" + csvRowNumber;
  }

  static String rowToPathwayId(int csvRowNumber) {
    return "pathway" + csvRowNumber;
  }

  static GtfsPathway createPathway(
          int fromStopRow, int toStopRow, GtfsPathwayIsBidirectional isBidirectional) {
    int pathwayRow = fromStopRow * 10 + toStopRow;
    return new GtfsPathway.Builder()
            .setCsvRowNumber(pathwayRow)
            .setPathwayId(rowToPathwayId(pathwayRow))
            .setFromStopId(rowToStopId(fromStopRow))
            .setToStopId(rowToStopId(toStopRow))
            .setIsBidirectional(isBidirectional)
            .build();
  }

  @Test
  public void toStopHasExternalAccess_yieldsNotice() {
    GtfsStop stop1 = createStop(1, GtfsStopAccess.ACCESSIBLE_VIA_PATHWAYS, "platform1");
    GtfsStop stop2 = createStop(2, GtfsStopAccess.NOT_ACCESSIBLE_VIA_PATHWAYS, "platform2");
    GtfsPathway pathway = createPathway(1, 2, GtfsPathwayIsBidirectional.BIDIRECTIONAL);
    assertThat(generateNotices(List.of(pathway), List.of(stop1, stop2)))
            .containsExactly(
                    new PathwayStopAccessValidator.PathwayToStopWithAccessOutsideOfStationPathwaysNotice(pathway.csvRowNumber(),"platform2",
                            pathway.pathwayId(), stop2.stopId()));
  }

  @Test
  public void noStopsHaveExternalAccess_yieldNotice() {
    GtfsStop stop1 = createStop(1, GtfsStopAccess.ACCESSIBLE_VIA_PATHWAYS, "platform1");
    GtfsStop stop2 = createStop(2, GtfsStopAccess.ACCESSIBLE_VIA_PATHWAYS, "platform2");
    GtfsPathway pathway = createPathway(1, 2, GtfsPathwayIsBidirectional.BIDIRECTIONAL);
    assertThat(generateNotices(List.of(pathway), List.of(stop1, stop2))).isEmpty();
  }

}
