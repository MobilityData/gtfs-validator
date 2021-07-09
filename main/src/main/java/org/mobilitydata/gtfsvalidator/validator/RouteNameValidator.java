/*
 * Copyright 2020 Google LLC, MobilityData IO
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

import static org.mobilitydata.gtfsvalidator.table.GtfsRouteTableLoader.FILENAME;

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.NoticeExport;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;

/**
 * Validates short and long name for a single route.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link RouteBothShortAndLongNameMissingNotice}
 *   <li>{@link RouteShortAndLongNameEqualNotice}
 *   <li>{@link RouteShortNameTooLongNotice}
 *   <li>{@link SameNameAndDescriptionForRouteNotice}
 * </ul>
 */
@GtfsValidator
public class RouteNameValidator extends SingleEntityValidator<GtfsRoute> {
  private static final int MAX_SHORT_NAME_LENGTH = 12;

  @Override
  public void validate(GtfsRoute entity, NoticeContainer noticeContainer) {
    final boolean hasLongName = entity.hasRouteLongName();
    final boolean hasShortName = entity.hasRouteShortName();

    if (!hasLongName && !hasShortName) {
      noticeContainer.addValidationNotice(
          new RouteBothShortAndLongNameMissingNotice(entity.routeId(), entity.csvRowNumber()));
    }

    if (hasShortName
        && hasLongName
        && entity.routeShortName().equalsIgnoreCase(entity.routeLongName())) {
      noticeContainer.addValidationNotice(
          new RouteShortAndLongNameEqualNotice(
              entity.routeId(), entity.csvRowNumber(),
              entity.routeShortName(), entity.routeLongName()));
    }

    if (hasShortName && entity.routeShortName().length() > MAX_SHORT_NAME_LENGTH) {
      noticeContainer.addValidationNotice(
          new RouteShortNameTooLongNotice(
              entity.routeId(), entity.csvRowNumber(), entity.routeShortName()));
    }
    if (entity.hasRouteDesc()) {
      String routeDesc = entity.routeDesc();
      String routeId = entity.routeId();
      if (hasShortName && !isValidRouteDesc(routeDesc, entity.routeShortName())) {
        noticeContainer.addValidationNotice(
            new SameNameAndDescriptionForRouteNotice(
                entity.csvRowNumber(), routeId, routeDesc, "route_short_name"));
        return;
      }
      if (hasLongName && !isValidRouteDesc(routeDesc, entity.routeLongName())) {
        noticeContainer.addValidationNotice(
            new SameNameAndDescriptionForRouteNotice(
                entity.csvRowNumber(), routeId, routeDesc, "route_long_name"));
      }
    }
  }

  private boolean isValidRouteDesc(String routeDesc, String routeShortOrLongName) {
    // ignore lower case and upper case difference
    return !routeDesc.equalsIgnoreCase(routeShortOrLongName);
  }

  /**
   * Both `routes.route_short_name` and `routes.route_long_name` are missing for a route.
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class RouteBothShortAndLongNameMissingNotice extends ValidationNotice {
    @NoticeExport
    RouteBothShortAndLongNameMissingNotice(String routeId, long csvRowNumber) {
      super(
          ImmutableMap.of(
              "routeId", routeId,
              "csvRowNumber", csvRowNumber),
          SeverityLevel.ERROR);
    }
  }

  /**
   * Short and long name are equal for a route.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class RouteShortAndLongNameEqualNotice extends ValidationNotice {
    @NoticeExport
    RouteShortAndLongNameEqualNotice(
        String routeId, long csvRowNumber, String routeShortName, String routeLongName) {
      super(
          ImmutableMap.of(
              "routeId", routeId,
              "csvRowNumber", csvRowNumber,
              "routeShortName", routeShortName,
              "routeLongName", routeLongName),
          SeverityLevel.WARNING);
    }
  }

  /**
   * Short name of a route is too long (more than 12 characters,
   * https://gtfs.org/best-practices/#routestxt).
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class RouteShortNameTooLongNotice extends ValidationNotice {
    @NoticeExport
    RouteShortNameTooLongNotice(String routeId, long csvRowNumber, String routeShortName) {
      super(
          ImmutableMap.of(
              "routeId", routeId,
              "csvRowNumber", csvRowNumber,
              "routeShortName", routeShortName),
          SeverityLevel.WARNING);
    }
  }

  /**
   * A {@code GtfsRoute} has identical value for `routes.route_desc` and
   * `routes.route_long_name`{@code /}`routes`{@code /}route_short_name.
   *
   * <p>"Do not simply duplicate the name of the location."
   * (http://gtfs.org/reference/static#routestxt)
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class SameNameAndDescriptionForRouteNotice extends ValidationNotice {
    @NoticeExport
    SameNameAndDescriptionForRouteNotice(
        long csvRowNumber, String routeId, String routeDesc, String routeShortOrLongName) {
      super(
          new ImmutableMap.Builder<String, Object>()
              .put("filename", FILENAME)
              .put("routeId", routeId)
              .put("csvRowNumber", csvRowNumber)
              .put("routeDesc", routeDesc)
              .put("specifiedField", routeShortOrLongName)
              .build(),
          SeverityLevel.ERROR);
    }
  }
}
