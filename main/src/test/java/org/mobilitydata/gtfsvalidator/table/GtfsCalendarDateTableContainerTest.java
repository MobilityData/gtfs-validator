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
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import sun.util.calendar.CalendarDate;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class GtfsCalendarDateTableContainerTest {
    @Test
    public void forEmptyFile() {
        GtfsCalendarDateTableContainer tableContainer = GtfsCalendarDateTableContainer.forEmptyFile();
        assertThat(tableContainer.getEntities()).isEmpty();
        assertThat(tableContainer.gtfsFilename()).matches("calendar_dates.txt");
        assertThat(tableContainer.isEmptyFile()).isTrue();
        assertThat(tableContainer.hasInvalidHeaders()).isFalse();
        assertThat(tableContainer.isMissingFile()).isFalse();
    }

    @Test
    public void forMissingFile() {
        GtfsCalendarDateTableContainer tableContainer = GtfsCalendarDateTableContainer.forMissingFile();
        assertThat(tableContainer.getEntities()).isEmpty();
        assertThat(tableContainer.gtfsFilename()).matches("calendar_dates.txt");
        assertThat(tableContainer.isEmptyFile()).isFalse();
        assertThat(tableContainer.hasInvalidHeaders()).isFalse();
        assertThat(tableContainer.isMissingFile()).isTrue();
    }

    @Test
    public void forInvalidHeaders() {
        GtfsCalendarDateTableContainer tableContainer = GtfsCalendarDateTableContainer.forInvalidHeaders();
        assertThat(tableContainer.getEntities()).isEmpty();
        assertThat(tableContainer.gtfsFilename()).matches("calendar_dates.txt");
        assertThat(tableContainer.isEmptyFile()).isFalse();
        assertThat(tableContainer.hasInvalidHeaders()).isTrue();
        assertThat(tableContainer.isMissingFile()).isFalse();
    }

    @Test
    public void forEntities() {
        NoticeContainer noticeContainer = new NoticeContainer();
        List<GtfsCalendarDate> calendarDateCollection = new ArrayList<>();
        GtfsCalendarDate mockCalendarDate = mock(GtfsCalendarDate.class);
        calendarDateCollection.add(mockCalendarDate);
        GtfsCalendarDateTableContainer tableContainer =
                GtfsCalendarDateTableContainer.forEntities(calendarDateCollection, noticeContainer);
        assertThat(tableContainer.getEntities().isEmpty()).isFalse();
        assertThat(tableContainer.isEmptyFile()).isFalse();
        assertThat(tableContainer.gtfsFilename()).matches("calendar_dates.txt");
        assertThat(tableContainer.hasInvalidHeaders()).isFalse();
        assertThat(tableContainer.isMissingFile()).isFalse();
    }
}
