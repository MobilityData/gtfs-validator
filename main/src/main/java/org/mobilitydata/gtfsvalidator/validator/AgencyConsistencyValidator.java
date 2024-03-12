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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import java.time.ZoneId;
import java.util.Locale;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.*;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencySchema;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;

/**
 * Validates that all agencies have the same timezone and language and that agency_id field is set
 * if there is more than 1 agency.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link MissingRequiredFieldNotice} - multiple agencies present but no agency_id set
 *   <li>{@link MissingRecommendedFieldNotice} - single agency present but no agency_id set
 *   <li>{@link InconsistentAgencyTimezoneNotice} - inconsistent timezone among the agencies
 *   <li>{@link InconsistentAgencyLangNotice} - inconsistent language among the agencies
 * </ul>
 */
@GtfsValidator
public class AgencyConsistencyValidator extends FileValidator {

  private final GtfsAgencyTableContainer agencyTable;

  @Inject
  AgencyConsistencyValidator(GtfsAgencyTableContainer agencyTable) {
    this.agencyTable = agencyTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final int agencyCount = agencyTable.entityCount();
    if (agencyCount == 1) {
      GtfsAgency agency = agencyTable.getEntities().get(0);
      // agency_id is recommended even when there is only 1 agency
      if (!agency.hasAgencyId()) {
        noticeContainer.addValidationNotice(
            new MissingRecommendedFieldNotice(
                agencyTable.gtfsFilename(),
                agency.csvRowNumber(),
                GtfsAgency.AGENCY_ID_FIELD_NAME));
      }
      // no further validation required
      return;
    }
    if (agencyCount > 1) {
      for (GtfsAgency agency : agencyTable.getEntities()) {
        // agency_id is required when there are 2 or more agencies.
        if (!agency.hasAgencyId()) {
          noticeContainer.addValidationNotice(
              new MissingRequiredFieldNotice(
                  agencyTable.gtfsFilename(),
                  agency.csvRowNumber(),
                  GtfsAgency.AGENCY_ID_FIELD_NAME));
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
   * Inconsistent language among agencies.
   *
   * <p>Agencies from GTFS `agency.txt` have been found to have different languages.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs(GtfsAgencySchema.class),
      urls = {
        @UrlRef(
            label = "Original Python validator implementation",
            url = "https://github.com/google/transitfeed")
      })
  static class InconsistentAgencyLangNotice extends ValidationNotice {

    /** The row of the faulty record. */
    private final int csvRowNumber;

    /** Expected language. */
    private final String expected;

    /** Faulty record's language. */
    private final String actual;

    InconsistentAgencyLangNotice(int csvRowNumber, String expected, String actual) {
      this.csvRowNumber = csvRowNumber;
      this.expected = expected;
      this.actual = actual;
    }
  }

  /**
   * Inconsistent Timezone among agencies.
   *
   * <p>Agencies from GTFS `agency.txt` have been found to have different timezones.
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsAgencySchema.class))
  static class InconsistentAgencyTimezoneNotice extends ValidationNotice {

    /** The row of the faulty record. */
    private final int csvRowNumber;

    /** Expected timezone. */
    private final String expected;

    /** Faulty record's timezone. */
    private final String actual;

    InconsistentAgencyTimezoneNotice(int csvRowNumber, String expected, String actual) {
      this.csvRowNumber = csvRowNumber;
      this.expected = expected;
      this.actual = actual;
    }
  }
}
