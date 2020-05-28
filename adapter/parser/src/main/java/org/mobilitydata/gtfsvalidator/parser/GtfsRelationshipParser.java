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

package org.mobilitydata.gtfsvalidator.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import org.mobilitydata.gtfsvalidator.domain.entity.relationship_descriptor.RelationshipDescriptor;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.moblitydata.gtfsvalidator.tree.GtfsNodeMaker;

import java.io.IOException;

/**
 * This provides context to go from execution parameters contained in an .json file to an internal representation using
 * {@code ExecParam}.
 */
public class GtfsRelationshipParser implements GtfsSpecRepository.RelationshipDescriptorParser {
    private final ObjectReader objectReader;

    public GtfsRelationshipParser(final ObjectReader objectReader) {
        this.objectReader = objectReader;
    }

    /**
     * Method creates a RelationshipDescriptor representing the dependencies among GTFS files from a Json file
     * containing this information
     *
     * @return the {@link RelationshipDescriptor} representing the dependencies among GTFS files from a Json file
     * containing this information
     * @throws JsonProcessingException if Json file is malformed
     */
    public RelationshipDescriptor parse(final String gtfsSchemaAsString) throws IOException {
        try {
            final GtfsNodeMaker nodeMaker = objectReader.readValue(gtfsSchemaAsString);
            return nodeMaker.toGtfsTreeRootNode();
        } catch (JsonProcessingException e) {
            throw new IOException("GTFS Relationships are malformed in file gtfs-relationship-description.json. " +
                    "Please refer to documentation.");
        }
    }
}