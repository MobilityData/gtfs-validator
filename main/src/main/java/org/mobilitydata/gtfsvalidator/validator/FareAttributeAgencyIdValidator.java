/*
 * Copyright 2023 Google LLC
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
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareAttribute;
import org.mobilitydata.gtfsvalidator.table.GtfsFareAttributeTableContainer;

/**
 * Checks that {@code fare_attributes.agency_id} field is defined for every row if there is more
 * than 1 agency in the feed.
 *
 * <p>Generated notice: {@link MissingRequiredFieldNotice}.
 */
@GtfsValidator
public class FareAttributeAgencyIdValidator extends FileValidator {
  private final GtfsFareAttributeTableContainer fareAttributeTable;
  private final GtfsAgencyTableContainer agencyTable;

  @Inject
  FareAttributeAgencyIdValidator(
      GtfsFareAttributeTableContainer fareAttributeTable, GtfsAgencyTableContainer agencyTable) {
    this.fareAttributeTable = fareAttributeTable;
    this.agencyTable = agencyTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (agencyTable.entityCount() <= 1) {
      // fare_attributes.agency_id is not required when there is a single agency.
      return;
    }
    for (GtfsFareAttribute fareAttribute : fareAttributeTable.getEntities()) {
      if (!fareAttribute.hasAgencyId()) {
        noticeContainer.addValidationNotice(
            new MissingRequiredFieldNotice(GtfsFareAttribute.FILENAME, fareAttribute.csvRowNumber(),
                GtfsFareAttribute.AGENCY_ID_FIELD_NAME));
      }
    }
  }
}
