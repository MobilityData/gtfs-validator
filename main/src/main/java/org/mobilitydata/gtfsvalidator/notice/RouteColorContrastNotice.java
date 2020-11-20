package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;

public class RouteColorContrastNotice extends Notice {
    public RouteColorContrastNotice(String routeId, long csvRowNumber, GtfsColor routeColor, GtfsColor routeTextColor) {
        super(ImmutableMap.of("routeId", routeId,
                "csvRowNumber", csvRowNumber,
                "routeColor", routeColor.toHtmlColor(),
                "routeTextColor", routeTextColor.toHtmlColor()));
    }

    @Override
    public String getCode() {
        return "route_color_contrast";
    }
}
