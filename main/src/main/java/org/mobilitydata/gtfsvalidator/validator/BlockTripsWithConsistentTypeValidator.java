/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimaps;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates that trips that belong to the same block have consistent route types (e.g., bus does
 * not transfer to rail).
 */
@GtfsValidator
public class BlockTripsWithConsistentTypeValidator extends FileValidator {
  private final GtfsTripTableContainer tripTable;
  private final GtfsRouteTableContainer routeTable;

  @Inject
  BlockTripsWithConsistentTypeValidator(
      GtfsTripTableContainer tripTable, GtfsRouteTableContainer routeTable) {
    this.tripTable = tripTable;
    this.routeTable = routeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (List<GtfsTrip> tripsInBlock : Multimaps.asMap(tripTable.byBlockIdMap()).values()) {
      GtfsTrip firstTrip = tripsInBlock.get(0);
      // We don't check trips without a block id.
      if (!firstTrip.hasBlockId()) {
        continue;
      }

      Optional<GtfsRoute> firstRoute = routeTable.byRouteId(firstTrip.routeId());
      if (firstRoute.isEmpty()) {
        continue;
      }
      GtfsRouteType firstRouteType = firstRoute.get().routeType();
      for (GtfsTrip otherTrip : Iterables.skip(tripsInBlock, 1)) {
        if (firstTrip.routeId().equals(otherTrip.routeId())) {
          // Both trips are compatible since they belong to the same route.
          continue;
        }
        Optional<GtfsRoute> otherRoute = routeTable.byRouteId(otherTrip.routeId());
        if (otherRoute.isPresent()
            && !routesAreCompatibleThroughBlockTransfer(
                firstRouteType, otherRoute.get().routeType())) {
          noticeContainer.addValidationNotice(new BlockTripsWithInconsistentRouteTypesNotice(
              firstTrip, firstRoute.get(), otherTrip, otherRoute.get()));
        }
      }
    }
  }

  /** Tells if route types are compatible for block transfer. */
  private static boolean routesAreCompatibleThroughBlockTransfer(
      GtfsRouteType routeType1, GtfsRouteType routeType2) {
    if (routeType1.equals(routeType2)) {
      return true;
    }

    // The difference between rail and subway (metro) may be fuzzy, so we allow transfers between
    // them.
    return isRailOrSubway(routeType1) && isRailOrSubway(routeType2);
  }

  private static boolean isRailOrSubway(GtfsRouteType routeType) {
    return routeType.equals(GtfsRouteType.RAIL) || routeType.equals(GtfsRouteType.SUBWAY);
  }

  /**
   * Describes two trips that belong to the same block and have inconsistent route types (e.g., bus
   * vs train).
   */
  static class BlockTripsWithInconsistentRouteTypesNotice extends ValidationNotice {
    private final long csvRowNumber1;
    private final String tripId1;
    private final int routeType1;
    private final String routeTypeName1;
    private final long csvRowNumber2;
    private final String tripId2;
    private final int routeType2;
    private final String routeTypeName2;
    private final String blockId;

    BlockTripsWithInconsistentRouteTypesNotice(
        GtfsTrip firstTrip, GtfsRoute firstRoute, GtfsTrip otherTrip, GtfsRoute otherRoute) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber1 = firstTrip.csvRowNumber();
      this.tripId1 = firstTrip.tripId();
      this.routeType1 = firstRoute.routeType().getNumber();
      this.routeTypeName1 = firstRoute.routeType().toString();
      this.csvRowNumber2 = otherTrip.csvRowNumber();
      this.tripId2 = otherTrip.tripId();
      this.routeType2 = otherRoute.routeType().getNumber();
      this.routeTypeName2 = otherRoute.routeType().toString();
      this.blockId = firstTrip.blockId();
    }
  }
}
