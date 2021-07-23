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
import java.util.Map;
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
 * Validates that all {@code GtfsRoute} have {@code routes.route_url != agency.agency_url}.
 *
 * <p>Generated notice:
 *
 * <ul>
 *   <li>{@link SameRouteAndAgencyUrlNotice} - for a record from "route.txt", the value of
 *   {@code routes.route_url} is shared by a record from "agency.txt".
 * </ul>
 */
@GtfsValidator
public class UrlConsistencyValidator extends FileValidator {

  private final GtfsAgencyTableContainer agencyTable;
  private final GtfsRouteTableContainer routeTable;

  @Inject
  UrlConsistencyValidator(GtfsAgencyTableContainer agencyTable,
      GtfsRouteTableContainer routeTable) {
    this.agencyTable = agencyTable;
    this.routeTable = routeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (agencyTable.getEntities().isEmpty()) {
      return;
    }
    Map<String, GtfsAgency> agencyByUrlMap = agencyByUrlMap(agencyTable, noticeContainer);
    for (GtfsRoute route : routeTable.getEntities()) {
      if (!route.hasRouteUrl()) {
        return;
      }
      GtfsAgency agency = agencyByUrlMap.get(route.routeUrl().toLowerCase());
      if (agency != null) {
        noticeContainer.addValidationNotice(
            new SameRouteAndAgencyUrlNotice(
                route.csvRowNumber(),
                route.routeId(),
                agency.agencyName(),
                route.routeUrl(),
                agency.csvRowNumber()
            ));
      }
    }
  }

  /**
   * Maps {@code GtfsAgency}s by there URLs if provided.
   *
   * @param agencyTable the {@code GtfsAgencyTableContainer} to extract {@code GtfsAgency} from
   * @return agencies from {@code GtfsAgencyTableContainer}s mapped by there URLs (in lower case) if
   * provided
   */
  private Map<String, GtfsAgency> agencyByUrlMap(GtfsAgencyTableContainer agencyTable,
      NoticeContainer noticeContainer) {
    ImmutableMap.Builder<String, GtfsAgency> builder = new ImmutableMap.Builder<>();
    agencyTable.getEntities().forEach(agency -> {
      if (agency.hasAgencyUrl()) {
        try {
          builder.put(agency.agencyUrl().toLowerCase(), agency);
        } catch (IllegalArgumentException e) {
          noticeContainer.addValidationNotice(
              new DuplicateAgencyUrlNotice(agency.csvRowNumber(), agency.agencyName(),
                  agency.agencyUrl()));
        }
      }
    });
    return builder.build();
  }

  /**
   * A {@code GtfsRoute} has the same value for {@code routes.route_url} as a record from
   * "agency.txt".
   * <p>
   * {@code SeverityLevel.WARNING}
   */
  static class SameRouteAndAgencyUrlNotice extends ValidationNotice {

    @SchemaExport
    SameRouteAndAgencyUrlNotice(long csvRowNumber, String routeId, String agencyName,
        String routeUrl, long agencyCsvRowNumber) {
      super(
          ImmutableMap.of(
              "csvRowNumber", csvRowNumber,
              "routeId", routeId,
              "agencyName", agencyName,
              "routeUrl", routeUrl,
              "agencyCsvRowNumber", agencyCsvRowNumber),
          SeverityLevel.WARNING);
    }
  }

  /**
   * Two records from "agency.txt" share the same value for {@code agency.agency_url}.
   * <p>
   * {@code SeverityLevel.WARNING}
   */
  static class DuplicateAgencyUrlNotice extends ValidationNotice {

    @SchemaExport
    DuplicateAgencyUrlNotice(long csvRowNumber, String agencyName, String duplicateAgencyUrl) {
      super(
          ImmutableMap.of(
              "csvRowNumber", csvRowNumber,
              "agencyName", agencyName,
              "", duplicateAgencyUrl),
          SeverityLevel.WARNING);
    }
  }
}
