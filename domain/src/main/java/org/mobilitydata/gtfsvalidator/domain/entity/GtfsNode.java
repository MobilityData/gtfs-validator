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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "name")
public class GtfsNode {
    private String name;
    private List<GtfsNode> children;

    public GtfsNode() {
    }

    public GtfsNode(final String name, final List<GtfsNode> children) {
        this.name = name;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public List<GtfsNode> getChildren() {
        return children;
    }

    public List<String> getChildrenNameAsListOfString() {
        final List<String> toReturn = new ArrayList<>();
        for (GtfsNode child : getChildren()) {
            toReturn.add(child.getName());
        }
        return toReturn;
    }

    public HashSet<String> DFS(final HashSet<String> visited) {
        visited.add(getName());
        if (getChildren().size() == 0) {
            return visited;
        } else {
            for (GtfsNode child : getChildren()) {
                if (!visited.contains(child.getName())) {
                    visited.addAll(DFS(visited));
                }
            }
        }
        return visited;
    }
}