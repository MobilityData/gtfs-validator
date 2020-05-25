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

package org.mobilitydata.gtfsvalidator.domain.entity.schema;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class GtfsNodeTest {

    @Test
    void getChildrenShouldReturnEmptyList() {
        final GtfsNode underTest = new GtfsNode("node", new ArrayList<>());
        assertEquals(new ArrayList<>(), underTest.getChildren());
    }

    @Test
    void getChildrenShouldReturnNodeList() {
        final GtfsNode mockNode0 = mock(GtfsNode.class);
        final GtfsNode mockNode1 = mock(GtfsNode.class);
        final GtfsNode mockNode2 = mock(GtfsNode.class);
        final List<GtfsNode> gtfsNodeCollection = new ArrayList<>(List.of(mockNode0, mockNode1, mockNode2));
        final GtfsNode underTest = new GtfsNode("node", gtfsNodeCollection);

        assertEquals(gtfsNodeCollection, underTest.getChildren());
    }

    @Test
    void callDFSOnNodeWithoutChildrenShouldReturnHashsetWithNodeName() {
        final GtfsNode underTest = new GtfsNode("node", new ArrayList<>());
        assertEquals(List.of("node"), Arrays.asList(underTest.DFS(new HashSet<>()).toArray()));
    }

    @Test
    void callDFSOnNodeShouldReturnExpectedHashSet() {
        final GtfsNode leftGtfsNodeChild = new GtfsNode("left child", null);
        final GtfsNode leftGtfsNode = new GtfsNode("left", List.of(leftGtfsNodeChild));

        final GtfsNode rightGtfsNodeChild = new GtfsNode("right child", null);
        final GtfsNode rightGtfsNode = new GtfsNode("right", List.of(rightGtfsNodeChild));

        final GtfsNode middleGtfsNode = new GtfsNode("middle", null);

        final List<GtfsNode> gtfsNodeCollection = new ArrayList<>(List.of(leftGtfsNode, middleGtfsNode, rightGtfsNode));
        final GtfsNode underTest = new GtfsNode("node", gtfsNodeCollection);

        HashSet<String> toCheck = underTest.DFS(new HashSet<>());
        assertEquals(6, toCheck.size());
        assertTrue(toCheck.contains("node"));
        assertTrue(toCheck.contains("left"));
        assertTrue(toCheck.contains("left child"));
        assertTrue(toCheck.contains("right"));
        assertTrue(toCheck.contains("right child"));
        assertTrue(toCheck.contains("middle"));

        toCheck = leftGtfsNode.DFS(new HashSet<>());
        assertEquals(2, toCheck.size());
        assertTrue(toCheck.contains("left child"));
        assertTrue(toCheck.contains("left"));
    }
}