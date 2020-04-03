package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.UnexpectedValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

public class ProcessParsedRoute {

    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final Route.RouteBuilder builder;

    public ProcessParsedRoute(final ValidationResultRepository resultRepository,
                              final GtfsDataRepository gtfsDataRepository,
                              final Route.RouteBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    public void execute(final ParsedEntity validatedParsedRoute) throws NullPointerException {

        String routeId = (String) validatedParsedRoute.get("route_id");
        String agencyId = (String) validatedParsedRoute.get("agency_id");
        String routeShortName = (String) validatedParsedRoute.get("route_short_name");
        String routeLongName = (String) validatedParsedRoute.get("route_long_name");
        String routeDesc = (String) validatedParsedRoute.get("route_desc");
        Integer routeType = (Integer) validatedParsedRoute.get("route_type");
        String routeUrl = (String) validatedParsedRoute.get("route_url");
        String routeColor = (String) validatedParsedRoute.get("route_color");
        String routeTextColor = (String) validatedParsedRoute.get("route_text_color");
        Integer routeSortOrder = (Integer) validatedParsedRoute.get("route_sort_order");

        try {
            builder.routeId(routeId)
                    .agencyId(agencyId)
                    .routeShortName(routeShortName)
                    .routeLongName(routeLongName)
                    .routeDesc(routeDesc)
                    .routeType(routeType)
                    .routeUrl(routeUrl)
                    .routeColor(routeColor)
                    .routeTextColor(routeTextColor)
                    .routeSortOrder(routeSortOrder);

            gtfsDataRepository.addEntity(builder.build());

        } catch (NullPointerException e) {

            if (routeId == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("routes.txt", "route_id",
                        validatedParsedRoute.getEntityId()));
            } else {
                resultRepository.addNotice(new UnexpectedValueNotice("routes.txt",
                        "route_type", validatedParsedRoute.getEntityId(), routeType));
            }
            throw e;
        }
    }
}
