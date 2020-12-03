/*
 * Copyright 2020 Google LLC
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RouteBothShortAndLongNameMissingNotice;
import org.mobilitydata.gtfsvalidator.notice.RouteShortAndLongNameEqualNotice;
import org.mobilitydata.gtfsvalidator.notice.RouteShortNameTooLongNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class RouteNameValidatorTest {
    private List<Notice> validateRoute(GtfsRoute route) {
        NoticeContainer container = new NoticeContainer();
        RouteNameValidator validator = new RouteNameValidator();
        validator.validate(route, container);
        return container.getNotices();
    }

    private GtfsRoute createRoute(String shortName, String longName) {
        return new GtfsRoute.Builder()
                .setRouteId("r1")
                .setCsvRowNumber(1)
                .setRouteShortName(shortName)
                .setRouteLongName(longName)
                .build();
    }

    @Test
    public void routeBothShortAndLongNameMissing() {
        assertThat(validateRoute(createRoute(null, null)))
                .containsExactly(new RouteBothShortAndLongNameMissingNotice("r1", 1));
        assertThat(validateRoute(createRoute("S", null))).isEmpty();
        assertThat(validateRoute(createRoute(null, "Long"))).isEmpty();
        assertThat(validateRoute(createRoute("S", "Long"))).isEmpty();
    }

    @Test
    public void routeShortAndLongNameEqual() {
        assertThat(validateRoute(createRoute("S", "S")))
                .containsExactly(
                        new RouteShortAndLongNameEqualNotice("r1", 1, "S", "S"));
        // Compare case-insensitive.
        assertThat(validateRoute(createRoute("SA", "Sa")))
                .containsExactly(
                        new RouteShortAndLongNameEqualNotice("r1", 1, "SA", "Sa"));

        assertThat(validateRoute(createRoute("S", "Long"))).isEmpty();
    }

    @Test
    public void routeShortNameTooLong() {
        assertThat(validateRoute(createRoute("THISISMYSHORTNAME", null)))
                .containsExactly(
                        new RouteShortNameTooLongNotice("r1", 1, "THISISMYSHORTNAME"));

        assertThat(validateRoute(createRoute("SH", null))).isEmpty();
    }
}
