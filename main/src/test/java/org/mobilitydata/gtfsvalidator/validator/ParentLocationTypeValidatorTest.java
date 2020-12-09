/*
 * Copyright 2020 Google LLC
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

package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.WrongParentLocationTypeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class ParentLocationTypeValidatorTest {

    private List<ValidationNotice> validateChildAndParent(GtfsLocationType childType, GtfsLocationType parentType) {
        NoticeContainer noticeContainer = new NoticeContainer();
        ParentLocationTypeValidator validator = new ParentLocationTypeValidator();
        validator.stopTable = GtfsStopTableContainer.forEntities(ImmutableList.of(
                new GtfsStop.Builder()
                        .setCsvRowNumber(1)
                        .setStopId("child")
                        .setLocationType(childType.getNumber())
                        .setParentStation("parent")
                        .build(),
                new GtfsStop.Builder()
                        .setCsvRowNumber(2)
                        .setStopId("parent")
                        .setLocationType(parentType.getNumber())
                        .build()),
                noticeContainer);
        validator.validate(noticeContainer);
        return noticeContainer.getValidationNotices();
    }

    private List<ValidationNotice> validateNoParent(GtfsLocationType locationType) {
        NoticeContainer noticeContainer = new NoticeContainer();
        ParentLocationTypeValidator validator = new ParentLocationTypeValidator();
        validator.stopTable = GtfsStopTableContainer.forEntities(ImmutableList.of(
                new GtfsStop.Builder()
                        .setCsvRowNumber(1)
                        .setStopId("child")
                        .setLocationType(locationType.getNumber())
                        .build()),
                noticeContainer);
        validator.validate(noticeContainer);
        return noticeContainer.getValidationNotices();
    }

    @Test
    public void stopParent() {
        assertThat(validateChildAndParent(GtfsLocationType.STOP, GtfsLocationType.STATION)).isEmpty();
        assertThat(validateChildAndParent(GtfsLocationType.STOP, GtfsLocationType.ENTRANCE)).contains(
                new WrongParentLocationTypeNotice("child", 1, GtfsLocationType.STOP.getNumber(),
                        "parent", 2, GtfsLocationType.ENTRANCE.getNumber(),
                        GtfsLocationType.STATION.getNumber()));
    }

    @Test
    public void entranceParent() {
        assertThat(validateChildAndParent(GtfsLocationType.ENTRANCE, GtfsLocationType.STATION)).isEmpty();
        assertThat(validateChildAndParent(GtfsLocationType.ENTRANCE, GtfsLocationType.STOP)).contains(
                new WrongParentLocationTypeNotice("child", 1, GtfsLocationType.ENTRANCE.getNumber(),
                        "parent", 2, GtfsLocationType.STOP.getNumber(),
                        GtfsLocationType.STATION.getNumber()));
    }

    @Test
    public void genericNodeParent() {
        assertThat(validateChildAndParent(GtfsLocationType.GENERIC_NODE, GtfsLocationType.STATION)).isEmpty();
        assertThat(validateChildAndParent(GtfsLocationType.GENERIC_NODE, GtfsLocationType.STOP)).contains(
                new WrongParentLocationTypeNotice("child", 1, GtfsLocationType.GENERIC_NODE.getNumber(),
                        "parent", 2, GtfsLocationType.STOP.getNumber(),
                        GtfsLocationType.STATION.getNumber()));
    }

    @Test
    public void boardingAreaParent() {
        assertThat(validateChildAndParent(GtfsLocationType.BOARDING_AREA, GtfsLocationType.STOP)).isEmpty();
        assertThat(validateChildAndParent(GtfsLocationType.BOARDING_AREA, GtfsLocationType.STATION)).contains(
                new WrongParentLocationTypeNotice("child", 1, GtfsLocationType.BOARDING_AREA.getNumber(),
                        "parent", 2, GtfsLocationType.STATION.getNumber(),
                        GtfsLocationType.STOP.getNumber()));
    }

    @Test
    public void noParent() {
        // ParentLocationTypeValidator ignores rows without parent_station.
        assertThat(validateNoParent(GtfsLocationType.STOP)).isEmpty();
        assertThat(validateNoParent(GtfsLocationType.STATION)).isEmpty();
        assertThat(validateNoParent(GtfsLocationType.ENTRANCE)).isEmpty();
        assertThat(validateNoParent(GtfsLocationType.GENERIC_NODE)).isEmpty();
        assertThat(validateNoParent(GtfsLocationType.BOARDING_AREA)).isEmpty();
    }
}
