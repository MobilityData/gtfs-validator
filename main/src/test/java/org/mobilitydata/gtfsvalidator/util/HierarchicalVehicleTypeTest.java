package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.util.HierarchicalVehicleType.toBasicGtfsRouteType;

import org.junit.Test;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;

public class HierarchicalVehicleTypeTest {

  @Test
  public void toBasicGtfsRouteType_basicGtfs() {
    assertThat(toBasicGtfsRouteType(GtfsRouteType.LIGHT_RAIL.getNumber()))
        .isEqualTo(GtfsRouteType.LIGHT_RAIL);
    assertThat(toBasicGtfsRouteType(GtfsRouteType.SUBWAY.getNumber()))
        .isEqualTo(GtfsRouteType.SUBWAY);
    assertThat(toBasicGtfsRouteType(GtfsRouteType.RAIL.getNumber())).isEqualTo(GtfsRouteType.RAIL);
    assertThat(toBasicGtfsRouteType(GtfsRouteType.BUS.getNumber())).isEqualTo(GtfsRouteType.BUS);
    assertThat(toBasicGtfsRouteType(GtfsRouteType.FERRY.getNumber()))
        .isEqualTo(GtfsRouteType.FERRY);
    assertThat(toBasicGtfsRouteType(GtfsRouteType.CABLE_TRAM.getNumber()))
        .isEqualTo(GtfsRouteType.CABLE_TRAM);
    assertThat(toBasicGtfsRouteType(GtfsRouteType.AERIAL_LIFT.getNumber()))
        .isEqualTo(GtfsRouteType.AERIAL_LIFT);
    assertThat(toBasicGtfsRouteType(GtfsRouteType.FUNICULAR.getNumber()))
        .isEqualTo(GtfsRouteType.FUNICULAR);
    assertThat(toBasicGtfsRouteType(GtfsRouteType.TROLLEYBUS.getNumber()))
        .isEqualTo(GtfsRouteType.TROLLEYBUS);
    assertThat(toBasicGtfsRouteType(GtfsRouteType.MONORAIL.getNumber()))
        .isEqualTo(GtfsRouteType.MONORAIL);
  }

  @Test
  public void toBasicGtfsRouteType_hierarchicalVehicleTypes_rail() {
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.RAILWAY_SERVICE))
        .isEqualTo(GtfsRouteType.RAIL);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.HIGH_SPEED_RAIL))
        .isEqualTo(GtfsRouteType.RAIL);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.LONG_DISTANCE_TRAINS))
        .isEqualTo(GtfsRouteType.RAIL);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.INTER_REGIONAL_RAIL))
        .isEqualTo(GtfsRouteType.RAIL);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.SLEEPER_RAIL))
        .isEqualTo(GtfsRouteType.RAIL);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.REGIONAL_RAIL))
        .isEqualTo(GtfsRouteType.RAIL);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.TOURIST_RAILWAY))
        .isEqualTo(GtfsRouteType.RAIL);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.RAIL_SHUTTLE))
        .isEqualTo(GtfsRouteType.RAIL);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.SUBURBAN_RAILWAY))
        .isEqualTo(GtfsRouteType.RAIL);
  }

  @Test
  public void toBasicGtfsRouteType_hierarchicalVehicleTypes_coach() {
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.COACH_SERVICE))
        .isEqualTo(GtfsRouteType.BUS);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.INTERNATIONAL_COACH))
        .isEqualTo(GtfsRouteType.BUS);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.NATIONAL_COACH))
        .isEqualTo(GtfsRouteType.BUS);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.REGIONAL_COACH))
        .isEqualTo(GtfsRouteType.BUS);
  }

  @Test
  public void toBasicGtfsRouteType_hierarchicalVehicleTypes_urbanRailway() {
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.URBAN_RAILWAY_SERVICE))
        .isEqualTo(GtfsRouteType.SUBWAY);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.METRO)).isEqualTo(GtfsRouteType.SUBWAY);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.UNDERGROUND))
        .isEqualTo(GtfsRouteType.SUBWAY);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.URBAN_RAILWAY))
        .isEqualTo(GtfsRouteType.SUBWAY);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.MONORAIL))
        .isEqualTo(GtfsRouteType.MONORAIL);
  }

  @Test
  public void toBasicGtfsRouteType_hierarchicalVehicleTypes_bus() {
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.BUS_SERVICE))
        .isEqualTo(GtfsRouteType.BUS);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.REGIONAL_BUS))
        .isEqualTo(GtfsRouteType.BUS);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.EXPRESS_BUS))
        .isEqualTo(GtfsRouteType.BUS);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.LOCAL_BUS))
        .isEqualTo(GtfsRouteType.BUS);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.DEMAND_AND_RESPONSE_BUS))
        .isEqualTo(GtfsRouteType.BUS);
  }

  @Test
  public void toBasicGtfsRouteType_hierarchicalVehicleTypes_others() {
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.TROLLEYBUS_SERVICE))
        .isEqualTo(GtfsRouteType.TROLLEYBUS);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.TRAM_SERVICE))
        .isEqualTo(GtfsRouteType.LIGHT_RAIL);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.WATER_TRANSPORT_SERVICE))
        .isEqualTo(GtfsRouteType.FERRY);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.FERRY_SERVICE))
        .isEqualTo(GtfsRouteType.FERRY);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.AERIAL_LIFT_SERVICE))
        .isEqualTo(GtfsRouteType.AERIAL_LIFT);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.FUNICULAR_SERVICE))
        .isEqualTo(GtfsRouteType.FUNICULAR);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.COMMUNAL_TAXI))
        .isEqualTo(GtfsRouteType.BUS);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.MISCELLANEOUS_SERVICE))
        .isEqualTo(GtfsRouteType.UNRECOGNIZED);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.HORSE_DRAWN_CARRIAGE))
        .isEqualTo(GtfsRouteType.UNRECOGNIZED);
  }
}
