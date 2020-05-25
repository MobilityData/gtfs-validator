/*
 *
 *  * Copyright (c) 2020. MobilityData IO.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.moblitydata.gtfsvalidator.tree;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.schema.GtfsNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class GtfsNodeMakerTest {

    @Test
    public void gtfsNodeMakerShouldExtractAsGtfsNode() {
        final GtfsNode mockGtfsNode = mock(GtfsNode.class);

        final GtfsNodeMaker mockChild0 = mock(GtfsNodeMaker.class);
        when(mockChild0.getChildren()).thenReturn(null);
        when(mockChild0.toGtfsNode()).thenReturn(mockGtfsNode);

        final GtfsNodeMaker mockChild1 = mock(GtfsNodeMaker.class);
        when(mockChild1.getChildren()).thenReturn(null);
        when(mockChild1.toGtfsNode()).thenReturn(mockGtfsNode);

        final List<GtfsNodeMaker> childrenCollection = new ArrayList<>(List.of(mockChild0, mockChild1));
        final GtfsNodeMaker underTest = new GtfsNodeMaker("root", childrenCollection);

        final GtfsNode toCheck = underTest.toGtfsNode();
        assertEquals(2, toCheck.getChildren().size());
        assertEquals(mockGtfsNode, toCheck.getChildren().get(0));
        assertEquals(mockGtfsNode, toCheck.getChildren().get(1));

        verify(mockChild0, times(1)).toGtfsNode();
        verify(mockChild1, times(1)).toGtfsNode();

        verifyNoMoreInteractions(mockChild0, mockChild1, mockGtfsNode);
    }
}
