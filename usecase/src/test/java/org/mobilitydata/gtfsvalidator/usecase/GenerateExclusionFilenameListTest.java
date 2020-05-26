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

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GenerateExclusionFilenameListTest {
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

    @Test
    void malformedFilenameListShouldThrowException() {
        final GtfsSpecRepository mockGtfsSpecRepo = spy(GtfsSpecRepository.class);

        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.EXCLUSION_KEY))
                .thenReturn("[wrong_file_name.txt]");

        final GenerateExclusionFilenameList underTest =
                new GenerateExclusionFilenameList(mockGtfsSpecRepo, mockExecParamRepo);
        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::execute);

        assertEquals("Filename list to exclude is malformed: [wrong_file_name.txt]", exception.getMessage());
        verify(mockExecParamRepo, times(1)).getExecParamValue(ExecParamRepository.EXCLUSION_KEY);
        verify(mockGtfsSpecRepo, times(1)).getOptionalFilenameList();
        verify(mockGtfsSpecRepo, times(1)).getRequiredFilenameList();
        verifyNoMoreInteractions(mockExecParamRepo, mockGtfsSpecRepo);
    }

//    @Test
//    void malformedFilenameListShouldThrowException() {
//        final GtfsSpecRepository mockGtfsSpecRepo = spy(GtfsSpecRepository.class);
//
//        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
//        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.EXCLUSION_KEY))
//                .thenReturn("[wrong_file_name.txt]");
//
//        final GenerateExclusionFilenameList underTest =
//                new GenerateExclusionFilenameList(mockGtfsSpecRepo, mockExecParamRepo);
//        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::execute);
//
//        assertEquals("Filename list to exclude is malformed: [wrong_file_name.txt]", exception.getMessage());
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
}