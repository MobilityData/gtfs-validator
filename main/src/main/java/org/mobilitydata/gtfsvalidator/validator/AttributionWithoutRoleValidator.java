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

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.AttributionWithoutRoleNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAttribution;
import org.mobilitydata.gtfsvalidator.table.GtfsAttributionRole;
import org.mobilitydata.gtfsvalidator.table.GtfsAttributionTableContainer;

/**
 * Validates that for all attributions from GTFS file `attributions.txt`: at least one of the fields
 * is_producer, is_operator, or is_authority is set at 1
 * (http://gtfs.org/reference/static#attributionstxt).
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link AttributionWithoutRoleNotice} - is_producer, is_operator, and is_authority and
 *       undefined or set to 0
 * </ul>
 */
@GtfsValidator
public class AttributionWithoutRoleValidator extends FileValidator {

  @Inject GtfsAttributionTableContainer attributionTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsAttribution attribution : attributionTable.getEntities()) {
      if (attribution.isAuthority().equals(GtfsAttributionRole.NOT_ASSIGNED)
          && attribution.isProducer().equals(GtfsAttributionRole.NOT_ASSIGNED)
          && attribution.isOperator().equals(GtfsAttributionRole.NOT_ASSIGNED)) {
        noticeContainer.addValidationNotice(
            new AttributionWithoutRoleNotice(attribution.csvRowNumber()));
      }
    }
  }
}
