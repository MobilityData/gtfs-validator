/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.ShapeNotUsedNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_FIELD_NAME;
import static org.mockito.Mockito.*;

class ValidateShapeUsageTest {

    // suppressed warning regarding unchecked type since it is not required here
    @SuppressWarnings("unchecked")
    @Test
    void usedShapeShouldNotGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final String mockShapeId = "shape id";
        final Set<String> mockTripShapeIdCollection = mock(HashSet.class);
        when(mockTripShapeIdCollection.contains(mockShapeId)).thenReturn(true);

        final ValidateShapeUsage underTest = new ValidateShapeUsage();

        underTest.execute(mockResultRepo, mockShapeId, mockTripShapeIdCollection);

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockTripShapeIdCollection, times(1)).contains(ArgumentMatchers.eq(mockShapeId));
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockTripShapeIdCollection);
    }

    @Test
    void shapeNotUsedShouldGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final String mockShapeId = "shape id";

        // suppressed warning regarding unchecked type since it is not required here
        @SuppressWarnings("unchecked") final Set<String> mockTripShapeIdCollection = mock(HashSet.class);
        when(mockTripShapeIdCollection.contains(mockShapeId)).thenReturn(false);

        final ValidateShapeUsage underTest = new ValidateShapeUsage();

        underTest.execute(mockResultRepo, mockShapeId, mockTripShapeIdCollection);

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockTripShapeIdCollection, times(1)).contains(ArgumentMatchers.eq(mockShapeId));

        final ArgumentCaptor<ShapeNotUsedNotice> captor = ArgumentCaptor.forClass(ShapeNotUsedNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<ShapeNotUsedNotice> noticeList = captor.getAllValues();

        assertEquals("shapes.txt", noticeList.get(0).getFilename());
        assertEquals("shape_id", noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("shape id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockResultRepo, mockTripShapeIdCollection);
    }
}
