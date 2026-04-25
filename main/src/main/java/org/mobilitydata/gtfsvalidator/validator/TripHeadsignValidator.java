package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.INFO;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates that the trip headsign does not match the name of any intermediate stop (i.e., any stop
 * that is not the last stop of the trip).
 *
 * <p>Generated notice: {@link TripHeadsignMatchesIntermediateStopNotice}.
 */
@GtfsValidator
public class TripHeadsignValidator extends FileValidator {
  private final GtfsTripTableContainer tripTable;
  private final GtfsStopTimeTableContainer stopTimeTable;
  private final GtfsStopTableContainer stopTable;

  @Inject
  TripHeadsignValidator(
      GtfsTripTableContainer tripTable,
      GtfsStopTimeTableContainer stopTimeTable,
      GtfsStopTableContainer stopTable) {
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
    this.stopTable = stopTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTrip trip : tripTable.getEntities()) {
      if (!trip.hasTripHeadsign()) {
        continue;
      }
      String headsign = trip.tripHeadsign();
      String tripId = trip.tripId();

      List<GtfsStopTime> stopTimes = stopTimeTable.byTripId(tripId);
      if (stopTimes.size() < 2) {
        continue; // Not enough stops to have an intermediate stop
      }

      // Sort by stop_sequence to find the true last stop
      List<GtfsStopTime> sorted =
          stopTimes.stream()
              .sorted(Comparator.comparingInt(GtfsStopTime::stopSequence))
              .collect(Collectors.toList());

      String lastStopId = sorted.get(sorted.size() - 1).stopId();

      // Check all stops except the last
      for (int i = 0; i < sorted.size() - 1; i++) {
        GtfsStopTime intermediateStopTime = sorted.get(i);
        String stopId = intermediateStopTime.stopId();
        Optional<GtfsStop> stop = stopTable.byStopId(stopId);
        if (stop.isPresent()
            && stop.get().hasStopName()
            && stop.get().stopName().equalsIgnoreCase(headsign)) {
          noticeContainer.addValidationNotice(
              new TripHeadsignMatchesIntermediateStopNotice(
                  trip.csvRowNumber(),
                  tripId,
                  headsign,
                  stopId,
                  intermediateStopTime.stopSequence(),
                  lastStopId));
        }
      }
    }
  }

  /**
   * Trip headsign matches the name of an intermediate stop, not the last stop.
   *
   * <p>The `trip_headsign` matches the `stop_name` of a stop that is not the last stop of the trip.
   * This may confuse passengers boarding after that stop, since the headsign suggests the vehicle
   * is heading to a stop it has already passed.
   */
  @GtfsValidationNotice(
      severity = INFO,
      files = @FileRefs({GtfsTripSchema.class, GtfsStopTimeSchema.class, GtfsStopSchema.class}))
  static class TripHeadsignMatchesIntermediateStopNotice extends ValidationNotice {

    /** The row number of the faulty record in `trips.txt`. */
    private final long csvRowNumber;

    /** The id of the trip with the problematic headsign. */
    private final String tripId;

    /** The headsign value that matches an intermediate stop name. */
    private final String tripHeadsign;

    /** The id of the intermediate stop whose name matches the headsign. */
    private final String stopId1;

    /** The stop_sequence value of the intermediate stop that matches the headsign. */
    private final int stopSequence;

    /** The id of the actual last stop of the trip. */
    private final String stopId2;

    TripHeadsignMatchesIntermediateStopNotice(
        long csvRowNumber,
        String tripId,
        String tripHeadsign,
        String stopId1,
        int stopSequence,
        String stopId2) {
      this.csvRowNumber = csvRowNumber;
      this.tripId = tripId;
      this.tripHeadsign = tripHeadsign;
      this.stopId1 = stopId1;
      this.stopSequence = stopSequence;
      this.stopId2 = stopId2;
    }
  }
}
