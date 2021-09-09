/*
 * Copyright 2021 MobilityData IO
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
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.validator.UrlConsistencyValidator.SameRouteAndAgencyUrlNotice;
import org.mobilitydata.gtfsvalidator.validator.UrlConsistencyValidator.SameStopAndAgencyUrlNotice;
import org.mobilitydata.gtfsvalidator.validator.UrlConsistencyValidator.SameStopAndRouteUrlNotice;

@RunWith(JUnit4.class)
public class UrlConsistencyValidatorTest {

  private static List<ValidationNotice> generateNotices(
      List<GtfsAgency> agencies, List<GtfsRoute> routes, List<GtfsStop> stops) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new UrlConsistencyValidator(
            GtfsAgencyTableContainer.forEntities(agencies, noticeContainer),
            GtfsRouteTableContainer.forEntities(routes, noticeContainer),
            GtfsStopTableContainer.forEntities(stops, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static GtfsAgency createAgency(long csvRowNumber, String agencyName, String agencyUrl) {
    return new GtfsAgency.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setAgencyName(agencyName)
        .setAgencyUrl(agencyUrl)
        .setAgencyTimezone(ZoneId.of("America/Toronto"))
        .setAgencyLang(Locale.ENGLISH)
        .build();
  }

  private static GtfsRoute createRoute(long csvRowNumber, String agencyId, String routeUrl) {
    return new GtfsRoute.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setRouteUrl(routeUrl)
        .setRouteId(String.format("route id %s", csvRowNumber))
        .setAgencyId(agencyId)
        .setRouteShortName("route short name value")
        .setRouteLongName("route long name value")
        .setRouteType(GtfsRouteType.BUS)
        .build();
  }

  private static GtfsStop createStop(long csvRowNumber, String stopUrl) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(String.format("stop id %s", csvRowNumber))
        .setStopUrl(stopUrl)
        .build();
  }

  @Test
  public void differentRouteAndAgencyUrl_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createAgency(0, "first agency id value", "www.mobilitydata.org"),
                    createAgency(2, "other agency id value", "www.someotherurl")),
                ImmutableList.of(
                    createRoute(4, "first agency id value", "www.atotallydifferenturl.com")),
                ImmutableList.of()))
        .isEmpty();
  }

  @Test
  public void sameRouteAndAgencyUrl_generatesNotice() {
    ImmutableList<GtfsAgency> agencies =
        ImmutableList.of(
            createAgency(0, "first agency name value", "www.mobilitydata.org"),
            createAgency(2, "other agency name value", "www.anotherurl.com"),
            createAgency(6, "another agency name value", "www.MobilityData.org"));
    ImmutableList<GtfsRoute> routes =
        ImmutableList.of(
            createRoute(3, "route id value", "www.mobilitydata.org"),
            createRoute(4, "other route id value", null));
    assertThat(generateNotices(agencies, routes, ImmutableList.of()))
        .containsExactly(
            new SameRouteAndAgencyUrlNotice(routes.get(0), agencies.get(0)),
            new SameRouteAndAgencyUrlNotice(routes.get(0), agencies.get(2)));
  }

  @Test
  public void differentStopAndAgencyUrl_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createAgency(0, "first agency id value", "www.mobilitydata.org"),
                    createAgency(2, "other agency id value", "www.someotherurl")),
                ImmutableList.of(),
                ImmutableList.of(createStop(44, "stop url value"))))
        .isEmpty();
  }

  @Test
  public void sameStopAndAgencyUrl_generatesNotice() {
    ImmutableList<GtfsAgency> agencies =
        ImmutableList.of(
            createAgency(0, "first agency name value", "www.mobilitydata.org"),
            createAgency(2, "other agency name value", "www.anotherurl.com"),
            createAgency(4, "another agency name value", "www.mobilitydata.org"),
            createAgency(8, "some agency name value", null));

    ImmutableList<GtfsStop> stops =
        ImmutableList.of(
            createStop(456, "www.mobilitydata.org"),
            createStop(55, null),
            createStop(77, "www.anotherurl.com"));
    assertThat(generateNotices(agencies, ImmutableList.of(), stops))
        .containsExactly(
            new SameStopAndAgencyUrlNotice(stops.get(0), agencies.get(0)),
            new SameStopAndAgencyUrlNotice(stops.get(0), agencies.get(2)),
            new SameStopAndAgencyUrlNotice(stops.get(2), agencies.get(1)));
  }

  @Test
  public void differentStopAndRouteUrl_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(),
                ImmutableList.of(createRoute(8, "first agency id value", "www.mobilitydata.org")),
                ImmutableList.of(createStop(44, "stop url value"))))
        .isEmpty();
  }

  @Test
  public void sameStopAndRouteUrl_generatesNotice() {
    ImmutableList<GtfsRoute> routes =
        ImmutableList.of(createRoute(5, "first agency id value", "www.mobilitydata.org"));
    ImmutableList<GtfsStop> stops =
        ImmutableList.of(
            createStop(456, "www.mobilitydata.org"), createStop(88, "www.mobilitYData.org"));
    assertThat(generateNotices(ImmutableList.of(), routes, stops))
        .containsExactly(
            new SameStopAndRouteUrlNotice(stops.get(0), routes.get(0)),
            new SameStopAndRouteUrlNotice(stops.get(1), routes.get(0)));
  }
}
