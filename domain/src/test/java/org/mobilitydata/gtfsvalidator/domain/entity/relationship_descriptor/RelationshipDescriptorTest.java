/*
 *
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
 *
 */

package org.mobilitydata.gtfsvalidator.domain.entity.relationship_descriptor;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class RelationshipDescriptorTest {

    @Test
    void getChildrenShouldReturnEmptyList() {
        final RelationshipDescriptor underTest = new RelationshipDescriptor("node", new ArrayList<>());
        assertEquals(new ArrayList<>(), underTest.getChildren());
    }

    @Test
    void getChildrenShouldReturnNodeList() {
        final RelationshipDescriptor mockNode0 = mock(RelationshipDescriptor.class);
        final RelationshipDescriptor mockNode1 = mock(RelationshipDescriptor.class);
        final RelationshipDescriptor mockNode2 = mock(RelationshipDescriptor.class);
        final List<RelationshipDescriptor> relationshipDescriptorCollection =
                new ArrayList<>(List.of(mockNode0, mockNode1, mockNode2));
        final RelationshipDescriptor underTest =
                new RelationshipDescriptor("node", relationshipDescriptorCollection);

        assertEquals(relationshipDescriptorCollection, underTest.getChildren());
    }

    @Test
    void callDFSOnNodeWithoutChildrenShouldReturnHashsetWithNodeName() {
        final RelationshipDescriptor underTest = new RelationshipDescriptor("node", new ArrayList<>());
        assertEquals(List.of("node"), Arrays.asList(underTest.DFS(new HashSet<>()).toArray()));
    }

    @Test
    void callDFSOnNodeShouldReturnExpectedHashSet() {
        final RelationshipDescriptor leftRelationshipDescriptorChild =
                new RelationshipDescriptor("left child", null);
        final RelationshipDescriptor leftRelationshipDescriptor =
                new RelationshipDescriptor("left", List.of(leftRelationshipDescriptorChild));

        final RelationshipDescriptor rightRelationshipDescriptorChild =
                new RelationshipDescriptor("right child", null);
        final RelationshipDescriptor rightRelationshipDescriptor =
                new RelationshipDescriptor("right", List.of(rightRelationshipDescriptorChild));

        final RelationshipDescriptor middleRelationshipDescriptor =
                new RelationshipDescriptor("middle", null);

        final List<RelationshipDescriptor> relationshipDescriptorCollection =
                new ArrayList<>(
                        List.of(leftRelationshipDescriptor, middleRelationshipDescriptor, rightRelationshipDescriptor)
                );
        final RelationshipDescriptor underTest =
                new RelationshipDescriptor("node", relationshipDescriptorCollection);

        HashSet<String> toCheck = underTest.DFS(new HashSet<>());
        assertEquals(6, toCheck.size());
        assertTrue(toCheck.contains("node"));
        assertTrue(toCheck.contains("left"));
        assertTrue(toCheck.contains("left child"));
        assertTrue(toCheck.contains("right"));
        assertTrue(toCheck.contains("right child"));
        assertTrue(toCheck.contains("middle"));

        toCheck = leftRelationshipDescriptor.DFS(new HashSet<>());
        assertEquals(2, toCheck.size());
        assertTrue(toCheck.contains("left child"));
        assertTrue(toCheck.contains("left"));
    }
}