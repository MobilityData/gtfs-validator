package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

@GtfsValidator
public class InconsistentRouteTypeForBlockIdValidator extends FileValidator {
  private final GtfsTripTableContainer tripTable;
  private final GtfsRouteTableContainer routeTable;

  @Inject
  InconsistentRouteTypeForBlockIdValidator(
      GtfsTripTableContainer tripTable, GtfsRouteTableContainer routeTable) {
    this.tripTable = tripTable;
    this.routeTable = routeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // for each block id in trip get the route type of the trip and check if all route types for the
    // block id are the same
    tripTable
        .byBlockIdMap()
        .asMap()
        .forEach(
            (blockId, trips) -> {
              if (blockId != null && !blockId.isEmpty()) {
                List<GtfsRouteType> routeTypes =
                    trips.stream()
                        .map(trip -> routeTable.byRouteId(trip.routeId()))
                        .filter(Optional::isPresent)
                        .map(route -> route.get().routeType())
                        .distinct()
                        .collect(Collectors.toList());
                if (routeTypes.size() > 1) {
                  List<String> routeIds =
                      trips.stream().map(GtfsTrip::routeId).distinct().collect(Collectors.toList());
                  noticeContainer.addValidationNotice(
                      new InconsistentRouteTypeForBlockIdNotice(blockId, routeIds, routeTypes));
                }
              }
            });
  }

  /** A block should have the same route mode. */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @GtfsValidationNotice.FileRefs({GtfsTripSchema.class, GtfsRouteSchema.class}))
  static class InconsistentRouteTypeForBlockIdNotice extends ValidationNotice {
    /** The block_id value of the faulty trip. */
    private final String blockId;

    /** The route_id values associated with the block_id comma separated. */
    private final String routeIds;

    /** The route_type values associated with the block_id comma separated. */
    private final String routeTypes;

    public InconsistentRouteTypeForBlockIdNotice(
        String blockId, List<String> routeIds, List<GtfsRouteType> routeTypes) {
      this.blockId = blockId;
      this.routeIds = String.join(", ", routeIds);
      this.routeTypes =
          routeTypes.stream().map(String::valueOf).reduce((a, b) -> a + ", " + b).orElse("");
    }
  }
}
