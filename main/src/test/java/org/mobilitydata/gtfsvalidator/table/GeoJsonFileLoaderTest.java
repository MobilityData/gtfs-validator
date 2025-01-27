package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.InvalidGeometryNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

/** Runs GeoJsonFileLoader on test json data. */
@RunWith(JUnit4.class)
public class GeoJsonFileLoaderTest {

  static String validGeoJsonData;
  static String invalidPolygonGeoJsonData;
  NoticeContainer noticeContainer;

  @BeforeClass
  public static void setUpBeforeClass() {
    // Create the valid and invalid JSON data strings, using single quotes for readability.
    validGeoJsonData =
            String.join(
                    "\n",
                    "{",
                    "  'type': 'FeatureCollection',",
                    "  'features': [",
                    "    {",
                    "      'id': 'id1',",
                    "      'type': 'Feature',",
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
                    "    },",
                    "    {",
                    "      'id': 'id2',",
                    "      'type': 'Feature',",
                    "      'geometry': {",
                    "        'type': 'Polygon',",
                    "        'coordinates': [",
                    "          [",
                    "            [200.0, 0.0],",
                    "            [201.0, 0.0],",
                    "            [201.0, 2.0],",
                    "            [200.0, 2.0],",
                    "            [200.0, 0.0]",
                    "          ]",
                    "        ]",
                    "      },",
                    "      'properties': {}",
                    "    }",
                    "  ]",
                    "}");

    invalidPolygonGeoJsonData =
            String.join(
                    "\n",
                    "{",
                    "  'type': 'FeatureCollection',",
                    "  'features': [",
                    "    {",
                    "      'id': 'id_invalid',",
                    "      'type': 'Feature',",
                    "      'geometry': {",
                    "        'type': 'Polygon',",
                    "        'coordinates': [",
                    "          [",
                    "            [100.0, 0.0],",
                    "            [101.0, 0.0],",
                    "            [100.5, 0.5]"
                            + // Invalid Polygon: not closed
                            "          ]",
                    "        ]",
                    "      },",
                    "      'properties': {}",
                    "    }",
                    "  ]",
                    "}");

    // Replace single quotes with double quotes for JSON compliance
    validGeoJsonData = validGeoJsonData.replace("'", "\"");
    invalidPolygonGeoJsonData = invalidPolygonGeoJsonData.replace("'", "\"");
  }

  @Before
  public void setUp() {
    noticeContainer = new NoticeContainer();
  }

  @Test
  public void testGtfsGeoJsonFileLoader() /*throws ValidatorLoaderException*/ {

    var container = createLoader(validGeoJsonData);
    var geoJsonContainer = (GtfsGeoJsonFeaturesContainer) container;
    assertThat(container).isNotNull();
    assertThat(container.getTableStatus()).isEqualTo(TableStatus.PARSABLE_HEADERS_AND_ROWS);
    assertThat(geoJsonContainer.entityCount()).isEqualTo(2);
    assertThat(geoJsonContainer.getEntities().get(0).featureId()).isEqualTo("id1");
    assertThat(geoJsonContainer.getEntities().get(1).featureId()).isEqualTo("id2");
  }

  @Test
  public void testBrokenJson() {
    var container = createLoader("This is a broken json");
    assertThat(container.entityCount()).isEqualTo(0);
  }

  @Test
  public void testInvalidPolygonGeometry() {
    // Testing for invalid polygon where coordinates do not form a closed ring
    var container = createLoader(invalidPolygonGeoJsonData);

    // Check if the container is in the correct state
    assertThat(container.getTableStatus()).isEqualTo(TableStatus.UNPARSABLE_ROWS);

    // Check if the correct validation notice is generated for the invalid geometry
    List<InvalidGeometryNotice> notices =
            noticeContainer.getValidationNotices().stream()
                    .filter(InvalidGeometryNotice.class::isInstance)
                    .map(InvalidGeometryNotice.class::cast)
                    .collect(Collectors.toList());

    assertThat(notices.size()).isGreaterThan(0);
  }

  private GtfsEntityContainer createLoader(String jsonData) {
    GeoJsonFileLoader loader = new GeoJsonFileLoader();
    var fileDescriptor = new GtfsGeoJsonFileDescriptor();
    InputStream inputStream = new ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8));
    return loader.load(fileDescriptor, null, inputStream, noticeContainer);
  }
}
