/*
 * Copyright 2020 Google LLC
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
import org.mobilitydata.gtfsvalidator.notice.*;
import org.mobilitydata.gtfsvalidator.table.*;

/**
 * Checks that agency_id field in "fare_attributes.txt" is defined for every row if there is more
 * than 1 agency in the feed, recommended if only 1 agency.
 *
 * <p>Generated notice: {@link MissingRequiredFieldNotice}.
 *
 * <p>Generated notice: {@link MissingRecommendedFieldNotice}.
 */
@GtfsValidator
public class FareAttributeAgencyIdValidator extends FileValidator {
  private final GtfsAgencyTableContainer agencyTable;
  private final GtfsFareAttributeTableContainer attributeTable;

  @Inject
  FareAttributeAgencyIdValidator(
      GtfsAgencyTableContainer agencyTable, GtfsFareAttributeTableContainer attributeTable) {
    this.agencyTable = agencyTable;
    this.attributeTable = attributeTable;
  }

  @Override
  public boolean shouldCallValidate() {
    if (agencyTable == null || agencyTable.entityCount() == 0) {
      // agencyTable is a required file, so if it's null there will be a notice issued by another
      // validator.
      return false;
    }
    return attributeTable != null && attributeTable.entityCount() > 0;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // routes.agency_id is required when there are multiple agencies
    int totalAgencies = agencyTable.entityCount();

    for (GtfsFareAttribute fare : attributeTable.getEntities()) {
      if (!fare.hasAgencyId()) {
        if (totalAgencies > 1) {
          // add error notice if more than one agency
          noticeContainer.addValidationNotice(
              new MissingRequiredAgencyIdNotice(
                  attributeTable.gtfsFilename(), fare.csvRowNumber(), null));
        } else {
          // add warning notice if only one agency
          noticeContainer.addValidationNotice(
              new MissingRecommendedFieldNotice(
                  attributeTable.gtfsFilename(),
                  fare.csvRowNumber(),
                  GtfsFareAttribute.AGENCY_ID_FIELD_NAME));
        }
      }
    }
    // No need to check reference integrity because it is done by a validator generated from
    // @ForeignKey annotation.
  }
}
