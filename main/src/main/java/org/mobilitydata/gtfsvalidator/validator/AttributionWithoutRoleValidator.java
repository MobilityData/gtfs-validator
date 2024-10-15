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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsAttribution;
import org.mobilitydata.gtfsvalidator.table.GtfsAttributionRole;
import org.mobilitydata.gtfsvalidator.table.GtfsAttributionSchema;

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

  public boolean shouldCallValidate(ColumnInspector header) {
    if (!header.hasColumn(GtfsAttribution.IS_PRODUCER_FIELD_NAME)
        && !header.hasColumn(GtfsAttribution.IS_OPERATOR_FIELD_NAME)
        && !header.hasColumn(GtfsAttribution.IS_AUTHORITY_FIELD_NAME)) {
      return false;
    }
    return true;
  }

  /**
   * Attribution with no role.
   *
   * <p>At least one of the fields `is_producer`, `is_operator`, or `is_authority` should be set to
   * 1.
   */
  @GtfsValidationNotice(severity = WARNING, files = @FileRefs(GtfsAttributionSchema.class))
  static class AttributionWithoutRoleNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The id of the faulty record. */
    private final String attributionId;

    AttributionWithoutRoleNotice(int csvRowNumber, String attributionId) {
      this.csvRowNumber = csvRowNumber;
      this.attributionId = attributionId;
    }
  }
}
