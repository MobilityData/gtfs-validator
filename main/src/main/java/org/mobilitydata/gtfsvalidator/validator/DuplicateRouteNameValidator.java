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
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.util.HashMap;
import java.util.Map;
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
  private static final HashFunction HASH_FUNCTION = Hashing.farmHashFingerprint64();
  private final GtfsRouteTableContainer routeTable;

  @Inject
  DuplicateRouteNameValidator(GtfsRouteTableContainer routeTable) {
    this.routeTable = routeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final Map<Long, GtfsRoute> routeByKey = new HashMap<>(routeTable.entityCount());
    for (GtfsRoute route : routeTable.getEntities()) {
      GtfsRoute prevRoute = routeByKey.putIfAbsent(getRouteKey(route), route);
      if (prevRoute != null) {
        noticeContainer.addValidationNotice(new DuplicateRouteNameNotice(prevRoute, route));
      }
    }
  }

  /** Generates a hash based on route names, type and agency. */
  private static long getRouteKey(GtfsRoute route) {
    return HASH_FUNCTION
        .newHasher()
        .putUnencodedChars(route.routeLongName())
        .putChar('\0')
        .putUnencodedChars(route.routeShortName())
        .putChar('\0')
        .putInt(route.routeType().getNumber())
        .putUnencodedChars(route.agencyId())
        .hash()
        .asLong();
  }

  /**
   * Describes two routes that have the same long and short names, route type and belong to the same
   * agency.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class DuplicateRouteNameNotice extends ValidationNotice {

    /**
     * Constructor used while extracting notice information.
     *
     * @param csvRowNumber1 the first route's csv row number
     * @param routeId1 the first route's id
     * @param csvRowNumber2 the other route's csv row number
     * @param routeId2 the other route's id
     * @param routeShortName the duplicate route short name
     * @param routeLongName the duplicate route long name
     * @param routeTypeValue the route type value
     * @param agencyId the agency id
     */
    DuplicateRouteNameNotice(
        long csvRowNumber1,
        String routeId1,
        long csvRowNumber2,
        String routeId2,
        String routeShortName,
        String routeLongName,
        int routeTypeValue,
        String agencyId) {
      super(
          new ImmutableMap.Builder<String, Object>()
              .put("csvRowNumber1", csvRowNumber1)
              .put("routeId1", routeId1)
              .put("csvRowNumber2", csvRowNumber2)
              .put("routeId2", routeId2)
              .put("routeShortName", routeShortName)
              .put("routeLongName", routeLongName)
              .put("routeType", routeTypeValue)
              .put("agencyId", agencyId)
              .build(),
          SeverityLevel.WARNING);
    }

    /**
     * Default constructor for notice.
     *
     * @param route1 the first route to extract information from.
     * @param route2 the other route to extract information from.
     */
    DuplicateRouteNameNotice(GtfsRoute route1, GtfsRoute route2) {
      this(
          route1.csvRowNumber(),
          route1.routeId(),
          route2.csvRowNumber(),
          route2.routeId(),
          route1.routeShortName(),
          route1.routeLongName(),
          route1.routeTypeValue(),
          route1.agencyId());
    }
  }
}
