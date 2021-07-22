/*
 * Copyright 2021 MobilityData IO
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

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.SchemaExport;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

/**
 * Validates that all {@code GtfsRoute} have {@code routes.route_url != agency.agency_url} if there
 * is more than 1 agency.
 *
 * <p>Generated notice:
 *
 * <ul>
 *   <li>{@link SameRouteUrlAndAgencyUrlNotice} - a {@code GtfsRoute} has the same value for
 *   {@code routes.route_url} as the {@code GtfsAgency} it is related to.
 * </ul>
 */
@GtfsValidator
public class RouteUrlConsistencyValidator extends FileValidator {

  private final GtfsAgencyTableContainer agencyTable;
  private final GtfsRouteTableContainer routeTable;

  @Inject
  RouteUrlConsistencyValidator(GtfsAgencyTableContainer agencyTable,
      GtfsRouteTableContainer routeTable) {
    this.agencyTable = agencyTable;
    this.routeTable = routeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (agencyTable.getEntities().isEmpty()) {
      return;
    }
    for (GtfsRoute route : routeTable.getEntities()) {
      getReferenceUrl(agencyTable, route).ifPresent(agencyUrl -> {
        if (agencyUrl.equalsIgnoreCase(route.routeUrl())) {
          noticeContainer.addValidationNotice(
              new SameRouteUrlAndAgencyUrlNotice(
                  route.csvRowNumber(),
                  route.routeId(),
                  route.agencyId(),
                  route.routeUrl()
              ));
        }
      });
    }
  }

  /**
   * Returns the URL from {@code GtfsAgencyTableContainer} to be used to future comparison as an
   * {@code Optional}.
   *
   * @param agencyTable the {@code GtfsAgencyTableContainer} the collection of {@code GtfsAgency}
   * @param route       the route whose {@code routes.route_url} should not be a duplicate from
   *                    {@code agency.agency_url}
   * @return the URL from {@code GtfsAgencyTableContainer} to be used to future comparison as an
   * {@code Optional}.
   */
  private Optional<String> getReferenceUrl(GtfsAgencyTableContainer agencyTable, GtfsRoute route) {
    GtfsAgency agency = agencyTable.byAgencyId(route.agencyId());
    if (agency != null) {
      return Optional.of(agency.agencyUrl());
    }
    if (agencyTable.entityCount() < 2) {
      if (!route.hasAgencyId()) {
        return Optional.of(agencyTable.getEntities().get(0).agencyUrl());
      }
    }
    return Optional.empty();
  }

  /**
   * A {@code GtfsRoute} has the same value for {@code routes.route_url} as the {@code GtfsAgency}
   * it is related to.
   */
  static class SameRouteUrlAndAgencyUrlNotice extends ValidationNotice {

    @SchemaExport
    SameRouteUrlAndAgencyUrlNotice(long csvRowNumber, String routeId, String agencyId,
        String routeUrl) {
      super(
          ImmutableMap.of(
              "csvRowNumber", csvRowNumber,
              "routeId", routeId,
              "agencyId", agencyId,
              "routeUrl", routeUrl),
          SeverityLevel.WARNING);
    }
  }
}
