package org.mobilitydata.gtfsvalidator.input;

public enum GtfsFiles {
  AGENCY("agency.txt"),
  STOPS("stops.txt"),
  ROUTES("routes.txt"),
  TRIPS("trips.txt"),
  STOP_TIMES("stop_times.txt"),
  CALENDAR("calendar.txt"),
  CALENDAR_DATES("calendar_dates.txt"),
  FARE_ATTRIBUTES("fare_attributes.txt"),
  FARE_RULES("fare_rules.txt"),
  FARE_MEDIA("fare_media.txt"),
  FARE_PRODUCTS("fare_products.txt"),
  FARE_LEG_RULES("fare_leg_rules.txt"),
  FARE_TRANSFER_RULES("fare_transfer_rules.txt"),
  AREAS("areas.txt"),
  STOP_AREAS("stop_areas.txt"),
  SHAPES("shapes.txt"),
  FREQUENCIES("frequencies.txt"),
  TRANSFERS("transfers.txt"),
  PATHWAYS("pathways.txt"),
  LEVELS("levels.txt"),
  TRANSLATIONS("translations.txt"),
  FEED_INFO("feed_info.txt"),
  ATTRIBUTIONS("attributions.txt");

  private final String gtfsFileName;

  GtfsFiles(String gtfsFileName) {
    this.gtfsFileName = gtfsFileName;
  }

  public String getGtfsFileName() {
    return gtfsFileName;
  }

  /**
   * Check if a String value equals to Gtfs file name
   *
   * @param value
   * @return
   */
  public static boolean containsGtfsFile(String value) {
    GtfsFiles[] files = GtfsFiles.values();
    for (GtfsFiles f : files) {
      if (f.getGtfsFileName().equals(value)) {
        return true;
      }
    }
    return false;
  }
}
