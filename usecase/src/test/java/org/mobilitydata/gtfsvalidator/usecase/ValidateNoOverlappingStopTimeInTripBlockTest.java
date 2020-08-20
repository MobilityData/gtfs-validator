/*
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
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.OverlappingTripsInBlockNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.mockito.Mockito.*;

class ValidateNoOverlappingStopTimeInTripBlockTest {

    @Test
    void nonOverlappingTripsWithSameServiceIdShouldNotGenerateNotice() {
        // trips.txt
        // | routeId | tripId | serviceId | blockId |
        // |---------|--------|-----------|---------|
        // | 0       | 2      | a         | 7       |
        // | 0       | 5      | a         | 7       |
        // | 0       | 8      | a         | 7       |

        // stop_times.txt
        // | tripId | arrivalTime | departureTime | stopId | stopSequence  |
        // |--------|-------------|---------------|--------|---------------|
        // | 2      | 07:00       | 07:40         | 101    | 10            |
        // | 2      | 08:20       | 10:00         | 102    | 11            |
        // | 5      | 11:03       | 11:15         | 103    | 12            |
        // | 5      | 11:16       | 11:20         | 104    | 13            |
        // | 8      | 11:40       | 12:16         | 105    | 14            |
        // | 8      | 13:08       | 13:50         | 106    | 15            |

        // trips.txt + stop_times.txt
        // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
        // |---------|--------|-----------|---------|-----------------|----------------|
        // | 0       | 2      | a         | 7       | 07:00           | 10:00          |
        // | 0       | 5      | a         | 7       | 11:03           | 11:20          |
        // | 0       | 8      | a         | 7       | 11:40           | 13:50          |

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, List<Trip>> mockTripPerBlockIdCollection = new HashMap<>();
        final List<Trip> mockTripCollection = new ArrayList<>();


        final Trip firstMockTrip = mock(Trip.class);
        when(firstMockTrip.getTripId()).thenReturn("2");
        when(firstMockTrip.getServiceId()).thenReturn("a");
        when(firstMockTrip.getBlockId()).thenReturn("7");

        final Trip secondMockTrip = mock(Trip.class);
        when(secondMockTrip.getTripId()).thenReturn("5");
        when(secondMockTrip.getServiceId()).thenReturn("a");
        when(secondMockTrip.getBlockId()).thenReturn("7");

        final Trip thirdMockTrip = mock(Trip.class);
        when(thirdMockTrip.getTripId()).thenReturn("8");
        when(thirdMockTrip.getServiceId()).thenReturn("a");
        when(thirdMockTrip.getBlockId()).thenReturn("7");

        mockTripCollection.add(firstMockTrip);
        mockTripCollection.add(secondMockTrip);
        mockTripCollection.add(thirdMockTrip);
        mockTripPerBlockIdCollection.put("7", mockTripCollection);

        final SortedMap<Integer, StopTime> mockFirstTripStopTimeCollection = new TreeMap<>();

        final StopTime firstTripFirstStopTime = mock(StopTime.class);
        when(firstTripFirstStopTime.getArrivalTime()).thenReturn(700);
        when(firstTripFirstStopTime.getDepartureTime()).thenReturn(740);

        final StopTime firstTripLastStopTime = mock(StopTime.class);
        when(firstTripLastStopTime.getArrivalTime()).thenReturn(820);
        when(firstTripLastStopTime.getDepartureTime()).thenReturn(1000);

        mockFirstTripStopTimeCollection.put(10, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(11, firstTripLastStopTime);

        final SortedMap<Integer, StopTime> mockSecondTripStopTimeCollection = new TreeMap<>();

        final StopTime secondTripFirstStopTime = mock(StopTime.class);
        when(secondTripFirstStopTime.getArrivalTime()).thenReturn(1103);
        when(secondTripFirstStopTime.getDepartureTime()).thenReturn(1115);

        final StopTime secondTripLastStopTime = mock(StopTime.class);
        when(secondTripLastStopTime.getArrivalTime()).thenReturn(1116);
        when(secondTripLastStopTime.getDepartureTime()).thenReturn(1120);

        mockSecondTripStopTimeCollection.put(12, secondTripFirstStopTime);
        mockSecondTripStopTimeCollection.put(13, secondTripLastStopTime);

        final SortedMap<Integer, StopTime> mockThirdTripStopTimeCollection = new TreeMap<>();
        final StopTime thirdTripFirstStopTime = mock(StopTime.class);
        when(thirdTripFirstStopTime.getArrivalTime()).thenReturn(1140);
        when(thirdTripFirstStopTime.getDepartureTime()).thenReturn(1216);

        final StopTime thirdTripLastStopTime = mock(StopTime.class);
        when(thirdTripLastStopTime.getArrivalTime()).thenReturn(1308);
        when(thirdTripLastStopTime.getDepartureTime()).thenReturn(1350);

        mockThirdTripStopTimeCollection.put(14, thirdTripFirstStopTime);
        mockThirdTripStopTimeCollection.put(15, thirdTripLastStopTime);

        when(mockDataRepo.getAllTripByBlockId()).thenReturn(mockTripPerBlockIdCollection);
        when(mockDataRepo.getStopTimeByTripId("2")).thenReturn(mockFirstTripStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("5")).thenReturn(mockSecondTripStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("8")).thenReturn(mockThirdTripStopTimeCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtils = mock(TimeUtils.class);
        when(mockTimeUtils.convertIntegerToHMMSS(700)).thenReturn("700");
        when(mockTimeUtils.convertIntegerToHMMSS(1000)).thenReturn("1000");
        when(mockTimeUtils.convertIntegerToHMMSS(1103)).thenReturn("1103");
        when(mockTimeUtils.convertIntegerToHMMSS(1140)).thenReturn("1140");
        when(mockTimeUtils.convertIntegerToHMMSS(1350)).thenReturn("1350");

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();

        verify(firstMockTrip, times(5)).getTripId();
        verify(firstMockTrip, times(2)).getServiceId();
        verify(secondMockTrip, times(6)).getTripId();
        verify(secondMockTrip, times(2)).getServiceId();
        verify(thirdMockTrip, times(7)).getTripId();
        verify(thirdMockTrip, times(2)).getServiceId();

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(3)).getStopTimeByTripId("8");

        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getArrivalTime();

        verify(secondTripFirstStopTime, times(2)).getArrivalTime();

        verify(thirdTripFirstStopTime, times(3)).getArrivalTime();

        verify(firstTripLastStopTime, times(1)).getDepartureTime();

        verify(secondTripLastStopTime, times(2)).getDepartureTime();
        verify(secondTripLastStopTime, times(2)).getDepartureTime();

        verify(thirdTripLastStopTime, times(3)).getDepartureTime();
        verify(thirdTripLastStopTime, times(3)).getDepartureTime();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockLogger, mockDataRepo, mockTimeUtils, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripLastStopTime, secondTripFirstStopTime, secondTripLastStopTime,
                thirdTripFirstStopTime, thirdTripLastStopTime);
    }

    @Test
    void nonOverlappingTripsWithDifferentServiceIdShouldNotGenerateNoticeWhenCalendarAreProvided() {
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtils = mock(TimeUtils.class);

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");
        // todo
    }

    @Test
    void nonOverlappingTripsWithDifferentServiceIdShouldNotGenerateNoticeWhenCalendarAreNotProvided() {
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtils = mock(TimeUtils.class);

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");

        // todo
    }

    @Test
    void overlappingTripsWithSameServiceIdShouldGenerateNotice() {
        // trips.txt
        // | routeId | tripId | serviceId | blockId |
        // |---------|--------|-----------|---------|
        // | 0       | 2      | a         | 7       |
        // | 0       | 5      | a         | 7       |
        // | 0       | 8      | a         | 7       |

        // stop_times.txt
        // | tripId | arrivalTime | departureTime | stopId | stopSequence  |
        // |--------|-------------|---------------|--------|---------------|
        // | 2      | 07:00       | 07:40         | 101    | 10            |
        // | 2      | 08:20       | 10:00         | 102    | 11            |
        // | 5      | 09:03       | 11:15         | 103    | 12            |
        // | 5      | 11:16       | 11:20         | 104    | 13            |
        // | 8      | 11:40       | 12:16         | 105    | 14            |
        // | 8      | 13:08       | 13:50         | 106    | 15            |

        // trips.txt + stop_times.txt
        // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
        // |---------|--------|-----------|---------|-----------------|----------------|
        // | 0       | 2      | a         | 7       | 07:00           | 10:00          |
        // | 0       | 5      | a         | 7       | 09:03           | 11:20          | here trips overlap
        // | 0       | 8      | a         | 7       | 11:40           | 13:50          |

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, List<Trip>> mockTripPerBlockIdCollection = new HashMap<>();
        final List<Trip> mockTripCollection = new ArrayList<>();


        final Trip firstMockTrip = mock(Trip.class);
        when(firstMockTrip.getTripId()).thenReturn("2");
        when(firstMockTrip.getServiceId()).thenReturn("a");
        when(firstMockTrip.getBlockId()).thenReturn("7");

        final Trip secondMockTrip = mock(Trip.class);
        when(secondMockTrip.getTripId()).thenReturn("5");
        when(secondMockTrip.getServiceId()).thenReturn("a");
        when(secondMockTrip.getBlockId()).thenReturn("7");

        final Trip thirdMockTrip = mock(Trip.class);
        when(thirdMockTrip.getTripId()).thenReturn("8");
        when(thirdMockTrip.getServiceId()).thenReturn("a");
        when(thirdMockTrip.getBlockId()).thenReturn("7");

        mockTripCollection.add(firstMockTrip);
        mockTripCollection.add(secondMockTrip);
        mockTripCollection.add(thirdMockTrip);
        mockTripPerBlockIdCollection.put("7", mockTripCollection);

        final SortedMap<Integer, StopTime> mockFirstTripStopTimeCollection = new TreeMap<>();

        final StopTime firstTripFirstStopTime = mock(StopTime.class);
        when(firstTripFirstStopTime.getArrivalTime()).thenReturn(700);
        when(firstTripFirstStopTime.getDepartureTime()).thenReturn(740);

        final StopTime firstTripLastStopTime = mock(StopTime.class);
        when(firstTripLastStopTime.getArrivalTime()).thenReturn(820);
        when(firstTripLastStopTime.getDepartureTime()).thenReturn(1000);

        mockFirstTripStopTimeCollection.put(10, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(11, firstTripLastStopTime);

        final SortedMap<Integer, StopTime> mockSecondTripStopTimeCollection = new TreeMap<>();

        final StopTime secondTripFirstStopTime = mock(StopTime.class);
        when(secondTripFirstStopTime.getArrivalTime()).thenReturn(903);
        when(secondTripFirstStopTime.getDepartureTime()).thenReturn(1115);

        final StopTime secondTripLastStopTime = mock(StopTime.class);
        when(secondTripLastStopTime.getArrivalTime()).thenReturn(1116);
        when(secondTripLastStopTime.getDepartureTime()).thenReturn(1120);

        mockSecondTripStopTimeCollection.put(12, secondTripFirstStopTime);
        mockSecondTripStopTimeCollection.put(13, secondTripLastStopTime);

        final SortedMap<Integer, StopTime> mockThirdTripStopTimeCollection = new TreeMap<>();
        final StopTime thirdTripFirstStopTime = mock(StopTime.class);
        when(thirdTripFirstStopTime.getArrivalTime()).thenReturn(1140);
        when(thirdTripFirstStopTime.getDepartureTime()).thenReturn(1216);

        final StopTime thirdTripLastStopTime = mock(StopTime.class);
        when(thirdTripLastStopTime.getArrivalTime()).thenReturn(1308);
        when(thirdTripLastStopTime.getDepartureTime()).thenReturn(1350);

        mockThirdTripStopTimeCollection.put(14, thirdTripFirstStopTime);
        mockThirdTripStopTimeCollection.put(15, thirdTripLastStopTime);

        when(mockDataRepo.getAllTripByBlockId()).thenReturn(mockTripPerBlockIdCollection);
        when(mockDataRepo.getStopTimeByTripId("2")).thenReturn(mockFirstTripStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("5")).thenReturn(mockSecondTripStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("8")).thenReturn(mockThirdTripStopTimeCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtils = mock(TimeUtils.class);
        when(mockTimeUtils.convertIntegerToHMMSS(700)).thenReturn("700");
        when(mockTimeUtils.convertIntegerToHMMSS(1000)).thenReturn("1000");
        when(mockTimeUtils.convertIntegerToHMMSS(1103)).thenReturn("1103");
        when(mockTimeUtils.convertIntegerToHMMSS(1140)).thenReturn("1140");
        when(mockTimeUtils.convertIntegerToHMMSS(1350)).thenReturn("1350");

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();

        verify(firstMockTrip, times(6)).getTripId();
        verify(firstMockTrip, times(2)).getServiceId();
        verify(firstMockTrip, times(1)).getBlockId();
        verify(secondMockTrip, times(7)).getTripId();
        verify(secondMockTrip, times(2)).getServiceId();
        verify(thirdMockTrip, times(7)).getTripId();
        verify(thirdMockTrip, times(2)).getServiceId();

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(3)).getStopTimeByTripId("8");

        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getStopSequence();

        verify(secondTripFirstStopTime, times(2)).getArrivalTime();
        verify(secondTripFirstStopTime, times(1)).getStopSequence();

        verify(thirdTripFirstStopTime, times(3)).getArrivalTime();

        verify(firstTripLastStopTime, times(1)).getDepartureTime();
        verify(firstTripLastStopTime, times(1)).getStopSequence();

        verify(secondTripLastStopTime, times(2)).getDepartureTime();
        verify(secondTripLastStopTime, times(2)).getDepartureTime();
        verify(secondTripLastStopTime, times(1)).getStopSequence();

        verify(thirdTripLastStopTime, times(3)).getDepartureTime();
        verify(thirdTripLastStopTime, times(3)).getDepartureTime();

        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(700);
        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(1000);
        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(903);
        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(1120);

        final ArgumentCaptor<OverlappingTripsInBlockNotice> captor =
                ArgumentCaptor.forClass(OverlappingTripsInBlockNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<OverlappingTripsInBlockNotice> noticeList = captor.getAllValues();

        // todo: check notice properties, but first constructor of notice has to be reworked to record all these pieces
        //  of information
//        assertEquals("trips.txt", noticeList.get(0).getFilename());
//        assertEquals("start date", noticeList.get(0).getNoticeSpecific(Notice.KEY_FEED_INFO_START_DATE));
//        assertEquals("end date", noticeList.get(0).getNoticeSpecific(Notice.KEY_FEED_INFO_END_DATE));
//        assertEquals("feed_publisher_name", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FIRST_PART));
//        assertEquals("feed_publisher_url", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_SECOND_PART));
//        assertEquals("feed_lang", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_THIRD_PART));
//        assertEquals("feed publisher name", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FIRST_VALUE));
//        assertEquals("feed publisher url", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_SECOND_VALUE));
//        assertEquals("feed lang", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_THIRD_VALUE));

        verifyNoMoreInteractions(mockLogger, mockDataRepo, mockTimeUtils, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripLastStopTime, secondTripFirstStopTime, secondTripLastStopTime,
                thirdTripFirstStopTime, thirdTripLastStopTime, mockResultRepo);
    }

    @Test
    void overlappingTripsWithDifferentServiceIdShouldGenerateNoticeWhenCalendarAreProvided() {
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtils = mock(TimeUtils.class);

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");

        // todo
    }

    @Test
    void overlappingTripsWithDifferentServiceIdShouldGenerateNoticeWhenCalendarAreNotProvided() {
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtils = mock(TimeUtils.class);

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");

        // todo
    }

    @Test
    void tripWithNullFirstTimeShouldNotGenerateNotice() {
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtils = mock(TimeUtils.class);

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");

        // todo
    }

    @Test
    void tripWithNullLastTimeShouldNotGenerateNotice() {
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtils = mock(TimeUtils.class);

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");

        // todo
    }
}
