package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableLoader;

/**
 * Checks that agency_id field in "routes.txt" is defined for every row if there is more than 1 agency in the feed.
 *
 * Generated notices:
 * * MissingRequiredFieldError
 */
@GtfsValidator
public class TripAgencyIdValidator extends FileValidator {
    @Inject
    GtfsAgencyTableContainer agencyTable;

    @Inject
    GtfsRouteTableContainer routeTable;

    @Override
    public void validate(NoticeContainer noticeContainer) {
        if (agencyTable.entityCount() < 2) {
            // routes.agency_id is not required when there is a single agency.
            return;
        }
        for (GtfsRoute route : routeTable.getEntities()) {
            if (!route.hasAgencyId()) {
                noticeContainer.addNotice(
                        new MissingRequiredFieldError(
                                routeTable.gtfsFilename(),
                                route.csvRowNumber(),
                                GtfsRouteTableLoader.AGENCY_ID_FIELD_NAME));
            }
            // No need to check reference integrity because it is done by a validator generated from @ForeignKey annotation.
        }
    }
}
