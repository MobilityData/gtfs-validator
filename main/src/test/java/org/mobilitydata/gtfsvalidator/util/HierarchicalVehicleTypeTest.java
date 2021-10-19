package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
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

  private static void assertAllSubtypes(int baseHvt, GtfsRouteType expectedRouteType) {
    for (int i = 0; i < 99; ++i) {
      assertWithMessage(String.format("HVT %d", baseHvt + i))
          .that(toBasicGtfsRouteType(baseHvt + i))
          .isEqualTo(expectedRouteType);
    }
  }

  @Test
  public void toBasicGtfsRouteType_hierarchicalVehicleTypes_rail() {
    assertAllSubtypes(HierarchicalVehicleType.RAILWAY_SERVICE, GtfsRouteType.RAIL);
  }

  @Test
  public void toBasicGtfsRouteType_hierarchicalVehicleTypes_bus() {
    assertAllSubtypes(HierarchicalVehicleType.COACH_SERVICE, GtfsRouteType.BUS);
    assertAllSubtypes(HierarchicalVehicleType.BUS_SERVICE, GtfsRouteType.BUS);
    assertAllSubtypes(HierarchicalVehicleType.TROLLEYBUS_SERVICE, GtfsRouteType.TROLLEYBUS);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.COMMUNAL_TAXI))
        .isEqualTo(GtfsRouteType.BUS);
  }

  @Test
  public void toBasicGtfsRouteType_hierarchicalVehicleTypes_urbanRailway() {
    for (int i = 0; i < 99; ++i) {
      if (HierarchicalVehicleType.URBAN_RAILWAY_SERVICE + i != HierarchicalVehicleType.MONORAIL) {
        assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.URBAN_RAILWAY_SERVICE + i))
            .isEqualTo(GtfsRouteType.SUBWAY);
      }
    }
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.MONORAIL))
        .isEqualTo(GtfsRouteType.MONORAIL);
  }

  @Test
  public void toBasicGtfsRouteType_hierarchicalVehicleTypes_tram() {
    assertAllSubtypes(HierarchicalVehicleType.TRAM_SERVICE, GtfsRouteType.LIGHT_RAIL);
  }

  @Test
  public void toBasicGtfsRouteType_hierarchicalVehicleTypes_ferry() {
    assertAllSubtypes(HierarchicalVehicleType.WATER_TRANSPORT_SERVICE, GtfsRouteType.FERRY);
    assertAllSubtypes(HierarchicalVehicleType.FERRY_SERVICE, GtfsRouteType.FERRY);
  }

  @Test
  public void toBasicGtfsRouteType_hierarchicalVehicleTypes_cable() {
    assertAllSubtypes(HierarchicalVehicleType.AERIAL_LIFT_SERVICE, GtfsRouteType.AERIAL_LIFT);
    assertAllSubtypes(HierarchicalVehicleType.FUNICULAR_SERVICE, GtfsRouteType.FUNICULAR);
  }

  @Test
  public void toBasicGtfsRouteType_hierarchicalVehicleTypes_unrecognized() {
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.AIR_SERVICE))
        .isEqualTo(GtfsRouteType.UNRECOGNIZED);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.TAXI_SERVICE))
        .isEqualTo(GtfsRouteType.UNRECOGNIZED);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.MISCELLANEOUS_SERVICE))
        .isEqualTo(GtfsRouteType.UNRECOGNIZED);
    assertThat(toBasicGtfsRouteType(HierarchicalVehicleType.HORSE_DRAWN_CARRIAGE))
        .isEqualTo(GtfsRouteType.UNRECOGNIZED);
  }
}
