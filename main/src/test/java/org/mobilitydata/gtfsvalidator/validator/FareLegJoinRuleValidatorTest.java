package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.ForeignKeyViolationNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;

@RunWith(JUnit4.class)
public class FareLegJoinRuleValidatorTest {

  @Test
  public void missingForeignKey_yieldsNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFareLegJoinRuleTableContainer fareLegJoinRuleTableContainer =
        GtfsFareLegJoinRuleTableContainer.forEntities(
            List.of(
                new GtfsFareLegJoinRule.Builder()
                    .setCsvRowNumber(1)
                    .setFromNetworkId("network1")
                    .setToNetworkId("network2")
                    .build()),
            noticeContainer);
    GtfsRouteTableContainer routeTableContainer =
        GtfsRouteTableContainer.forEntities(
            List.of(
                new GtfsRoute.Builder()
                    .setCsvRowNumber(1)
                    .setRouteId("route1")
                    .setNetworkId("network1")
                    .build()),
            noticeContainer);
    GtfsNetworkTableContainer networkTableContainer =
        GtfsNetworkTableContainer.forEntities(
            List.of(new GtfsNetwork.Builder().setCsvRowNumber(1).setNetworkId("network1").build()),
            noticeContainer);
    FareLegJoinRuleValidator underTest =
        new FareLegJoinRuleValidator(
            networkTableContainer, routeTableContainer, fareLegJoinRuleTableContainer);

    underTest.validate(noticeContainer);
    assertThat(
            (int)
                noticeContainer.getValidationNotices().stream()
                    .filter(n -> n instanceof ForeignKeyViolationNotice)
                    .count())
        .isEqualTo(1);
  }

  @Test
  public void missingRequiredField_yieldsNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFareLegJoinRuleTableContainer fareLegJoinRuleTableContainer =
        GtfsFareLegJoinRuleTableContainer.forEntities(
            List.of(
                new GtfsFareLegJoinRule.Builder()
                    .setCsvRowNumber(1)
                    .setFromNetworkId("network1")
                    .setToNetworkId("network2")
                    .setFromStopId("stop1")
                    .build()),
            noticeContainer);
    GtfsRouteTableContainer routeTableContainer =
        GtfsRouteTableContainer.forEntities(
            List.of(
                new GtfsRoute.Builder()
                    .setCsvRowNumber(1)
                    .setRouteId("route1")
                    .setNetworkId("network1")
                    .build()),
            noticeContainer);
    GtfsNetworkTableContainer networkTableContainer =
        GtfsNetworkTableContainer.forEntities(
            List.of(new GtfsNetwork.Builder().setCsvRowNumber(1).setNetworkId("network2").build()),
            noticeContainer);
    FareLegJoinRuleValidator underTest =
        new FareLegJoinRuleValidator(
            networkTableContainer, routeTableContainer, fareLegJoinRuleTableContainer);

    underTest.validate(noticeContainer);
    assertThat(
            (int)
                noticeContainer.getValidationNotices().stream()
                    .filter(n -> n instanceof MissingRequiredFieldNotice)
                    .count())
        .isEqualTo(1);
  }
}
