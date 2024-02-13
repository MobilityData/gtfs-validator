package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.table.*;

public class NetworkIdConsistencyValidatorTest {

  private NoticeContainer noticeContainer;
  private GtfsRouteTableContainer routeTableContainer;
  private GtfsRouteNetworkTableContainer routeNetworkTableContainer;
  private GtfsNetworkTableContainer networkTableContainer;

  @Before
  public void setup() {
    noticeContainer = new NoticeContainer();
    routeTableContainer =
        GtfsRouteTableContainer.forHeaderAndEntities(
            new GtfsRouteTableDescriptor(),
            new CsvHeader(ImmutableList.of("route_id", "network_id").toArray(new String[0])),
            ImmutableList.of(
                new GtfsRoute.Builder().setRouteId("123").setNetworkId("network1").build()),
            noticeContainer);
    routeNetworkTableContainer =
        new GtfsRouteNetworkTableContainer(
            new GtfsRouteNetworkTableDescriptor(), GtfsTableContainer.TableStatus.MISSING_FILE);
    networkTableContainer =
        new GtfsNetworkTableContainer(
            new GtfsNetworkTableDescriptor(), GtfsTableContainer.TableStatus.MISSING_FILE);
  }

  @Test
  public void validatesConditionalForbiddenFilePresenceNoNotice() {
    NetworkIdConsistencyValidator validator =
        new NetworkIdConsistencyValidator(
            routeTableContainer, routeNetworkTableContainer, networkTableContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices().isEmpty());
  }

  @Test
  public void validatesConditionalForbiddenFilePresence1() {
    routeNetworkTableContainer =
        GtfsRouteNetworkTableContainer.forEntities(
            ImmutableList.of(
                new GtfsRouteNetwork.Builder().setRouteId("123").setNetworkId("network1").build()),
            noticeContainer);
    NetworkIdConsistencyValidator validator =
        new NetworkIdConsistencyValidator(
            routeTableContainer, routeNetworkTableContainer, networkTableContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices().size() == 1);
    NetworkIdConsistencyValidator.ConditionalForbiddenFileNotice notice =
        (NetworkIdConsistencyValidator.ConditionalForbiddenFileNotice)
            noticeContainer.getValidationNotices().get(0);
    NetworkIdConsistencyValidator.ConditionalForbiddenFileNotice expectedNotice =
        new NetworkIdConsistencyValidator.ConditionalForbiddenFileNotice(
            "routes.txt", "network_id", "route_networks.txt");
    assertThat(notice.toString().equals(expectedNotice.toString()));
  }

  @Test
  public void validatesConditionalForbiddenFilePresence2() {
    networkTableContainer =
        GtfsNetworkTableContainer.forEntities(
            ImmutableList.of(new GtfsNetwork.Builder().setNetworkId("network1").build()),
            noticeContainer);
    NetworkIdConsistencyValidator validator =
        new NetworkIdConsistencyValidator(
            routeTableContainer, routeNetworkTableContainer, networkTableContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices().size() == 1);
    NetworkIdConsistencyValidator.ConditionalForbiddenFileNotice notice =
        (NetworkIdConsistencyValidator.ConditionalForbiddenFileNotice)
            noticeContainer.getValidationNotices().get(0);
    NetworkIdConsistencyValidator.ConditionalForbiddenFileNotice expectedNotice =
        new NetworkIdConsistencyValidator.ConditionalForbiddenFileNotice(
            "routes.txt", "network_id", "networks.txt");
    assertThat(notice.toString().equals(expectedNotice.toString()));
  }

  @Test
  public void validatesUniqueRouteNetworkAssociation() {
    routeNetworkTableContainer =
        GtfsRouteNetworkTableContainer.forEntities(
            ImmutableList.of(
                new GtfsRouteNetwork.Builder().setRouteId("123").setNetworkId("network2").build()),
            noticeContainer);
    NetworkIdConsistencyValidator validator =
        new NetworkIdConsistencyValidator(
            routeTableContainer, routeNetworkTableContainer, networkTableContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices().size() == 2);
    NetworkIdConsistencyValidator.RouteNetworkAssociationDuplicateNotice notice =
        (NetworkIdConsistencyValidator.RouteNetworkAssociationDuplicateNotice)
            noticeContainer.getValidationNotices().get(1);
    NetworkIdConsistencyValidator.RouteNetworkAssociationDuplicateNotice expectedNotice =
        new NetworkIdConsistencyValidator.RouteNetworkAssociationDuplicateNotice(
            "123", "route_networks.txt", 0, "network2", "routes.txt", 0, "network1");
    assertThat(notice.toString().equals(expectedNotice.toString()));
  }
}
