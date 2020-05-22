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

package org.mobilitydata.gtfsvalidator.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GtfsSchemaTreeTest {

    @Test
    void getChildWithNameShouldReturnRelatedGtfsNode() {
        final GtfsNode mockGtfsNode0 = mock(GtfsNode.class);
        final GtfsNode mockGtfsNode1 = mock(GtfsNode.class);
        when(mockGtfsNode1.getName()).thenReturn("node1");
        final GtfsNode mockGtfsNode2 = mock(GtfsNode.class);
        final List<GtfsNode> gtfsNodeCollection = new ArrayList<>(List.of(mockGtfsNode0, mockGtfsNode1, mockGtfsNode2));
        final GtfsSchemaTree underTest = new GtfsSchemaTree(gtfsNodeCollection);

        assertEquals(mockGtfsNode1, underTest.getChildWithName("node1"));
    }

    @Test
    void getChildWithNonExistingNameShouldThrowException() {
        final GtfsNode mockGtfsNode0 = mock(GtfsNode.class);
        final GtfsNode mockGtfsNode1 = mock(GtfsNode.class);
        when(mockGtfsNode1.getName()).thenReturn("node1");
        final GtfsNode mockGtfsNode2 = mock(GtfsNode.class);
        final List<GtfsNode> gtfsNodeCollection = new ArrayList<>(List.of(mockGtfsNode0, mockGtfsNode1, mockGtfsNode2));
        final GtfsSchemaTree underTest = new GtfsSchemaTree(gtfsNodeCollection);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.getChildWithName("non existing name"));

        assertEquals("No file with file name: non existing name", exception.getMessage());
    }
}