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

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidateAllRequiredFilePresenceTest {

    //mock spec repo
    private static class MockSpecRepo implements GtfsSpecRepository {

        private final int homManyReq;

        public MockSpecRepo(int howManyReq) {
            this.homManyReq = howManyReq;
        }

        @Override
        public List<String> getRequiredFilenameList() {

            List<String> toReturn = new ArrayList<>(this.homManyReq);

            for (int i = 0; i < this.homManyReq; ++i) {
                toReturn.add("req" + i + ".req");
            }

            return toReturn;
        }

        @Override
        public List<String> getOptionalFilenameList() {
            return null;
        }

        @Override
        public List<String> getRequiredHeadersForFile(RawFileInfo fileInfo) {
            return null;
        }

        @Override
        public List<String> getOptionalHeadersForFile(RawFileInfo fileInfo) {
            return null;
        }

        @Override
        public RawEntityParser getParserForFile(RawFileInfo file) {
            return null;
        }

        @Override
        public ParsedEntityTypeValidator getValidatorForFile(RawFileInfo file) {
            return null;
        }
    }

    //mock raw file repo
    private static class MockRawFileRepo implements RawFileRepository {

        private final int homManyReq;
        private final int homManyOpt;

        public MockRawFileRepo(int howManyReq, int howManyOpt) {
            this.homManyReq = howManyReq;
            this.homManyOpt = howManyOpt;
        }

        @Override
        public RawFileInfo create(RawFileInfo fileInfo) {
            return null;
        }

        @Override
        public Optional<RawFileInfo> findByName(String filename) {
            return Optional.empty();
        }

        @Override
        public Collection<String> getActualHeadersForFile(RawFileInfo file) {
            return null;
        }

        @Override
        public Set<String> getFilenameAll() {
            Set<String> toReturn = new HashSet<>(this.homManyReq + this.homManyOpt);

            for (int i = 0; i < this.homManyReq; ++i) {
                toReturn.add("req" + i + ".req");
            }

            for (int j = 0; j < this.homManyOpt; ++j) {
                toReturn.add("opt" + j + ".opt");
            }

            return toReturn;
        }

        @Override
        public Optional<RawEntityProvider> getProviderForFile(RawFileInfo file) {
            return Optional.empty();
        }
    }

    //mock validation result repo
    private static class MockValidationResultRepo implements ValidationResultRepository {
        public List<Notice> notices = new ArrayList<>();

        @Override
        public InfoNotice addNotice(InfoNotice newInfo) {
            notices.add(newInfo);
            return null;
        }

        @Override
        public WarningNotice addNotice(WarningNotice newWarning) {
            notices.add(newWarning);
            return null;
        }

        @Override
        public ErrorNotice addNotice(ErrorNotice newError) {
            notices.add(newError);
            return null;
        }

        @Override
        public Collection<Notice> getAll() {
            return null;
        }

        @Override
        public NoticeExporter getExporter(String outputPath) {
            return null;
        }

        @Override
        public Notice addNotice(Notice newNotice) {
            return null;
        }
    }

    @Test
    void allRequiredPresentShouldNotGenerateNotice() {

        MockValidationResultRepo mockResultRepo = new MockValidationResultRepo();

        ValidateAllRequiredFilePresence underTest = new ValidateAllRequiredFilePresence(
                new MockSpecRepo(10),
                new MockRawFileRepo(10, 10),
                mockResultRepo

        );

        underTest.execute();
        assertEquals(0, mockResultRepo.notices.size());
    }


    // requiredFiles : ecreire des notice splutot qu'une exception
    @Test
    void missingRequiredShouldGenerateOneNoticePerMissingFile() {

        MockValidationResultRepo mockResultRepo = new MockValidationResultRepo();

        ValidateAllRequiredFilePresence underTest = new ValidateAllRequiredFilePresence(
                new MockSpecRepo(15),
                new MockRawFileRepo(10, 10),
                mockResultRepo
        );

        underTest.execute();

        assertEquals(5, mockResultRepo.notices.size());

        Notice notice = mockResultRepo.notices.get(0);
        assertThat(notice, instanceOf(MissingRequiredFileNotice.class));
        assertEquals("E003", notice.getId());
        assertEquals("Missing required file", notice.getTitle());
        assertEquals("req10.req", notice.getFilename());

        notice = mockResultRepo.notices.get(1);
        assertThat(notice, instanceOf(MissingRequiredFileNotice.class));
        assertEquals("E003", notice.getId());
        assertEquals("Missing required file", notice.getTitle());
        assertEquals("req11.req", notice.getFilename());

        notice = mockResultRepo.notices.get(2);
        assertThat(notice, instanceOf(MissingRequiredFileNotice.class));
        assertEquals("E003", notice.getId());
        assertEquals("Missing required file", notice.getTitle());
        assertEquals("req12.req", notice.getFilename());

        notice = mockResultRepo.notices.get(3);
        assertThat(notice, instanceOf(MissingRequiredFileNotice.class));
        assertEquals("E003", notice.getId());
        assertEquals("Missing required file", notice.getTitle());
        assertEquals("req13.req", notice.getFilename());

        notice = mockResultRepo.notices.get(4);
        assertThat(notice, instanceOf(MissingRequiredFileNotice.class));
        assertEquals("E003", notice.getId());
        assertEquals("Missing required file", notice.getTitle());
        assertEquals("req14.req", notice.getFilename());
    }

    @Test
    void validateOptionalAll() {
    }
}