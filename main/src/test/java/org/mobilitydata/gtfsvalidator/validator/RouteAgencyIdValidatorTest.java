package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;

public class RouteAgencyIdValidatorTest {

  @Test
  public void agencyIdRequiredErrorWhenMoreThanOneAgency() {

    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsAgencyTableContainer agencyTable =
        GtfsAgencyTableContainer.forEntities(
            ImmutableList.of(
                new GtfsAgency.Builder()
                    .setCsvRowNumber(0)
                    .setAgencyId("Agency 1")
                    .setAgencyName("Agency 1")
                    .build(),
                new GtfsAgency.Builder()
                    .setCsvRowNumber(1)
                    .setAgencyId("Agency 2")
                    .setAgencyName("Agency 2")
                    .build()),
            noticeContainer);

    GtfsRouteTableContainer routeTable =
        GtfsRouteTableContainer.forEntities(
            ImmutableList.of(
                new GtfsRoute.Builder()
                    .setCsvRowNumber(0)
                    .setAgencyId("agency0")
                    .setRouteId("route_0")
                    .setRouteShortName("Route 0")
                    .build(),
                new GtfsRoute.Builder()
                    .setCsvRowNumber(1)
                    .setAgencyId(null)
                    .setRouteId("route_1")
                    .setRouteShortName("Route 1")
                    .build()),
            noticeContainer);
    new RouteAgencyIdValidator(agencyTable, routeTable).validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new MissingRequiredFieldNotice(
                routeTable.gtfsFilename(), 1, GtfsRoute.AGENCY_ID_FIELD_NAME));
  }

  @Test
  public void agencyIdRecommendedWarningWhenOnlyOneAgency() {

    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsAgencyTableContainer agencyTable =
        GtfsAgencyTableContainer.forEntities(
            ImmutableList.of(
                new GtfsAgency.Builder()
                    .setCsvRowNumber(1)
                    .setAgencyId(null)
                    .setAgencyName("Agency with no ID")
                    .build()),
            noticeContainer);

    GtfsRouteTableContainer routeTable =
        GtfsRouteTableContainer.forEntities(
            ImmutableList.of(
                new GtfsRoute.Builder()
                    .setCsvRowNumber(0)
                    .setAgencyId(null)
                    .setRouteId("route_0")
                    .setRouteShortName("Route 0")
                    .build()),
            noticeContainer);
    new RouteAgencyIdValidator(agencyTable, routeTable).validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new MissingRecommendedFieldNotice(
                routeTable.gtfsFilename(), 0, GtfsRoute.AGENCY_ID_FIELD_NAME));
  }

  @Test
  public void SingleAgencyAndAgencyIdSpecified_noNotice() {

    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsAgencyTableContainer agencyTable =
        GtfsAgencyTableContainer.forEntities(
            ImmutableList.of(
                new GtfsAgency.Builder()
                    .setCsvRowNumber(0)
                    .setAgencyId("agency1")
                    .setAgencyName("Agency 1")
                    .build()),
            noticeContainer);

    GtfsRouteTableContainer routeTable =
        GtfsRouteTableContainer.forEntities(
            ImmutableList.of(
                new GtfsRoute.Builder()
                    .setCsvRowNumber(0)
                    .setAgencyId("agency1")
                    .setRouteId("route_0")
                    .setRouteShortName("Route 0")
                    .build(),
                new GtfsRoute.Builder()
                    .setCsvRowNumber(1)
                    .setAgencyId("agency1")
                    .setRouteId("route_1")
                    .setRouteShortName("Route 1")
                    .build()),
            noticeContainer);
    new RouteAgencyIdValidator(agencyTable, routeTable).validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void MoreThanOneAgencyAndAgencyIdsSpecified_noNotice() {

    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsAgencyTableContainer agencyTable =
        GtfsAgencyTableContainer.forEntities(
            ImmutableList.of(
                new GtfsAgency.Builder()
                    .setCsvRowNumber(0)
                    .setAgencyId("agency1")
                    .setAgencyName("Agency 1")
                    .build(),
                new GtfsAgency.Builder()
                    .setCsvRowNumber(1)
                    .setAgencyId("agency2")
                    .setAgencyName("Agency 2")
                    .build()),
            noticeContainer);

    GtfsRouteTableContainer routeTable =
        GtfsRouteTableContainer.forEntities(
            ImmutableList.of(
                new GtfsRoute.Builder()
                    .setCsvRowNumber(0)
                    .setAgencyId("agency1")
                    .setRouteId("route_0")
                    .setRouteShortName("Route 0")
                    .build(),
                new GtfsRoute.Builder()
                    .setCsvRowNumber(1)
                    .setAgencyId("agency2")
                    .setRouteId("route_1")
                    .setRouteShortName("Route 1")
                    .build()),
            noticeContainer);
    new RouteAgencyIdValidator(agencyTable, routeTable).validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
