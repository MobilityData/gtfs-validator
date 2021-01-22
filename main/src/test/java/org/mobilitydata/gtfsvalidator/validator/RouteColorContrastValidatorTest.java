/*
 * Copyright 2020 Google LLC, MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RouteColorContrastNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;

public class RouteColorContrastValidatorTest {

  public static GtfsRoute createRoute(
      long csvRowNumber,
      String routeId,
      String agencyId,
      String routeShortName,
      String routeLongName,
      String routeDesc,
      int routeType,
      String routeUrl,
      GtfsColor routeColor,
      GtfsColor routeTextColor,
      int continuousPickup,
      int continuousDropOff) {
    return new GtfsRoute.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setRouteId(routeId)
        .setAgencyId(agencyId)
        .setRouteShortName(routeShortName)
        .setRouteLongName(routeLongName)
        .setRouteDesc(routeDesc)
        .setRouteType(routeType)
        .setRouteUrl(routeUrl)
        .setRouteColor(routeColor)
        .setRouteTextColor(routeTextColor)
        .setContinuousPickup(continuousPickup)
        .setContinuousDropOff(continuousDropOff)
        .build();
  }

  @Test
  public void noRouteColorShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsRoute route =
        createRoute(
            2,
            "route id value",
            "agency id value",
            "route short name value",
            "route long name value",
            "route desc value",
            2,
            "route url value",
            null,
            GtfsColor.fromInt(222),
            1,
            1);

    RouteColorContrastValidator underTest = new RouteColorContrastValidator();
    underTest.validate(route, noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void noRouteTextColorShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsRoute route =
        createRoute(
            2,
            "route id value",
            "agency id value",
            "route short name value",
            "route long name value",
            "route desc value",
            2,
            "route url value",
            GtfsColor.fromInt(222),
            null,
            1,
            1);

    RouteColorContrastValidator underTest = new RouteColorContrastValidator();
    underTest.validate(route, noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void contrastingRouteColorAndRouteTextColorShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    // route.route_color: white
    // route.route_text_color: black
    GtfsRoute route =
        createRoute(
            2,
            "route id value",
            "agency id value",
            "route short name value",
            "route long name value",
            "route desc value",
            2,
            "route url value",
            GtfsColor.fromString("ffffff"),
            GtfsColor.fromString("000000"),
            1,
            1);

    RouteColorContrastValidator underTest = new RouteColorContrastValidator();
    underTest.validate(route, noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void nonContrastingRouteColorAndRouteTextColorShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsRoute route =
        createRoute(
            2,
            "route id value",
            "agency id value",
            "route short name value",
            "route long name value",
            "route desc value",
            2,
            "route url value",
            GtfsColor.fromString("4a4444"),
            GtfsColor.fromString("3d3838"),
            1,
            1);

    RouteColorContrastValidator underTest = new RouteColorContrastValidator();
    underTest.validate(route, noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new RouteColorContrastNotice(
                "route id value",
                2,
                GtfsColor.fromString("4a4444"),
                GtfsColor.fromString("3d3838")));
  }
}
