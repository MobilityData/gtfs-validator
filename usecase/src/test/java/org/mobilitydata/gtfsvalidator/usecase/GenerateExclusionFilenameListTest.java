/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.mobilitydata.gtfsvalidator.usecase;

import java.util.List;


@SuppressWarnings("unused")
        // to avoid lint, all the list initialized will be used later when implementing the tests
class GenerateExclusionFilenameListTest {
    private final static List<String> translationExclusionList = List.of("translations.txt");
    private final static List<String> attributionExclusionList = List.of("attributions.txt", "translations.txt");
    private final static List<String> feedIntoExclusionList = List.of("feed_info.txt", "translations.txt");
    private final static List<String> levelExclusionList = List.of("levels.txt", "stops.txt", "stop_times.txt",
            "transfers.txt", "pathways.txt", "translations.txt");
    private final static List<String> stopExclusionList = List.of("stops.txt", "stop_times.txt", "transfers.txt",
            "pathways.txt", "translations.txt");
    private final static List<String> stopTimeExclusionList = List.of("stop_times.txt", "translations.txt");
    private final static List<String> transferExclusionList = List.of("transfers.txt", "translations.txt");
    private final static List<String> pathwayExclusionList = List.of("pathways.txt", "translations.txt");
    private final static List<String> frequenciesExclusionList = List.of("frequencies.txt", "translations.txt");
    private final static List<String> shapeExclusionList = List.of("shapes.txt", "translations.txt", "trips.txt",
            "stop_times.txt", "frequencies.txt", "attributions.txt");
    private final static List<String> fareRuleExclusionList = List.of("fare_rules.txt", "translations.txt");


