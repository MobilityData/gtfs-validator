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

package org.mobilitydata.gtfsvalidator.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.schema.GtfsNode;
import org.moblitydata.gtfsvalidator.tree.GtfsNodeMaker;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GtfsSchemaParserTest {

    @Test
    void schemaShouldBeParsedAsGtfsNode() throws IOException {
        final String schemaAsString = "        {\n" +
                "            \"name\": \"root\",\n" +
                "                \"children\": [\n" +
                "                        {\"name\": \"left child\", \"children\": []},\n" +
                "                        {\"name\": \"right child\", \"children\": []}\n" +
                "                    ]\n" +
                "        }";
        final ObjectReader mockObjectReader = new ObjectMapper().readerFor(GtfsNodeMaker.class);
        final GtfsSchemaParser underTest = new GtfsSchemaParser(mockObjectReader);

        final GtfsNode toCheck = underTest.parse(schemaAsString);

        assertEquals("root", toCheck.getName());
        assertEquals(2, toCheck.getChildren().size());

        assertEquals("left child", toCheck.getChildWithName("left child").getName());
        assertEquals(List.of(), toCheck.getChildWithName("left child").getChildren());

        assertEquals("right child", toCheck.getChildWithName("right child").getName());
        assertEquals(List.of(), toCheck.getChildWithName("right child").getChildren());
    }

    @Test
    void malformedSchemaShouldThrowException() {
        final String schemaAsString = "        {\n" +
                "            \"names\": \"root\",\n" +
                "                \"children\": [\n" +
                "                        {\"name\": \"left child\", \"children\": []},\n" +
                "                        {\"name\": \"right child\", \"children\": []}\n" +
                "                    ]\n" +
                "        }";
        final ObjectReader mockObjectReader = new ObjectMapper().readerFor(GtfsNodeMaker.class);
        final GtfsSchemaParser underTest = new GtfsSchemaParser(mockObjectReader);

        assertThrows(JsonProcessingException.class, () -> underTest.parse(schemaAsString));
    }
}