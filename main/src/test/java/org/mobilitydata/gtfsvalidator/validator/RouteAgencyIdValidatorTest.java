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

import com.google.common.collect.ImmutableList;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

public class RouteAgencyIdValidatorTest {
  private static GtfsAgencyTableContainer createAgencyTable(
      NoticeContainer noticeContainer, List<GtfsAgency> entities) {
    return GtfsAgencyTableContainer.forEntities(entities, noticeContainer);
  }

  public static GtfsAgency createAgency(
      long csvRowNumber,
      String agencyId,
      String agencyName,
      String agencyUrl,
      ZoneId agencyTimezone,
      Locale agencyLang) {
    return new GtfsAgency.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setAgencyId(agencyId)
        .setAgencyName(agencyName)
        .setAgencyUrl(agencyUrl)
        .setAgencyTimezone(agencyTimezone)
        .setAgencyLang(agencyLang)
        .build();
  }

  private static GtfsRouteTableContainer createRouteTable(
      NoticeContainer noticeContainer, List<GtfsRoute> entities) {
    return GtfsRouteTableContainer.forEntities(entities, noticeContainer);
  }

  public static GtfsRoute createRoute(
      long csvRowNumber,
      String routeId,
      String agencyId,
      String routeShortName,
      String routeLongName,
      String routeDesc,
      int routeType) {
    return new GtfsRoute.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setRouteId(routeId)
        .setAgencyId(agencyId)
        .setRouteShortName(routeShortName)
        .setRouteLongName(routeLongName)
        .setRouteDesc(routeDesc)
        .setRouteType(routeType)
        .build();
  }

  @Test
  public void onlyOneAgencyInDatasetShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    RouteAgencyIdValidator underTest = new RouteAgencyIdValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(
                    2,
                    null,
                    "route id value",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA)));
    underTest.routeTable =
        createRouteTable(
            noticeContainer,
            ImmutableList.of(
                createRoute(
                    3, "route id value", null, "short name", "long name", "route desc", 2)));
    underTest.validate(noticeContainer);
  }

  @Test
  public void undefinedRouteAgencyIdShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    RouteAgencyIdValidator underTest = new RouteAgencyIdValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(
                    2,
                    "first agency id",
                    "agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA),
                createAgency(
                    3,
                    "second agency id",
                    "second agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA)));
    underTest.routeTable =
        createRouteTable(
            noticeContainer,
            ImmutableList.of(
                createRoute(
                    3, "route id value", null, "short name", "long name", "route desc", 2)));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new MissingRequiredFieldError("routes.txt", 3, "agency_id"));
  }

  @Test
  public void definedRouteAgencyIdShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    RouteAgencyIdValidator underTest = new RouteAgencyIdValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(
                    2,
                    "first agency id",
                    "agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA),
                createAgency(
                    3,
                    "second agency id",
                    "second agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA)));
    underTest.routeTable =
        createRouteTable(
            noticeContainer,
            ImmutableList.of(
                createRoute(
                    3,
                    "route id value",
                    "first agency id",
                    "short name",
                    "long name",
                    "route desc",
                    2)));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
