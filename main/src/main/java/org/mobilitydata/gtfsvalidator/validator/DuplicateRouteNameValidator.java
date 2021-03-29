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

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

/** Validates that combinations of route type, short and long name are unique within an agency. */
@GtfsValidator
public class DuplicateRouteNameValidator extends FileValidator {
  private final GtfsRouteTableContainer routeTable;

  @Inject
  DuplicateRouteNameValidator(GtfsRouteTableContainer routeTable) {
    this.routeTable = routeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final Map<Integer, GtfsRoute> routeByKey = new HashMap<>(routeTable.entityCount());
    for (GtfsRoute route : routeTable.getEntities()) {
      GtfsRoute prevRoute = routeByKey.putIfAbsent(getRouteKey(route), route);
      if (prevRoute != null) {
        noticeContainer.addValidationNotice(new DuplicateRouteNameNotice(prevRoute, route));
      }
    }
  }

  /** Generates a hash based on route names, type and agency. */
  private static int getRouteKey(GtfsRoute route) {
    return Objects.hash(
        route.routeLongName(), route.routeShortName(), route.routeType(), route.agencyId());
  }

  /**
   * Describes two routes that have the same long and short names, route type and belong to the same
   * agency.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class DuplicateRouteNameNotice extends ValidationNotice {
    DuplicateRouteNameNotice(GtfsRoute route1, GtfsRoute route2) {
      super(
          new ImmutableMap.Builder<String, Object>()
              .put("csvRowNumber1", route1.csvRowNumber())
              .put("routeId1", route1.routeId())
              .put("csvRowNumber2", route2.csvRowNumber())
              .put("routeId2", route2.routeId())
              .put("routeShortName", route1.routeShortName())
              .put("routeLongName", route1.routeLongName())
              .put("routeType", route1.routeTypeValue())
              .put("agencyId", route1.agencyId())
              .build(),
          SeverityLevel.WARNING);
    }
  }
}
