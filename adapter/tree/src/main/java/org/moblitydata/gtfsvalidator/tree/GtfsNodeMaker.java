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

package org.moblitydata.gtfsvalidator.tree;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.mobilitydata.gtfsvalidator.domain.entity.relationship_descriptor.RelationshipDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to deserialize Json file containing information regarding dependencies among a GTFS dataset files. Fields
 * are declared as not final because of requirements from Jackson library. For the same reason, the default class
 * constructor is used to create this object.
 */
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "name")
public class GtfsNodeMaker {
    private String name;
    private List<GtfsNodeMaker> children;

    // to avoid lint, default constructor is actually used during deserialization of JSON file by Jackson
    @SuppressWarnings("unused")
    public GtfsNodeMaker() {
    }

    public GtfsNodeMaker(final String name, final List<GtfsNodeMaker> children) {
        this.name = name;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public List<GtfsNodeMaker> getChildren() {
        return children;
    }

    /**
     * Method used to extract a {@code RelationshipDescriptor} from a {@code GtfsNodeMaker}
     *
     * @return the {@link RelationshipDescriptor} extracted from this {@link GtfsNodeMaker}
     */
    public RelationshipDescriptor toGtfsTreeRootNode() {
        final ArrayList<RelationshipDescriptor> childRelationshipDescriptorCollection = new ArrayList<>();
        getChildren().forEach(child -> childRelationshipDescriptorCollection.add(child.toGtfsTreeRootNode()));
        return new RelationshipDescriptor(getName(), childRelationshipDescriptorCollection);
    }
}