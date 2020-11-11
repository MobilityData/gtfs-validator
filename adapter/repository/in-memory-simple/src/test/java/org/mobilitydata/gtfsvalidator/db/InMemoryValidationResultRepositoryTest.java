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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.CannotUnzipInputArchiveNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.NonStandardHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.TooManyValidationErrorException;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class InMemoryValidationResultRepositoryTest {

    private static final String TEST_FILE_NAME = "test.tst";

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void addingNoticeShouldExtendNoticeList() throws TooManyValidationErrorException {

        WarningNotice warningNotice = new NonStandardHeaderNotice(TEST_FILE_NAME, "extra");

        ErrorNotice errorNotice = new CannotUnzipInputArchiveNotice(TEST_FILE_NAME);

        ValidationResultRepository underTest = new InMemoryValidationResultRepository(false);

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

    @Test
    void abortOnErrorRepoShouldThrowExceptionOnError() {

        ErrorNotice errorNotice = new CannotUnzipInputArchiveNotice(TEST_FILE_NAME);

        ValidationResultRepository underTest = new InMemoryValidationResultRepository(true);

        assertThrows(TooManyValidationErrorException.class, () ->
                underTest.addNotice(errorNotice));
    }

    @Test
    void getErrorNoticeCountShouldReturnExactNumberOfErrorNotice() {
        ValidationResultRepository underTest = new InMemoryValidationResultRepository(false);
        assertEquals(0, underTest.getErrorNoticeCount());

        ErrorNotice errorNotice = mock(ErrorNotice.class);
        underTest.addNotice(errorNotice);
        assertEquals(1, underTest.getErrorNoticeCount());
        underTest.addNotice(errorNotice);
        assertEquals(2, underTest.getErrorNoticeCount());
    }

    @Test
    void getWarningNoticeCountShouldReturnExactNumberOfWarningNotice() {
        ValidationResultRepository underTest = new InMemoryValidationResultRepository(false);
        assertEquals(0, underTest.getWarningNoticeCount());

        WarningNotice warningNotice = mock(WarningNotice.class);
        underTest.addNotice(warningNotice);
        assertEquals(1, underTest.getWarningNoticeCount());
        underTest.addNotice(warningNotice);
        assertEquals(2, underTest.getWarningNoticeCount());
    }

    @Test
    void getInfoNoticeCountShouldReturnExactNumberOfInfoNotice() {
        ValidationResultRepository underTest = new InMemoryValidationResultRepository(false);
        assertEquals(0, underTest.getInfoNoticeCount());

        InfoNotice infoNotice = mock(InfoNotice.class);
        underTest.addNotice(infoNotice);
        assertEquals(1, underTest.getInfoNoticeCount());
        underTest.addNotice(infoNotice);
        assertEquals(2, underTest.getInfoNoticeCount());
    }

    @Test
    void getNoticeCountShouldReturnTotalNumberOfNoticeInRepo() {
        ValidationResultRepository underTest = new InMemoryValidationResultRepository(false);
        assertEquals(0, underTest.getNoticeCount());

        InfoNotice infoNotice = mock(InfoNotice.class);
        WarningNotice warningNotice = mock(WarningNotice.class);
        ErrorNotice errorNotice = mock(ErrorNotice.class);

        underTest.addNotice(infoNotice);
        underTest.addNotice(warningNotice);
        underTest.addNotice(errorNotice);

        assertEquals(3, underTest.getNoticeCount());
    }
}
