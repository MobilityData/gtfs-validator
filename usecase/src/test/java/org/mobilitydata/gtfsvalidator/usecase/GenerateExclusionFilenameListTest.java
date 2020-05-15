//package org.mobilitydata.gtfsvalidator.usecase;
//
//import com.fasterxml.jackson.databind.ObjectReader;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//
//class GenerateExclusionFilenameListTest {
//    private final static List<String> translationExclusionList = List.of("translations.txt");
//    private final static List<String> attributionExclusionList = List.of("attributions.txt", "translations.txt");
//    private final static List<String> feedIntoExclusionList = List.of("feed_info.txt", "translations.txt");
//    private final static List<String> levelExclusionList = List.of("levels.txt", "stops.txt", "stop_times.txt",
//            "transfers.txt", "pathways.txt", "translations.txt");
//    private final static List<String> stopExclusionList = List.of("stops.txt", "stop_times.txt", "transfers.txt",
//            "pathways.txt", "translations.txt");
//    private final static List<String> stopTimeExclusionList = List.of("stop_times.txt", "translations.txt");
//    private final static List<String> transferExclusionList = List.of("transfers.txt", "translations.txt");
//    private final static List<String> pathwayExclusionList = List.of("pathways.txt", "translations.txt");
//    private final static List<String> frequenciesExclusionList = List.of("frequencies.txt", "translations.txt");
//    private final static List<String> shapeExclusionList = List.of("shapes.txt", "translations.txt", "trips.txt",
//            "stop_times.txt", "frequencies.txt", "attributions.txt");
//    private final static List<String> fareRuleExclusionList = List.of("fare_rules.txt", "translations.txt");
//    private final static List<String> fareAttributeExclusionList = List.of("fare_attributes.txt", "fare_rules.txt",
//            "translations.txt");
//    private final static List<String> calendarDateExclusionList = List.of("calendar_dates.txt", "translations.txt",
//            "trips.txt", "stop_times.txt", "frequencies.txt", "attributions.txt");
//    private final static List<String> calendarExclusionList = List.of("calendar.txt", "calendar_dates.txt",
//            "translations.txt", "trips.txt", "stop_times.txt", "frequencies.txt", "attributions.txt");
//    private final static List<String> tripExclusionList = List.of("trips.txt", "stop_times.txt",
//            "frequencies.txt", "translations.txt", "attributions.txt");
//    private final static List<String> routeExclusionList = List.of("routes.txt", "trips.txt",
//            "fare_rules.txt", "translations.txt", "attributions.txt", "stop_times.txt", "frequencies.txt");
//    private final static List<String> agencyExclusionList = List.of("agency.txt", "routes.txt",
//            "fare_attributes.txt", "attributions.txt", "fare_rules.txt", "trips.txt", "frequencies.txt",
//            "stop_times.txt", "translations.txt");
//
//
//    private final static String schemaTreeAsString = "{\n" +
//            "  \"agency.txt\": {\n" +
//            "    \"routes.txt\": {\n" +
//            "      \"trips.txt\": {\n" +
//            "        \"attributions.txt\": {\n" +
//            "          \"translations.txt\": {}\n" +
//            "        },\n" +
//            "        \"frequencies.txt\": {\n" +
//            "          \"translations.txt\": {}\n" +
//            "        },\n" +
//            "        \"stop_times.txt\": {\n" +
//            "          \"translations.txt\": {}\n" +
//            "        }\n" +
//            "      },\n" +
//            "      \"fare_rules.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      },\n" +
//            "      \"attributions.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      }\n" +
//            "    },\n" +
//            "    \"fare_attributes.txt\": {\n" +
//            "      \"fare_rules.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      },\n" +
//            "      \"translations.txt\": {}\n" +
//            "    },\n" +
//            "    \"attributions.txt\": {\n" +
//            "      \"translations.txt\": {}\n" +
//            "    }\n" +
//            "  },\n" +
//            "  \"stops.txt\": {\n" +
//            "    \"stop_times.txt\": {\n" +
//            "      \"translations.txt\": {}\n" +
//            "    },\n" +
//            "    \"transfers.txt\": {\n" +
//            "      \"translations.txt\": {}\n" +
//            "    },\n" +
//            "    \"pathways.txt\": {\n" +
//            "      \"translations.txt\": {}\n" +
//            "    }\n" +
//            "  },\n" +
//            "  \"routes.txt\": {\n" +
//            "    \"trips.txt\": {\n" +
//            "      \"attributions.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      },\n" +
//            "      \"frequencies.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      },\n" +
//            "      \"stop_times.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      }\n" +
//            "    },\n" +
//            "    \"fare_rules.txt\": {\n" +
//            "      \"translations.txt\": {}\n" +
//            "    },\n" +
//            "    \"translations.txt\": {},\n" +
//            "    \"attributions.txt\": {\n" +
//            "      \"translations.txt\": {}\n" +
//            "    }\n" +
//            "  },\n" +
//            "  \"trips.txt\": {\n" +
//            "    \"stop_times.txt\": {\n" +
//            "      \"translations.txt\": {}\n" +
//            "    },\n" +
//            "    \"frequencies.txt\": {\n" +
//            "      \"translations.txt\": {}\n" +
//            "    },\n" +
//            "    \"translations.txt\": {},\n" +
//            "    \"attributions.txt\": {\n" +
//            "      \"translations.txt\": {}\n" +
//            "    }\n" +
//            "  },\n" +
//            "  \"stop_times.txt\": {\n" +
//            "    \"translations.txt\": {}\n" +
//            "  },\n" +
//            "  \"calendar.txt\": {\n" +
//            "    \"translations.txt\": {},\n" +
//            "    \"trips.txt\": {\n" +
//            "      \"stop_times.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      },\n" +
//            "      \"frequencies.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      },\n" +
//            "      \"translations.txt\": {},\n" +
//            "      \"attributions.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      }\n" +
//            "    },\n" +
//            "    \"calendar_dates.txt\": {\n" +
//            "      \"translations.txt\": {},\n" +
//            "      \"trips.txt\": {\n" +
//            "        \"stop_times.txt\": {\n" +
//            "          \"translations.txt\": {}\n" +
//            "        },\n" +
//            "        \"frequencies.txt\": {\n" +
//            "          \"translations.txt\": {}\n" +
//            "        },\n" +
//            "        \"translations.txt\": {},\n" +
//            "        \"attributions.txt\": {\n" +
//            "          \"translations.txt\": {}\n" +
//            "        }\n" +
//            "      }\n" +
//            "    }\n" +
//            "  },\n" +
//            "  \"calendar_dates.txt\": {\n" +
//            "    \"translations.txt\": {},\n" +
//            "    \"trips.txt\": {\n" +
//            "      \"stop_times.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      },\n" +
//            "      \"frequencies.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      },\n" +
//            "      \"translations.txt\": {},\n" +
//            "      \"attributions.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      }\n" +
//            "    }\n" +
//            "  },\n" +
//            "  \"fare_attributes.txt\": {\n" +
//            "    \"fare_rules.txt\": {\n" +
//            "      \"translations.txt\": {}\n" +
//            "    },\n" +
//            "    \"translations.txt\": {}\n" +
//            "  },\n" +
//            "  \"fare_rules.txt\": {\n" +
//            "    \"translations.txt\": {}\n" +
//            "  },\n" +
//            "  \"shapes.txt\": {\n" +
//            "    \"translations.txt\": {},\n" +
//            "    \"trips.txt\": {\n" +
//            "      \"stop_times.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      },\n" +
//            "      \"frequencies.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      },\n" +
//            "      \"attributions.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      }\n" +
//            "    }\n" +
//            "  },\n" +
//            "  \"frequencies.txt\": {\n" +
//            "    \"translations.txt\": {}\n" +
//            "  },\n" +
//            "  \"transfers.txt\": {\n" +
//            "    \"translations.txt\": {}\n" +
//            "  },\n" +
//            "  \"pathways.txt\": {\n" +
//            "    \"translations.txt\": {}\n" +
//            "  },\n" +
//            "  \"levels.txt\": {\n" +
//            "    \"stops.txt\": {\n" +
//            "      \"stop_times.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      },\n" +
//            "      \"transfers.txt\": {},\n" +
//            "      \"pathways.txt\": {\n" +
//            "        \"translations.txt\": {}\n" +
//            "      }\n" +
//            "    },\n" +
//            "    \"translations.txt\": {}\n" +
//            "  },\n" +
//            "  \"translations.txt\": {},\n" +
//            "  \"feed_info.txt\": {\n" +
//            "    \"translations.txt\": {}\n" +
//            "  },\n" +
//            "  \"attributions.txt\": {\n" +
//            "    \"translations.txt\": {}\n" +
//            "  }\n" +
//            "}";
//
//    @Test
//    void excludeAttributionShouldReturnAttributionExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        assertEquals(attributionExclusionList, underTest.execute(List.of("attributions.txt")));
//    }
//
//    @Test
//    void excludeFeedInfoShouldReturnFeedInfoExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        assertEquals(feedIntoExclusionList, underTest.execute(List.of("feed_info.txt")));
//    }
//
//    @Test
//    void excludeTranslationShouldReturnTranslationExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        assertEquals(translationExclusionList, underTest.execute(List.of("translations.txt")));
//    }
//
//    @Test
//    void excludeLevelShouldReturnLevelExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        final List<String> toCheck = underTest.execute(List.of("levels.txt"));
//        assertEquals(levelExclusionList.size(), toCheck.size());
//        for (String filename : toCheck) {
//            assertTrue(levelExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludePathwayShouldReturnPathwayExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        final List<String> toCheck = underTest.execute(List.of("pathways.txt"));
//        assertEquals(pathwayExclusionList.size(), toCheck.size());
//        for (String filename : toCheck) {
//            assertTrue(pathwayExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludeTransferShouldReturnPathwayExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        final List<String> toCheck = underTest.execute(List.of("transfers.txt"));
//        assertEquals(transferExclusionList.size(), toCheck.size());
//        for (String filename : toCheck) {
//            assertTrue(transferExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludeFrequenciesShouldReturnFrequenciesExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        final List<String> toCheck = underTest.execute(List.of("frequencies.txt"));
//        assertEquals(frequenciesExclusionList.size(), toCheck.size());
//        for (String filename : toCheck) {
//            assertTrue(frequenciesExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludeShapeShouldReturnShapeExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        final List<String> toCheck = underTest.execute(List.of("shapes.txt"));
//        assertEquals(shapeExclusionList.size(), toCheck.size());
//        for (String filename : toCheck) {
//            assertTrue(shapeExclusionList.contains(filename));
//        }
//    }
//
//    // todo: to be continued ad double checked + add test for illegal file name
//
//    @Test
//    void excludeFareRuleShouldReturnFareRuleExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        final List<String> toCheck = underTest.execute(List.of("fare_rules.txt"));
//        assertEquals(fareRuleExclusionList.size(), toCheck.size());
//        for (String filename : toCheck) {
//            assertTrue(fareRuleExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludeFareAttributeShouldReturnFareAttributeExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        final List<String> toCheck = underTest.execute(List.of("fare_attributes.txt"));
//        assertEquals(fareAttributeExclusionList.size(), toCheck.size());
//        for (String filename : toCheck) {
//            assertTrue(fareAttributeExclusionList.contains(filename));
//        }
//    }
//
//
//    @Test
//    void excludeCalendarDateShouldReturnCalendarDateExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        final List<String> toCheck = underTest.execute(List.of("calendar_dates.txt"));
//        assertEquals(calendarDateExclusionList.size(), toCheck.size());
//        for (String filename : toCheck) {
//            assertTrue(calendarDateExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludeCalendarShouldReturnCalendarExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        final List<String> toCheck = underTest.execute(List.of("calendar.txt"));
//        assertEquals(calendarExclusionList.size(), toCheck.size());
//        for (String filename : toCheck) {
//            assertTrue(calendarExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludeStopTimeShouldReturnStopTimeExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        final List<String> toCheck = underTest.execute(List.of("stop_times.txt"));
//        assertEquals(stopTimeExclusionList.size(), toCheck.size());
//        for (String filename : toCheck) {
//            assertTrue(stopTimeExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludeTripShouldReturnTripExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        final List<String> toCheck = underTest.execute(List.of("trips.txt"));
//        assertEquals(tripExclusionList.size(), toCheck.size());
//        for (String filename : toCheck) {
//            assertTrue(tripExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludeRouteShouldReturnRouteExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        final List<String> toCheck = underTest.execute(List.of("routes.txt"));
//        assertEquals(routeExclusionList.size(), toCheck.size());
//        for (String filename : toCheck) {
//            assertTrue(routeExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludeStopShouldReturnStopExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        final List<String> toCheck = underTest.execute(List.of("stops.txt"));
//        assertEquals(stopExclusionList.size(), toCheck.size());
//        for (String filename : toCheck) {
//            assertTrue(stopExclusionList.contains(filename));
//        }
//    }
//
//    @Test
//    void excludeAgencyShouldReturnAgencyExclusionList() throws IOException {
//        final ObjectReader mockObjectReader = mock(ObjectReader.class);
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(schemaTreeAsString, mockObjectReader);
//        final List<String> toCheck = underTest.execute(List.of("agency.txt"));
//        assertEquals(agencyExclusionList.size(), toCheck.size());
//        for (String filename : toCheck) {
//            assertTrue(agencyExclusionList.contains(filename));
//        }
//    }
//}
