/*
 * Copyright 2024 MobilityData
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

/** Runs GeojsonFileLoader on test json data. */
@RunWith(JUnit4.class)
public class GeojsonFileLoaderTest {

  static String validGeojsonData;

  @BeforeClass
  public static void setUpBeforeClass() {
    // To make the json text clearer, use single quotes and replace them by double quotes before
    // using
    validGeojsonData =
        String.join(
            "\n",
            "{",
            "  'type': 'FeatureCollection',",
            "  'features': [",
            "    {",
            "      'id': 'id1',",
            "      'type': 'Feature',",
            "      'geometry': {",
            "        'type': 'Point',",
            "        'coordinates': [",
            "          [102.0, 0.0],",
            "          [103.0, 1.0],",
            "          [104.0, 0.0],",
            "          [105.0, 1.0]",
            "        ]",
            "      },",
            "      'properties': {}",
            "    },",
            "    {",
            "      'type': 'Feature',",
            "      'id': 'id2',",
            "      'geometry': {",
            "        'type': 'Polygon',",
            "        'coordinates': [",
            "          [",
            "            [100.0, 0.0],",
            "            [101.0, 0.0],",
            "            [101.0, 1.0],",
            "            [100.0, 1.0],",
            "            [100.0, 0.0]",
            "          ]",
            "        ]",
            "      },",
            "      'properties': {}",
            "    }",
            "  ]",
            "}");

    validGeojsonData = validGeojsonData.replace("'", "\"");
  }

  @Test
  public void testGtfsGeojsonFileLoader() /*throws ValidatorLoaderException*/ {

    var container = createLoader(validGeojsonData);
    var geojsonContainer = (GtfsGeojsonFeaturesContainer) container;
    assertNotNull(container);
    assertEquals(
        "Test geojson file is not parsable",
        container.getTableStatus(),
        TableStatus.PARSABLE_HEADERS_AND_ROWS);
    assertEquals(2, container.entityCount());
    assertEquals("id1", geojsonContainer.getEntities().get(0).featureId());
    assertEquals("id2", geojsonContainer.getEntities().get(1).featureId());
  }

  @Test
  public void testBrokenJson() {
    var container = createLoader("This is a broken json");
    assertEquals(
        "Parsing the Geojson file should fail, returning an empty list of entities",
        0,
        container.entityCount());
  }

  private GtfsEntityContainer createLoader(String jsonData) {
    GeojsonFileLoader loader = new GeojsonFileLoader();
    var fileDescriptor = new GtfsGeojsonFileDescriptor();
    NoticeContainer noticeContainer = new NoticeContainer();
    InputStream inputStream = new ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8));
    return loader.load(fileDescriptor, null, inputStream, noticeContainer);
  }
}
