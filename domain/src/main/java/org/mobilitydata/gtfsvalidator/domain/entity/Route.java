package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Route {

    @NotNull
    final String route_id;

    private final String agency_id;
    private final String route_short_name;
    private final String route_long_name;
    private final String route_desc;

    @NotNull
    final RouteType route_type;

    private final String route_url;
    private final String route_color;
    private final String route_text_color;
    private final int route_sort_order;


    public Route(@NotNull String route_id, String agency_id, String route_short_name, String route_long_name,
                 String route_desc, @NotNull RouteType route_type, String route_url, String route_color,
                 String route_text_color, int route_sort_order) {
        this.route_id = route_id;
        this.agency_id = agency_id;
        this.route_short_name = route_short_name;
        this.route_long_name = route_long_name;
        this.route_desc = route_desc;
        this.route_type = route_type;
        this.route_url = route_url;
        this.route_color = route_color;
        this.route_text_color = route_text_color;
        this.route_sort_order = route_sort_order;
    }


    public String getRoute_id() {
        return route_id;
    }

    public String getAgency_id() {
        return agency_id;
    }

    public String getRoute_short_name() {
        return route_short_name;
    }

    public String getRoute_long_name() {
        return route_long_name;
    }

    public String getRoute_desc() {
        return route_desc;
    }

    public RouteType getRoute_type() {
        return route_type;
    }

    public String getRoute_url() {
        return route_url;
    }

    public String getRoute_color() {
        return route_color;
    }

    public String getRoute_text_color() {
        return route_text_color;
    }

    public int getRoute_sort_order() {
        return route_sort_order;
    }

    public static class RouteBuilder {

        protected String route_id;
        protected String agency_id;
        protected String route_short_name;
        protected String route_long_name;
        protected String route_desc;
        protected RouteType route_type;
        protected String route_url;
        protected String route_color;
        protected String route_text_color;
        protected int route_sort_order;

        public RouteBuilder routeId(@NotNull String route_id) {
            this.route_id = route_id;
            return this;
        }

        public RouteBuilder routeAgencyId(@Nullable String agency_id) {
            this.agency_id = agency_id;
            return this;
        }

        public RouteBuilder routeShortName(@Nullable String route_short_name) {
            this.route_short_name = route_short_name;
            return this;
        }

        public RouteBuilder routeLongName(@Nullable String route_long_name) {
            this.route_long_name = route_long_name;
            return this;
        }

        public RouteBuilder routeDesc(@Nullable String route_desc) {
            this.route_desc = route_desc;
            return this;
        }

        public RouteBuilder routeType(@NotNull int route_type) {
            this.route_type = RouteType.fromInt(route_type);
            return this;
        }

        public RouteBuilder routeUrl(@Nullable String route_url) {
            this.route_url = route_url;
            return this;
        }

        public RouteBuilder routeColor(@Nullable String route_color) {
            this.route_color = route_color;
            return this;
        }

        public RouteBuilder routeTextColor(@Nullable String route_text_color) {
            this.route_text_color = route_text_color;
            return this;
        }

        public RouteBuilder routeSortOrder(@Nullable int route_sort_order) {
            this.route_sort_order = route_sort_order;
            return this;
        }

        public Route build() {
            Route route = new Route(route_id, agency_id, route_short_name, route_long_name, route_desc, route_type,
                    route_url, route_color, route_text_color, route_sort_order);
            return route;
        }
    }
}