    private final static String schemaTreeAsString = "{\n" +
            "  \"agency.txt\": {\n" +
            "    \"routes.txt\": {\n" +
            "      \"trips.txt\": {\n" +
            "        \"attributions.txt\": {\n" +
            "          \"translations.txt\": {}\n" +
            "        },\n" +
            "        \"frequencies.txt\":  {\n" +
            "          \"translations.txt\": {}\n" +
            "        },\n" +
            "        \"stop_times.txt\":  {\n" +
            "          \"translations.txt\": {}\n" +
            "        }\n" +
            "      },\n" +
            "      \"fare_rules.txt\" : {},\n" +
            "      \"attributions.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      }\n" +
            "    },\n" +
            "    \"fare_attributes.txt\": {\n" +
            "      \"fare_rules.txt\": {\n" +
            "        \"translations.txt\": {},\n" +
            "        \"stops.txt\": {\n" +
            "          \"stop_times.txt\": {\n" +
            "            \"translations.txt\": {}\n" +
            "          },\n" +
            "          \"transfers.txt\": {\n" +
            "            \"translations.txt\": {}\n" +
            "          },\n" +
            "          \"pathways.txt\": {\n" +
            "            \"translations.txt\": {}\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"translations.txt\": {}\n" +
            "    },\n" +
            "    \"attributions.txt\":  {\n" +
            "      \"translations.txt\": {}\n" +
            "    }\n" +
            "  },\n" +
            "  \"stops.txt\": {\n" +
            "    \"stop_times.txt\": {\n" +
            "      \"translations.txt\": {}\n" +
            "    },\n" +
            "    \"transfers.txt\": {\n" +
            "      \"translations.txt\": {}\n" +
            "    },\n" +
            "    \"pathways.txt\": {\n" +
            "      \"translations.txt\": {}\n" +
            "    }\n" +
            "  },\n" +
            "  \"routes.txt\": {\n" +
            "    \"trips.txt\": {\n" +
            "      \"attributions.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      },\n" +
            "      \"frequencies.txt\":  {\n" +
            "        \"translations.txt\": {}\n" +
            "      },\n" +
            "      \"stop_times.txt\":  {\n" +
            "        \"translations.txt\": {}\n" +
            "      }\n" +
            "    },\n" +
            "    \"fare_rules.txt\": {\n" +
            "      \"translations.txt\": {},\n" +
            "      \"stops.txt\": {\n" +
            "        \"stop_times.txt\": {\n" +
            "          \"translations.txt\": {}\n" +
            "        },\n" +
            "        \"transfers.txt\": {\n" +
            "          \"translations.txt\": {}\n" +
            "        },\n" +
            "        \"pathways.txt\": {\n" +
            "          \"translations.txt\": {}\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"translations.txt\": {},\n" +
            "    \"attributions.txt\": {\n" +
            "      \"translations.txt\": {}\n" +
            "    }\n" +
            "  },\n" +
            "  \"trips.txt\": {\n" +
            "    \"stop_times.txt\": {\n" +
            "      \"translations.txt\": {}\n" +
            "    },\n" +
            "    \"frequencies.txt\": {\n" +
            "      \"translations.txt\": {}\n" +
            "    },\n" +
            "    \"translations.txt\": {},\n" +
            "    \"attributions.txt\": {\n" +
            "      \"translations.txt\": {}\n" +
            "    }\n" +
            "  },\n" +
            "  \"stop_times.txt\": {\n" +
            "    \"translations.txt\": {}\n" +
            "  },\n" +
            "  \"calendar.txt\": {\n" +
            "    \"translations.txt\": {},\n" +
            "    \"trips.txt\": {\n" +
            "      \"stop_times.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      },\n" +
            "      \"frequencies.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      },\n" +
            "      \"translations.txt\": {},\n" +
            "      \"attributions.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      }\n" +
            "    },\n" +
            "    \"calendar_dates.txt\": {\n" +
            "      \"translations.txt\": {},\n" +
            "      \"trips.txt\": {\n" +
            "        \"stop_times.txt\": {\n" +
            "          \"translations.txt\": {}\n" +
            "        },\n" +
            "        \"frequencies.txt\": {\n" +
            "          \"translations.txt\": {}\n" +
            "        },\n" +
            "        \"translations.txt\": {},\n" +
            "        \"attributions.txt\": {\n" +
            "          \"translations.txt\": {}\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"calendar_dates.txt\": {\n" +
            "    \"translations.txt\": {},\n" +
            "    \"trips.txt\": {\n" +
            "      \"stop_times.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      },\n" +
            "      \"frequencies.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      },\n" +
            "      \"translations.txt\": {},\n" +
            "      \"attributions.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"fare_attributes.txt\": {\n" +
            "    \"fare_rules.txt\": {\n" +
            "      \"translations.txt\": {},\n" +
            "      \"stops.txt\": {\n" +
            "        \"stop_times.txt\": {\n" +
            "          \"translations.txt\": {}\n" +
            "        },\n" +
            "        \"transfers.txt\": {\n" +
            "          \"translations.txt\": {}\n" +
            "        },\n" +
            "        \"pathways.txt\": {\n" +
            "          \"translations.txt\": {}\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"translations.txt\": {}\n" +
            "  },\n" +
            "  \"fare_rules.txt\": {\n" +
            "    \"translations.txt\": {},\n" +
            "    \"stops.txt\": {\n" +
            "      \"stop_times.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      },\n" +
            "      \"transfers.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      },\n" +
            "      \"pathways.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"shapes.txt\": {\n" +
            "    \"translations.txt\": {},\n" +
            "    \"trips.txt\": {\n" +
            "      \"stop_times.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      },\n" +
            "      \"frequencies.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      },\n" +
            "      \"attributions.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"frequencies.txt\": {\n" +
            "    \"translations.txt\": {}\n" +
            "  },\n" +
            "  \"transfers.txt\": {\n" +
            "    \"translations.txt\": {}\n" +
            "  },\n" +
            "  \"pathways.txt\": {\n" +
            "    \"translations.txt\": {}\n" +
            "  },\n" +
            "  \"levels.txt\": {\n" +
            "    \"stops.txt\": {\n" +
            "      \"stop_times.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      },\n" +
            "      \"transfers.txt\": {},\n" +
            "      \"pathways.txt\": {\n" +
            "        \"translations.txt\": {}\n" +
            "      }\n" +
            "    },\n" +
            "    \"translations.txt\": {}\n" +
            "  },\n" +
            "  \"translations.txt\": {},\n" +
            "  \"feed_info.txt\": {\n" +
            "    \"translations.txt\": {}\n" +
            "  },\n" +
            "  \"attributions.txt\": {\n" +
            "    \"translations.txt\": {}\n" +
            "  }\n" +
            "}";

//    @Test
//    void excludeAttributionShouldReturnAttributionExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        assertEquals(attributionExclusionList, underTest.execute(List.of("attributions.txt"), mockObjectReader));
//    }
//
//    @Test
//    void excludeFeedInfoShouldReturnFeedInfoExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        assertEquals(feedIntoExclusionList, underTest.execute(List.of("feed_info.txt"), mockObjectReader));
//    }
//
//    @Test
//    void excludeTranslationShouldReturnTranslationExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        assertEquals(translationExclusionList, underTest.execute(List.of("translations.txt"), mockObjectReader));
//    }
//
//    @Test
//    void excludeLevelShouldReturnLevelExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        final List<String> toCheck = underTest.execute(List.of("levels.txt"), mockObjectReader);
//        assertEquals(levelExclusionList.size(), toCheck.size());
//        for (String filename : toCheck){
//            assertTrue(levelExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludePathwayShouldReturnPathwayExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        final List<String> toCheck = underTest.execute(List.of("pathways.txt"), mockObjectReader);
//        assertEquals(pathwayExclusionList.size(), toCheck.size());
//        for (String filename : toCheck){
//            assertTrue(pathwayExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludeTransferShouldReturnPathwayExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        final List<String> toCheck = underTest.execute(List.of("transfers.txt"), mockObjectReader);
//        assertEquals(transferExclusionList.size(), toCheck.size());
//        for (String filename : toCheck){
//            assertTrue(transferExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludeFrequenciesShouldReturnFrequenciesExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        final List<String> toCheck = underTest.execute(List.of("frequencies.txt"), mockObjectReader);
//        assertEquals(frequenciesExclusionList.size(), toCheck.size());
//        for (String filename : toCheck){
//            assertTrue(frequenciesExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludeShapeShouldReturnShapeExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        final List<String> toCheck = underTest.execute(List.of("shapes.txt"), mockObjectReader);
//        assertEquals(shapeExclusionList.size(), toCheck.size());
//        for (String filename : toCheck){
//            assertTrue(shapeExclusionList.contains(filename));
//        }
//    }

