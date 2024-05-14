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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteSchema;
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
   * Two distinct routes have either the same `route_short_name`, the same `route_long_name`, or the
   * same combination of `route_short_name` and `route_long_name`.
   *
   * <p>All routes of the same `route_type` with the same `agency_id` should have unique
   * combinations of `route_short_name` and `route_long_name`.
   *
   * <p>Note that there may be valid cases where routes have the same short and long name, e.g., if
   * they serve different areas. However, different directions must be modeled as the same route.
   *
   * <p>Example of bad data:
   *
   * <table style="table-layout:auto; width:auto;">
   *   <tr>
   *     <th><code>route_id</code></th>
   *     <th><code>route_short_name</code></th>
   *     <th><code>route_long_name</code></th>
   *   </tr>
   *   <tr>
   *     <td>route1</td>
   *     <td>U1</td>
   *     <td>Southern</td>
   *   </tr>
   *   <tr>
   *     <td>route2</td>
   *     <td>U1</td>
   *     <td>Southern</td>
   *   </tr>
   * </table>
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs(GtfsRouteSchema.class),
      bestPractices = @FileRefs(GtfsRouteSchema.class))
  static class DuplicateRouteNameNotice extends ValidationNotice {

    /** The row number of the first occurrence. */
    private final int csvRowNumber1;

    /** The id of the the first occurrence. */
    private final String routeId1;

    /** The row number of the other occurrence. */
    private final int csvRowNumber2;

    /** The id of the the other occurrence. */
    private final String routeId2;

    /** Common `routes.route_short_name`. */
    private final String routeShortName;

    /** Common `routes.route_long_name`. */
    private final String routeLongName;

    /** Common `routes.route_type`. */
    private final int routeTypeValue;

    /** Common `routes.agency_id`. */
    private final String agencyId;

    DuplicateRouteNameNotice(GtfsRoute route1, GtfsRoute route2) {
      this.csvRowNumber1 = route1.csvRowNumber();
      this.routeId1 = route1.routeId();
      this.csvRowNumber2 = route2.csvRowNumber();
      this.routeId2 = route2.routeId();
      this.routeShortName = route1.routeShortName();
      this.routeLongName = route1.routeLongName();
      this.routeTypeValue = route1.routeTypeValue();
      this.agencyId = route1.agencyId();
    }
  }
}
