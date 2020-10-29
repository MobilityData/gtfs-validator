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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.InvalidAgencyIdNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingShortAndLongNameForRouteNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_ENUM_VALUE;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_FIELD_NAME;

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

        assertEquals(1, noticeCollection.size());

        final MissingRequiredValueNotice notice = noticeCollection.get(0);
        assertEquals("routes.txt", notice.getFilename());
        assertEquals("ERROR", notice.getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("route_id", notice.getNoticeSpecific(KEY_FIELD_NAME));
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

        assertEquals(1, noticeCollection.size());
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);
        assertEquals("routes.txt", notice.getFilename());
        assertEquals("ERROR", notice.getLevel());
        assertEquals(21, notice.getCode());
        assertEquals("route_type", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(STRING_TEST_VALUE, notice.getEntityId());
        assertEquals(15, notice.getNoticeSpecific(KEY_ENUM_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createRouteWithNullRouteTypeShouldGenerateMissingRequiredValueNotice() {
        final Route.RouteBuilder underTest = new Route.RouteBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.routeId(STRING_TEST_VALUE)
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(null)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();

        assertEquals(1, noticeCollection.size());

        final MissingRequiredValueNotice notice = noticeCollection.get(0);
        assertEquals("routes.txt", notice.getFilename());
        assertEquals("ERROR", notice.getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("route_type", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(STRING_TEST_VALUE, notice.getEntityId());
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

    @Test
    void createRouteWithBlankAgencyIdShouldGenerateNotice() {
        final Route.RouteBuilder underTest = new Route.RouteBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.routeId(STRING_TEST_VALUE)
                .agencyId("   ")
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(1)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<InvalidAgencyIdNotice> noticeCollection =
                (List<InvalidAgencyIdNotice>) entityBuildResult.getData();

        assertEquals(1, noticeCollection.size());
        final InvalidAgencyIdNotice notice = noticeCollection.get(0);
        assertEquals("routes.txt", notice.getFilename());
        assertEquals("ERROR", notice.getLevel());
        assertEquals(31, notice.getCode());
        assertEquals("agency_id", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(STRING_TEST_VALUE, notice.getEntityId());
    }

    @Test
    void createRouteWithBlankRouteShortNameAndRouteLongNameShouldGenerateNotice() {
        final Route.RouteBuilder underTest = new Route.RouteBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.routeId(STRING_TEST_VALUE)
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(" ")
                .routeLongName("    ")
                .routeDesc(STRING_TEST_VALUE)
                .routeType(1)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingShortAndLongNameForRouteNotice> noticeCollection =
                (List<MissingShortAndLongNameForRouteNotice>) entityBuildResult.getData();

        assertEquals(1, noticeCollection.size());
        final MissingShortAndLongNameForRouteNotice notice = noticeCollection.get(0);
        assertEquals("routes.txt", notice.getFilename());
        assertEquals("ERROR", notice.getLevel());
        assertEquals(27, notice.getCode());
        assertEquals(STRING_TEST_VALUE, notice.getEntityId());
    }

    @Test
    void createRouteWithNullRouteShortNameAndRouteLongNameShouldGenerateNotice() {
        final Route.RouteBuilder underTest = new Route.RouteBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.routeId(STRING_TEST_VALUE)
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(null)
                .routeLongName(null)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(1)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingShortAndLongNameForRouteNotice> noticeCollection =
                (List<MissingShortAndLongNameForRouteNotice>) entityBuildResult.getData();

        assertEquals(1, noticeCollection.size());
        final MissingShortAndLongNameForRouteNotice notice = noticeCollection.get(0);
        assertEquals("routes.txt", notice.getFilename());
        assertEquals("ERROR", notice.getLevel());
        assertEquals(27, notice.getCode());
        assertEquals(STRING_TEST_VALUE, notice.getEntityId());
    }

    @Test
    void createRouteWithNullRouteShortNameAndBlankRouteLongNameShouldGenerateNotice() {
        final Route.RouteBuilder underTest = new Route.RouteBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.routeId(STRING_TEST_VALUE)
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(null)
                .routeLongName(" ")
                .routeDesc(STRING_TEST_VALUE)
                .routeType(1)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingShortAndLongNameForRouteNotice> noticeCollection =
                (List<MissingShortAndLongNameForRouteNotice>) entityBuildResult.getData();

        assertEquals(1, noticeCollection.size());
        final MissingShortAndLongNameForRouteNotice notice = noticeCollection.get(0);
        assertEquals("routes.txt", notice.getFilename());
        assertEquals("ERROR", notice.getLevel());
        assertEquals(27, notice.getCode());
        assertEquals(STRING_TEST_VALUE, notice.getEntityId());
    }

    @Test
    void createRouteWithBlankRouteShortNameAndNullRouteLongNameShouldGenerateNotice() {
        final Route.RouteBuilder underTest = new Route.RouteBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.routeId(STRING_TEST_VALUE)
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(" ")
                .routeLongName(null)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(1)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingShortAndLongNameForRouteNotice> noticeCollection =
                (List<MissingShortAndLongNameForRouteNotice>) entityBuildResult.getData();

        assertEquals(1, noticeCollection.size());
        final MissingShortAndLongNameForRouteNotice notice = noticeCollection.get(0);
        assertEquals("routes.txt", notice.getFilename());
        assertEquals("ERROR", notice.getLevel());
        assertEquals(27, notice.getCode());
        assertEquals(STRING_TEST_VALUE, notice.getEntityId());
    }

    @Test
    void createRouteWithNullAgencyIdShouldNotGenerateNotice() {
        final Route.RouteBuilder underTest = new Route.RouteBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.routeId(STRING_TEST_VALUE)
                .agencyId(null)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(1)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof Route);
    }
}
