package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.DuplicateRouteNameNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

public class DuplicateRouteNameValidatorTest {

  private GtfsRoute createRoute(
      int csvRowNumber,
      String routeId,
      String agencyId,
      String shortName,
      String longName,
      int routeType) {
    return new GtfsRoute.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setRouteId(routeId)
        .setAgencyId(agencyId)
        .setRouteShortName(shortName)
        .setRouteLongName(longName)
        .setRouteType(routeType)
        .build();
  }

  private static List<ValidationNotice> generateNotices(List<GtfsRoute> routes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new DuplicateRouteNameValidator(GtfsRouteTableContainer.forEntities(routes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void duplicateRouteLongNamesOfRoutesFromDifferentAgenciesShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createRoute(
                        2, "1st route id value", "agency id", "short name", "duplicate value", 2),
                    createRoute(
                        4,
                        "2nd route id value",
                        "other agency id",
                        "other short name",
                        "duplicate value",
                        3),
                    createRoute(8, "3rd route id value", null, "another one", "duplicate value", 2),
                    createRoute(
                        8,
                        "4th route id value",
                        "agenCY ID",
                        "other sname",
                        "duplicate value",
                        2))))
        .isEmpty();
  }

  @Test
  public void duplicateRouteLongNamesOfRoutesFromSameAgencyShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createRoute(
                        2, "1st route id value", "agency id", "short name", "duplicate value", 2),
                    createRoute(
                        4,
                        "2nd route id value",
                        "agency id",
                        "other short name",
                        "duplicate value",
                        2),
                    createRoute(
                        8,
                        "3rd route id value",
                        "agency id",
                        "another one",
                        "duplicate value",
                        2))))
        .containsExactly(
            new DuplicateRouteNameNotice("route_long_name", 4, "2nd route id value"),
            new DuplicateRouteNameNotice("route_long_name", 8, "3rd route id value"));
  }

  @Test
  public void duplicateRouteShortNamesOfRoutesFromDifferentAgenciesShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createRoute(
                        2, "1st route id value", null, "duplicate value", "1st long name", 2),
                    createRoute(
                        4,
                        "2nd route id value",
                        "agency id",
                        "duplicate value",
                        "2nd long name",
                        3),
                    createRoute(
                        8,
                        "3rd route id value",
                        "other agency id",
                        "duplicate value",
                        "3rd long name",
                        3))))
        .isEmpty();
  }

  @Test
  public void duplicateRouteShortNamesOfRoutesFromSameAgencyShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createRoute(
                        2,
                        "1st route id value",
                        "agency id",
                        "duplicate value",
                        "1st long name",
                        2),
                    createRoute(
                        4,
                        "2nd route id value",
                        "agency id",
                        "duplicate value",
                        "2nd long name",
                        2),
                    createRoute(
                        8,
                        "3rd route id value",
                        "agency id",
                        "duplicate value",
                        "3rd long name",
                        2))))
        .containsExactly(
            new DuplicateRouteNameNotice("route_short_name", 4, "2nd route id value"),
            new DuplicateRouteNameNotice("route_short_name", 8, "3rd route id value"));
  }

  @Test
  public void uniqueRouteLongNameShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createRoute(2, "1st route id value", "agency id", null, "1st value", 2),
                    createRoute(5, "4th route id value", "agency id", null, "1st value", 3),
                    createRoute(4, "2nd route id value", "another agency id", null, "2nd value", 3),
                    createRoute(8, "3rd route id value", "another one", null, "3rd value", 3))))
        .isEmpty();
  }

  @Test
  public void uniqueRouteShortNameShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createRoute(2, "1st route id value", "agency id", "1st value", null, 2),
                    createRoute(5, "4th route id value", "agency id", "1st value", null, 3),
                    createRoute(4, "2nd route id value", "another agency id", "2nd value", null, 3),
                    createRoute(8, "3rd route id value", "another one", "3rd value", null, 3))))
        .isEmpty();
  }

  @Test
  public void duplicateRouteNamesCombinationShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createRoute(2, "1st route id value", "agency id", "short name", "long name", 2),
                    createRoute(
                        5, "4th route id value", "agency id", "short name", "long value", 3),
                    createRoute(4, "2nd route id value", "agency id", "short name", "long name", 2),
                    createRoute(
                        8,
                        "3rd route id value",
                        "agency id",
                        "other short value",
                        "other long name",
                        3))))
        .containsExactly(
            new DuplicateRouteNameNotice("route_short_name", 4, "2nd route id value"),
            new DuplicateRouteNameNotice("route_long_name", 4, "2nd route id value"));
  }
}
