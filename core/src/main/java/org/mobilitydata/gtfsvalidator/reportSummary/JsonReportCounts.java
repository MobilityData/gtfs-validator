package org.mobilitydata.gtfsvalidator.reportSummary;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class JsonReportCounts {

  /*
   * Use these strings as keys in the counts map. Also used to specify the info that will appear in
   * the json report. Adding elements to feedInfo will not automatically be included in the json
   * report and should be explicitly handled in the json report code.
   */
  public static final String COUNTS_SHAPES = "Shapes";
  public static final String COUNTS_STOPS = "Stops";
  public static final String COUNTS_ROUTES = "Routes";
  public static final String COUNTS_TRIPS = "Trips";
  public static final String COUNTS_AGENCIES = "Agencies";
  public static final String COUNTS_BLOCKS = "Blocks";

  public JsonReportCounts(Map<String, Integer> counts) {
    shapes = counts.get(COUNTS_SHAPES);
    stops = counts.get(COUNTS_STOPS);
    routes = counts.get(COUNTS_ROUTES);
    trips = counts.get(COUNTS_TRIPS);
    agencies = counts.get(COUNTS_AGENCIES);
    blocks = counts.get(COUNTS_BLOCKS);
  }

  /** Number of shapes in `shapes.txt`. */
  @SerializedName("Shapes")
  Integer shapes;

  /** Number of stops in `stops.txt`. */
  @SerializedName("Stops")
  Integer stops;

  /** Number of routes in `routes.txt`. */
  @SerializedName("Routes")
  Integer routes;

  /** Number of trips in `trips.txt`. */
  @SerializedName("Trips")
  Integer trips;

  /** Number of agencies in `agency.txt`. */
  @SerializedName("Agencies")
  Integer agencies;

  /** Number of blocks in `blocks.txt`. */
  @SerializedName("Blocks")
  Integer blocks;
}
