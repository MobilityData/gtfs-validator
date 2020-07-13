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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.InvalidAgencyIdNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_FIELD_NAME;

class AgencyTest {
    private static final String STRING_TEST_VALUE = "test_value";
    private static final String BLANK_STRING_VALUE = "   ";

    // Field agencyName is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createAgencyWithNullAgencyNameShouldGenerateMissingRequiredValueNotice() {
        final Agency.AgencyBuilder underTest = new Agency.AgencyBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.agencyId(STRING_TEST_VALUE)
                .agencyName(null)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("agency.txt", notice.getFilename());
        assertEquals("agency_name", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(STRING_TEST_VALUE, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    // Field agencyUrl is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createAgencyWithNullAgencyUrlShouldGenerateMissingRequiredValueNotice() {
        final Agency.AgencyBuilder underTest = new Agency.AgencyBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.agencyId(STRING_TEST_VALUE)
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(null)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("agency.txt", notice.getFilename());
        assertEquals("agency_url", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(STRING_TEST_VALUE, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    // Field agencyTimezone is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createAgencyWithNullTimezoneShouldGenerateMissingRequiredValueNotice() {
        final Agency.AgencyBuilder underTest = new Agency.AgencyBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.agencyId(STRING_TEST_VALUE)
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(null)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("agency.txt", notice.getFilename());
        assertEquals("agency_timezone", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(STRING_TEST_VALUE, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createAgencyWithValidValuesShouldNotGenerateNotice() {
        final Agency.AgencyBuilder underTest = new Agency.AgencyBuilder();

        underTest.agencyId(STRING_TEST_VALUE);
        underTest.agencyName(STRING_TEST_VALUE);
        underTest.agencyUrl(STRING_TEST_VALUE);
        underTest.agencyTimezone(STRING_TEST_VALUE);
        underTest.agencyLang(STRING_TEST_VALUE);
        underTest.agencyPhone(STRING_TEST_VALUE);
        underTest.agencyFareUrl(STRING_TEST_VALUE);
        underTest.agencyEmail(STRING_TEST_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        assertTrue(entityBuildResult.getData() instanceof Agency);
    }

    @Test
    void createAgencyWithBlankAgencyIdShouldGenerateInvalidAgencyIdNotice() {
        final Agency.AgencyBuilder underTest = new Agency.AgencyBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.agencyId(BLANK_STRING_VALUE)
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed warning since cast check is not useful here: the test is designed so that method .getData()
        // returns a list of notices.
        //noinspection unchecked
        final List<InvalidAgencyIdNotice> noticeCollection =
                (List<InvalidAgencyIdNotice>) entityBuildResult.getData();
        final InvalidAgencyIdNotice notice = noticeCollection.get(0);

        assertEquals("agency.txt", notice.getFilename());
        assertEquals("agency_id", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(BLANK_STRING_VALUE, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createAgencyWithNullAgencyIdShouldAllocateDefaultValueAndShouldNotGenerateNotice() {
        final Agency.AgencyBuilder underTest = new Agency.AgencyBuilder();

        underTest.agencyId(null);
        underTest.agencyName(STRING_TEST_VALUE);
        underTest.agencyUrl(STRING_TEST_VALUE);
        underTest.agencyTimezone(STRING_TEST_VALUE);
        underTest.agencyLang(STRING_TEST_VALUE);
        underTest.agencyPhone(STRING_TEST_VALUE);
        underTest.agencyFareUrl(STRING_TEST_VALUE);
        underTest.agencyEmail(STRING_TEST_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final Agency toCheck = (Agency) entityBuildResult.getData();
        assertEquals("defaultAgencyId", toCheck.getAgencyId());
    }
}
