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
import org.mobilitydata.gtfsvalidator.usecase.notice.ExtraFileFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidateAllOptionalFilenameTest {

    //mock spec repo
    private static class MockSpecRepo implements GtfsSpecRepository {

        private final int howManyReq;
        private final int howManyOpt;

        public MockSpecRepo(int howManyReq, int howManyOpt) {
            this.howManyReq = howManyReq;
            this.howManyOpt = howManyOpt;
        }

        @Override
        public List<String> getRequiredFilenameList() {

            List<String> toReturn = new ArrayList<>(this.howManyReq);

            for (int i = 0; i < this.howManyReq; ++i) {
                toReturn.add("req" + i + ".req");
            }

            return toReturn;
        }

        @Override
        public List<String> getOptionalFilenameList() {
            List<String> toReturn = new ArrayList<>(this.howManyOpt);

            for (int i = 0; i < this.howManyOpt; ++i) {
                toReturn.add("opt" + i + ".opt");
            }

            return toReturn;
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

        private final int howManyReq;
        private final int homManyOpt;
        private final int howManyExtra;

        public MockRawFileRepo(int howManyReq, int howManyOpt, int howManyExtra) {
            this.howManyReq = howManyReq;
            this.homManyOpt = howManyOpt;
            this.howManyExtra = howManyExtra;
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
            Set<String> toReturn = new HashSet<>(this.howManyReq + this.homManyOpt);

            for (int i = 0; i < this.howManyReq; ++i) {
                toReturn.add("req" + i + ".req");
            }

            for (int j = 0; j < this.homManyOpt; ++j) {
                toReturn.add("opt" + j + ".opt");
            }

            for (int k = 0; k < this.howManyExtra; ++k) {
                toReturn.add("extra" + k + ".extra");
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
        public final List<Notice> notices = new ArrayList<>();

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
        public Notice addNotice(Notice newNotice) {
            return null;
        }
    }

    @Test
    void allExtraPresentShouldGenerateNotice() {
        MockSpecRepo mockSpecRepo = new MockSpecRepo(1, 2);
        MockRawFileRepo mockRawFileRepo = new MockRawFileRepo(1, 2, 3);
        MockValidationResultRepo mockResultRepo = new MockValidationResultRepo();

        ValidateAllOptionalFilename underTest = new ValidateAllOptionalFilename(
                mockSpecRepo,
                mockRawFileRepo,
                mockResultRepo);

        List<String> result = underTest.execute();

        assertEquals(2, result.size());
        assertEquals(List.of("opt0.opt", "opt1.opt"), result);

        assertEquals(3, mockResultRepo.notices.size());

        Notice notice = mockResultRepo.notices.get(0);
        assertThat(notice, instanceOf(ExtraFileFoundNotice.class));
        assertEquals("extra0.extra", notice.getFilename());
        assertEquals("W004", notice.getId());
        assertEquals("Non standard file found", notice.getTitle());
        assertEquals("Extra file extra0.extra found in archive", notice.getDescription());

        notice = mockResultRepo.notices.get(1);
        assertEquals("extra2.extra", notice.getFilename());
        assertThat(notice, instanceOf(ExtraFileFoundNotice.class));
        assertEquals("W004", notice.getId());
        assertEquals("Non standard file found", notice.getTitle());
        assertEquals("Extra file extra2.extra found in archive", notice.getDescription());

        notice = mockResultRepo.notices.get(2);
        assertEquals("extra1.extra", notice.getFilename());
        assertThat(notice, instanceOf(ExtraFileFoundNotice.class));
        assertEquals("W004", notice.getId());
        assertEquals("Non standard file found", notice.getTitle());
        assertEquals("Extra file extra1.extra found in archive", notice.getDescription());
    }
}