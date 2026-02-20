package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import com.ibm.icu.impl.Pair;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

public class InconsistentRouteTypeForBlockIdValidatorTest {
  private record TripMetadata(String blockId, String routeId, String serviceId) {}

  private static List<GtfsRoute> createRouteTable(List<Pair<String, GtfsRouteType>> routes) {
    return routes.stream()
        .map(
            route ->
                new GtfsRoute.Builder().setRouteId(route.first).setRouteType(route.second).build())
        .collect(ImmutableList.toImmutableList());
  }

  private static List<GtfsTrip> createTripTable(Map<String, TripMetadata> trips) {
    return trips.entrySet().stream()
        .map(
            entry -> {
              String tripId = entry.getKey();
              TripMetadata metadata = entry.getValue();
              return new GtfsTrip.Builder()
                  .setTripId(tripId)
                  .setBlockId(metadata.blockId)
                  .setRouteId(metadata.routeId)
                  .setServiceId(metadata.serviceId)
                  .build();
            })
        .collect(ImmutableList.toImmutableList());
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsRoute> routes, List<GtfsTrip> trips) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new InconsistentRouteTypeForBlockIdValidator(
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsRouteTableContainer.forEntities(routes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void sameRouteTypeForBlockIdDoesNotGenerateNotice() {
    List<GtfsRoute> routes =
        createRouteTable(
            List.of(Pair.of("r0", GtfsRouteType.BUS), Pair.of("r1", GtfsRouteType.BUS)));
    List<GtfsTrip> trips =
        createTripTable(
            Map.of(
                "t0", new TripMetadata("b0", "r0", "s0"),
                "t1", new TripMetadata("b0", "r1", "s0")));
    assertThat(generateNotices(routes, trips)).isEmpty();
  }

  @Test
  public void differentRouteTypeForBlockIdGeneratesNotice() {
    List<GtfsRoute> routes =
        createRouteTable(
            List.of(Pair.of("r0", GtfsRouteType.BUS), Pair.of("r1", GtfsRouteType.RAIL)));
    List<GtfsTrip> trips =
        createTripTable(
            Map.of(
                "t0", new TripMetadata("b0", "r0", "s0"),
                "t1", new TripMetadata("b0", "r1", "s0")));
    List<ValidationNotice> notices = generateNotices(routes, trips);
    assertThat(notices).hasSize(1);
  }
}
