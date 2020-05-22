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

import java.util.List;
import java.util.Objects;

public class GtfsSchemaTree {
    private List<GtfsNode> root;

    public GtfsSchemaTree() {
    }

    public GtfsSchemaTree(final List<GtfsNode> root) {
        this.root = root;
    }

    public List<GtfsNode> getRoot() {
        return root;
    }

    public GtfsNode getChildWithName(String nodeName) {
        for (GtfsNode node : getRoot()) {
            if (Objects.equals(node.getName(), nodeName)) {
                return node;
            }
        }
        throw new IllegalArgumentException("No file with file name: " + nodeName);
    }
}