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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.NOTICE_SPECIFIC_KEY__ENUM_VALUE;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.NOTICE_SPECIFIC_KEY__FIELD_NAME;

class TransferTest {
    private final static String FROM_STOP_ID = "stop id 0";
    private final static String TO_STOP_ID = "stop id 1";
    private final static int VALID_TRANSFER_TYPE_VALUE = 1;
    private final static int VALID_MIN_TRANSFER_TIME_VALUE = 20;

    // Field fromStopId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createTransferWithNullFromStopIdShouldGenerateNotice() {
        final Transfer.TransferBuilder underTest = new Transfer.TransferBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.fromStopId(null)
                .toStopId(TO_STOP_ID)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(VALID_MIN_TRANSFER_TIME_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("transfers.txt", notice.getFilename());
        assertEquals("from_stop_id", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals("null;stop id 1", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    // Field toStopId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createTransferWithNullToStopIdShouldGenerateNotice() {
        final Transfer.TransferBuilder underTest = new Transfer.TransferBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.fromStopId(FROM_STOP_ID)
                .toStopId(null)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(VALID_MIN_TRANSFER_TIME_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("transfers.txt", notice.getFilename());
        assertEquals("to_stop_id", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals("stop id 0;null", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createTransferWithUnexpectedTransferTypeValueShouldGenerateNotice() {
        final Transfer.TransferBuilder underTest = new Transfer.TransferBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.fromStopId(FROM_STOP_ID)
                .toStopId(TO_STOP_ID)
                .transferType(55)
                .minTransferTime(VALID_MIN_TRANSFER_TIME_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) entityBuildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals("transfers.txt", notice.getFilename());
        assertEquals("transfer_type", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals("stop id 0;stop id 1", notice.getEntityId());
        assertEquals(55, notice.getExtra(NOTICE_SPECIFIC_KEY__ENUM_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createTransferWithNullMinTransferTimeValueShouldNotGenerateNotice() {
        final Transfer.TransferBuilder underTest = new Transfer.TransferBuilder();

        underTest.fromStopId(FROM_STOP_ID)
                .toStopId(TO_STOP_ID)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(VALID_MIN_TRANSFER_TIME_VALUE);

        assertTrue(underTest.build()
                .getData() instanceof Transfer);
    }
}