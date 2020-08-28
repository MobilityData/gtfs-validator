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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.ExceptionType;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.BlockTripsWithOverlappingStopTimesNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateNoOverlappingStopTimeInTripBlockTest {
    // suppressed warning regarding ignored result of method, since methods are called in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
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
        verify(mockDataRepo, times(1)).getCalendarAll();

        verify(firstMockTrip, times(5)).getTripId();
        verify(firstMockTrip, times(2)).getServiceId();
        verify(secondMockTrip, times(5)).getTripId();
        verify(secondMockTrip, times(2)).getServiceId();
        verify(thirdMockTrip, times(5)).getTripId();
        verify(thirdMockTrip, times(2)).getServiceId();

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(3)).getStopTimeByTripId("8");

        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getStopSequence();
        verify(firstTripLastStopTime, times(1)).getStopSequence();

        verify(secondTripFirstStopTime, times(2)).getArrivalTime();
        verify(secondTripFirstStopTime, times(1)).getStopSequence();

        verify(thirdTripFirstStopTime, times(3)).getArrivalTime();
        verify(thirdTripFirstStopTime, times(1)).getStopSequence();

        verify(firstTripLastStopTime, times(1)).getDepartureTime();

        verify(secondTripLastStopTime, times(2)).getDepartureTime();
        verify(secondTripLastStopTime, times(1)).getStopSequence();
        verify(secondTripLastStopTime, times(2)).getDepartureTime();


        verify(thirdTripLastStopTime, times(3)).getDepartureTime();
        verify(thirdTripLastStopTime, times(1)).getStopSequence();

        verify(mockTimeUtils, times(3))
                .arePeriodsOverlapping(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt());

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockLogger, mockDataRepo, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripLastStopTime, secondTripFirstStopTime, secondTripLastStopTime,
                thirdTripFirstStopTime, thirdTripLastStopTime, mockTimeUtils);
    }

    // suppressed warning regarding ignored result of method, since methods are called in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nonOverlappingTripsWithDifferentServiceIdShouldNotGenerateNoticeWhenCalendarAreProvided() {
        // trips.txt
        // | routeId | tripId | serviceId | blockId |
        // |---------|--------|-----------|---------|
        // | 0       | 2      | a         | 7       |
        // | 0       | 5      | b         | 7       |
        // | 0       | 8      | c         | 7       |

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
        // | 0       | 5      | b         | 7       | 11:03           | 11:20          |
        // | 0       | 8      | c         | 7       | 11:40           | 13:50          |

        // calendar.txt
        // | serviceId | monday | tuesday | wednesday | thursday | friday | saturday | sunday | start_date | end_date |
        // |-----------|--------|---------|-----------|----------|--------|----------|--------|------------|----------|
        // | a         | 1      | 1       | 0         | 0        | 0      | 0        | 0      | 20200801   | 20200831 |
        // | b         | 1      | 0       | 1         | 0        | 0      | 0        | 0      | 20200901   | 20209030 |
        // | c         | 0      | 0       | 0         | 1        | 1      | 1        | 0      | 20200801   | 20200831 |

        // Here no service overlap: services a and c share the same service period: from august 1st 2020 to august 31st
        // 2020, but these services are not active on the same days.
        // Services a and b do not overlap: a ends before b starts
        // Services b and c also do not overlap: c ends before b starts.

        // calendar_dates.txt: file not provided

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, List<Trip>> mockTripPerBlockIdCollection = new HashMap<>();
        final List<Trip> mockTripCollection = new ArrayList<>();

        final Trip firstMockTrip = mock(Trip.class);
        when(firstMockTrip.getTripId()).thenReturn("2");
        when(firstMockTrip.getServiceId()).thenReturn("a");
        when(firstMockTrip.getBlockId()).thenReturn("7");

        final Trip secondMockTrip = mock(Trip.class);
        when(secondMockTrip.getTripId()).thenReturn("5");
        when(secondMockTrip.getServiceId()).thenReturn("b");
        when(secondMockTrip.getBlockId()).thenReturn("7");

        final Trip thirdMockTrip = mock(Trip.class);
        when(thirdMockTrip.getTripId()).thenReturn("8");
        when(thirdMockTrip.getServiceId()).thenReturn("c");
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

        final Map<String, Calendar> mockCalendarCollection = new HashMap<>();
        final Calendar calendarForServiceA = mock(Calendar.class);
        final Calendar calendarForServiceB = mock(Calendar.class);
        final Calendar calendarForServiceC = mock(Calendar.class);
        mockCalendarCollection.put("a", calendarForServiceA);
        mockCalendarCollection.put("b", calendarForServiceB);
        mockCalendarCollection.put("c", calendarForServiceC);
        when(calendarForServiceA.areCalendarOverlapping(calendarForServiceB)).thenReturn(false);
        when(calendarForServiceA.areCalendarOverlapping(calendarForServiceC)).thenReturn(false);
        when(calendarForServiceB.areCalendarOverlapping(calendarForServiceC)).thenReturn(false);
        final LocalDate serviceAStartDate = LocalDate.of(2020, 8, 1);
        final LocalDate serviceAEndDate = LocalDate.of(2020, 8, 31);
        final LocalDate serviceBStartDate = LocalDate.of(2020, 9, 1);
        final LocalDate serviceBEndDate = LocalDate.of(2020, 9, 30);
        final LocalDate serviceCStartDate = LocalDate.of(2020, 8, 1);
        final LocalDate serviceCEndDate = LocalDate.of(2020, 8, 31);

        when(calendarForServiceA.getStartDate()).thenReturn(serviceAStartDate);
        when(calendarForServiceA.getEndDate()).thenReturn(serviceAEndDate);
        when(calendarForServiceB.getStartDate()).thenReturn(serviceBStartDate);
        when(calendarForServiceB.getEndDate()).thenReturn(serviceBEndDate);
        when(calendarForServiceC.getStartDate()).thenReturn(serviceCStartDate);
        when(calendarForServiceC.getEndDate()).thenReturn(serviceCEndDate);
        when(calendarForServiceA.getCalendarsCommonOperationDayCollection(calendarForServiceC))
                .thenReturn(new ArrayList<>());

        when(mockDataRepo.getCalendarAll()).thenReturn(mockCalendarCollection);
        when(mockDataRepo.getCalendarByServiceId("a")).thenReturn(calendarForServiceA);
        when(mockDataRepo.getCalendarByServiceId("b")).thenReturn(calendarForServiceB);
        when(mockDataRepo.getCalendarByServiceId("c")).thenReturn(calendarForServiceC);

        when(mockDataRepo.getCalendarDateAll()).thenReturn(new HashMap<>());

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtils = mock(TimeUtils.class);

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();

        verify(firstMockTrip, times(5)).getTripId();
        verify(firstMockTrip, times(4)).getServiceId();
        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");

        verify(secondMockTrip, times(5)).getTripId();
        verify(secondMockTrip, times(4)).getServiceId();
        verify(mockDataRepo, times(2)).getStopTimeByTripId("5");

        verify(thirdMockTrip, times(5)).getTripId();
        verify(thirdMockTrip, times(4)).getServiceId();
        verify(mockDataRepo, times(3)).getStopTimeByTripId("8");

        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getStopSequence();

        verify(firstTripLastStopTime, times(1)).getDepartureTime();
        verify(firstTripLastStopTime, times(1)).getStopSequence();

        verify(secondTripFirstStopTime, times(2)).getArrivalTime();
        verify(secondTripFirstStopTime, times(1)).getStopSequence();
        verify(secondTripLastStopTime, times(2)).getDepartureTime();
        verify(secondTripLastStopTime, times(1)).getStopSequence();

        verify(thirdTripFirstStopTime, times(3)).getArrivalTime();
        verify(thirdTripFirstStopTime, times(1)).getStopSequence();
        verify(thirdTripLastStopTime, times(3)).getDepartureTime();
        verify(thirdTripLastStopTime, times(1)).getStopSequence();

        verify(mockDataRepo, times(1)).getCalendarAll();

        verify(mockDataRepo, times(2)).getCalendarByServiceId("a");
        verify(mockDataRepo, times(2)).getCalendarByServiceId("b");
        verify(mockDataRepo, times(2)).getCalendarByServiceId("c");

        verify(calendarForServiceA, times(1))
                .areCalendarOverlapping(ArgumentMatchers.eq(calendarForServiceB));
        verify(calendarForServiceA, times(1))
                .areCalendarOverlapping(ArgumentMatchers.eq(calendarForServiceC));
        verify(calendarForServiceB, times(1))
                .areCalendarOverlapping(ArgumentMatchers.eq(calendarForServiceC));

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockLogger, mockDataRepo, mockTimeUtils, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripLastStopTime, secondTripFirstStopTime, secondTripLastStopTime,
                thirdTripFirstStopTime, thirdTripLastStopTime, calendarForServiceA, calendarForServiceB,
                calendarForServiceC);
    }

    // suppressed warning regarding ignored result of method, since methods are called in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nonOverlappingTripsWithDifferentServiceIdShouldNotGenerateNoticeWhenCalendarAreNotProvided() {
        // trips.txt
        // | routeId | tripId | serviceId | blockId |
        // |---------|--------|-----------|---------|
        // | 0       | 2      | a         | 7       |
        // | 0       | 5      | b         | 7       |
        // | 0       | 8      | c         | 7       |

        // stop_times.txt
        // | tripId | arrivalTime | departureTime | stopId | stopSequence  |
        // |--------|-------------|---------------|--------|---------------|
        // | 2      | 07:00       | 07:40         | 101    | 10            |
        // | 2      | 08:20       | 10:00         | 102    | 11            |
        // | 5      | 11:03       | 11:15         | 103    | 12            |
        // | 5      | 11:16       | 11:20         | 104    | 13            |
        // | 8      | 07:00       | 07:40         | 105    | 14            |
        // | 8      | 08:20       | 10:00         | 106    | 15            |

        // trips.txt + stop_times.txt
        // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
        // |---------|--------|-----------|---------|-----------------|----------------|
        // | 0       | 2      | a         | 7       | 07:00           | 10:00          |
        // | 0       | 5      | b         | 7       | 11:03           | 11:20          |
        // | 0       | 8      | c         | 7       | 11:40           | 13:50          |

        // calendar.txt: file not provided

        // calendar_dates.txt:
        // | serviceId | date     | exception_type |
        // |-----------|----------|----------------|
        // | a         | 20200723 | 1              |
        // | a         | 20200801 | 1              |
        // | b         | 20200904 | 0              |
        // | c         | 20201008 | 1              |

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, List<Trip>> mockTripPerBlockIdCollection = new HashMap<>();
        final List<Trip> mockTripCollection = new ArrayList<>();

        final Trip firstMockTrip = mock(Trip.class);
        when(firstMockTrip.getTripId()).thenReturn("2");
        when(firstMockTrip.getServiceId()).thenReturn("a");
        when(firstMockTrip.getBlockId()).thenReturn("7");

        final Trip secondMockTrip = mock(Trip.class);
        when(secondMockTrip.getTripId()).thenReturn("5");
        when(secondMockTrip.getServiceId()).thenReturn("b");
        when(secondMockTrip.getBlockId()).thenReturn("7");

        final Trip thirdMockTrip = mock(Trip.class);
        when(thirdMockTrip.getTripId()).thenReturn("8");
        when(thirdMockTrip.getServiceId()).thenReturn("c");
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
        when(thirdTripFirstStopTime.getArrivalTime()).thenReturn(700);
        when(thirdTripFirstStopTime.getDepartureTime()).thenReturn(740);

        final StopTime thirdTripLastStopTime = mock(StopTime.class);
        when(thirdTripLastStopTime.getArrivalTime()).thenReturn(820);
        when(thirdTripLastStopTime.getDepartureTime()).thenReturn(1000);

        mockThirdTripStopTimeCollection.put(14, thirdTripFirstStopTime);
        mockThirdTripStopTimeCollection.put(15, thirdTripLastStopTime);

        when(mockDataRepo.getAllTripByBlockId()).thenReturn(mockTripPerBlockIdCollection);
        when(mockDataRepo.getStopTimeByTripId("2")).thenReturn(mockFirstTripStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("5")).thenReturn(mockSecondTripStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("8")).thenReturn(mockThirdTripStopTimeCollection);
        when(mockDataRepo.getCalendarAll()).thenReturn(new HashMap<>());

        final Map<String, Map<String, CalendarDate>> mockCalendarDateCollection = new HashMap<>();
        final Map<String, CalendarDate> calendarDateCollectionServiceA = new HashMap<>();
        final Map<String, CalendarDate> calendarDateCollectionServiceB = new HashMap<>();
        final Map<String, CalendarDate> calendarDateCollectionServiceC = new HashMap<>();
        final CalendarDate firstCalendarDateForServiceA = mock(CalendarDate.class);
        final CalendarDate secondCalendarDateForServiceA = mock(CalendarDate.class);
        final CalendarDate calendarDateForServiceB = mock(CalendarDate.class);
        final CalendarDate calendarDateForServiceC = mock(CalendarDate.class);
        calendarDateCollectionServiceA.put("20200723", firstCalendarDateForServiceA);
        calendarDateCollectionServiceA.put("20200801", secondCalendarDateForServiceA);
        calendarDateCollectionServiceB.put("20200904", calendarDateForServiceB);
        calendarDateCollectionServiceC.put("20201008", calendarDateForServiceC);
        when(firstCalendarDateForServiceA.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);
        when(secondCalendarDateForServiceA.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);
        when(calendarDateForServiceB.getExceptionType()).thenReturn(ExceptionType.REMOVED_SERVICE);
        when(calendarDateForServiceC.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);
        mockCalendarDateCollection.put("a", calendarDateCollectionServiceA);
        mockCalendarDateCollection.put("b", calendarDateCollectionServiceB);
        mockCalendarDateCollection.put("c", calendarDateCollectionServiceC);
        when(mockDataRepo.getCalendarDateAll()).thenReturn(mockCalendarDateCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtils = mock(TimeUtils.class);

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();
        verify(mockDataRepo, times(1)).getCalendarAll();

        verify(firstMockTrip, times(5)).getTripId();
        verify(firstMockTrip, times(4)).getServiceId();
        verify(secondMockTrip, times(5)).getTripId();
        verify(secondMockTrip, times(4)).getServiceId();
        verify(thirdMockTrip, times(5)).getTripId();
        verify(thirdMockTrip, times(4)).getServiceId();

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(3)).getStopTimeByTripId("8");
        verify(mockDataRepo, times(6)).getCalendarDateAll();

        verify(firstCalendarDateForServiceA, times(2)).getExceptionType();
        verify(secondCalendarDateForServiceA, times(2)).getExceptionType();
        verify(calendarDateForServiceB, times(2)).getExceptionType();
        verify(calendarDateForServiceC, times(2)).getExceptionType();

        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getStopSequence();
        verify(firstTripLastStopTime, times(1)).getStopSequence();

        verify(secondTripFirstStopTime, times(2)).getArrivalTime();
        verify(secondTripFirstStopTime, times(1)).getStopSequence();

        verify(thirdTripFirstStopTime, times(3)).getArrivalTime();
        verify(thirdTripFirstStopTime, times(1)).getStopSequence();

        verify(firstTripLastStopTime, times(1)).getDepartureTime();

        verify(secondTripLastStopTime, times(2)).getDepartureTime();
        verify(secondTripLastStopTime, times(1)).getStopSequence();

        verify(thirdTripLastStopTime, times(3)).getDepartureTime();
        verify(thirdTripLastStopTime, times(1)).getStopSequence();

        verifyNoInteractions(mockResultRepo, mockTimeUtils);
        verifyNoMoreInteractions(mockLogger, mockDataRepo, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripLastStopTime, secondTripFirstStopTime, secondTripLastStopTime,
                thirdTripFirstStopTime, thirdTripLastStopTime, firstCalendarDateForServiceA,
                secondCalendarDateForServiceA, calendarDateForServiceB, calendarDateForServiceC);
    }

    // suppressed warning regarding ignored result of method, since methods are called in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
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
        when(firstTripFirstStopTime.getStopSequence()).thenReturn(10);

        final StopTime firstTripLastStopTime = mock(StopTime.class);
        when(firstTripLastStopTime.getArrivalTime()).thenReturn(820);
        when(firstTripLastStopTime.getDepartureTime()).thenReturn(1000);
        when(firstTripLastStopTime.getStopSequence()).thenReturn(11);

        mockFirstTripStopTimeCollection.put(10, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(11, firstTripLastStopTime);

        final SortedMap<Integer, StopTime> mockSecondTripStopTimeCollection = new TreeMap<>();

        final StopTime secondTripFirstStopTime = mock(StopTime.class);
        when(secondTripFirstStopTime.getArrivalTime()).thenReturn(903);
        when(secondTripFirstStopTime.getDepartureTime()).thenReturn(1115);
        when(secondTripFirstStopTime.getStopSequence()).thenReturn(12);

        final StopTime secondTripLastStopTime = mock(StopTime.class);
        when(secondTripLastStopTime.getArrivalTime()).thenReturn(1116);
        when(secondTripLastStopTime.getDepartureTime()).thenReturn(1120);
        when(secondTripLastStopTime.getStopSequence()).thenReturn(13);

        mockSecondTripStopTimeCollection.put(12, secondTripFirstStopTime);
        mockSecondTripStopTimeCollection.put(13, secondTripLastStopTime);

        final SortedMap<Integer, StopTime> mockThirdTripStopTimeCollection = new TreeMap<>();
        final StopTime thirdTripFirstStopTime = mock(StopTime.class);
        when(thirdTripFirstStopTime.getArrivalTime()).thenReturn(1140);
        when(thirdTripFirstStopTime.getDepartureTime()).thenReturn(1216);
        when(thirdTripFirstStopTime.getStopSequence()).thenReturn(14);

        final StopTime thirdTripLastStopTime = mock(StopTime.class);
        when(thirdTripLastStopTime.getArrivalTime()).thenReturn(1308);
        when(thirdTripLastStopTime.getDepartureTime()).thenReturn(1350);
        when(thirdTripLastStopTime.getStopSequence()).thenReturn(15);

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
        when(mockTimeUtils.convertIntegerToHMMSS(903)).thenReturn("903");
        when(mockTimeUtils.convertIntegerToHMMSS(1120)).thenReturn("1120");
        when(mockTimeUtils.convertIntegerToHMMSS(1140)).thenReturn("1140");
        when(mockTimeUtils.convertIntegerToHMMSS(1350)).thenReturn("1350");
        when(mockTimeUtils.arePeriodsOverlapping(700, 1000,
                903, 1120)).thenReturn(true);

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();

        verify(firstMockTrip, times(5)).getTripId();
        verify(firstMockTrip, times(2)).getServiceId();
        verify(secondMockTrip, times(5)).getTripId();
        verify(secondMockTrip, times(2)).getServiceId();
        verify(thirdMockTrip, times(5)).getTripId();
        verify(thirdMockTrip, times(2)).getServiceId();

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(3)).getStopTimeByTripId("8");
        verify(mockDataRepo, times(1)).getCalendarAll();

        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getStopSequence();

        verify(secondTripFirstStopTime, times(2)).getArrivalTime();
        verify(secondTripFirstStopTime, times(2)).getStopSequence();

        verify(thirdTripFirstStopTime, times(3)).getArrivalTime();
        verify(thirdTripFirstStopTime, times(1)).getStopSequence();

        verify(firstTripLastStopTime, times(1)).getDepartureTime();
        verify(firstTripLastStopTime, times(1)).getStopSequence();

        verify(secondTripLastStopTime, times(2)).getDepartureTime();
        verify(secondTripLastStopTime, times(2)).getDepartureTime();
        verify(secondTripLastStopTime, times(2)).getStopSequence();

        verify(thirdTripLastStopTime, times(3)).getDepartureTime();
        verify(thirdTripLastStopTime, times(1)).getStopSequence();

        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(700);
        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(1000);
        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(903);
        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(1120);
        verify(mockTimeUtils, times(3))
                .arePeriodsOverlapping(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());

        final ArgumentCaptor<BlockTripsWithOverlappingStopTimesNotice> captor =
                ArgumentCaptor.forClass(BlockTripsWithOverlappingStopTimesNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<BlockTripsWithOverlappingStopTimesNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("2", noticeList.get(0).getEntityId());
        assertEquals("7", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_BLOCK_ID));
        assertEquals("5", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_CONFLICTING_TRIP_ID));
        assertEquals("700", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_FIRST_TIME));
        assertEquals("1000", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_LAST_TIME));
        assertEquals("903", noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_TRIP_FIRST_TIME));
        assertEquals("1120", noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_TRIP_LAST_TIME));
        assertEquals(10, noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_FIRST_STOP_SEQUENCE));
        assertEquals(11, noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_LAST_STOP_SEQUENCE));
        assertEquals(12, noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_TRIP_FIRST_STOP_SEQUENCE));
        assertEquals(13, noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_TRIP_LAST_STOP_SEQUENCE));

        verifyNoMoreInteractions(mockLogger, mockDataRepo, mockTimeUtils, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripLastStopTime, secondTripFirstStopTime, secondTripLastStopTime,
                thirdTripFirstStopTime, thirdTripLastStopTime, mockResultRepo);
    }

    // suppressed warning regarding ignored result of method, since methods are called in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void overlappingTripsWithDifferentServiceIdShouldGenerateNoticeWhenCalendarAreProvided() {
        // trips.txt
        // | routeId | tripId | serviceId | blockId |
        // |---------|--------|-----------|---------|
        // | 0       | 2      | a         | 7       |
        // | 0       | 5      | b         | 7       |
        // | 0       | 8      | c         | 7       |

        // stop_times.txt
        // | tripId | arrivalTime | departureTime | stopId | stopSequence  |
        // |--------|-------------|---------------|--------|---------------|
        // | 2      | 07:00       | 07:40         | 101    | 10            |
        // | 2      | 08:20       | 10:00         | 102    | 11            |
        // | 5      | 17:23       | 18:08         | 103    | 12            |
        // | 5      | 18:20       | 18:57         | 104    | 13            |
        // | 8      | 09:40       | 12:16         | 105    | 14            |
        // | 8      | 13:08       | 13:50         | 106    | 15            |

        // trips.txt + stop_times.txt
        // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
        // |---------|--------|-----------|---------|-----------------|----------------|
        // | 0       | 2      | a         | 7       | 07:00           | 10:00          |
        // | 0       | 5      | b         | 7       | 17:23           | 18:57          |
        // | 0       | 8      | c         | 7       | 09:40           | 13:50          |

        // calendar.txt
        // | serviceId | monday | tuesday | wednesday | thursday | friday | saturday | sunday | start_date | end_date |
        // |-----------|--------|---------|-----------|----------|--------|----------|--------|------------|----------|
        // | a         | 1      | 1       | 0         | 0        | 0      | 0        | 0      | 20200801   | 20200831 |
        // | b         | 1      | 0       | 1         | 0        | 0      | 0        | 0      | 20200901   | 20209030 |
        // | c         | 0      | 1       | 0         | 1        | 1      | 1        | 0      | 20200801   | 20200831 |
        //
        // Here, services a and c overlap: they share the same date range and are both active on tuesday. Specifically
        // on tuesday, service a starts at 07:00 and ends at 10:00 - and service b starts at 09:40 and ends at 13:30.
        // Which means that trips 2 and 8 overlap on tuesdays between 09:40 and 10:00: that is an error and should
        // generate a notice.

        // calendar_dates.txt: file not provided
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtils = mock(TimeUtils.class);
        when(mockTimeUtils.convertIntegerToHMMSS(700)).thenReturn("700");
        when(mockTimeUtils.convertIntegerToHMMSS(1000)).thenReturn("1000");
        when(mockTimeUtils.convertIntegerToHMMSS(940)).thenReturn("940");
        when(mockTimeUtils.convertIntegerToHMMSS(1350)).thenReturn("1350");
        when(mockTimeUtils.arePeriodsOverlapping(700, 1000,
                940, 1350)).thenReturn(true);

        final Trip firstMockTrip = mock(Trip.class);
        when(firstMockTrip.getTripId()).thenReturn("2");
        when(firstMockTrip.getServiceId()).thenReturn("a");
        when(firstMockTrip.getBlockId()).thenReturn("7");

        final Trip secondMockTrip = mock(Trip.class);
        when(secondMockTrip.getTripId()).thenReturn("5");
        when(secondMockTrip.getServiceId()).thenReturn("b");
        when(secondMockTrip.getBlockId()).thenReturn("7");

        final Trip thirdMockTrip = mock(Trip.class);
        when(thirdMockTrip.getTripId()).thenReturn("8");
        when(thirdMockTrip.getServiceId()).thenReturn("c");
        when(thirdMockTrip.getBlockId()).thenReturn("7");

        final List<Trip> mockTripCollection = new ArrayList<>();
        final Map<String, List<Trip>> mockTripPerBlockIdCollection = new HashMap<>();

        mockTripCollection.add(firstMockTrip);
        mockTripCollection.add(secondMockTrip);
        mockTripCollection.add(thirdMockTrip);
        mockTripPerBlockIdCollection.put("7", mockTripCollection);

        final SortedMap<Integer, StopTime> mockFirstTripStopTimeCollection = new TreeMap<>();

        final StopTime firstTripFirstStopTime = mock(StopTime.class);
        when(firstTripFirstStopTime.getArrivalTime()).thenReturn(700);
        when(firstTripFirstStopTime.getDepartureTime()).thenReturn(740);
        when(firstTripFirstStopTime.getStopSequence()).thenReturn(10);

        final StopTime firstTripLastStopTime = mock(StopTime.class);
        when(firstTripLastStopTime.getArrivalTime()).thenReturn(820);
        when(firstTripLastStopTime.getDepartureTime()).thenReturn(1000);
        when(firstTripLastStopTime.getStopSequence()).thenReturn(11);

        mockFirstTripStopTimeCollection.put(10, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(11, firstTripLastStopTime);

        final SortedMap<Integer, StopTime> mockSecondTripStopTimeCollection = new TreeMap<>();

        final StopTime secondTripFirstStopTime = mock(StopTime.class);
        when(secondTripFirstStopTime.getArrivalTime()).thenReturn(1723);
        when(secondTripFirstStopTime.getDepartureTime()).thenReturn(1808);

        final StopTime secondTripLastStopTime = mock(StopTime.class);
        when(secondTripLastStopTime.getArrivalTime()).thenReturn(1820);
        when(secondTripLastStopTime.getDepartureTime()).thenReturn(1857);

        mockSecondTripStopTimeCollection.put(12, secondTripFirstStopTime);
        mockSecondTripStopTimeCollection.put(13, secondTripLastStopTime);

        final SortedMap<Integer, StopTime> mockThirdTripStopTimeCollection = new TreeMap<>();
        final StopTime thirdTripFirstStopTime = mock(StopTime.class);
        when(thirdTripFirstStopTime.getArrivalTime()).thenReturn(940);
        when(thirdTripFirstStopTime.getDepartureTime()).thenReturn(1216);
        when(thirdTripFirstStopTime.getStopSequence()).thenReturn(14);

        final StopTime thirdTripLastStopTime = mock(StopTime.class);
        when(thirdTripLastStopTime.getArrivalTime()).thenReturn(1308);
        when(thirdTripLastStopTime.getDepartureTime()).thenReturn(1350);
        when(thirdTripLastStopTime.getStopSequence()).thenReturn(15);

        mockThirdTripStopTimeCollection.put(14, thirdTripFirstStopTime);
        mockThirdTripStopTimeCollection.put(15, thirdTripLastStopTime);

        when(mockDataRepo.getAllTripByBlockId()).thenReturn(mockTripPerBlockIdCollection);
        when(mockDataRepo.getStopTimeByTripId("2")).thenReturn(mockFirstTripStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("5")).thenReturn(mockSecondTripStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("8")).thenReturn(mockThirdTripStopTimeCollection);

        final Map<String, Calendar> mockCalendarCollection = new HashMap<>();
        final Calendar calendarForServiceA = mock(Calendar.class);
        final Calendar calendarForServiceB = mock(Calendar.class);
        final Calendar calendarForServiceC = mock(Calendar.class);
        mockCalendarCollection.put("a", calendarForServiceA);
        mockCalendarCollection.put("b", calendarForServiceB);
        mockCalendarCollection.put("c", calendarForServiceC);
        when(calendarForServiceA.areCalendarOverlapping(calendarForServiceB)).thenReturn(false);
        when(calendarForServiceA.areCalendarOverlapping(calendarForServiceC)).thenReturn(true);
        when(calendarForServiceB.areCalendarOverlapping(calendarForServiceC)).thenReturn(false);
        final LocalDate serviceAStartDate = LocalDate.of(2020, 8, 1);
        final LocalDate serviceAEndDate = LocalDate.of(2020, 8, 31);
        final LocalDate serviceBStartDate = LocalDate.of(2020, 9, 1);
        final LocalDate serviceBEndDate = LocalDate.of(2020, 9, 30);
        final LocalDate serviceCStartDate = LocalDate.of(2020, 8, 1);
        final LocalDate serviceCEndDate = LocalDate.of(2020, 8, 31);

        when(calendarForServiceA.getStartDate()).thenReturn(serviceAStartDate);
        when(calendarForServiceA.getEndDate()).thenReturn(serviceAEndDate);
        when(calendarForServiceB.getStartDate()).thenReturn(serviceBStartDate);
        when(calendarForServiceB.getEndDate()).thenReturn(serviceBEndDate);
        when(calendarForServiceC.getStartDate()).thenReturn(serviceCStartDate);
        when(calendarForServiceC.getEndDate()).thenReturn(serviceCEndDate);
        when(calendarForServiceA.getCalendarsCommonOperationDayCollection(calendarForServiceC))
                .thenReturn(List.of("tuesday"));

        when(mockDataRepo.getCalendarAll()).thenReturn(mockCalendarCollection);
        when(mockDataRepo.getCalendarByServiceId("a")).thenReturn(calendarForServiceA);
        when(mockDataRepo.getCalendarByServiceId("b")).thenReturn(calendarForServiceB);
        when(mockDataRepo.getCalendarByServiceId("c")).thenReturn(calendarForServiceC);

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getCalendarAll();
        verify(mockDataRepo, times(1)).getAllTripByBlockId();
        verify(mockDataRepo, times(2)).getCalendarByServiceId("a");
        verify(mockDataRepo, times(2)).getCalendarByServiceId("b");
        verify(mockDataRepo, times(2)).getCalendarByServiceId("c");

        verify(firstMockTrip, times(5)).getTripId();
        verify(firstMockTrip, times(4)).getServiceId();
        verify(secondMockTrip, times(5)).getTripId();
        verify(secondMockTrip, times(4)).getServiceId();
        verify(thirdMockTrip, times(5)).getTripId();
        verify(thirdMockTrip, times(4)).getServiceId();

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(3)).getStopTimeByTripId("8");

        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getStopSequence();
        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripLastStopTime, times(1)).getStopSequence();

        verify(secondTripFirstStopTime, times(2)).getArrivalTime();
        verify(secondTripFirstStopTime, times(1)).getStopSequence();
        verify(secondTripLastStopTime, times(1)).getStopSequence();

        verify(thirdTripFirstStopTime, times(3)).getArrivalTime();
        verify(thirdTripFirstStopTime, times(2)).getStopSequence();
        verify(thirdTripLastStopTime, times(2)).getStopSequence();

        verify(firstTripLastStopTime, times(1)).getDepartureTime();

        verify(secondTripLastStopTime, times(2)).getDepartureTime();

        verify(calendarForServiceA, times(1)).areCalendarOverlapping(calendarForServiceB);
        verify(calendarForServiceA, times(1)).areCalendarOverlapping(calendarForServiceC);
        verify(calendarForServiceB, times(1)).areCalendarOverlapping(calendarForServiceC);
        verify(calendarForServiceA, times(2))
                .getCalendarsCommonOperationDayCollection(calendarForServiceC);

        verify(thirdTripLastStopTime, times(3)).getDepartureTime();
        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(700);
        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(1000);
        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(940);
        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(1350);
        verify(mockTimeUtils, times(1))
                .arePeriodsOverlapping(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());

        final ArgumentCaptor<BlockTripsWithOverlappingStopTimesNotice> captor =
                ArgumentCaptor.forClass(BlockTripsWithOverlappingStopTimesNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<BlockTripsWithOverlappingStopTimesNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("2", noticeList.get(0).getEntityId());
        assertEquals("7", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_BLOCK_ID));
        assertEquals("8", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_CONFLICTING_TRIP_ID));
        assertEquals("700", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_FIRST_TIME));
        assertEquals("1000", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_LAST_TIME));
        assertEquals("940", noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_TRIP_FIRST_TIME));
        assertEquals("1350", noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_TRIP_LAST_TIME));
        assertEquals(10, noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_FIRST_STOP_SEQUENCE));
        assertEquals(11, noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_LAST_STOP_SEQUENCE));
        assertEquals(14, noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_TRIP_FIRST_STOP_SEQUENCE));
        assertEquals(15, noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_TRIP_LAST_STOP_SEQUENCE));
        assertEquals(List.of("tuesday"), noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_DAY_LIST));

        verifyNoMoreInteractions(mockLogger, mockDataRepo, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripLastStopTime, secondTripFirstStopTime, secondTripLastStopTime,
                thirdTripFirstStopTime, thirdTripLastStopTime, mockResultRepo, calendarForServiceA, calendarForServiceB,
                calendarForServiceC, mockTimeUtils);
    }

    // suppressed warning regarding ignored result of method, since methods are called in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void overlappingTripsWithDifferentServiceIdShouldGenerateNoticeWhenCalendarAreNotProvided() {
        // trips.txt
        // | routeId | tripId | serviceId | blockId |
        // |---------|--------|-----------|---------|
        // | 0       | 2      | a         | 7       |
        // | 0       | 5      | b         | 7       |
        // | 0       | 8      | c         | 7       |

        // stop_times.txt
        // | tripId | arrivalTime | departureTime | stopId | stopSequence  |
        // |--------|-------------|---------------|--------|---------------|
        // | 2      | 07:00       | 07:40         | 101    | 10            |
        // | 2      | 08:20       | 10:00         | 102    | 11            |
        // | 5      | 11:03       | 11:15         | 103    | 12            |
        // | 5      | 11:16       | 11:20         | 104    | 13            |
        // | 8      | 09:40       | 10:20         | 105    | 14            |
        // | 8      | 10:30       | 13:50         | 106    | 15            |

        // trips.txt + stop_times.txt
        // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
        // |---------|--------|-----------|---------|-----------------|----------------|
        // | 0       | 2      | a         | 7       | 07:00           | 10:00          |
        // | 0       | 5      | b         | 7       | 11:03           | 11:20          |
        // | 0       | 8      | c         | 7       | 09:40           | 13:50          |

        // calendar.txt: file not provided

        // calendar_dates.txt:
        // | serviceId | date     | exception_type |
        // |-----------|----------|----------------|
        // | a         | 20200723 | 1              |
        // | a         | 20200801 | 1              |
        // | b         | 20200904 | 0              |
        // | c         | 20200723 | 1              |
        // Here, services a and c overlap: they are both active on 2020-07-23.
        // Service a starts at 07:00 and ends at 10:00 - and service c starts at 09:40 and ends at 13:50.
        // Which means that trips 2 and 8 overlap on 2020-07-23 between 09:40 and 10:00: that is an error and should
        // generate a notice.

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, List<Trip>> mockTripPerBlockIdCollection = new HashMap<>();
        final List<Trip> mockTripCollection = new ArrayList<>();

        final Trip firstMockTrip = mock(Trip.class);
        when(firstMockTrip.getTripId()).thenReturn("2");
        when(firstMockTrip.getServiceId()).thenReturn("a");
        when(firstMockTrip.getBlockId()).thenReturn("7");

        final Trip secondMockTrip = mock(Trip.class);
        when(secondMockTrip.getTripId()).thenReturn("5");
        when(secondMockTrip.getServiceId()).thenReturn("b");
        when(secondMockTrip.getBlockId()).thenReturn("7");

        final Trip thirdMockTrip = mock(Trip.class);
        when(thirdMockTrip.getTripId()).thenReturn("8");
        when(thirdMockTrip.getServiceId()).thenReturn("c");
        when(thirdMockTrip.getBlockId()).thenReturn("7");

        mockTripCollection.add(firstMockTrip);
        mockTripCollection.add(secondMockTrip);
        mockTripCollection.add(thirdMockTrip);
        mockTripPerBlockIdCollection.put("7", mockTripCollection);

        final SortedMap<Integer, StopTime> mockFirstTripStopTimeCollection = new TreeMap<>();

        final StopTime firstTripFirstStopTime = mock(StopTime.class);
        when(firstTripFirstStopTime.getArrivalTime()).thenReturn(700);
        when(firstTripFirstStopTime.getDepartureTime()).thenReturn(740);
        when(firstTripFirstStopTime.getStopSequence()).thenReturn(10);

        final StopTime firstTripLastStopTime = mock(StopTime.class);
        when(firstTripLastStopTime.getArrivalTime()).thenReturn(820);
        when(firstTripLastStopTime.getDepartureTime()).thenReturn(1000);
        when(firstTripLastStopTime.getStopSequence()).thenReturn(11);

        mockFirstTripStopTimeCollection.put(10, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(11, firstTripLastStopTime);

        final SortedMap<Integer, StopTime> mockSecondTripStopTimeCollection = new TreeMap<>();

        final StopTime secondTripFirstStopTime = mock(StopTime.class);
        when(secondTripFirstStopTime.getArrivalTime()).thenReturn(1103);
        when(secondTripFirstStopTime.getDepartureTime()).thenReturn(1115);
        when(secondTripFirstStopTime.getStopSequence()).thenReturn(12);

        final StopTime secondTripLastStopTime = mock(StopTime.class);
        when(secondTripLastStopTime.getArrivalTime()).thenReturn(1116);
        when(secondTripLastStopTime.getDepartureTime()).thenReturn(1120);
        when(secondTripLastStopTime.getStopSequence()).thenReturn(13);

        mockSecondTripStopTimeCollection.put(12, secondTripFirstStopTime);
        mockSecondTripStopTimeCollection.put(13, secondTripLastStopTime);

        final SortedMap<Integer, StopTime> mockThirdTripStopTimeCollection = new TreeMap<>();
        final StopTime thirdTripFirstStopTime = mock(StopTime.class);
        when(thirdTripFirstStopTime.getArrivalTime()).thenReturn(940);
        when(thirdTripFirstStopTime.getDepartureTime()).thenReturn(1020);
        when(thirdTripFirstStopTime.getStopSequence()).thenReturn(14);

        final StopTime thirdTripLastStopTime = mock(StopTime.class);
        when(thirdTripLastStopTime.getArrivalTime()).thenReturn(1030);
        when(thirdTripLastStopTime.getDepartureTime()).thenReturn(1350);
        when(thirdTripLastStopTime.getStopSequence()).thenReturn(15);

        mockThirdTripStopTimeCollection.put(14, thirdTripFirstStopTime);
        mockThirdTripStopTimeCollection.put(15, thirdTripLastStopTime);

        when(mockDataRepo.getAllTripByBlockId()).thenReturn(mockTripPerBlockIdCollection);
        when(mockDataRepo.getStopTimeByTripId("2")).thenReturn(mockFirstTripStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("5")).thenReturn(mockSecondTripStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("8")).thenReturn(mockThirdTripStopTimeCollection);
        when(mockDataRepo.getCalendarAll()).thenReturn(new HashMap<>());

        final Map<String, Map<String, CalendarDate>> mockCalendarDateCollection = new HashMap<>();
        final Map<String, CalendarDate> calendarDateCollectionServiceA = new HashMap<>();
        final Map<String, CalendarDate> calendarDateCollectionServiceB = new HashMap<>();
        final Map<String, CalendarDate> calendarDateCollectionServiceC = new HashMap<>();
        final CalendarDate firstCalendarDateForServiceA = mock(CalendarDate.class);
        final CalendarDate secondCalendarDateForServiceA = mock(CalendarDate.class);
        final CalendarDate calendarDateForServiceB = mock(CalendarDate.class);
        final CalendarDate calendarDateForServiceC = mock(CalendarDate.class);
        calendarDateCollectionServiceA.put("20200723", firstCalendarDateForServiceA);
        calendarDateCollectionServiceA.put("20200801", secondCalendarDateForServiceA);
        calendarDateCollectionServiceB.put("20200904", calendarDateForServiceB);
        calendarDateCollectionServiceC.put("20200723", calendarDateForServiceC);
        when(firstCalendarDateForServiceA.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);
        when(secondCalendarDateForServiceA.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);
        when(calendarDateForServiceB.getExceptionType()).thenReturn(ExceptionType.REMOVED_SERVICE);
        when(calendarDateForServiceC.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);
        mockCalendarDateCollection.put("a", calendarDateCollectionServiceA);
        mockCalendarDateCollection.put("b", calendarDateCollectionServiceB);
        mockCalendarDateCollection.put("c", calendarDateCollectionServiceC);
        when(mockDataRepo.getCalendarDateAll()).thenReturn(mockCalendarDateCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtils = mock(TimeUtils.class);
        when(mockTimeUtils.convertIntegerToHMMSS(700)).thenReturn("700");
        when(mockTimeUtils.convertIntegerToHMMSS(1000)).thenReturn("1000");
        when(mockTimeUtils.convertIntegerToHMMSS(940)).thenReturn("940");
        when(mockTimeUtils.convertIntegerToHMMSS(1350)).thenReturn("1350");
        when(mockTimeUtils.arePeriodsOverlapping(700, 1000,
                940, 1350)).thenReturn(true);

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();
        verify(mockDataRepo, times(1)).getCalendarAll();

        verify(firstMockTrip, times(5)).getTripId();
        verify(firstMockTrip, times(4)).getServiceId();
        verify(secondMockTrip, times(5)).getTripId();
        verify(secondMockTrip, times(4)).getServiceId();
        verify(thirdMockTrip, times(6)).getTripId();
        verify(thirdMockTrip, times(4)).getServiceId();

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(3)).getStopTimeByTripId("8");
        verify(mockDataRepo, times(6)).getCalendarDateAll();

        verify(firstCalendarDateForServiceA, times(2)).getExceptionType();
        verify(secondCalendarDateForServiceA, times(2)).getExceptionType();
        verify(calendarDateForServiceB, times(2)).getExceptionType();
        verify(calendarDateForServiceC, times(2)).getExceptionType();

        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getStopSequence();
        verify(firstTripLastStopTime, times(1)).getStopSequence();

        verify(secondTripFirstStopTime, times(2)).getArrivalTime();
        verify(secondTripFirstStopTime, times(1)).getStopSequence();
        verify(secondTripLastStopTime, times(1)).getStopSequence();

        verify(thirdTripFirstStopTime, times(3)).getArrivalTime();
        verify(thirdTripFirstStopTime, times(2)).getStopSequence();

        verify(firstTripLastStopTime, times(1)).getDepartureTime();

        verify(secondTripLastStopTime, times(2)).getDepartureTime();
        verify(secondTripLastStopTime, times(2)).getDepartureTime();

        verify(thirdTripLastStopTime, times(3)).getDepartureTime();
        verify(thirdTripFirstStopTime, times(2)).getStopSequence();
        verify(thirdTripLastStopTime, times(2)).getStopSequence();

        final ArgumentCaptor<BlockTripsWithOverlappingStopTimesNotice> captor =
                ArgumentCaptor.forClass(BlockTripsWithOverlappingStopTimesNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<BlockTripsWithOverlappingStopTimesNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("2", noticeList.get(0).getEntityId());
        assertEquals("7", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_BLOCK_ID));
        assertEquals("8", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_CONFLICTING_TRIP_ID));
        assertEquals("700", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_FIRST_TIME));
        assertEquals("1000", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_LAST_TIME));
        assertEquals("940", noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_TRIP_FIRST_TIME));
        assertEquals("1350", noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_TRIP_LAST_TIME));
        assertEquals(10, noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_FIRST_STOP_SEQUENCE));
        assertEquals(11, noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_LAST_STOP_SEQUENCE));
        assertEquals(14, noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_TRIP_FIRST_STOP_SEQUENCE));
        assertEquals(15, noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_TRIP_LAST_STOP_SEQUENCE));
        assertEquals(Set.of("20200723"), noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_DATE_LIST));

        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(700);
        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(1000);
        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(940);
        verify(mockTimeUtils, times(1)).convertIntegerToHMMSS(1350);
        verify(mockTimeUtils, times(1))
                .arePeriodsOverlapping(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());

        verifyNoMoreInteractions(mockLogger, mockDataRepo, mockResultRepo, firstMockTrip, secondMockTrip,
                thirdMockTrip, firstTripFirstStopTime, firstTripLastStopTime, secondTripFirstStopTime,
                secondTripLastStopTime, thirdTripFirstStopTime, thirdTripLastStopTime, firstCalendarDateForServiceA,
                secondCalendarDateForServiceA, calendarDateForServiceB, calendarDateForServiceC, mockTimeUtils);
    }

    // suppressed warning regarding ignored result of method, since methods are called in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void tripWithNullFirstTimeShouldNotGenerateNotice() {
        // trips.txt
        // | routeId | tripId | serviceId | blockId |
        // |---------|--------|-----------|---------|
        // | 0       | 2      | a         | 7       |
        // | 0       | 5      | a         | 7       |
        // | 0       | 8      | a         | 7       |

        // stop_times.txt
        // | tripId | arrivalTime | departureTime | stopId | stopSequence  |
        // |--------|-------------|---------------|--------|---------------|
        // | 2      | null        | 07:40         | 101    | 10            |
        // | 2      | 08:20       | 10:00         | 102    | 11            |
        // | 5      | 11:03       | 11:15         | 103    | 12            |
        // | 5      | 11:16       | 11:20         | 104    | 13            |
        // | 8      | 11:40       | 12:16         | 105    | 14            |
        // | 8      | 13:08       | 13:50         | 106    | 15            |

        // trips.txt + stop_times.txt
        // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
        // |---------|--------|-----------|---------|-----------------|----------------|
        // | 0       | 2      | a         | 7       | null            | 10:00          |
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
        when(firstTripFirstStopTime.getArrivalTime()).thenReturn(null);
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

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();
        verify(mockDataRepo, times(1)).getCalendarAll();

        verify(firstMockTrip, times(4)).getTripId();
        verify(secondMockTrip, times(4)).getTripId();
        verify(secondMockTrip, times(1)).getServiceId();
        verify(thirdMockTrip, times(4)).getTripId();
        verify(thirdMockTrip, times(1)).getServiceId();

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(1)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("8");

        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getStopSequence();
        verify(firstTripLastStopTime, times(1)).getStopSequence();

        verify(secondTripFirstStopTime, times(1)).getArrivalTime();
        verify(secondTripFirstStopTime, times(1)).getStopSequence();

        verify(thirdTripFirstStopTime, times(2)).getArrivalTime();
        verify(thirdTripFirstStopTime, times(1)).getStopSequence();

        verify(firstTripLastStopTime, times(1)).getDepartureTime();

        verify(secondTripLastStopTime, times(1)).getDepartureTime();
        verify(secondTripLastStopTime, times(1)).getStopSequence();
        verify(secondTripLastStopTime, times(1)).getDepartureTime();

        verify(thirdTripLastStopTime, times(2)).getDepartureTime();
        verify(thirdTripLastStopTime, times(1)).getStopSequence();

        verify(mockTimeUtils, times(1))
                .arePeriodsOverlapping(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockLogger, mockDataRepo, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripLastStopTime, secondTripFirstStopTime, secondTripLastStopTime,
                thirdTripFirstStopTime, thirdTripLastStopTime, mockTimeUtils);
    }

    // suppressed warning regarding ignored result of method, since methods are called in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void tripWithNullLastTimeShouldNotGenerateNotice() {
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
        // | 2      | 08:20       | null          | 102    | 11            |
        // | 5      | 11:03       | 11:15         | 103    | 12            |
        // | 5      | 11:16       | 11:20         | 104    | 13            |
        // | 8      | 11:40       | 12:16         | 105    | 14            |
        // | 8      | 13:08       | 13:50         | 106    | 15            |

        // trips.txt + stop_times.txt
        // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
        // |---------|--------|-----------|---------|-----------------|----------------|
        // | 0       | 2      | a         | 7       | 07:00           | null           |
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
        when(firstTripLastStopTime.getDepartureTime()).thenReturn(null);

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

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E052 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();
        verify(mockDataRepo, times(1)).getCalendarAll();

        verify(firstMockTrip, times(4)).getTripId();
        verify(secondMockTrip, times(4)).getTripId();
        verify(secondMockTrip, times(1)).getServiceId();
        verify(thirdMockTrip, times(4)).getTripId();
        verify(thirdMockTrip, times(1)).getServiceId();

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(1)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("8");

        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getStopSequence();
        verify(firstTripLastStopTime, times(1)).getStopSequence();

        verify(secondTripFirstStopTime, times(1)).getArrivalTime();
        verify(secondTripFirstStopTime, times(1)).getStopSequence();

        verify(thirdTripFirstStopTime, times(2)).getArrivalTime();
        verify(thirdTripFirstStopTime, times(1)).getStopSequence();

        verify(firstTripLastStopTime, times(1)).getDepartureTime();

        verify(secondTripLastStopTime, times(1)).getDepartureTime();
        verify(secondTripLastStopTime, times(1)).getStopSequence();
        verify(secondTripLastStopTime, times(1)).getDepartureTime();

        verify(thirdTripLastStopTime, times(2)).getDepartureTime();
        verify(thirdTripLastStopTime, times(1)).getStopSequence();

        verify(mockTimeUtils, times(1))
                .arePeriodsOverlapping(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockLogger, mockDataRepo, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripLastStopTime, secondTripFirstStopTime, secondTripLastStopTime,
                thirdTripFirstStopTime, thirdTripLastStopTime, mockTimeUtils);
    }
}
