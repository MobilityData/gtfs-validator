package org.mobilitydata.gtfsvalidator.input;

public enum GrfsFileEnum {
    AGENCY("agency.txt"),
    STOPS("stops.txt"),
    ROUTES("routes.txt"),
    TRIPS("trips.txt"),
    STOPTIMES("stop_times.txt"),
    CALENDAR("calendar.txt"),
    CALENDARDATES("calendar_dates.txt"),
    FAREATTRIBUTES("fare_attributes.txt"),
    FARERULES("fare_rules.txt"),
    FAREMEDIA("fare_media.txt"),
    FAREPRODUCTS("fare_products.txt"),
    FARELEGRULES("fare_leg_rules.txt"),
    FARETRANSFERRULES("fare_transfer_rules.txt"),
    AREAS("areas.txt"),
    STOPAREAS("stop_areas.txt"),
    SHAPES("shapes.txt"),
    FREQUENCIES("frequencies.txt"),
    TRANSFERS("transfers.txt"),
    PATHWAYS("pathways.txt"),
    LEVELS("levels.txt"),
    TRANSLATIONS("translations.txt"),
    FEEDINFO("feed_info.txt"),
    ATTRIBUTIOMS("attributions.txt");

    private final String gtfsFileName;
    GrfsFileEnum(String gtfsFileName) {
        this.gtfsFileName = gtfsFileName;
    }

    public String getGtfsFileName() {
        return gtfsFileName;
    }
}
