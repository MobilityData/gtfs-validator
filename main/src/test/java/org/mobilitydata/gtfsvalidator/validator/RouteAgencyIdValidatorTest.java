package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;

public class RouteAgencyIdValidatorTest {

  public static GtfsRoute createRoute(
      int rowNumber, String routeId, String agencyId, String shortName) {

    return new GtfsRoute.Builder()
        .setCsvRowNumber(rowNumber)
        .setAgencyId(agencyId)
        .setRouteId(routeId)
        .setRouteShortName(shortName)
        .build();
  }

  public static GtfsAgency createAgency(int csvRowNumber, String agencyId, String agencyName) {
    return new GtfsAgency.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setAgencyId(agencyId)
        .setAgencyName(agencyName)
        .build();
  }

  @Test
  public void agencyIdRequiredWhenMoreThanOneAgency() {

    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsAgencyTableContainer agencyTable =
        GtfsAgencyTableContainer.forEntities(
            ImmutableList.of(
                createAgency(0, "oneAgencyId", "Agency with Id"),
                createAgency(1, null, "Agency with no ID")),
            noticeContainer);
    GtfsRouteTableContainer routeTable =
        GtfsRouteTableContainer.forEntities(
            ImmutableList.of(
                createRoute(0, "route_0", "agency0", "Route 0"),
                createRoute(1, "route_1", null, "Route 1")),
            noticeContainer);
    new RouteAgencyIdValidator(agencyTable, routeTable).validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new MissingRequiredFieldNotice(
                routeTable.gtfsFilename(), 1, GtfsRoute.AGENCY_ID_FIELD_NAME));
  }

  @Test
  public void agencyIdWarningWhenOnlyOneAgencyWithNoAgencyId() {

    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsAgencyTableContainer agencyTable =
        GtfsAgencyTableContainer.forEntities(
            ImmutableList.of(createAgency(1, null, "Agency with no ID")), noticeContainer);
    GtfsRouteTableContainer routeTable =
        GtfsRouteTableContainer.forEntities(
            ImmutableList.of(createRoute(0, "route_0", null, "Route 0")), noticeContainer);
    new RouteAgencyIdValidator(agencyTable, routeTable).validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new RouteAgencyIdValidator.AgencyIdRecommendedNotice(0));
  }

  @Test
  public void agencyIdWarningWhenOnlyOneAgencyWithAgencyId() {

    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsAgencyTableContainer agencyTable =
        GtfsAgencyTableContainer.forEntities(
            ImmutableList.of(createAgency(1, "agencyId", "Agency with ID")), noticeContainer);
    GtfsRouteTableContainer routeTable =
        GtfsRouteTableContainer.forEntities(
            ImmutableList.of(createRoute(0, "route_0", null, "Route 0")), noticeContainer);
    new RouteAgencyIdValidator(agencyTable, routeTable).validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new RouteAgencyIdValidator.AgencyIdRecommendedNotice(0));
  }
}
