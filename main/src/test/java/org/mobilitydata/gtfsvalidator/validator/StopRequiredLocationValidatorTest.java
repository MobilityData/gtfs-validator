package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.validator.StopRequiredLocationValidator.StopWithoutLocationNotice;

public class StopRequiredLocationValidatorTest {

  private static List<ValidationNotice> generateNotices(GtfsStop stop) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopRequiredLocationValidator().validate(stop, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void missingLatLongForStop_generatesNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setLocationType(GtfsLocationType.STOP)
                    .build()))
        .containsExactly(new StopWithoutLocationNotice(4, "stop id value", GtfsLocationType.STOP));
  }

  @Test
  public void missingLatLongForStation_generatesNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setLocationType(GtfsLocationType.STATION)
                    .build()))
        .containsExactly(
            new StopWithoutLocationNotice(4, "stop id value", GtfsLocationType.STATION));
  }

  @Test
  public void missingLatLongForEntrance_generatesNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setLocationType(GtfsLocationType.ENTRANCE)
                    .build()))
        .containsExactly(
            new StopWithoutLocationNotice(4, "stop id value", GtfsLocationType.ENTRANCE));
  }

  /* Missing Lat & Lon for Generic Node and Boarding Area should not generate errors */
  @Test
  public void missingLatLongForGenericNode_generatesNoNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setLocationType(GtfsLocationType.GENERIC_NODE)
                    .build()))
        .isEmpty();
  }

  @Test
  public void missingLatLongForBoardingArea_generatesNoNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setLocationType(GtfsLocationType.BOARDING_AREA)
                    .build()))
        .isEmpty();
  }

  /* Supplied Lat & Lon for Stop, Station, Entrance/Exit types should not generate errors */
  @Test
  public void givenLatLongForStop_generatesNoNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setStopLat(25.25)
                    .setStopLon(35.35)
                    .setLocationType(GtfsLocationType.STOP)
                    .build()))
        .isEmpty();
  }

  @Test
  public void givenLatLongForStation_generatesNoNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setStopLat(25.25)
                    .setStopLon(35.35)
                    .setLocationType(GtfsLocationType.STATION)
                    .build()))
        .isEmpty();
  }

  @Test
  public void givenLatLongForEntrance_generatesNoNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setStopLat(25.25)
                    .setStopLon(35.35)
                    .setLocationType(GtfsLocationType.ENTRANCE)
                    .build()))
        .isEmpty();
  }

  /* Missing Lat OR Lon for Stop, Station, Entrance/Exit types should not generate errors */
  @Test
  public void givenLatButMissingLongForStop_generatesNoNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setStopLat(25.25)
                    .setLocationType(GtfsLocationType.STOP)
                    .build()))
        .containsExactly(new StopWithoutLocationNotice(4, "stop id value", GtfsLocationType.STOP));
  }

  @Test
  public void givenLongButMissingLatForStop_generatesNoNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setStopLon(25.25)
                    .setLocationType(GtfsLocationType.STOP)
                    .build()))
        .containsExactly(new StopWithoutLocationNotice(4, "stop id value", GtfsLocationType.STOP));
  }

  @Test
  public void givenLatButMissingLongForStation_generatesNoNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setStopLat(25.25)
                    .setLocationType(GtfsLocationType.STATION)
                    .build()))
        .containsExactly(
            new StopWithoutLocationNotice(4, "stop id value", GtfsLocationType.STATION));
  }

  @Test
  public void givenLongButMissingLatForStation_generatesNoNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setStopLon(25.25)
                    .setLocationType(GtfsLocationType.STATION)
                    .build()))
        .containsExactly(
            new StopWithoutLocationNotice(4, "stop id value", GtfsLocationType.STATION));
  }

  @Test
  public void givenLatButMissingLongForEntrance_generatesNoNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setStopLat(25.25)
                    .setLocationType(GtfsLocationType.ENTRANCE)
                    .build()))
        .containsExactly(
            new StopWithoutLocationNotice(4, "stop id value", GtfsLocationType.ENTRANCE));
  }

  @Test
  public void givenLongButMissingLatForEntrance_generatesNoNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setStopLon(25.25)
                    .setLocationType(GtfsLocationType.ENTRANCE)
                    .build()))
        .containsExactly(
            new StopWithoutLocationNotice(4, "stop id value", GtfsLocationType.ENTRANCE));
  }
}
