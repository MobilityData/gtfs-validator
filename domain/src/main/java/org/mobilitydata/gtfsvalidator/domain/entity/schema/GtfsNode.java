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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Class used to contain information about dependencies among files of a GTFS dataset
 */
public class GtfsNode {
    private String name;
    private List<GtfsNode> children;

    // Default constructor is required for tests
    public GtfsNode() {
    }

    public GtfsNode(final String name,
                    final List<GtfsNode> children) {
        this.name = name;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public List<GtfsNode> getChildren() {
        return children != null ? children : new ArrayList<>();
    }

    /**
     * Method returns the {@link GtfsNode} with name given as parameter
     *
     * @param nodeName name of the node to search in the tree
     * @return the {@link GtfsNode} with name nodeName
     */
    public GtfsNode getChildByName(final String nodeName) {
        return getChildren().stream().filter(child -> child.getName().equals(nodeName))
                .findAny()
                .orElseThrow(NullPointerException::new);
    }

    /**
     * Method perform Depth First Search algorithm to find all reachable nodes from this node
     * @param visited  list of visited {@link GtfsNode}
     * @return list of reachable nodes from this node
     */
    public HashSet<String> DFS(final HashSet<String> visited) {
        visited.add(getName());
        for (GtfsNode child : getChildren()) {
            if (!visited.contains(child.getName())) {
                visited.addAll(child.DFS(visited));
            }
        }
        return visited;
    }
}