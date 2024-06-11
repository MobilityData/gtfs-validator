package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.validator.TransferDirection.TRANSFER_FROM;
import static org.mobilitydata.gtfsvalidator.validator.TransferDirection.TRANSFER_TO;

import org.junit.Test;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;

public class TransferDirectionTest {

  @Test
  public void testFieldName() {
    assertThat(TRANSFER_FROM.stopIdFieldName()).isEqualTo("from_stop_id");
    assertThat(TRANSFER_TO.stopIdFieldName()).isEqualTo("to_stop_id");

    assertThat(TRANSFER_FROM.routeIdFieldName()).isEqualTo("from_route_id");
    assertThat(TRANSFER_TO.routeIdFieldName()).isEqualTo("to_route_id");

    assertThat(TRANSFER_FROM.tripIdFieldName()).isEqualTo("from_trip_id");
    assertThat(TRANSFER_TO.tripIdFieldName()).isEqualTo("to_trip_id");
  }

  @Test
  public void testHasMethodsForEmptyTransfer() {
    GtfsTransfer emptyTransfer = GtfsTransfer.builder().build();

    assertThat(TRANSFER_FROM.hasStopId(emptyTransfer)).isFalse();
    assertThat(TRANSFER_TO.hasStopId(emptyTransfer)).isFalse();

    assertThat(TRANSFER_FROM.hasRouteId(emptyTransfer)).isFalse();
    assertThat(TRANSFER_TO.hasRouteId(emptyTransfer)).isFalse();

    assertThat(TRANSFER_FROM.hasTripId(emptyTransfer)).isFalse();
    assertThat(TRANSFER_TO.hasTripId(emptyTransfer)).isFalse();
  }

  @Test
  public void testHasMethods() {
    assertThat(TRANSFER_FROM.hasStopId(GtfsTransfer.builder().setFromStopId("a").build())).isTrue();
    assertThat(TRANSFER_TO.hasStopId(GtfsTransfer.builder().setToStopId("a").build())).isTrue();

    assertThat(TRANSFER_FROM.hasRouteId(GtfsTransfer.builder().setFromRouteId("a").build()))
        .isTrue();
    assertThat(TRANSFER_TO.hasRouteId(GtfsTransfer.builder().setToRouteId("a").build())).isTrue();

    assertThat(TRANSFER_FROM.hasTripId(GtfsTransfer.builder().setFromTripId("a").build())).isTrue();
    assertThat(TRANSFER_TO.hasTripId(GtfsTransfer.builder().setToTripId("a").build())).isTrue();
  }

  @Test
  public void testIdMethods() {
    GtfsTransfer transfer =
        GtfsTransfer.builder()
            .setFromStopId("stopA")
            .setFromRouteId("routeA")
            .setFromTripId("tripA")
            .setToStopId("stopB")
            .setToRouteId("routeB")
            .setToTripId("tripB")
            .build();

    assertThat(TRANSFER_FROM.stopId(transfer)).isEqualTo("stopA");
    assertThat(TRANSFER_TO.stopId(transfer)).isEqualTo("stopB");

    assertThat(TRANSFER_FROM.routeId(transfer)).isEqualTo("routeA");
    assertThat(TRANSFER_TO.routeId(transfer)).isEqualTo("routeB");

    assertThat(TRANSFER_FROM.tripId(transfer)).isEqualTo("tripA");
    assertThat(TRANSFER_TO.tripId(transfer)).isEqualTo("tripB");
  }
}
