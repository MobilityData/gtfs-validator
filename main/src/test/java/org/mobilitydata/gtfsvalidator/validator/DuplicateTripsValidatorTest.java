package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.DuplicateTripsValidator.DuplicateTripsNotice;

@RunWith(JUnit4.class)
public class DuplicateTripsValidatorTest {

  private static GtfsTrip trip(int csvRowNumber, String tripId, String serviceId) {
    return new GtfsTrip.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setServiceId(serviceId)
        .build();
  }

  /** Builds stop times for a trip, one per stop, using the same time for arrival and departure. */
  private static void addStopTimes(
      List<GtfsStopTime> stopTimes, String tripId, String[] stopIds, String[] times) {
    for (int i = 0; i < stopIds.length; ++i) {
      stopTimes.add(
          new GtfsStopTime.Builder()
              .setCsvRowNumber(stopTimes.size() + 2)
              .setTripId(tripId)
              .setStopId(stopIds[i])
              .setStopSequence(i + 1)
              .setArrivalTime(GtfsTime.fromString(times[i]))
              .setDepartureTime(GtfsTime.fromString(times[i]))
              .build());
    }
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsTrip> trips, List<GtfsStopTime> stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new DuplicateTripsValidator(
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static final String[] STOPS = {"s1", "s2", "s3"};
  private static final String[] TIMES = {"08:00:00", "08:10:00", "08:20:00"};

  @Test
  public void identicalTrips_yieldsNotice() {
    List<GtfsTrip> trips = ImmutableList.of(trip(2, "t1", "WEEK"), trip(3, "t2", "WEEK"));
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    addStopTimes(stopTimes, "t1", STOPS, TIMES);
    addStopTimes(stopTimes, "t2", STOPS, TIMES);

    assertThat(generateNotices(trips, stopTimes))
        .containsExactly(
            new DuplicateTripsNotice(
                trips.get(0), stopTimes.get(0), trips.get(1), stopTimes.get(3)));
  }

  @Test
  public void threeIdenticalTrips_eachReportedAgainstFirst() {
    List<GtfsTrip> trips =
        ImmutableList.of(trip(2, "t1", "WEEK"), trip(3, "t2", "WEEK"), trip(4, "t3", "WEEK"));
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    addStopTimes(stopTimes, "t1", STOPS, TIMES);
    addStopTimes(stopTimes, "t2", STOPS, TIMES);
    addStopTimes(stopTimes, "t3", STOPS, TIMES);

    assertThat(generateNotices(trips, stopTimes))
        .containsExactly(
            new DuplicateTripsNotice(
                trips.get(0), stopTimes.get(0), trips.get(1), stopTimes.get(3)),
            new DuplicateTripsNotice(
                trips.get(0), stopTimes.get(0), trips.get(2), stopTimes.get(6)));
  }

  @Test
  public void differentServiceId_noNotice() {
    List<GtfsTrip> trips = ImmutableList.of(trip(2, "t1", "WEEK"), trip(3, "t2", "SAT"));
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    addStopTimes(stopTimes, "t1", STOPS, TIMES);
    addStopTimes(stopTimes, "t2", STOPS, TIMES);

    assertThat(generateNotices(trips, stopTimes)).isEmpty();
  }

  @Test
  public void differentTimes_noNotice() {
    List<GtfsTrip> trips = ImmutableList.of(trip(2, "t1", "WEEK"), trip(3, "t2", "WEEK"));
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    addStopTimes(stopTimes, "t1", STOPS, TIMES);
    addStopTimes(stopTimes, "t2", STOPS, new String[] {"08:00:00", "08:10:00", "08:21:00"});

    assertThat(generateNotices(trips, stopTimes)).isEmpty();
  }

  @Test
  public void differentStopOrder_noNotice() {
    List<GtfsTrip> trips = ImmutableList.of(trip(2, "t1", "WEEK"), trip(3, "t2", "WEEK"));
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    addStopTimes(stopTimes, "t1", STOPS, TIMES);
    addStopTimes(stopTimes, "t2", new String[] {"s1", "s3", "s2"}, TIMES);

    assertThat(generateNotices(trips, stopTimes)).isEmpty();
  }

  @Test
  public void prefixTrip_noNotice() {
    List<GtfsTrip> trips = ImmutableList.of(trip(2, "t1", "WEEK"), trip(3, "t2", "WEEK"));
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    addStopTimes(stopTimes, "t1", STOPS, TIMES);
    addStopTimes(stopTimes, "t2", new String[] {"s1", "s2"}, new String[] {"08:00:00", "08:10:00"});

    assertThat(generateNotices(trips, stopTimes)).isEmpty();
  }

  @Test
  public void stopTimesOutOfOrderInFile_yieldsNotice() {
    List<GtfsTrip> trips = ImmutableList.of(trip(2, "t1", "WEEK"), trip(3, "t2", "WEEK"));
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    addStopTimes(stopTimes, "t1", STOPS, TIMES);
    // Rows of t2 are written in reverse order; stop_sequence still gives s1, s2, s3.
    for (int i = STOPS.length - 1; i >= 0; --i) {
      stopTimes.add(
          new GtfsStopTime.Builder()
              .setCsvRowNumber(stopTimes.size() + 2)
              .setTripId("t2")
              .setStopId(STOPS[i])
              .setStopSequence(i + 1)
              .setArrivalTime(GtfsTime.fromString(TIMES[i]))
              .setDepartureTime(GtfsTime.fromString(TIMES[i]))
              .build());
    }

    // The first stop time of t2 (stop_sequence 1) is the last row in the file.
    assertThat(generateNotices(trips, stopTimes))
        .containsExactly(
            new DuplicateTripsNotice(
                trips.get(0), stopTimes.get(0), trips.get(1), stopTimes.get(5)));
  }

  @Test
  public void tripWithoutStopTimes_noNotice() {
    List<GtfsTrip> trips = ImmutableList.of(trip(2, "t1", "WEEK"), trip(3, "t2", "WEEK"));

    assertThat(generateNotices(trips, ImmutableList.of())).isEmpty();
  }
}
