package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.InconsistentAgencyFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableLoader;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Validates that all agencies have the same timezone and language and that agency_id field is set if there is more than
 * 1 agency.
 *
 * Generated notices:
 * * MissingRequiredFieldError - multiple agencies present but no agency_id set
 * * InconsistentAgencyFieldNotice - inconsistent timezone or language among the agencies
 */
@GtfsValidator
public class AgencyConsistencyValidator extends FileValidator {
    @Inject
    GtfsAgencyTableContainer agencyTable;

    @Override
    public void validate(NoticeContainer noticeContainer) {
        final int agencyCount = agencyTable.entityCount();
        if (agencyCount < 2) {
            return;
        }

        for (GtfsAgency agency : agencyTable.getEntities()) {
            // agency_id is required when there are 2 or more agencies.
            if (!agency.hasAgencyId()) {
                noticeContainer.addNotice(
                        new MissingRequiredFieldError(agencyTable.gtfsFilename(),
                                agency.csvRowNumber(),
                                GtfsAgencyTableLoader.AGENCY_ID_FIELD_NAME));
            }
        }

        TimeZone commonTimezone = agencyTable.getEntities().get(0).agencyTimezone();
        boolean hasLanguage = agencyTable.getEntities().get(0).hasAgencyLang();
        Locale commonLanguage = agencyTable.getEntities().get(0).agencyLang();
        // Timezone and language must be the same for all agencies.
        for (int i = 1; i < agencyCount; ++i) {
            GtfsAgency agency = agencyTable.getEntities().get(i);
            if (!commonTimezone.getID().equals(agency.agencyTimezone().getID())) {
                noticeContainer.addNotice(
                        new InconsistentAgencyFieldNotice(
                                agency.csvRowNumber(),
                                GtfsAgencyTableLoader.AGENCY_TIMEZONE_FIELD_NAME,
                                commonTimezone.getID(),
                                agency.agencyTimezone().getID()));
            }
            if (hasLanguage != agency.hasAgencyLang()
                    || (hasLanguage && agency.hasAgencyLang() && !commonLanguage.equals(agency.agencyLang()))) {
                noticeContainer.addNotice(
                        new InconsistentAgencyFieldNotice(
                                agency.csvRowNumber(),
                                GtfsAgencyTableLoader.AGENCY_LANG_FIELD_NAME,
                                hasLanguage ? commonLanguage.getLanguage() : "",
                                agency.hasAgencyLang() ? agency.agencyLang().getLanguage() : ""));
            }
        }
    }
}


