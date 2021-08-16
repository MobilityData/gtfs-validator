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

  private static GtfsRoute createRoute(String agencyId, String routeUrl) {
    return new GtfsRoute.Builder()
        .setCsvRowNumber(3)
        .setRouteUrl(routeUrl)
        .setRouteId("route id value")
        .setAgencyId(agencyId)
        .setRouteShortName("route short name value")
        .setRouteLongName("route long name value")
        .setRouteType(GtfsRouteType.BUS)
        .build();
  }

  private static GtfsStop createStop(long csvRowNumber, String stopId, String stopUrl) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(stopId)
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
                    createRoute("first agency id value", "www.atotallydifferenturl.com")),
                ImmutableList.of()))
        .isEmpty();
  }

  @Test
  public void sameRouteAndAgencyUrl_generatesNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createAgency(0, "first agency name value", "www.mobilitydata.org"),
                    createAgency(2, "other agency name value", "www.anotherurl.com")),
                ImmutableList.of(createRoute("route id value", "www.mobilitydata.org")),
                ImmutableList.of()))
        .containsExactly(
            new SameRouteAndAgencyUrlNotice(
                3, "route id value", "first agency name value", "www.mobilitydata.org", 0));
  }

  @Test
  public void differentStopAndAgencyUrl_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createAgency(0, "first agency id value", "www.mobilitydata.org"),
                    createAgency(2, "other agency id value", "www.someotherurl")),
                ImmutableList.of(),
                ImmutableList.of(createStop(44, "stop id value", "stop url value"))))
        .isEmpty();
  }

  @Test
  public void sameStopAndAgencyUrl_generatesNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createAgency(0, "first agency name value", "www.mobilitydata.org"),
                    createAgency(2, "other agency name value", "www.anotherurl.com")),
                ImmutableList.of(),
                ImmutableList.of(createStop(456, "stop id value", "www.mobilitydata.org"))))
        .containsExactly(
            new SameStopAndAgencyUrlNotice(
                456, "stop id value", "first agency name value", "www.mobilitydata.org", 0));
  }

  @Test
  public void differentStopAndRouteUrl_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(),
                ImmutableList.of(createRoute("first agency id value", "www.mobilitydata.org")),
                ImmutableList.of(createStop(44, "stop id value", "stop url value"))))
        .isEmpty();
  }

  @Test
  public void sameStopAndRouteUrl_generatesNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(),
                ImmutableList.of(createRoute("first agency id value", "www.mobilitydata.org")),
                ImmutableList.of(createStop(456, "stop id value", "www.mobilitydata.org"))))
        .containsExactly(
            new SameStopAndRouteUrlNotice(
                456, "stop id value", "www.mobilitydata.org", "route id value", 3));
  }
}
