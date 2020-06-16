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

package org.mobilitydata.gtfsvalidator.db;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.CannotUnzipInputArchiveNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.NonStandardHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryValidationResultRepositoryTest {

    private static final String TEST_FILE_NAME = "test.tst";

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void addingNoticeShouldExtendNoticeList() {

        WarningNotice warningNotice = new NonStandardHeaderNotice(TEST_FILE_NAME, "extra");

        ErrorNotice errorNotice = new CannotUnzipInputArchiveNotice(TEST_FILE_NAME);

        ValidationResultRepository underTest = new InMemoryValidationResultRepository();

        underTest.addNotice(warningNotice);
        assertEquals(1, underTest.getAll().size());

        Notice testedNotice = underTest.getAll().stream()
                .filter(notice -> notice.getCode() == warningNotice.getCode())
                .findAny()
                .get();

        assertThat(testedNotice, instanceOf(NonStandardHeaderNotice.class));

        underTest.addNotice(errorNotice);
        assertEquals(2, underTest.getAll().size());

        testedNotice = underTest.getAll().stream()
                .filter(notice -> notice.getCode() == errorNotice.getCode())
                .findAny()
                .get();

        assertThat(testedNotice, instanceOf(CannotUnzipInputArchiveNotice.class));
    }
}