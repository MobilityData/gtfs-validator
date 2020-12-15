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

package org.mobilitydata.gtfsvalidator.table;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Currency;

import static com.google.common.truth.Truth.assertThat;

/**
 * Runs GtfsRouteTableLoader on test CSV data.
 */
@RunWith(JUnit4.class)
public class GtfsRouteTableLoaderTest {
    private static final GtfsFeedName FEED_NAME = GtfsFeedName.parseString("au-sydney-buses");

    @Test
    public void validFileShouldNotGenerateNotice() throws IOException {
        ValidatorLoader validatorLoader = new ValidatorLoader();
        Reader reader =
                new StringReader(
                        "route_id,agency_id,route_short_name,route_long_name,route_type"
                                + System.lineSeparator() +
                                "route id value,agency id value,short name,long name,2");
        GtfsRouteTableLoader loader = new GtfsRouteTableLoader();
        NoticeContainer noticeContainer = new NoticeContainer();
        GtfsRouteTableContainer tableContainer =
                (GtfsRouteTableContainer) loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

        assertThat(noticeContainer.getNotices()).isEmpty();
        assertThat(tableContainer.entityCount()).isEqualTo(1);
        GtfsRoute route = tableContainer.byRouteId("route id value");
        assertThat(route).isNotNull();
        assertThat(route.routeId()).matches("route id value");
        assertThat(route.agencyId()).matches("agency id value");
        assertThat(route.routeShortName()).matches("short name");
        assertThat(route.routeLongName()).matches("long name");
        assertThat(route.routeType()).isEqualTo(GtfsRouteType.RAIL);

        reader.close();
    }

    @Test
    public void missingRequiredFieldShouldGenerateNotice() throws IOException {
        ValidatorLoader validatorLoader = new ValidatorLoader();
        Reader reader =
                new StringReader(
                        "route_id,agency_id,route_short_name,route_long_name,route_type"
                                + System.lineSeparator() +
                                "route id value,agency id value,short name,long name,");
        GtfsRouteTableLoader loader = new GtfsRouteTableLoader();
        NoticeContainer noticeContainer = new NoticeContainer();
        GtfsRouteTableContainer tableContainer =
                (GtfsRouteTableContainer) loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

        assertThat(noticeContainer.getNotices()).isNotEmpty();
        assertThat(noticeContainer.getNotices().get(0).getCode()).matches("missing_required_field");
        assertThat(noticeContainer.getNotices().get(0).getContext()).containsEntry("filename", "routes.txt");
        assertThat(noticeContainer.getNotices().get(0).getContext()).containsEntry("csvRowNumber", 2L);
        assertThat(noticeContainer.getNotices().get(0).getContext()).containsEntry("fieldName", "route_type");
        assertThat(tableContainer.entityCount()).isEqualTo(0);
        reader.close();
    }

    @Test
    public void emptyFileShouldGenerateNotice() throws IOException {
        ValidatorLoader validatorLoader = new ValidatorLoader();
        Reader reader = new StringReader("");
        GtfsRouteTableLoader loader = new GtfsRouteTableLoader();
        NoticeContainer noticeContainer = new NoticeContainer();

        loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

        assertThat(noticeContainer.getNotices()).isNotEmpty();
        assertThat(noticeContainer.getNotices().get(0).getClass().getSimpleName())
                .isEqualTo("EmptyFileNotice");
        reader.close();
    }
}
