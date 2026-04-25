package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.validator.TripHeadsignValidator.TripHeadsignMatchesIntermediateStopNotice;

public class TripHeadsignValidatorTest {

  @Test
  public void headsignMatchingLastStopShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createTrip(1, "r1", "s1", "t0", "Central Station")),
                ImmutableList.of(
                    createStopTime(0, "t0", "stop_a", 1),
                    createStopTime(0, "t0", "stop_b", 2),
                    createStopTime(0, "t0", "stop_central", 3)),
                ImmutableList.of(
                    createStop("stop_a", "Airport"),
                    createStop("stop_b", "City Hall"),
                    createStop("stop_central", "Central Station"))))
        .isEmpty();
  }

  @Test
  public void headsignMatchingIntermediateStopShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createTrip(1, "r1", "s1", "t0", "City Hall")),
                ImmutableList.of(
                    createStopTime(0, "t0", "stop_a", 1),
                    createStopTime(0, "t0", "stop_b", 2),
                    createStopTime(0, "t0", "stop_central", 3)),
                ImmutableList.of(
                    createStop("stop_a", "Airport"),
                    createStop("stop_b", "City Hall"),
                    createStop("stop_central", "Central Station"))))
        .containsExactly(
            new TripHeadsignMatchesIntermediateStopNotice(
                1, "t0", "City Hall", "stop_b", 2, "stop_central"));
  }

  @Test
  public void tripWithNoHeadsignShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createTrip(1, "r1", "s1", "t0", null)),
                ImmutableList.of(
                    createStopTime(0, "t0", "stop_a", 1), createStopTime(0, "t0", "stop_b", 2)),
                ImmutableList.of(
                    createStop("stop_a", "Airport"), createStop("stop_b", "City Hall"))))
        .isEmpty();
  }

  @Test
  public void tripWithSingleStopShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createTrip(1, "r1", "s1", "t0", "Airport")),
                ImmutableList.of(createStopTime(0, "t0", "stop_a", 1)),
                ImmutableList.of(createStop("stop_a", "Airport"))))
        .isEmpty();
  }

  @Test
  public void multipleIntermediateStopsMatchingHeadsignShouldGenerateOneNoticeEach() {
    // Both stop_a and stop_b share the same name as the headsign.  The validator checks every
    // intermediate stop independently, so a separate notice should fire for each match.
    assertThat(
            generateNotices(
                ImmutableList.of(createTrip(1, "r1", "s1", "t0", "City Hall")),
                ImmutableList.of(
                    createStopTime(0, "t0", "stop_a", 1),
                    createStopTime(0, "t0", "stop_b", 2),
                    createStopTime(0, "t0", "stop_c", 3)),
                ImmutableList.of(
                    createStop("stop_a", "City Hall"),
                    createStop("stop_b", "City Hall"),
                    createStop("stop_c", "Central Station"))))
        .containsExactly(
            new TripHeadsignMatchesIntermediateStopNotice(
                1, "t0", "City Hall", "stop_a", 1, "stop_c"),
            new TripHeadsignMatchesIntermediateStopNotice(
                1, "t0", "City Hall", "stop_b", 2, "stop_c"));
  }

  @Test
  public void intermediateStopAbsentFromStopsTableShouldNotGenerateNotice() {
    // When a stop_id referenced in stop_times.txt does not exist in stops.txt the validator's
    // stopTable.byStopId() returns empty.  The broken foreign key is reported by a separate rule;
    // this validator should simply skip the missing stop rather than crash or emit a false notice.
    assertThat(
            generateNotices(
                ImmutableList.of(createTrip(1, "r1", "s1", "t0", "Ghost Stop")),
                ImmutableList.of(
                    createStopTime(0, "t0", "stop_ghost", 1), createStopTime(0, "t0", "stop_b", 2)),
                ImmutableList.of(
                    // stop_ghost is intentionally absent from the stops table
                    createStop("stop_b", "Central Station"))))
        .isEmpty();
  }

  @Test
  public void headsignMatchingFirstStopOfMultiStopTripShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createTrip(1, "r1", "s1", "t0", "Airport")),
                ImmutableList.of(
                    createStopTime(0, "t0", "stop_a", 1),
                    createStopTime(0, "t0", "stop_b", 2),
                    createStopTime(0, "t0", "stop_c", 3)),
                ImmutableList.of(
                    createStop("stop_a", "Airport"),
                    createStop("stop_b", "City Hall"),
                    createStop("stop_c", "Central Station"))))
        .containsExactly(
            new TripHeadsignMatchesIntermediateStopNotice(
                1, "t0", "Airport", "stop_a", 1, "stop_c"));
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsTrip> trips, List<GtfsStopTime> stopTimes, List<GtfsStop> stops) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new TripHeadsignValidator(
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer),
            GtfsStopTableContainer.forEntities(stops, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static GtfsTrip createTrip(
      int csvRowNumber, String routeId, String serviceId, String tripId, String tripHeadsign) {
    return new GtfsTrip.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setRouteId(routeId)
        .setServiceId(serviceId)
        .setTripId(tripId)
        .setTripHeadsign(tripHeadsign)
        .build();
  }

  private static GtfsStopTime createStopTime(
      int csvRowNumber, String tripId, String stopId, int stopSequence) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setStopId(stopId)
        .setStopSequence(stopSequence)
        .build();
  }

  private static GtfsStop createStop(String stopId, String stopName) {
    return new GtfsStop.Builder().setStopId(stopId).setStopName(stopName).build();
  }
}
