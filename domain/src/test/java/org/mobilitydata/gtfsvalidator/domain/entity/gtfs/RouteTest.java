/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteTest {
    private static final String STRING_TEST_VALUE = "test_value";
    private static final int INT_TEST_VALUE = 0;

    // Field routeId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @SuppressWarnings("ConstantConditions")
    @Test
    public void createRouteWithNullRouteIdShouldGenerateMissingRequiredValueNotice() {
        final Route.RouteBuilder underTest = new Route.RouteBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.routeId(null)
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(INT_TEST_VALUE)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();

        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("routes.txt", notice.getFilename());
        assertEquals("route_id", notice.getFieldName());
        assertEquals("no id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createRouteWithInvalidRouteTypeShouldGenerateUnexpectedEnumValueNotice() {
        final Route.RouteBuilder underTest = new Route.RouteBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.routeId(STRING_TEST_VALUE)
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(15)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) entityBuildResult.getData();

        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);
        assertEquals("routes.txt", notice.getFilename());
        assertEquals("route_type", notice.getFieldName());
        assertEquals(STRING_TEST_VALUE, notice.getEntityId());
        assertEquals("15", notice.getEnumValue());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createRouteWithValidValuesForFieldShouldNotGenerateNotice() {
        final Route.RouteBuilder underTest = new Route.RouteBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.routeId(STRING_TEST_VALUE)
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(INT_TEST_VALUE)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof Route);
    }
}