    // todo: to be continued ad double checked + add test for illegal file name

//    @Test
//    void excludeFareRuleShouldReturnFareRuleExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        final List<String> toCheck = underTest.execute(List.of("fare_rules.txt"));
//        assertEquals(fareRuleExclusionList.size(), toCheck.size());
//        for (String filename : toCheck){
//            assertTrue(fareRuleExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludeFareAttributeShouldReturnFareAttributeExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        assertEquals(fareAttributeExclusionList, underTest.execute(List.of("fare_attribute.txt")));
//    }
//
//    @Test
//    void excludeCalendarDateShouldReturnCalendarDateExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        assertEquals(calendarDateExclusionList, underTest.execute(List.of("calendar_dates.txt")));
//    }
//
//    @Test
//    void excludeCalendarShouldReturnCalendarExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        assertEquals(calendarExclusionList, underTest.execute(List.of("calendar.txt")));
//    }
//
//    @Test
//    void excludeStopTimeShouldReturnStopTimeExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        assertEquals(stopTimeExclusionList, underTest.execute(List.of("stop_times.txt")));
//    }
//
//    @Test
//    void excludeTripShouldReturnTripExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        assertEquals(tripExclusionList, underTest.execute(List.of("trips.txt")));
//    }
//
//    @Test
//    void excludeRouteShouldReturnRouteExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        assertEquals(routeExclusionList, underTest.execute(List.of("routes.txt")));
//    }
//
//    @Test
//    void excludeStopShouldReturnStopExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        assertEquals(stopExclusionList, underTest.execute(List.of("stops.txt")));
//    }
//
//    @Test
//    void excludeAgencyShouldReturnAgencyExclusionList() throws IOException {
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString);
//        assertEquals(agencyExclusionList, underTest.execute(List.of("agency.txt")));
//    }
}
