package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsRouteType.BUS;
import static org.mobilitydata.gtfsvalidator.table.GtfsRouteType.LIGHT_RAIL;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;
import org.mobilitydata.gtfsvalidator.validator.DuplicateRouteNameValidator.DuplicateRouteNameNotice;

public class DuplicateRouteNameValidatorTest {

  private static GtfsRoute createRoute(
      int csvRowNumber,
      String routeId,
      String agencyId,
      @Nullable String shortName,
      @Nullable String longName,
      GtfsRouteType routeType) {
    return new GtfsRoute.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setRouteId(routeId)
        .setAgencyId(agencyId)
        .setRouteShortName(shortName)
        .setRouteLongName(longName)
        .setRouteType(routeType.getNumber())
        .build();
  }

  private static List<ValidationNotice> generateNotices(List<GtfsRoute> routes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new DuplicateRouteNameValidator(GtfsRouteTableContainer.forEntities(routes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void sameNamesTypeAgency_yieldsNotice() {
    GtfsRoute route1 = createRoute(2, "route1", "agency1", "L1", "Dulwich Hill", LIGHT_RAIL);
    GtfsRoute route2 = createRoute(3, "route2", "agency1", "L1", "Dulwich Hill", LIGHT_RAIL);
    assertThat(generateNotices(ImmutableList.of(route1, route2)))
        .containsExactly(new DuplicateRouteNameNotice(route1, route2));
  }

  @Test
  public void manyRoutes_yieldsMultipleNotices() {
    GtfsRoute route1 = createRoute(2, "route1", "agency1", "L1", "Dulwich Hill", LIGHT_RAIL);
    GtfsRoute route2 = createRoute(3, "route2", "agency1", "L1", "Dulwich Hill", LIGHT_RAIL);
    GtfsRoute route3 = createRoute(4, "route3", "agency1", "L1", "Dulwich Hill", LIGHT_RAIL);
    assertThat(generateNotices(ImmutableList.of(route1, route2, route3)))
        .containsExactly(
            new DuplicateRouteNameNotice(route1, route2),
            new DuplicateRouteNameNotice(route1, route3));
  }

  @Test
  public void noShortNames_yieldsNotice() {
    GtfsRoute route1 = createRoute(2, "route1", "agency1", null, "Dulwich Hill", LIGHT_RAIL);
    GtfsRoute route2 = createRoute(3, "route2", "agency1", null, "Dulwich Hill", LIGHT_RAIL);
    assertThat(generateNotices(ImmutableList.of(route1, route2)))
        .containsExactly(new DuplicateRouteNameNotice(route1, route2));
  }

  @Test
  public void noLongNames_yieldsNotice() {
    GtfsRoute route1 = createRoute(2, "route1", "agency1", "L1", null, LIGHT_RAIL);
    GtfsRoute route2 = createRoute(3, "route2", "agency1", "L1", null, LIGHT_RAIL);
    assertThat(generateNotices(ImmutableList.of(route1, route2)))
        .containsExactly(new DuplicateRouteNameNotice(route1, route2));
  }

  @Test
  public void differentShortNames_yieldsNoNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createRoute(2, "route1", "agency1", "L1", "North Line", LIGHT_RAIL),
                    createRoute(2, "route2", "agency1", "L2", "North Line", LIGHT_RAIL))))
        .isEmpty();
  }

  @Test
  public void differentLongNames_yieldsNoNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createRoute(2, "route1", "agency1", "L1", "North Line", LIGHT_RAIL),
                    createRoute(2, "route2", "agency1", "L1", "South Line", LIGHT_RAIL))))
        .isEmpty();
  }

  @Test
  public void differentAgencies_yieldsNoNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createRoute(2, "route1", "agency1", "L1", "North Line", LIGHT_RAIL),
                    createRoute(2, "route2", "agency2", "L1", "North Line", LIGHT_RAIL))))
        .isEmpty();
  }

  @Test
  public void differentTypes_yieldsNoNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createRoute(2, "route1", "agency1", "L1", "North Line", LIGHT_RAIL),
                    createRoute(2, "route2", "agency1", "L2", "North Line", BUS))))
        .isEmpty();
  }
}
