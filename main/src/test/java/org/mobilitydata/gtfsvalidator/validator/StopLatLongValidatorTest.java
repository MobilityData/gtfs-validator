package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import javax.annotation.Nullable;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;

public class StopLatLongValidatorTest {
  private static GtfsStop createStop(
      int csvRowNumber,
      String stopId,
      @Nullable Double lat,
      @Nullable Double lon,
      GtfsLocationType type) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(stopId)
        .setStopLat(lat)
        .setStopLon(lon)
        .setLocationType(type)
        .build();
  }

  private static List<ValidationNotice> generateNotices(GtfsStop stop) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopLatLongValidator().validate(stop, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  /* Missing Lat & Lon for Stop, Station, Entrance/Exit types should generate errors */
  @Test
  public void missingLatLongForStop_generatesNotice() {
    assertThat(generateNotices(createStop(4, "stop id value", null, null, GtfsLocationType.STOP)))
        .containsExactly(
            new StopLatLongValidator.LatLongRequiredForStopType(
                4, "stop id value", GtfsLocationType.STOP));
  }

  @Test
  public void missingLatLongForStation_generatesNotice() {
    assertThat(
            generateNotices(createStop(4, "stop id value", null, null, GtfsLocationType.STATION)))
        .containsExactly(
            new StopLatLongValidator.LatLongRequiredForStopType(
                4, "stop id value", GtfsLocationType.STATION));
  }

  @Test
  public void missingLatLongForEntrance_generatesNotice() {
    assertThat(
            generateNotices(createStop(4, "stop id value", null, null, GtfsLocationType.ENTRANCE)))
        .containsExactly(
            new StopLatLongValidator.LatLongRequiredForStopType(
                4, "stop id value", GtfsLocationType.ENTRANCE));
  }

  /* Missing Lat & Lon for Generic Node and Boarding Area should not generate errors */
  @Test
  public void missingLatLongForGenericNode_generatesNoNotice() {
    assertThat(
            generateNotices(
                createStop(4, "stop id value", null, null, GtfsLocationType.GENERIC_NODE)))
        .isEmpty();
  }

  @Test
  public void missingLatLongForBoardingArea_generatesNoNotice() {
    assertThat(
            generateNotices(
                createStop(4, "stop id value", null, null, GtfsLocationType.BOARDING_AREA)))
        .isEmpty();
  }

  /* Supplied Lat & Lon for Stop, Station, Entrance/Exit types should not generate errors */
  @Test
  public void givenLatLongForStop_generatesNoNotice() {
    assertThat(generateNotices(createStop(4, "stop id value", 25.25, 35.35, GtfsLocationType.STOP)))
        .isEmpty();
  }

  @Test
  public void givenLatLongForStation_generatesNoNotice() {
    assertThat(
            generateNotices(createStop(4, "stop id value", 25.25, 35.35, GtfsLocationType.STATION)))
        .isEmpty();
  }

  @Test
  public void givenLatLongForEntrance_generatesNoNotice() {
    assertThat(
            generateNotices(
                createStop(4, "stop id value", 25.25, 35.35, GtfsLocationType.ENTRANCE)))
        .isEmpty();
  }

  /* Missing Lat OR Lon for Stop, Station, Entrance/Exit types should not generate errors */
  @Test
  public void givenLatButMissingLongForStop_generatesNoNotice() {
    assertThat(generateNotices(createStop(4, "stop id value", 25.25, null, GtfsLocationType.STOP)))
        .containsExactly(
            new StopLatLongValidator.LatLongRequiredForStopType(
                4, "stop id value", GtfsLocationType.STOP));
  }

  @Test
  public void givenLongButMissingLatForStop_generatesNoNotice() {
    assertThat(generateNotices(createStop(4, "stop id value", null, 25.25, GtfsLocationType.STOP)))
        .containsExactly(
            new StopLatLongValidator.LatLongRequiredForStopType(
                4, "stop id value", GtfsLocationType.STOP));
  }

  @Test
  public void givenLatButMissingLongForStation_generatesNoNotice() {
    assertThat(
            generateNotices(createStop(4, "stop id value", 25.25, null, GtfsLocationType.STATION)))
        .containsExactly(
            new StopLatLongValidator.LatLongRequiredForStopType(
                4, "stop id value", GtfsLocationType.STATION));
  }

  @Test
  public void givenLongButMissingLatForStation_generatesNoNotice() {
    assertThat(
            generateNotices(createStop(4, "stop id value", null, 25.25, GtfsLocationType.STATION)))
        .containsExactly(
            new StopLatLongValidator.LatLongRequiredForStopType(
                4, "stop id value", GtfsLocationType.STATION));
  }

  @Test
  public void givenLatButMissingLongForEntrance_generatesNoNotice() {
    assertThat(
            generateNotices(createStop(4, "stop id value", 25.25, null, GtfsLocationType.ENTRANCE)))
        .containsExactly(
            new StopLatLongValidator.LatLongRequiredForStopType(
                4, "stop id value", GtfsLocationType.ENTRANCE));
  }

  @Test
  public void givenLongButMissingLatForEntrance_generatesNoNotice() {
    assertThat(
            generateNotices(createStop(4, "stop id value", null, 25.25, GtfsLocationType.ENTRANCE)))
        .containsExactly(
            new StopLatLongValidator.LatLongRequiredForStopType(
                4, "stop id value", GtfsLocationType.ENTRANCE));
  }
}
