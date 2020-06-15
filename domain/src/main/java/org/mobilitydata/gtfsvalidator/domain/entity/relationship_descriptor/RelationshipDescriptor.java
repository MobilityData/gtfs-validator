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

package org.mobilitydata.gtfsvalidator.domain.entity.relationship_descriptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Class used to contain information about dependencies among files of a GTFS dataset. Data is represented using the
 * concept of tree. This represents a tree made of nodes of the same class.
 */
public class RelationshipDescriptor {
    private String name;
    private List<RelationshipDescriptor> children;

    // Default constructor is required for tests
    public RelationshipDescriptor() {
    }

    public RelationshipDescriptor(final String name,
                                  final List<RelationshipDescriptor> children) {
        this.name = name;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public List<RelationshipDescriptor> getChildren() {
        return children != null ? children : new ArrayList<>();
    }

    /**
     * Method returns the {@link RelationshipDescriptor} with name given as parameter
     *
     * @param name name of the {@link RelationshipDescriptor} (node) to search in the tree
     * @return the {@link RelationshipDescriptor} with the name given as parameter
     */
    public RelationshipDescriptor getChildByName(final String name) throws NullPointerException {
        return getChildren().stream().filter(child -> child.getName().equals(name))
                .findAny()
                .orElseThrow(NullPointerException::new);
    }

    /**
     * Method perform Depth First Search algorithm to find all reachable nodes from this node
     *
     * @param visited list of visited {@link RelationshipDescriptor}
     * @return list of reachable nodes from this node
     */
    public HashSet<String> DFS(final HashSet<String> visited) {
        visited.add(getName());
        for (RelationshipDescriptor child : getChildren()) {
            if (!visited.contains(child.getName())) {
                visited.addAll(child.DFS(visited));
            }
        }
        return visited;
    }
}