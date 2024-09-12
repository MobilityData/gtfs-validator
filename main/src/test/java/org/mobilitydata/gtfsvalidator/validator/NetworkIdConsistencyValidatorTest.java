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
                GtfsRoute.builder().setRouteId("123").setNetworkId("network1").build()),
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
                GtfsRouteNetwork.builder().setRouteId("123").setNetworkId("network1").build()),
            noticeContainer);
    NetworkIdConsistencyValidator validator =
        new NetworkIdConsistencyValidator(
            routeTableContainer, routeNetworkTableContainer, networkTableContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices().size() == 1);
    NetworkIdConsistencyValidator.RouteNetworksSpecifiedInMoreThanOneFileNotice notice =
        (NetworkIdConsistencyValidator.RouteNetworksSpecifiedInMoreThanOneFileNotice)
            noticeContainer.getValidationNotices().get(0);
    NetworkIdConsistencyValidator.RouteNetworksSpecifiedInMoreThanOneFileNotice expectedNotice =
        new NetworkIdConsistencyValidator.RouteNetworksSpecifiedInMoreThanOneFileNotice(
            "routes.txt", "route_networks.txt", "network_id");
    assertThat(notice.toString().equals(expectedNotice.toString()));
  }

  @Test
  public void validatesConditionalForbiddenFilePresence2() {
    networkTableContainer =
        GtfsNetworkTableContainer.forEntities(
            ImmutableList.of(GtfsNetwork.builder().setNetworkId("network1").build()),
            noticeContainer);
    NetworkIdConsistencyValidator validator =
        new NetworkIdConsistencyValidator(
            routeTableContainer, routeNetworkTableContainer, networkTableContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices().size() == 1);
    NetworkIdConsistencyValidator.RouteNetworksSpecifiedInMoreThanOneFileNotice notice =
        (NetworkIdConsistencyValidator.RouteNetworksSpecifiedInMoreThanOneFileNotice)
            noticeContainer.getValidationNotices().get(0);
    NetworkIdConsistencyValidator.RouteNetworksSpecifiedInMoreThanOneFileNotice expectedNotice =
        new NetworkIdConsistencyValidator.RouteNetworksSpecifiedInMoreThanOneFileNotice(
            "routes.txt", "networks.txt", "network_id");
    assertThat(notice.toString().equals(expectedNotice.toString()));
  }
}
