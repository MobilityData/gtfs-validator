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

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
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
 *   <li>all {@code GtfsStop} have: {@code stops.route_url != agency.agency_url}.
 *   <li>all {@code GtfsStop} have: {@code stops.route_url != routes.route_url}.
 * </ul>
 *
 * <p>Generated notice:
 *
 * <ul>
 *   <li>{@link SameRouteAndAgencyUrlNotice} - for a record from "routes.txt", the value of {@code
 *       routes.route_url} is shared by a record from "agency.txt".
 *   <li>{@link SameStopAndAgencyUrlNotice} - for a record from "stops.txt", the value of {@code
 *       stops.route_url} is shared by a record from "agency.txt". *
 *   <li>{@link SameStopAndRouteUrlNotice} - for a record from "stops.txt", the value of {@code
 *       stops.route_url} is shared by a record from "routes.txt".
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
    Map<String, GtfsAgency> agencyByUrlMap = agenciesByUrlMap(agencyTable);
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
                agency.csvRowNumber()));
      }
    }
    for (GtfsStop stop : stopTable.getEntities()) {
      if (!stop.hasStopUrl()) {
        return;
      }
      GtfsAgency agency = agencyByUrlMap.get(stop.stopUrl().toLowerCase());
      if (agency != null) {
        noticeContainer.addValidationNotice(
            new SameStopAndAgencyUrlNotice(
                stop.csvRowNumber(),
                stop.stopId(),
                agency.agencyName(),
                stop.stopUrl(),
                agency.csvRowNumber()));
      }
    }
    Map<String, GtfsRoute> routesByUrlMap = routesByUrlMap(routeTable);
    Map<String, GtfsStop> stopsByUrlMap = stopsByUrlMap(stopTable);

    Maps.filterEntries(
            stopsByUrlMap, entry -> routesByUrlMap.get(entry.getValue().stopUrl()) != null)
        .values()
        .forEach(
            stopWithDuplicateUrl -> {
              noticeContainer.addValidationNotice(
                  new SameStopAndRouteUrlNotice(
                      stopWithDuplicateUrl.csvRowNumber(),
                      stopWithDuplicateUrl.stopId(),
                      stopWithDuplicateUrl.stopUrl(),
                      routesByUrlMap.get(stopWithDuplicateUrl.stopUrl()).routeId(),
                      routesByUrlMap.get(stopWithDuplicateUrl.stopUrl()).csvRowNumber()));
            });
  }

  /**
   * Maps {@code GtfsAgency}s by there URLs if provided.
   *
   * @param stopTable the {@code GtfsStopTableContainer} to extract {@code GtfsStop} from
   * @return routes from {@code GtfsStopTableContainer}s mapped by there {@code routes.route_url}
   *     (in lower case) if provided.
   */
  private Map<String, GtfsStop> stopsByUrlMap(GtfsStopTableContainer stopTable) {
    Map<String, GtfsStop> stopsByUrl = new HashMap<>();
    stopTable
        .getEntities()
        .forEach(
            stop -> {
              if (stop.hasStopUrl()) {
                if (stopsByUrl.get(stop.stopUrl()) == null) {
                  stopsByUrl.put(stop.stopUrl().toLowerCase(), stop);
                }
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
  private Map<String, GtfsRoute> routesByUrlMap(GtfsRouteTableContainer routeTable) {
    Map<String, GtfsRoute> routesByUrl = new HashMap<>();
    routeTable
        .getEntities()
        .forEach(
            route -> {
              if (route.hasRouteUrl()) {
                if (routesByUrl.get(route.routeUrl()) == null) {
                  routesByUrl.put(route.routeUrl().toLowerCase(), route);
                }
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
  private Map<String, GtfsAgency> agenciesByUrlMap(GtfsAgencyTableContainer agencyTable) {
    Map<String, GtfsAgency> agenciesByUrl = new HashMap<>();
    agencyTable
        .getEntities()
        .forEach(
            agency -> {
              if (agency.hasAgencyUrl()) {
                if (agenciesByUrl.get(agency.agencyUrl()) == null) {
                  agenciesByUrl.put(agency.agencyUrl().toLowerCase(), agency);
                }
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
    private long csvRowNumber;
    private String stopId;
    private String stopUrl;
    private String routeId;
    private long routeCsvRowNumber;

    SameStopAndRouteUrlNotice(
        long csvRowNumber, String stopId, String stopUrl, String routeId, long routeCsvRowNumber) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = csvRowNumber;
      this.stopId = stopId;
      this.stopUrl = stopUrl;
      this.routeId = routeId;
      this.routeCsvRowNumber = routeCsvRowNumber;
    }
  }

  /**
   * A {@code GtfsRoute} has the same value for {@code routes.route_url} as a record from
   * "agency.txt".
   *
   * <p>{@code SeverityLevel.WARNING}
   */
  static class SameRouteAndAgencyUrlNotice extends ValidationNotice {
    private long csvRowNumber;
    private String routeId;
    private String agencyName;
    private String routeUrl;
    private long agencyCsvRowNumber;

    SameRouteAndAgencyUrlNotice(
        long csvRowNumber,
        String routeId,
        String agencyName,
        String routeUrl,
        long agencyCsvRowNumber) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = csvRowNumber;
      this.routeId = routeId;
      this.agencyName = agencyName;
      this.routeUrl = routeUrl;
      this.agencyCsvRowNumber = agencyCsvRowNumber;
    }
  }

  /**
   * A {@code GtfsStop} has the same value for {@code stops.stop_url} as a record from "agency.txt".
   *
   * <p>{@code SeverityLevel.WARNING}
   */
  static class SameStopAndAgencyUrlNotice extends ValidationNotice {
    private long csvRowNumber;
    private String stopId;
    private String agencyName;
    private String stopUrl;
    private long agencyCsvRowNumber;

    SameStopAndAgencyUrlNotice(
        long csvRowNumber,
        String stopId,
        String agencyName,
        String stopUrl,
        long agencyCsvRowNumber) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = csvRowNumber;
      this.stopId = stopId;
      this.agencyName = agencyName;
      this.stopUrl = stopUrl;
      this.agencyCsvRowNumber = agencyCsvRowNumber;
    }
  }
}
