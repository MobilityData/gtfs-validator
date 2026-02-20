package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import com.ibm.icu.impl.Pair;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

public class InconsistentRouteTypeForInSeatTransferValidatorTest {
  private record TransferMetadata(
      String fromRouteId, String toRouteId, GtfsTransferType transferType) {}

  private static List<GtfsTransfer> createTransferTable(List<TransferMetadata> transfers) {
    return transfers.stream()
        .map(
            transfer ->
                new GtfsTransfer.Builder()
                    .setFromRouteId(transfer.fromRouteId)
                    .setToRouteId(transfer.toRouteId)
                    .setTransferType(transfer.transferType)
                    .build())
        .collect(ImmutableList.toImmutableList());
  }

  private static List<GtfsRoute> createRouteTable(List<Pair<String, GtfsRouteType>> routes) {
    return routes.stream()
        .map(
            route ->
                new GtfsRoute.Builder().setRouteId(route.first).setRouteType(route.second).build())
        .collect(ImmutableList.toImmutableList());
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsRoute> routes, List<GtfsTransfer> transfers) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new InconsistentRouteTypeForInSeatTransferValidator(
            GtfsTransferTableContainer.forEntities(transfers, new NoticeContainer()),
            GtfsRouteTableContainer.forEntities(routes, new NoticeContainer()))
        .validate(noticeContainer);

    return noticeContainer.getValidationNotices();
  }

  @Test
  public void sameRouteTypeForInSeatTransferDoesNotGenerateNotice() {
    List<GtfsRoute> routes =
        createRouteTable(
            List.of(Pair.of("r0", GtfsRouteType.BUS), Pair.of("r1", GtfsRouteType.BUS)));
    List<GtfsTransfer> transfers =
        createTransferTable(
            List.of(new TransferMetadata("r0", "r1", GtfsTransferType.IN_SEAT_TRANSFER_ALLOWED)));
    assertThat(generateNotices(routes, transfers)).isEmpty();
  }

  @Test
  public void otherTransferTypeForInSeatTransferDoesNotGenerateNotice() {
    List<GtfsRoute> routes =
        createRouteTable(
            List.of(Pair.of("r0", GtfsRouteType.BUS), Pair.of("r1", GtfsRouteType.RAIL)));
    List<GtfsTransfer> transfers =
        createTransferTable(
            List.of(new TransferMetadata("r0", "r1", GtfsTransferType.RECOMMENDED)));
    assertThat(generateNotices(routes, transfers)).isEmpty();
  }

  @Test
  public void differentRouteTypeForInSeatTransferGeneratesNotice() {
    List<GtfsRoute> routes =
        createRouteTable(
            List.of(Pair.of("r0", GtfsRouteType.BUS), Pair.of("r1", GtfsRouteType.RAIL)));
    List<GtfsTransfer> transfers =
        createTransferTable(
            List.of(new TransferMetadata("r0", "r1", GtfsTransferType.IN_SEAT_TRANSFER_ALLOWED)));
    assertThat(generateNotices(routes, transfers)).hasSize(1);
  }
}
