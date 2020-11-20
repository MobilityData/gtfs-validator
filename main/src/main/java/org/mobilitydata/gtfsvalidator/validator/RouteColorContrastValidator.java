package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RouteColorContrastNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;

/**
 * Validates that there is enough contrast between route_color and route_text_color in "routes.txt".
 *
 * Generated notices:
 * * RouteColorContrastNotice
 */
@GtfsValidator
public class RouteColorContrastValidator extends SingleEntityValidator<GtfsRoute> {
    /**
     * The maximum difference between the luma of the route display color
     * and text color, beyond which a warning is produced.
     * http://www.w3.org/TR/2000/WD-AERT-20000426#color-contrast
     * recommends a threshold of 125, but that is for normal text and too harsh
     * for big colored logos like line names, so we allow a tighter threshold.
     */
    private static final int MAX_ROUTE_COLOR_LUMA_DIFFERENCE = 72;

    @Override
    public void validate(GtfsRoute entity, NoticeContainer noticeContainer) {
        if (!entity.hasRouteColor() || !entity.hasRouteTextColor()) {
            // Some of the colors is not given explicitly.
            return;
        }
        if (Math.abs(entity.routeColor().rec601Luma() - entity.routeTextColor().rec601Luma()) <
                MAX_ROUTE_COLOR_LUMA_DIFFERENCE) {
            noticeContainer.addNotice(new RouteColorContrastNotice(entity.routeId(),
                    entity.csvRowNumber(), entity.routeColor(), entity.routeTextColor()));
        }
    }
}
