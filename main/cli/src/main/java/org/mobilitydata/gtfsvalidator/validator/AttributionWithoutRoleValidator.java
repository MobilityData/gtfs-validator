/*
 * Copyright 2021 Google LLC, MobilityData IO
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
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.SchemaExport;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsAttribution;
import org.mobilitydata.gtfsvalidator.table.GtfsAttributionRole;

/**
 * Validates that an attribution has at least one role: is_producer, is_operator, or is_authority.
 *
 * <p>Generated notice: {@link AttributionWithoutRoleNotice}.
 */
@GtfsValidator
public class AttributionWithoutRoleValidator extends SingleEntityValidator<GtfsAttribution> {

  private static boolean hasSomeRole(GtfsAttribution attribution) {
    return attribution.isProducer().equals(GtfsAttributionRole.ASSIGNED)
        || attribution.isAuthority().equals(GtfsAttributionRole.ASSIGNED)
        || attribution.isOperator().equals(GtfsAttributionRole.ASSIGNED);
  }

  @Override
  public void validate(GtfsAttribution attribution, NoticeContainer noticeContainer) {
    if (!hasSomeRole(attribution)) {
      noticeContainer.addValidationNotice(
          new AttributionWithoutRoleNotice(
              attribution.csvRowNumber(), attribution.attributionId()));
    }
  }

  /**
   * A row from GTFS file `attributions.txt` has fields `attributions.is_producer`,
   * `attributions.is_operator`, and `attributions.is_authority` not defined or set at 0.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class AttributionWithoutRoleNotice extends ValidationNotice {

    @SchemaExport
    AttributionWithoutRoleNotice(long csvRowNumber, String attributionId) {
      super(
          ImmutableMap.of("csvRowNumber", csvRowNumber, "attributionId", attributionId),
          SeverityLevel.WARNING);
    }
  }
}
