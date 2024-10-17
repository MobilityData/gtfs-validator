package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;

public class GtfsFareLegRuleDuplicateKeyTest {
  private LoadingHelper helper = new LoadingHelper();

  private static GtfsFareLegRule createFareLegRule(
      String networkId,
      String fromAreaId,
      String toAreaId,
      String fromTimeframeGroupId,
      String toTimeframeGroupId,
      String fareProductId) {
    return new GtfsFareLegRule.Builder()
        .setCsvRowNumber(1)
        .setNetworkId(networkId)
        .setFromAreaId(fromAreaId)
        .setToAreaId(toAreaId)
        .setFromTimeframeGroupId(fromTimeframeGroupId)
        .setToTimeframeGroupId(toTimeframeGroupId)
        .setFareProductId(fareProductId)
        .build();
  }

  @Test
  public void noDuplicateKeyNoticeWithNetworkIdAsPrimaryKey() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFareLegRuleTableContainer tableContainer =
        GtfsFareLegRuleTableContainer.forEntities(
            ImmutableList.of(
                createFareLegRule(
                    "network1", "area1", "area2", "timeframe1", "timeframe2", "fare1"),
                createFareLegRule(
                    "network2", "area1", "area2", "timeframe1", "timeframe2", "fare1")),
            noticeContainer);

    assertThat(helper.getValidationNotices()).isEmpty();
  }
}
