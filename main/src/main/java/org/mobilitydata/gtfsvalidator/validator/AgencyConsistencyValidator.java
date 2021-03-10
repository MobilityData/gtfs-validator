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

import java.time.ZoneId;
import java.util.Locale;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.AgencyIdBlankNotice;
import org.mobilitydata.gtfsvalidator.notice.AgencyIdMissingNotice;
import org.mobilitydata.gtfsvalidator.notice.InconsistentAgencyLangNotice;
import org.mobilitydata.gtfsvalidator.notice.InconsistentAgencyTimezoneNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableLoader;

/**
 * Validates that all agencies have the same timezone and language and that agency_id field is set
 * if there is more than 1 agency.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link MissingRequiredFieldError} - multiple agencies present but no agency_id set
 *   <li>{@link InconsistentAgencyTimezoneNotice} - inconsistent timezone among the agencies
 *   <li>{@link InconsistentAgencyLangNotice} - inconsistent language among the agencies
 *   <li>{@link AgencyIdBlankNotice} - agency_id is blank, e.g. " "
 *   <li>{@link AgencyIdMissingNotice} - agency_id is missing
 * </ul>
 */
@GtfsValidator
public class AgencyConsistencyValidator extends FileValidator {

  @Inject GtfsAgencyTableContainer agencyTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final int agencyCount = agencyTable.entityCount();

    if (agencyCount < 2) {
      for (GtfsAgency agency : agencyTable.getEntities()) {
        if (!agency.hasAgencyId()) {
          noticeContainer.addValidationNotice(new AgencyIdMissingNotice(agency.csvRowNumber()));
        }
        if (isAgencyIdBlank(agency)) {
          noticeContainer.addValidationNotice(new AgencyIdBlankNotice(agency.csvRowNumber()));
        }
      }
      return;
    }

    for (GtfsAgency agency : agencyTable.getEntities()) {
      if (!agency.hasAgencyId()) {
        // agency_id is required when there are 2 or more agencies.
        noticeContainer.addValidationNotice(
            new MissingRequiredFieldError(
                agencyTable.gtfsFilename(),
                agency.csvRowNumber(),
                GtfsAgencyTableLoader.AGENCY_ID_FIELD_NAME));
      }
      if (isAgencyIdBlank(agency)) {
        noticeContainer.addValidationNotice(new AgencyIdBlankNotice(agency.csvRowNumber()));
      }
    }

    // agency_timezone field is required and it must be the same for all agencies.
    ZoneId commonTimezone = agencyTable.getEntities().get(0).agencyTimezone();
    for (int i = 1; i < agencyCount; ++i) {
      GtfsAgency agency = agencyTable.getEntities().get(i);
      if (!commonTimezone.equals(agency.agencyTimezone())) {
        noticeContainer.addValidationNotice(
            new InconsistentAgencyTimezoneNotice(
                agency.csvRowNumber(), commonTimezone.getId(), agency.agencyTimezone().getId()));
      }
    }

    // agency_lang field is optional. All provided values must be the same for all agencies.
    Locale commonLanguage = null;
    for (int i = 0; i < agencyCount; ++i) {
      GtfsAgency agency = agencyTable.getEntities().get(i);
      if (!agency.hasAgencyLang()) {
        // This is OK to omit agency_lang.
        continue;
      }
      if (commonLanguage == null) {
        // This is the first agency that has language specified.
        commonLanguage = agency.agencyLang();
      } else if (!commonLanguage.equals(agency.agencyLang())) {
        noticeContainer.addValidationNotice(
            new InconsistentAgencyLangNotice(
                agency.csvRowNumber(),
                commonLanguage.getLanguage(),
                agency.agencyLang().getLanguage()));
      }
    }
  }

  /**
   * Checks is an `agency.agency_id` is blank i.e. `agency_id` is a sequence of whitespaces
   *
   * @param agency the {@code GtfsAgency} to check the id
   * @return true if `agency.agency_id` is blank (i.e. `agency_id` is a sequence of whitespaces),
   *     false otherwise
   */
  private boolean isAgencyIdBlank(GtfsAgency agency) {
    return !agency.hasAgencyId() || StringUtils.isBlank(agency.agencyId());
  }
}
