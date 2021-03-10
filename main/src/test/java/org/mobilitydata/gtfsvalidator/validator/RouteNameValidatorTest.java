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

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RouteBothShortAndLongNameMissingNotice;
import org.mobilitydata.gtfsvalidator.notice.RouteNameMissingOrEmptyNotice;
import org.mobilitydata.gtfsvalidator.notice.RouteShortAndLongNameEqualNotice;
import org.mobilitydata.gtfsvalidator.notice.RouteShortNameTooLongNotice;
import org.mobilitydata.gtfsvalidator.notice.SameNameAndDescriptionForRouteNotice;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;

@RunWith(JUnit4.class)
public class RouteNameValidatorTest {
  private List<ValidationNotice> validateRoute(GtfsRoute route) {
    NoticeContainer container = new NoticeContainer();
    RouteNameValidator validator = new RouteNameValidator();
    validator.validate(route, container);
    return container.getValidationNotices();
  }

  private GtfsRoute createRoute(String shortName, String longName, String desc) {
    return new GtfsRoute.Builder()
        .setRouteId("r1")
        .setCsvRowNumber(1)
        .setRouteShortName(shortName)
        .setRouteLongName(longName)
        .setRouteDesc(desc)
        .build();
  }

  @Test
  public void routeBothShortAndLongNameMissing() {
    assertThat(validateRoute(createRoute(null, null, null)))
        .containsExactly(new RouteBothShortAndLongNameMissingNotice("r1", 1));
    assertThat(validateRoute(createRoute("S", "Long", null))).isEmpty();
  }

  @Test
  public void routeShortAndLongNameEqual() {
    assertThat(validateRoute(createRoute("S", "S", null)))
        .containsExactly(new RouteShortAndLongNameEqualNotice("r1", 1, "S", "S"));
    // Compare case-insensitive.
    assertThat(validateRoute(createRoute("SA", "Sa", null)))
        .containsExactly(new RouteShortAndLongNameEqualNotice("r1", 1, "SA", "Sa"));

    assertThat(validateRoute(createRoute("S", "Long", null))).isEmpty();
  }

  @Test
  public void routeShortNameTooLong() {
    assertThat(validateRoute(createRoute("THISISMYSHORTNAME", "long name", null)))
        .containsExactly(new RouteShortNameTooLongNotice("r1", 1, "THISISMYSHORTNAME"));

    assertThat(validateRoute(createRoute("SH", "long name", null))).isEmpty();
  }

  @Test
  public void equalRouteShortNameAndRouteDescShouldGenerateNotice() {
    assertThat(validateRoute(createRoute("duplicate", "long name", "duplicate")))
        .containsExactly(
            new SameNameAndDescriptionForRouteNotice(1, "r1", "duplicate", "route_short_name"));
    // include difference with lower case and upper case characters
    assertThat(validateRoute(createRoute("DuplicATE", "long name", "duplicate")))
        .containsExactly(
            new SameNameAndDescriptionForRouteNotice(1, "r1", "duplicate", "route_short_name"));
  }

  @Test
  public void equalRouteLongNameAndRouteDescShouldGenerateNotice() {
    assertThat(validateRoute(createRoute("S", "duplicate", "duplicate")))
        .containsExactly(
            new SameNameAndDescriptionForRouteNotice(1, "r1", "duplicate", "route_long_name"));
    // include difference with lower case and upper case characters
    assertThat(validateRoute(createRoute("S", "duplicate", "DuplicATE")))
        .containsExactly(
            new SameNameAndDescriptionForRouteNotice(1, "r1", "DuplicATE", "route_long_name"));
  }

  @Test
  public void noLongNameDifferentRouteShortNameAndRouteDescShouldGenerateNotice() {
    assertThat(validateRoute(createRoute("short name", null, "desc")))
        .containsExactly(new RouteNameMissingOrEmptyNotice(1, "route_long_name"));
  }

  @Test
  public void noShortNameDifferentRouteLongNameAndRouteDescShouldGenerateNotice() {
    assertThat(validateRoute(createRoute(null, "long name", "desc")))
        .containsExactly(new RouteNameMissingOrEmptyNotice(1, "route_short_name"));
  }

  @Test
  public void allNamesProvidedAndDifferentFromRouteDescShouldNotGenerateNotice() {
    assertThat(validateRoute(createRoute("short name", "long name", "desc"))).isEmpty();
  }

  @Test
  public void equalRouteShortNameRouteLongNameAndRouteDescShouldGenerateTwoNotices() {
    assertThat(validateRoute(createRoute("duplicate", "duplicate", "duplicate")))
        .contains(new RouteShortAndLongNameEqualNotice("r1", 1, "duplicate", "duplicate"));
    assertThat(validateRoute(createRoute("duplicate", "duplicate", "duplicate")))
        .contains(
            new SameNameAndDescriptionForRouteNotice(1, "r1", "duplicate", "route_short_name"));
  }

  @Test
  public void nullOrEmptyRouteLongNameShouldGenerateNotice() {
    assertThat(validateRoute(createRoute("short name", null, "desc")))
        .containsExactly(new RouteNameMissingOrEmptyNotice(1, "route_long_name"));
    assertThat(validateRoute(createRoute("short name", "", "desc")))
        .containsExactly(new RouteNameMissingOrEmptyNotice(1, "route_long_name"));
  }

  @Test
  public void nullOrEmptyShortNameShouldGenerateNotice() {
    assertThat(validateRoute(createRoute(null, "long name", "desc")))
        .containsExactly(new RouteNameMissingOrEmptyNotice(1, "route_short_name"));
    assertThat(validateRoute(createRoute("", "long name", "desc")))
        .containsExactly(new RouteNameMissingOrEmptyNotice(1, "route_short_name"));
  }
}
