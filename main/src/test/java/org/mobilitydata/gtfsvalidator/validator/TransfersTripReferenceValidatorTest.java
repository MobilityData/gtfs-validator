package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferType;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip.Builder;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.validator.TransfersTripReferenceValidator.TransferWithInvalidTripAndRouteNotice;
import org.mobilitydata.gtfsvalidator.validator.TransfersTripReferenceValidator.TransferWithInvalidTripAndStopNotice;

public class TransfersTripReferenceValidatorTest {

  private NoticeContainer noticeContainer = new NoticeContainer();

  @Test
  public void testValidTripReferences() {
    // Trips with valid stop and route references.  For the second trip, the transfer references
    // the parent station for a stop reference.
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(
            ImmutableList.of(
                new Builder().setTripId("t0").setRouteId("r0").build(),
                new Builder().setTripId("t1").setRouteId("r1").build()),
            noticeContainer);
    GtfsStopTableContainer stops =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                new GtfsStop.Builder().setStopId("s0").build(),
                new GtfsStop.Builder().setStopId("s1_stop").setParentStation("s1_station").build(),
                new GtfsStop.Builder()
                    .setStopId("s1_station")
                    .setLocationType(GtfsLocationType.STATION)
                    .build()),
            noticeContainer);
    GtfsStopTimeTableContainer stopTimes =
        GtfsStopTimeTableContainer.forEntities(
            ImmutableList.of(
                new GtfsStopTime.Builder().setTripId("t0").setStopId("s0").build(),
                new GtfsStopTime.Builder().setTripId("t1").setStopId("s1_stop").build()),
            noticeContainer);
    GtfsTransferTableContainer transfers =
        GtfsTransferTableContainer.forEntities(
            ImmutableList.of(
                new GtfsTransfer.Builder()
                    .setCsvRowNumber(2)
                    .setFromStopId("s0")
                    .setFromRouteId("r0")
                    .setFromTripId("t0")
                    .setToStopId("s1_station")
                    .setToRouteId("r1")
                    .setToTripId("t1")
                    .setTransferType(GtfsTransferType.IMPOSSIBLE)
                    .build()),
            noticeContainer);

    new TransfersTripReferenceValidator(transfers, trips, stopTimes, stops)
        .validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void testInvalidTripReferences() {
    // Trips with invalid stop and route references.  For the from-trip, the route id reference is
    // invalid.  For to-trip, the stop id reference doesn't match the stop-times associated with the
    // trip.
    GtfsTripTableContainer trips =
        GtfsTripTableContainer.forEntities(
            ImmutableList.of(
                new Builder().setTripId("t0").setRouteId("r0").build(),
                new Builder().setTripId("t1").setRouteId("r1").build()),
            noticeContainer);
    GtfsStopTableContainer stops =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                new GtfsStop.Builder().setStopId("s0").build(),
                new GtfsStop.Builder().setStopId("s1").build(),
                new GtfsStop.Builder().setStopId("s2").build()),
            noticeContainer);
    GtfsStopTimeTableContainer stopTimes =
        GtfsStopTimeTableContainer.forEntities(
            ImmutableList.of(
                new GtfsStopTime.Builder().setTripId("t0").setStopId("s0").build(),
                new GtfsStopTime.Builder().setTripId("t1").setStopId("s1").build()),
            noticeContainer);
    GtfsTransferTableContainer transfers =
        GtfsTransferTableContainer.forEntities(
            ImmutableList.of(
                new GtfsTransfer.Builder()
                    .setCsvRowNumber(2)
                    .setFromStopId("s0")
                    // This is not the expected route id.
                    .setFromRouteId("DNE")
                    .setFromTripId("t0")
                    // This stop is not associated with the trip's stop-times.
                    .setToStopId("s2")
                    .setToRouteId("r1")
                    .setToTripId("t1")
                    .setTransferType(GtfsTransferType.IMPOSSIBLE)
                    .build()),
            noticeContainer);

    new TransfersTripReferenceValidator(transfers, trips, stopTimes, stops)
        .validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new TransferWithInvalidTripAndRouteNotice(
                2, "from_trip_id", "t0", "from_route_id", "DNE", "r0"),
            new TransferWithInvalidTripAndStopNotice(2, "to_trip_id", "t1", "to_stop_id", "s2"));
  }
}
