package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;
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
 * Validates that stops do not serve incompatible route modes.
 *
 * <p>Generated notice: {@link StopServesConflictingModesNotice}.
 */
@GtfsValidator
public class StopServesConflictingModesValidator extends FileValidator {
  private final GtfsStopTableContainer stopTable;
  private final GtfsStopTimeTableContainer stopTimeTable;
  private final GtfsTripTableContainer tripTable;
  private final GtfsRouteTableContainer routeTable;

  @Inject
  StopServesConflictingModesValidator(
      GtfsStopTableContainer stopTable,
      GtfsStopTimeTableContainer stopTimeTable,
      GtfsTripTableContainer tripTable,
      GtfsRouteTableContainer routeTable) {
    this.stopTable = stopTable;
    this.stopTimeTable = stopTimeTable;
    this.tripTable = tripTable;
    this.routeTable = routeTable;
  }

  @Override
  public boolean shouldCallValidate() {
    return stopTable != null
        && stopTimeTable != null
        && tripTable != null
        && routeTable != null
        && !stopTable.isMissingFile()
        && !stopTimeTable.isMissingFile()
        && !tripTable.isMissingFile()
        && !routeTable.isMissingFile()
        && stopTable.isParsedSuccessfully()
        && stopTimeTable.isParsedSuccessfully()
        && tripTable.isParsedSuccessfully()
        && routeTable.isParsedSuccessfully()
        && stopTable.hasColumn(GtfsStop.STOP_ID_FIELD_NAME)
        && stopTimeTable.hasColumn(GtfsStopTime.STOP_ID_FIELD_NAME)
        && stopTimeTable.hasColumn(GtfsStopTime.TRIP_ID_FIELD_NAME)
        && tripTable.hasColumn(GtfsTrip.TRIP_ID_FIELD_NAME)
        && tripTable.hasColumn(GtfsTrip.ROUTE_ID_FIELD_NAME)
        && routeTable.hasColumn(GtfsRoute.ROUTE_ID_FIELD_NAME)
        && routeTable.hasColumn(GtfsRoute.ROUTE_TYPE_FIELD_NAME);
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (!shouldCallValidate()) {
      return;
    }

    for (GtfsStop stop : stopTable.getEntities()) {
      if (!stop.hasStopId()
          || stop.stopId().isEmpty()
          || stop.locationType() != GtfsLocationType.STOP) {
        continue;
      }

      List<GtfsStopTime> stopTimes = stopTimeTable.byStopId(stop.stopId());
      if (stopTimes.isEmpty()) {
        continue;
      }

      Set<String> routeIds = new TreeSet<>();
      // Keep route IDs and route types deterministically ordered.
      Set<GtfsRouteType> routeTypes =
          new TreeSet<>(Comparator.comparingInt(GtfsRouteType::getNumber));
      for (GtfsStopTime stopTime : stopTimes) {
        if (!stopTime.hasTripId() || stopTime.tripId().isEmpty()) {
          continue;
        }
        Optional<GtfsTrip> trip = tripTable.byTripId(stopTime.tripId());
        if (trip.isEmpty() || !trip.get().hasRouteId() || trip.get().routeId().isEmpty()) {
          continue;
        }
        Optional<GtfsRoute> route = routeTable.byRouteId(trip.get().routeId());
        // A missing route_type defaults to LIGHT_RAIL, so require the field first.
        if (route.isEmpty()
            || !route.get().hasRouteId()
            || route.get().routeId().isEmpty()
            || !route.get().hasRouteType()) {
          continue;
        }
        GtfsRouteType routeType = route.get().routeType();
        if (routeType == GtfsRouteType.UNRECOGNIZED) {
          continue;
        }
        routeIds.add(route.get().routeId());
        routeTypes.add(routeType);
      }

      if (hasConflictingModes(routeTypes)) {
        noticeContainer.addValidationNotice(
            new StopServesConflictingModesNotice(
                stop.stopId(),
                String.join(", ", routeIds),
                routeTypes.stream().map(GtfsRouteType::name).collect(Collectors.joining(", "))));
      }
    }
  }

  private static boolean hasConflictingModes(Set<GtfsRouteType> routeTypes) {
    boolean hasFunicularFamilyConflict =
        routeTypes.size() > 1
            && containsAny(
                routeTypes,
                GtfsRouteType.FERRY,
                GtfsRouteType.AERIAL_LIFT,
                GtfsRouteType.FUNICULAR);
    boolean hasBusRailConflict =
        containsAny(routeTypes, GtfsRouteType.BUS, GtfsRouteType.TROLLEYBUS)
            && containsAny(routeTypes, GtfsRouteType.SUBWAY, GtfsRouteType.RAIL);
    // Monorail is compatible only with subway, rail, and monorail.
    boolean hasMonorailConflict =
        routeTypes.contains(GtfsRouteType.MONORAIL)
            && containsAny(
                routeTypes,
                GtfsRouteType.LIGHT_RAIL,
                GtfsRouteType.BUS,
                GtfsRouteType.FERRY,
                GtfsRouteType.CABLE_TRAM,
                GtfsRouteType.AERIAL_LIFT,
                GtfsRouteType.FUNICULAR,
                GtfsRouteType.TROLLEYBUS);
    return hasFunicularFamilyConflict || hasBusRailConflict || hasMonorailConflict;
  }

  private static boolean containsAny(
      Set<GtfsRouteType> routeTypes, GtfsRouteType... expectedRouteTypes) {
    for (GtfsRouteType expectedRouteType : expectedRouteTypes) {
      if (routeTypes.contains(expectedRouteType)) {
        return true;
      }
    }
    return false;
  }

  /**
   * A stop serves incompatible route modes.
   *
   * <p>Bus or trolleybus should not share a stop with subway or rail. Ferry, aerial lift, and
   * funicular should not share a stop with any different mode. Monorail may share a stop only with
   * subway, rail, or another monorail route.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files =
          @FileRefs({
            GtfsStopSchema.class,
            GtfsStopTimeSchema.class,
            GtfsTripSchema.class,
            GtfsRouteSchema.class
          }))
  public static class StopServesConflictingModesNotice extends ValidationNotice {
    /** The faulty stop ID. */
    private final String stopId;

    /** Route IDs serving the faulty stop, in lexicographic order. */
    private final String routeIds;

    /** Distinct route type names serving the faulty stop, in numeric order. */
    private final String routeTypes;

    public StopServesConflictingModesNotice(String stopId, String routeIds, String routeTypes) {
      this.stopId = stopId;
      this.routeIds = routeIds;
      this.routeTypes = routeTypes;
    }
  }
}
