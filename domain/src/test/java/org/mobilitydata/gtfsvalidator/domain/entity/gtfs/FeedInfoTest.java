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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeedInfoTest {
    private static final String STRING_TEST_VALUE = "string test value";

    @Test
    void createFeedInfoWithNullFeedPublisherNameShouldGenerateNotice() {
        final FeedInfo.FeedInfoBuilder underTest = new FeedInfo.FeedInfoBuilder();
        // suppressed warning regarding nullability of parameter used in method .feedPublisherName for the purpose of
        // this test, since this parameter is annotated as non null
        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.feedPublisherName(null)
                .feedPublisherUrl(STRING_TEST_VALUE)
                .feedLang(STRING_TEST_VALUE)
                .feedStartDate(LocalDateTime.now())
                .feedEndDate(LocalDateTime.now())
                .feedVersion(STRING_TEST_VALUE)
                .feedContactEmail(STRING_TEST_VALUE)
                .feedContactUrl(STRING_TEST_VALUE)
                .build();

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection
                = (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        assertEquals(1, noticeCollection.size());

        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("feed_info.txt", notice.getFilename());
        assertEquals("feed_publisher_name", notice.getFieldName());
        assertEquals("no id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFeedInfoWithNullFeedPublisherUrlShouldGenerateNotice() {
        final FeedInfo.FeedInfoBuilder underTest = new FeedInfo.FeedInfoBuilder();
        // suppressed warning regarding nullability of parameter used in method .feedPublisherUrl for the purpose of
        // this test, since this parameter is annotated as non null
        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult =
                underTest.feedPublisherName(STRING_TEST_VALUE)
                        .feedPublisherUrl(null)
                        .feedLang(STRING_TEST_VALUE)
                        .feedStartDate(LocalDateTime.now())
                        .feedEndDate(LocalDateTime.now())
                        .feedVersion(STRING_TEST_VALUE)
                        .feedContactEmail(STRING_TEST_VALUE)
                        .feedContactUrl(STRING_TEST_VALUE)
                        .build();

        assertTrue(entityBuildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("feed_info.txt", notice.getFilename());
        assertEquals("feed_contact_url", notice.getFieldName());
        assertEquals("no id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFeedInfoWithNullFeedLangShouldGenerateNotice() {
        final FeedInfo.FeedInfoBuilder underTest = new FeedInfo.FeedInfoBuilder();
        // suppressed warning regarding nullability of parameter used in method .feedLang for the purpose of
        // this test, since this parameter is annotated as non null
        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult =
                underTest.feedPublisherName(STRING_TEST_VALUE)
                        .feedPublisherUrl(STRING_TEST_VALUE)
                        .feedLang(null)
                        .feedStartDate(LocalDateTime.now())
                        .feedEndDate(LocalDateTime.now())
                        .feedVersion(STRING_TEST_VALUE)
                        .feedContactEmail(STRING_TEST_VALUE)
                        .feedContactUrl(STRING_TEST_VALUE)
                        .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();

        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("feed_info.txt", notice.getFilename());
        assertEquals("feed_lang", notice.getFieldName());
        assertEquals("no id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
        assertTrue(entityBuildResult.getData() instanceof List);
    }

    @Test
    void createValidFeedInfoShouldNotGenerateNotice() {
        final FeedInfo.FeedInfoBuilder underTest = new FeedInfo.FeedInfoBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.feedPublisherName(STRING_TEST_VALUE)
                .feedPublisherUrl(STRING_TEST_VALUE)
                .feedLang(STRING_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof FeedInfo);
    }
}
