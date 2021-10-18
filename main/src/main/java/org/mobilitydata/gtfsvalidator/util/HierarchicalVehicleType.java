package org.mobilitydata.gtfsvalidator.util;

import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;

/**
 * Hierarchical Vehicle Type (HVT) codes from European TPEG standard provide a support a more rich
 * set of vehicle types than the standard GTFS.
 *
 * <p>HVT are not a part of the GTFS standard (and probably won't be) but they are widely used. The
 * standard GTFS validator emits a warning for a hierarchical vehicle type but nevertheless keeps
 * its constant that can be retrieved via {@code GtfsRoute.routeTypeValue()}.
 *
 * <p>This class provides methods for validators that want to support HVT.
 *
 * @see <a href="https://developers.google.com/transit/gtfs/reference/extended-route-types">Extended
 *     GTFS Route Types</a>
 */
public class HierarchicalVehicleType {

  /**
   * Converts hierarchical vehicle type (HVT) to basic GTFS route types.
   *
   * <p>If a basic GTFS route type is passed (e.g., 0-7), then it is returned as-is.
   *
   * <p>Returns {@link GtfsRouteType#UNRECOGNIZED} for HVT that can't be represented by a basic GTFS
   * route type.
   */
  public static GtfsRouteType toBasicGtfsRouteType(int hierarchicalVehicleType) {
    if (hierarchicalVehicleType >= 0 && hierarchicalVehicleType <= 7
        || hierarchicalVehicleType == 11
        || hierarchicalVehicleType == 12) {
      return GtfsRouteType.forNumber(hierarchicalVehicleType);
    }

    switch (hierarchicalVehicleType) {
      case COMMUNAL_TAXI:
        return GtfsRouteType.BUS;
      case MONORAIL:
        return GtfsRouteType.MONORAIL;
      default:
        break;
    }

    switch ((hierarchicalVehicleType / 100) * 100) {
      case RAILWAY_SERVICE:
        return GtfsRouteType.RAIL;
      case BUS_SERVICE:
      case COACH_SERVICE:
        return GtfsRouteType.BUS;
      case URBAN_RAILWAY_SERVICE:
        return GtfsRouteType.SUBWAY;
      case TROLLEYBUS_SERVICE:
        return GtfsRouteType.TROLLEYBUS;
      case TRAM_SERVICE:
        return GtfsRouteType.LIGHT_RAIL;
      case FERRY_SERVICE:
      case WATER_TRANSPORT_SERVICE:
        return GtfsRouteType.FERRY;
      case AERIAL_LIFT_SERVICE:
        return GtfsRouteType.AERIAL_LIFT;
      case FUNICULAR_SERVICE:
        return GtfsRouteType.FUNICULAR;
      default:
        return GtfsRouteType.UNRECOGNIZED;
    }
  }

  public static final int RAILWAY_SERVICE = 100;
  public static final int HIGH_SPEED_RAIL = 101;
  public static final int LONG_DISTANCE_TRAINS = 102;
  public static final int INTER_REGIONAL_RAIL = 103;
  public static final int CAR_TRANSPORT_RAIL = 104;
  public static final int SLEEPER_RAIL = 105;
  public static final int REGIONAL_RAIL = 106;
  public static final int TOURIST_RAILWAY = 107;
  public static final int RAIL_SHUTTLE = 108;
  public static final int SUBURBAN_RAILWAY = 109;
  public static final int REPLACEMENT_RAIL = 110;
  public static final int SPECIAL_RAIL = 111;
  public static final int LORRY_TRANSPORT_RAIL = 112;
  public static final int ALL_RAIL_SERVICES = 113;
  public static final int CROSS_COUNTRY_RAIL = 114;
  public static final int VEHICLE_TRANSPORT_RAIL = 115;
  public static final int RACK_AND_PINION_RAILWAY = 116;
  public static final int ADDITIONAL_RAIL = 117;
  public static final int COACH_SERVICE = 200;
  public static final int INTERNATIONAL_COACH = 201;
  public static final int NATIONAL_COACH = 202;
  public static final int SHUTTLE_COACH = 203;
  public static final int REGIONAL_COACH = 204;
  public static final int SPECIAL_COACH = 205;
  public static final int SIGHTSEEING_COACH = 206;
  public static final int TOURIST_COACH = 207;
  public static final int COMMUTER_COACH = 208;
  public static final int ALL_COACH_SERVICES = 209;
  public static final int URBAN_RAILWAY_SERVICE = 400;
  public static final int METRO = 401;
  public static final int UNDERGROUND = 402;
  public static final int URBAN_RAILWAY = 403;
  public static final int ALL_URBAN_RAILWAY_SERVICES = 404;
  public static final int MONORAIL = 405;
  public static final int BUS_SERVICE = 700;
  public static final int REGIONAL_BUS = 701;
  public static final int EXPRESS_BUS = 702;
  public static final int STOPPING_BUS = 703;
  public static final int LOCAL_BUS = 704;
  public static final int NIGHT_BUS = 705;
  public static final int POST_BUS = 706;
  public static final int SPECIAL_NEEDS_BUS = 707;
  public static final int MOBILITY_BUS = 708;
  public static final int MOBILITY_BUS_FOR_REGISTERED_DISABLED = 709;
  public static final int SIGHTSEEING_BUS = 710;
  public static final int SHUTTLE_BUS = 711;
  public static final int SCHOOL_BUS = 712;
  public static final int SCHOOL_AND_PUBLIC_SERVICE_BUS = 713;
  public static final int RAIL_REPLACEMENT_BUS = 714;
  public static final int DEMAND_AND_RESPONSE_BUS = 715;
  public static final int ALL_BUS_SERVICES = 716;
  public static final int TROLLEYBUS_SERVICE = 800;
  public static final int TRAM_SERVICE = 900;
  public static final int CITY_TRAM = 901;
  public static final int LOCAL_TRAM = 902;
  public static final int REGIONAL_TRAM = 903;
  public static final int SIGHTSEEING_TRAM = 904;
  public static final int SHUTTLE_TRAM = 905;
  public static final int ALL_TRAM_SERVICES = 906;
  public static final int WATER_TRANSPORT_SERVICE = 1000;
  public static final int AIR_SERVICE = 1100;
  public static final int FERRY_SERVICE = 1200;
  public static final int AERIAL_LIFT_SERVICE = 1300;
  public static final int FUNICULAR_SERVICE = 1400;
  public static final int TAXI_SERVICE = 1500;
  public static final int COMMUNAL_TAXI = 1501;
  public static final int WATER_TAXI = 1502;
  public static final int RAIL_TAXI = 1503;
  public static final int BIKE_TAXI = 1504;
  public static final int LICENSED_TAXI = 1505;
  public static final int PRIVATE_HIRE_SERVICE_VEHICLE = 1506;
  public static final int ALL_TAXI_SERVICES = 1507;
  public static final int MISCELLANEOUS_SERVICE = 1700;
  public static final int HORSE_DRAWN_CARRIAGE = 1702;

  /* Private constructor to disable instantiation. */
  private HierarchicalVehicleType() {}
}
