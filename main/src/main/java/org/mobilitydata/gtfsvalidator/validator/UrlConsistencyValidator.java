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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.List;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

/**
 * Validates that:
 *
 * <ul>
 *   <li>all {@code GtfsRoute} have: {@code routes.route_url != agency.agency_url}.
 *   <li>all {@code GtfsStop} have: {@code stops.stop_url != agency.agency_url}.
 *   <li>all {@code GtfsStop} have: {@code stops.stop_url != routes.route_url}.
 * </ul>
 */
@GtfsValidator
public class UrlConsistencyValidator extends FileValidator {

  private final GtfsAgencyTableContainer agencyTable;
  private final GtfsRouteTableContainer routeTable;
  private final GtfsStopTableContainer stopTable;

  @Inject
  UrlConsistencyValidator(
      GtfsAgencyTableContainer agencyTable,
      GtfsRouteTableContainer routeTable,
      GtfsStopTableContainer stopTable) {
    this.agencyTable = agencyTable;
    this.routeTable = routeTable;
    this.stopTable = stopTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    ListMultimap<String, GtfsAgency> agencyByUrlMap = agenciesByUrlMap(agencyTable);
    ListMultimap<String, GtfsRoute> routesByUrlMap = routesByUrlMap(routeTable);
    for (GtfsRoute route : routeTable.getEntities()) {
      if (!route.hasRouteUrl()) {
        continue;
      }
      List<GtfsAgency> agencies = agencyByUrlMap.get(route.routeUrl().toLowerCase());
      for (GtfsAgency agency : agencies) {
        noticeContainer.addValidationNotice(new SameRouteAndAgencyUrlNotice(route, agency));
      }
    }
    for (GtfsStop stop : stopTable.getEntities()) {
      if (!stop.hasStopUrl()) {
        continue;
      }
      List<GtfsAgency> agencies = agencyByUrlMap.get(stop.stopUrl().toLowerCase());
      for (GtfsAgency agency : agencies) {
        noticeContainer.addValidationNotice(new SameStopAndAgencyUrlNotice(stop, agency));
      }
      List<GtfsRoute> routes = routesByUrlMap.get(stop.stopUrl().toLowerCase());
      for (GtfsRoute route : routes) {
        noticeContainer.addValidationNotice(new SameStopAndRouteUrlNotice(stop, route));
      }
    }
  }

  /**
   * Maps {@code GtfsAgency}s by there URLs if provided.
   *
   * @param stopTable the {@code GtfsStopTableContainer} to extract {@code GtfsStop} from
   * @return routes from {@code GtfsStopTableContainer}s mapped by there {@code routes.route_url} if
   *     provided.
   */
  private ListMultimap<String, GtfsStop> stopsByUrlMap(GtfsStopTableContainer stopTable) {
    ListMultimap<String, GtfsStop> stopsByUrl = ArrayListMultimap.create();
    stopTable
        .getEntities()
        .forEach(
            stop -> {
              if (stop.hasStopUrl()) {
                stopsByUrl.put(stop.stopUrl().toLowerCase(), stop);
              }
            });
    return stopsByUrl;
  }

  /**
   * Maps {@code GtfsRoute}s by their URLs if provided.
   *
   * @param routeTable the {@code GtfsRouteTableContainer} to extract {@code GtfsRoute} from
   * @return routes from {@code GtfsRouteTableContainer}s mapped by there {@code routes.route_url}
   *     (in lower case) if provided.
   */
  private ListMultimap<String, GtfsRoute> routesByUrlMap(GtfsRouteTableContainer routeTable) {
    ListMultimap<String, GtfsRoute> routesByUrl = ArrayListMultimap.create();
    routeTable
        .getEntities()
        .forEach(
            route -> {
              if (route.hasRouteUrl()) {
                routesByUrl.put(route.routeUrl().toLowerCase(), route);
              }
            });
    return routesByUrl;
  }

  /**
   * Maps {@code GtfsAgency}s by their URLs if provided.
   *
   * @param agencyTable the {@code GtfsAgencyTableContainer} to extract {@code GtfsAgency} from
   * @return agencies from {@code GtfsAgencyTableContainer}s mapped by there URLs (in lower case) if
   *     provided.
   */
  private ListMultimap<String, GtfsAgency> agenciesByUrlMap(GtfsAgencyTableContainer agencyTable) {
    ListMultimap<String, GtfsAgency> agenciesByUrl = ArrayListMultimap.create();
    agencyTable
        .getEntities()
        .forEach(
            agency -> {
              if (agency.hasAgencyUrl()) {
                agenciesByUrl.put(agency.agencyUrl().toLowerCase(), agency);
              }
            });
    return agenciesByUrl;
  }

  /**
   * A {@code GtfsStop} has the same value for {@code stops.stop_url} as a record from "routes.txt".
   *
   * <p>{@code SeverityLevel.WARNING}
   */
  static class SameStopAndRouteUrlNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String stopId;
    private final String stopUrl;
    private final String routeId;
    private final long routeCsvRowNumber;

    SameStopAndRouteUrlNotice(GtfsStop stop, GtfsRoute route) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = stop.csvRowNumber();
      this.stopId = stop.stopId();
      this.stopUrl = stop.stopUrl();
      this.routeId = route.routeId();
      this.routeCsvRowNumber = route.csvRowNumber();
    }
  }

  /**
   * A {@code GtfsRoute} has the same value for {@code routes.route_url} as a record from
   * "agency.txt".
   *
   * <p>{@code SeverityLevel.WARNING}
   */
  static class SameRouteAndAgencyUrlNotice extends ValidationNotice {
    private final long routeCsvRowNumber;
    private final String routeId;
    private final String agencyName;
    private final String routeUrl;
    private final long agencyCsvRowNumber;

    SameRouteAndAgencyUrlNotice(GtfsRoute route, GtfsAgency agency) {
      super(SeverityLevel.WARNING);
      this.routeCsvRowNumber = route.csvRowNumber();
      this.routeId = route.routeId();
      this.agencyName = agency.agencyName();
      this.routeUrl = route.routeUrl();
      this.agencyCsvRowNumber = agency.csvRowNumber();
    }
  }

  /**
   * A {@code GtfsStop} has the same value for {@code stops.stop_url} as a record from "agency.txt".
   *
   * <p>{@code SeverityLevel.WARNING}
   */
  static class SameStopAndAgencyUrlNotice extends ValidationNotice {
    private final long stopCsvRowNumber;
    private final String stopId;
    private final String agencyName;
    private final String stopUrl;
    private final long agencyCsvRowNumber;

    SameStopAndAgencyUrlNotice(GtfsStop stop, GtfsAgency agency) {
      super(SeverityLevel.WARNING);
      this.stopCsvRowNumber = stop.csvRowNumber();
      this.stopId = stop.stopId();
      this.agencyName = agency.agencyName();
      this.stopUrl = stop.stopUrl();
      this.agencyCsvRowNumber = agency.csvRowNumber();
    }
  }
}
