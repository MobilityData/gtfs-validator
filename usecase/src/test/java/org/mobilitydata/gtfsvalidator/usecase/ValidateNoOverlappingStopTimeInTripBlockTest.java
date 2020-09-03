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
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateNoOverlappingStopTimeInTripBlockTest {
    private static final LocalTime NOON = LocalTime.NOON;
    private static final int FIVE_AM_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(5, 0, 0).until(NOON, ChronoUnit.SECONDS);
    private static final int SIX_AM_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(6, 0, 0).until(NOON, ChronoUnit.SECONDS);
    private static final int SEVEN_AM_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(7, 0, 0).until(NOON, ChronoUnit.SECONDS);
    private static final int SEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(7, 40, 0).until(NOON, ChronoUnit.SECONDS);
    private static final int EIGHT_AM_20_MIN_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(8, 20, 0).until(NOON, ChronoUnit.SECONDS);
    private static final int NINE_AM_03_MIN_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(9, 3, 0).until(NOON, ChronoUnit.SECONDS);
    private static final int NINE_AM_40_MIN_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(9, 40, 0).until(NOON, ChronoUnit.SECONDS);
    private static final int TEN_AM_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(10, 0, 0).until(NOON, ChronoUnit.SECONDS);
    private static final int TEN_AM_20_MIN_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(10, 20, 0).until(NOON, ChronoUnit.SECONDS);
    private static final int TEN_AM_30_MIN_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(10, 30, 0).until(NOON, ChronoUnit.SECONDS);
    private static final int ELEVEN_AM_03_MIN_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(11, 3, 0).until(NOON, ChronoUnit.SECONDS);
    private static final int ELEVEN_AM_15_MIN_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(11, 15, 0).until(NOON, ChronoUnit.SECONDS);
    private static final int ELEVEN_AM_16_MIN_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(11, 16, 0).until(NOON, ChronoUnit.SECONDS);
    private static final int ELEVEN_AM_20_MIN_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(11, 20, 0).until(NOON, ChronoUnit.SECONDS);
    private static final int ELEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON =
            (int) -LocalTime.of(11, 40, 0).until(NOON, ChronoUnit.SECONDS);

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
        // | 2      | 05:00       | 05:00         |  99    |  8            |
        // | 2      | 06:00       | 06:00         | 100    |  9            |
        // | 2      | 07:00       | 07:40         | 101    | 10            |
        // | 2      | 08:20       | 10:00         | 102    | 11            |
        // | 5      | 11:03       | 11:15         | 103    | 12            |
        // | 5      | 11:16       | 11:20         | 104    | 13            |
        // | 8      | 11:40       | 12:16         | 105    | 14            |
        // | 8      | 13:08       | 13:50         | 106    | 15            |

        // trips.txt + stop_times.txt
        // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
        // |---------|--------|-----------|---------|-----------------|----------------|
        // | 0       | 2      | a         | 7       | 05:00           | 10:00          |
        // | 0       | 5      | a         | 7       | 11:03           | 11:20          |
        // | 0       | 8      | a         | 7       | 11:40           | 13:50          |

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, List<Trip>> mockTripPerBlockIdCollection = new HashMap<>();
        final List<Trip> mockTripCollection = new ArrayList<>();

        final Trip firstMockTrip = mock(Trip.class);
        when(firstMockTrip.getTripId()).thenReturn("2");
        when(firstMockTrip.getServiceId()).thenReturn("a");
        when(firstMockTrip.getBlockId()).thenReturn("7");
        when(firstMockTrip.hasSameServiceId(any())).thenReturn(true);

        final Trip secondMockTrip = mock(Trip.class);
        when(secondMockTrip.getTripId()).thenReturn("5");
        when(secondMockTrip.getServiceId()).thenReturn("a");
        when(secondMockTrip.getBlockId()).thenReturn("7");
        when(secondMockTrip.hasSameServiceId(any())).thenReturn(true);

        final Trip thirdMockTrip = mock(Trip.class);
        when(thirdMockTrip.getTripId()).thenReturn("8");
        when(thirdMockTrip.getServiceId()).thenReturn("a");
        when(thirdMockTrip.getBlockId()).thenReturn("7");
        when(thirdMockTrip.hasSameServiceId(any())).thenReturn(true);

        mockTripCollection.add(firstMockTrip);
        mockTripCollection.add(secondMockTrip);
        mockTripCollection.add(thirdMockTrip);
        mockTripPerBlockIdCollection.put("7", mockTripCollection);

        final SortedMap<Integer, StopTime> mockFirstTripStopTimeCollection = new TreeMap<>();

        final StopTime firstTripFirstStopTime = mock(StopTime.class);
        when(firstTripFirstStopTime.getArrivalTime()).thenReturn(FIVE_AM_AS_SECS_BEFORE_NOON);
        when(firstTripFirstStopTime.getDepartureTime()).thenReturn(FIVE_AM_AS_SECS_BEFORE_NOON);

        final StopTime firstTripSecondStopTime = mock(StopTime.class);
        when(firstTripSecondStopTime.getArrivalTime()).thenReturn(SIX_AM_AS_SECS_BEFORE_NOON);
        when(firstTripSecondStopTime.getDepartureTime()).thenReturn(SIX_AM_AS_SECS_BEFORE_NOON);

        final StopTime firstTripThirdStopTime = mock(StopTime.class);
        when(firstTripThirdStopTime.getArrivalTime()).thenReturn(SEVEN_AM_AS_SECS_BEFORE_NOON);
        when(firstTripThirdStopTime.getDepartureTime()).thenReturn(SEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON);

        final StopTime firstTripLastStopTime = mock(StopTime.class);
        when(firstTripLastStopTime.getArrivalTime()).thenReturn(EIGHT_AM_20_MIN_AS_SECS_BEFORE_NOON);
        when(firstTripLastStopTime.getDepartureTime()).thenReturn(TEN_AM_AS_SECS_BEFORE_NOON);

        mockFirstTripStopTimeCollection.put(8, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(9, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(10, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(11, firstTripLastStopTime);

        final SortedMap<Integer, StopTime> mockSecondTripStopTimeCollection = new TreeMap<>();

        final StopTime secondTripFirstStopTime = mock(StopTime.class);
        when(secondTripFirstStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_03_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripFirstStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_15_MIN_AS_SECS_BEFORE_NOON);

        final StopTime secondTripLastStopTime = mock(StopTime.class);
        when(secondTripLastStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_16_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripLastStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_20_MIN_AS_SECS_BEFORE_NOON);

        mockSecondTripStopTimeCollection.put(12, secondTripFirstStopTime);
        mockSecondTripStopTimeCollection.put(13, secondTripLastStopTime);

        final SortedMap<Integer, StopTime> mockThirdTripStopTimeCollection = new TreeMap<>();
        final StopTime thirdTripFirstStopTime = mock(StopTime.class);
        when(thirdTripFirstStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON);
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
        when(mockTimeUtils.convertIntegerToHHMMSS(SEVEN_AM_AS_SECS_BEFORE_NOON)).thenReturn("07:00:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(TEN_AM_AS_SECS_BEFORE_NOON)).thenReturn("10:00:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(ELEVEN_AM_03_MIN_AS_SECS_BEFORE_NOON)).thenReturn("11:03:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(ELEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON)).thenReturn("11:40:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(1350)).thenReturn("13:50:00");

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E054 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();
        verify(mockDataRepo, times(1)).getCalendarAll();

        verify(firstMockTrip, times(4)).getTripId();
        verify(secondMockTrip, times(4)).getTripId();
        verify(secondMockTrip, times(1)).hasSameServiceId(firstMockTrip);
        verify(thirdMockTrip, times(4)).getTripId();
        verify(thirdMockTrip, times(1)).hasSameServiceId(firstMockTrip);
        verify(thirdMockTrip, times(1)).hasSameServiceId(secondMockTrip);

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(3)).getStopTimeByTripId("8");

        verify(firstTripFirstStopTime, times(2)).getArrivalTime();
        verify(firstTripFirstStopTime, times(2)).getArrivalTime();

        verify(secondTripFirstStopTime, times(4)).getArrivalTime();

        verify(thirdTripFirstStopTime, times(6)).getArrivalTime();

        verify(firstTripLastStopTime, times(2)).getDepartureTime();

        verify(secondTripLastStopTime, times(4)).getDepartureTime();
        verify(secondTripLastStopTime, times(4)).getDepartureTime();

        verify(thirdTripLastStopTime, times(6)).getDepartureTime();

        verify(mockTimeUtils, times(3))
                .arePeriodsOverlapping(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt());

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockLogger, mockDataRepo, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripSecondStopTime, firstTripThirdStopTime, firstTripLastStopTime,
                secondTripFirstStopTime, secondTripLastStopTime, thirdTripFirstStopTime, thirdTripLastStopTime,
                mockTimeUtils);
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
        // | 2      | 05:00       | 05:00         |  99    |  8            |
        // | 2      | 06:00       | 06:00         | 100    |  9            |
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
        when(firstMockTrip.hasSameServiceId(any())).thenReturn(false);

        final Trip secondMockTrip = mock(Trip.class);
        when(secondMockTrip.getTripId()).thenReturn("5");
        when(secondMockTrip.getServiceId()).thenReturn("b");
        when(secondMockTrip.getBlockId()).thenReturn("7");
        when(secondMockTrip.hasSameServiceId(any())).thenReturn(false);

        final Trip thirdMockTrip = mock(Trip.class);
        when(thirdMockTrip.getTripId()).thenReturn("8");
        when(thirdMockTrip.getServiceId()).thenReturn("c");
        when(thirdMockTrip.getBlockId()).thenReturn("7");
        when(thirdMockTrip.hasSameServiceId(any())).thenReturn(false);

        mockTripCollection.add(firstMockTrip);
        mockTripCollection.add(secondMockTrip);
        mockTripCollection.add(thirdMockTrip);
        mockTripPerBlockIdCollection.put("7", mockTripCollection);

        final SortedMap<Integer, StopTime> mockFirstTripStopTimeCollection = new TreeMap<>();

        final StopTime firstTripFirstStopTime = mock(StopTime.class);
        when(firstTripFirstStopTime.getArrivalTime()).thenReturn(FIVE_AM_AS_SECS_BEFORE_NOON);
        when(firstTripFirstStopTime.getDepartureTime()).thenReturn(FIVE_AM_AS_SECS_BEFORE_NOON);

        final StopTime firstTripSecondStopTime = mock(StopTime.class);
        when(firstTripSecondStopTime.getArrivalTime()).thenReturn(SIX_AM_AS_SECS_BEFORE_NOON);
        when(firstTripSecondStopTime.getDepartureTime()).thenReturn(SIX_AM_AS_SECS_BEFORE_NOON);

        final StopTime firstTripThirdStopTime = mock(StopTime.class);
        when(firstTripThirdStopTime.getArrivalTime()).thenReturn(SEVEN_AM_AS_SECS_BEFORE_NOON);
        when(firstTripThirdStopTime.getDepartureTime()).thenReturn(SEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON);

        final StopTime firstTripLastStopTime = mock(StopTime.class);
        when(firstTripLastStopTime.getArrivalTime()).thenReturn(EIGHT_AM_20_MIN_AS_SECS_BEFORE_NOON);
        when(firstTripLastStopTime.getDepartureTime()).thenReturn(TEN_AM_AS_SECS_BEFORE_NOON);

        mockFirstTripStopTimeCollection.put(8, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(9, firstTripSecondStopTime);
        mockFirstTripStopTimeCollection.put(10, firstTripThirdStopTime);
        mockFirstTripStopTimeCollection.put(11, firstTripLastStopTime);

        final SortedMap<Integer, StopTime> mockSecondTripStopTimeCollection = new TreeMap<>();

        final StopTime secondTripFirstStopTime = mock(StopTime.class);
        when(secondTripFirstStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_03_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripFirstStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_15_MIN_AS_SECS_BEFORE_NOON);

        final StopTime secondTripLastStopTime = mock(StopTime.class);
        when(secondTripLastStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_16_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripLastStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_20_MIN_AS_SECS_BEFORE_NOON);

        mockSecondTripStopTimeCollection.put(12, secondTripFirstStopTime);
        mockSecondTripStopTimeCollection.put(13, secondTripLastStopTime);

        final SortedMap<Integer, StopTime> mockThirdTripStopTimeCollection = new TreeMap<>();
        final StopTime thirdTripFirstStopTime = mock(StopTime.class);
        when(thirdTripFirstStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON);
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
        when(calendarForServiceA.isOverlapping(calendarForServiceB)).thenReturn(false);
        when(calendarForServiceA.isOverlapping(calendarForServiceC)).thenReturn(false);
        when(calendarForServiceB.isOverlapping(calendarForServiceC)).thenReturn(false);

        when(calendarForServiceA.getStartDate()).thenReturn(LocalDate.of(2020, 8, 1));
        when(calendarForServiceA.getEndDate()).thenReturn(LocalDate.of(2020, 8, 31));
        when(calendarForServiceB.getStartDate()).thenReturn(LocalDate.of(2020, 9, 1));
        when(calendarForServiceB.getEndDate()).thenReturn(LocalDate.of(2020, 9, 30));
        when(calendarForServiceC.getStartDate()).thenReturn(LocalDate.of(2020, 8, 1));
        when(calendarForServiceC.getEndDate()).thenReturn(LocalDate.of(2020, 8, 31));
        when(calendarForServiceA.getOverlappingDays(calendarForServiceC)).thenReturn(new HashSet<>());

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

        verify(mockLogger, times(1)).info("validating rule 'E054 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();

        verify(firstMockTrip, times(4)).getTripId();
        verify(firstMockTrip, times(2)).getServiceId();
        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");

        verify(secondMockTrip, times(4)).getTripId();
        verify(secondMockTrip, times(2)).getServiceId();
        verify(secondMockTrip, times(1)).hasSameServiceId(firstMockTrip);
        verify(mockDataRepo, times(2)).getStopTimeByTripId("5");

        verify(thirdMockTrip, times(4)).getTripId();
        verify(thirdMockTrip, times(2)).getServiceId();
        verify(thirdMockTrip, times(1)).hasSameServiceId(secondMockTrip);
        verify(thirdMockTrip, times(1)).hasSameServiceId(firstMockTrip);
        verify(mockDataRepo, times(3)).getStopTimeByTripId("8");

        verify(firstTripFirstStopTime, times(2)).getArrivalTime();

        verify(firstTripLastStopTime, times(2)).getDepartureTime();

        verify(secondTripFirstStopTime, times(3)).getArrivalTime();
        verify(secondTripLastStopTime, times(3)).getDepartureTime();

        verify(thirdTripFirstStopTime, times(4)).getArrivalTime();
        verify(thirdTripLastStopTime, times(4)).getDepartureTime();

        verify(mockDataRepo, times(1)).getCalendarAll();

        verify(mockDataRepo, times(2)).getCalendarByServiceId("a");
        verify(mockDataRepo, times(2)).getCalendarByServiceId("b");
        verify(mockDataRepo, times(2)).getCalendarByServiceId("c");

        verify(calendarForServiceA, times(1))
                .isOverlapping(ArgumentMatchers.eq(calendarForServiceB));
        verify(calendarForServiceA, times(1))
                .isOverlapping(ArgumentMatchers.eq(calendarForServiceC));
        verify(calendarForServiceB, times(1))
                .isOverlapping(ArgumentMatchers.eq(calendarForServiceC));

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockLogger, mockDataRepo, mockTimeUtils, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripSecondStopTime, firstTripThirdStopTime, firstTripLastStopTime,
                secondTripFirstStopTime, secondTripLastStopTime, thirdTripFirstStopTime, thirdTripLastStopTime,
                calendarForServiceA, calendarForServiceB, calendarForServiceC);
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
        // | 2      | 05:00       | 05:00         |  99    |  8            |
        // | 2      | 06:00       | 06:00         | 100    |  9            |
        // | 2      | 07:00       | 07:40         | 101    | 10            |
        // | 2      | 08:20       | 10:00         | 102    | 11            |
        // | 5      | 11:03       | 11:15         | 103    | 12            |
        // | 5      | 11:16       | 11:20         | 104    | 13            |
        // | 8      | 07:00       | 07:40         | 105    | 14            |
        // | 8      | 08:20       | 10:00         | 106    | 15            |

        // trips.txt + stop_times.txt
        // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
        // |---------|--------|-----------|---------|-----------------|----------------|
        // | 0       | 2      | a         | 7       | 05:00           | 10:00          |
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

        // Here no service overlap:
        // - service a is active on two days: 2020-07-23 and 2020-08-01
        // - service b is active only on 2020-09-04
        // - service c is active only on 2020-01-08
        // all services are active on different days, hence no overlap.

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, List<Trip>> mockTripPerBlockIdCollection = new HashMap<>();
        final List<Trip> mockTripCollection = new ArrayList<>();

        final Trip firstMockTrip = mock(Trip.class);
        when(firstMockTrip.getTripId()).thenReturn("2");
        when(firstMockTrip.getServiceId()).thenReturn("a");
        when(firstMockTrip.getBlockId()).thenReturn("7");
        when(firstMockTrip.hasSameServiceId(any())).thenReturn(false);

        final Trip secondMockTrip = mock(Trip.class);
        when(secondMockTrip.getTripId()).thenReturn("5");
        when(secondMockTrip.getServiceId()).thenReturn("b");
        when(secondMockTrip.getBlockId()).thenReturn("7");
        when(secondMockTrip.hasSameServiceId(any())).thenReturn(false);

        final Trip thirdMockTrip = mock(Trip.class);
        when(thirdMockTrip.getTripId()).thenReturn("8");
        when(thirdMockTrip.getServiceId()).thenReturn("c");
        when(thirdMockTrip.getBlockId()).thenReturn("7");
        when(thirdMockTrip.hasSameServiceId(any())).thenReturn(false);

        mockTripCollection.add(firstMockTrip);
        mockTripCollection.add(secondMockTrip);
        mockTripCollection.add(thirdMockTrip);
        mockTripPerBlockIdCollection.put("7", mockTripCollection);

        final SortedMap<Integer, StopTime> mockFirstTripStopTimeCollection = new TreeMap<>();

        final StopTime firstTripFirstStopTime = mock(StopTime.class);
        when(firstTripFirstStopTime.getArrivalTime()).thenReturn(FIVE_AM_AS_SECS_BEFORE_NOON);
        when(firstTripFirstStopTime.getDepartureTime()).thenReturn(FIVE_AM_AS_SECS_BEFORE_NOON);

        final StopTime firstTripSecondStopTime = mock(StopTime.class);
        when(firstTripSecondStopTime.getArrivalTime()).thenReturn(SIX_AM_AS_SECS_BEFORE_NOON);
        when(firstTripSecondStopTime.getDepartureTime()).thenReturn(SIX_AM_AS_SECS_BEFORE_NOON);

        final StopTime firstTripThirdStopTime = mock(StopTime.class);
        when(firstTripThirdStopTime.getArrivalTime()).thenReturn(SEVEN_AM_AS_SECS_BEFORE_NOON);
        when(firstTripThirdStopTime.getDepartureTime()).thenReturn(SEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON);

        final StopTime firstTripLastStopTime = mock(StopTime.class);
        when(firstTripLastStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_20_MIN_AS_SECS_BEFORE_NOON);
        when(firstTripLastStopTime.getDepartureTime()).thenReturn(TEN_AM_AS_SECS_BEFORE_NOON);

        mockFirstTripStopTimeCollection.put(8, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(9, firstTripSecondStopTime);
        mockFirstTripStopTimeCollection.put(10, firstTripThirdStopTime);
        mockFirstTripStopTimeCollection.put(11, firstTripLastStopTime);

        final SortedMap<Integer, StopTime> mockSecondTripStopTimeCollection = new TreeMap<>();

        final StopTime secondTripFirstStopTime = mock(StopTime.class);
        when(secondTripFirstStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_03_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripFirstStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_15_MIN_AS_SECS_BEFORE_NOON);

        final StopTime secondTripLastStopTime = mock(StopTime.class);
        when(secondTripLastStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_16_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripLastStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_20_MIN_AS_SECS_BEFORE_NOON);

        mockSecondTripStopTimeCollection.put(12, secondTripFirstStopTime);
        mockSecondTripStopTimeCollection.put(13, secondTripLastStopTime);

        final SortedMap<Integer, StopTime> mockThirdTripStopTimeCollection = new TreeMap<>();
        final StopTime thirdTripFirstStopTime = mock(StopTime.class);
        when(thirdTripFirstStopTime.getArrivalTime()).thenReturn(SEVEN_AM_AS_SECS_BEFORE_NOON);
        when(thirdTripFirstStopTime.getDepartureTime()).thenReturn(SEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON);

        final StopTime thirdTripLastStopTime = mock(StopTime.class);
        when(thirdTripLastStopTime.getArrivalTime()).thenReturn(EIGHT_AM_20_MIN_AS_SECS_BEFORE_NOON);
        when(thirdTripLastStopTime.getDepartureTime()).thenReturn(TEN_AM_AS_SECS_BEFORE_NOON);

        mockThirdTripStopTimeCollection.put(14, thirdTripFirstStopTime);
        mockThirdTripStopTimeCollection.put(15, thirdTripLastStopTime);

        when(mockDataRepo.getAllTripByBlockId()).thenReturn(mockTripPerBlockIdCollection);
        when(mockDataRepo.getStopTimeByTripId("2")).thenReturn(mockFirstTripStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("5")).thenReturn(mockSecondTripStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("8")).thenReturn(mockThirdTripStopTimeCollection);
        when(mockDataRepo.getCalendarAll()).thenReturn(new HashMap<>());

        final Map<String, Map<String, CalendarDate>> mockCalendarDateCollection = new HashMap<>();
        final HashMap<String, CalendarDate> calendarDateCollectionServiceA = new HashMap<>();
        final HashMap<String, CalendarDate> calendarDateCollectionServiceB = new HashMap<>();
        final HashMap<String, CalendarDate> calendarDateCollectionServiceC = new HashMap<>();
        final CalendarDate firstCalendarDateForServiceA = mock(CalendarDate.class);
        final CalendarDate secondCalendarDateForServiceA = mock(CalendarDate.class);
        final CalendarDate calendarDateForServiceB = mock(CalendarDate.class);
        final CalendarDate calendarDateForServiceC = mock(CalendarDate.class);

        calendarDateCollectionServiceA.put("2020-07-23", firstCalendarDateForServiceA);
        calendarDateCollectionServiceA.put("2020-08-01", secondCalendarDateForServiceA);
        calendarDateCollectionServiceB.put("2020-09-04", calendarDateForServiceB);
        calendarDateCollectionServiceC.put("2020-01-08", calendarDateForServiceC);

        when(firstCalendarDateForServiceA.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);
        when(firstCalendarDateForServiceA.getDate()).thenReturn(LocalDate.of(2020, 7, 23));

        when(secondCalendarDateForServiceA.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);
        when(secondCalendarDateForServiceA.getDate()).thenReturn(LocalDate.of(2020, 8, 1));

        when(calendarDateForServiceB.getExceptionType()).thenReturn(ExceptionType.REMOVED_SERVICE);
        when(calendarDateForServiceB.getDate()).thenReturn(LocalDate.of(2020, 9, 4));

        when(calendarDateForServiceC.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);
        when(calendarDateForServiceC.getDate()).thenReturn(LocalDate.of(2020, 1, 8));

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

        verify(mockLogger, times(1)).info("validating rule 'E054 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();
        verify(mockDataRepo, times(1)).getCalendarAll();

        verify(firstMockTrip, times(4)).getTripId();
        verify(firstMockTrip, times(2)).getServiceId();

        verify(secondMockTrip, times(4)).getTripId();
        verify(secondMockTrip, times(2)).getServiceId();
        verify(secondMockTrip, times(1)).hasSameServiceId(firstMockTrip);

        verify(thirdMockTrip, times(4)).getTripId();
        verify(thirdMockTrip, times(2)).getServiceId();
        verify(thirdMockTrip, times(1)).hasSameServiceId(firstMockTrip);
        verify(thirdMockTrip, times(1)).hasSameServiceId(secondMockTrip);

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(3)).getStopTimeByTripId("8");
        verify(mockDataRepo, times(6)).getCalendarDateAll();

        verify(calendarDateForServiceB, times(1)).getExceptionType();

        verify(calendarDateForServiceC, times(2)).getExceptionType();
        verify(calendarDateForServiceC, times(2)).getDate();

        verify(firstTripFirstStopTime, times(2)).getArrivalTime();

        verify(secondTripFirstStopTime, times(3)).getArrivalTime();

        verify(thirdTripFirstStopTime, times(4)).getArrivalTime();

        verify(firstTripLastStopTime, times(2)).getDepartureTime();

        verify(secondTripLastStopTime, times(3)).getDepartureTime();

        verify(thirdTripLastStopTime, times(4)).getDepartureTime();

        verifyNoInteractions(mockResultRepo, mockTimeUtils);
        verifyNoMoreInteractions(mockLogger, mockDataRepo, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripSecondStopTime, firstTripThirdStopTime, firstTripLastStopTime,
                secondTripFirstStopTime, secondTripLastStopTime, thirdTripFirstStopTime, thirdTripLastStopTime,
                firstCalendarDateForServiceA, secondCalendarDateForServiceA, calendarDateForServiceB,
                calendarDateForServiceC);
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
        // | 2      | 05:00       | 05:00         |  99    |  8            |
        // | 2      | 06:00       | 06:00         | 100    |  9            |
        // | 2      | 07:00       | 07:40         | 101    | 10            |
        // | 2      | 08:20       | 10:00         | 102    | 11            |
        // | 5      | 09:03       | 11:15         | 103    | 12            |
        // | 5      | 11:16       | 11:20         | 104    | 13            |
        // | 8      | 11:40       | 12:16         | 105    | 14            |
        // | 8      | 13:08       | 13:50         | 106    | 15            |

        // trips.txt + stop_times.txt
        // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
        // |---------|--------|-----------|---------|-----------------|----------------|
        // | 0       | 2      | a         | 7       | 05:00           | 10:00          |
        // | 0       | 5      | a         | 7       | 09:03           | 11:20          | here trips overlap
        // | 0       | 8      | a         | 7       | 11:40           | 13:50          |

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, List<Trip>> mockTripPerBlockIdCollection = new HashMap<>();
        final List<Trip> mockTripCollection = new ArrayList<>();

        final Trip firstMockTrip = mock(Trip.class);
        when(firstMockTrip.getTripId()).thenReturn("2");
        when(firstMockTrip.getServiceId()).thenReturn("a");
        when(firstMockTrip.getBlockId()).thenReturn("7");
        when(firstMockTrip.hasSameServiceId(any())).thenReturn(true);

        final Trip secondMockTrip = mock(Trip.class);
        when(secondMockTrip.getTripId()).thenReturn("5");
        when(secondMockTrip.getServiceId()).thenReturn("a");
        when(secondMockTrip.getBlockId()).thenReturn("7");
        when(secondMockTrip.hasSameServiceId(any())).thenReturn(true);

        final Trip thirdMockTrip = mock(Trip.class);
        when(thirdMockTrip.getTripId()).thenReturn("8");
        when(thirdMockTrip.getServiceId()).thenReturn("a");
        when(thirdMockTrip.getBlockId()).thenReturn("7");
        when(thirdMockTrip.hasSameServiceId(any())).thenReturn(true);

        mockTripCollection.add(firstMockTrip);
        mockTripCollection.add(secondMockTrip);
        mockTripCollection.add(thirdMockTrip);
        mockTripPerBlockIdCollection.put("7", mockTripCollection);

        final SortedMap<Integer, StopTime> mockFirstTripStopTimeCollection = new TreeMap<>();

        final StopTime firstTripFirstStopTime = mock(StopTime.class);
        when(firstTripFirstStopTime.getArrivalTime()).thenReturn(FIVE_AM_AS_SECS_BEFORE_NOON);
        when(firstTripFirstStopTime.getDepartureTime()).thenReturn(FIVE_AM_AS_SECS_BEFORE_NOON);
        when(firstTripFirstStopTime.getStopSequence()).thenReturn(8);

        final StopTime firstTripSecondStopTime = mock(StopTime.class);
        when(firstTripSecondStopTime.getArrivalTime()).thenReturn(SIX_AM_AS_SECS_BEFORE_NOON);
        when(firstTripSecondStopTime.getDepartureTime()).thenReturn(SIX_AM_AS_SECS_BEFORE_NOON);
        when(firstTripSecondStopTime.getStopSequence()).thenReturn(9);

        final StopTime firstTripThirdStopTime = mock(StopTime.class);
        when(firstTripThirdStopTime.getArrivalTime()).thenReturn(SEVEN_AM_AS_SECS_BEFORE_NOON);
        when(firstTripThirdStopTime.getDepartureTime()).thenReturn(SEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON);
        when(firstTripThirdStopTime.getStopSequence()).thenReturn(10);

        final StopTime firstTripLastStopTime = mock(StopTime.class);
        when(firstTripLastStopTime.getArrivalTime()).thenReturn(EIGHT_AM_20_MIN_AS_SECS_BEFORE_NOON);
        when(firstTripLastStopTime.getDepartureTime()).thenReturn(TEN_AM_AS_SECS_BEFORE_NOON);
        when(firstTripLastStopTime.getStopSequence()).thenReturn(11);

        mockFirstTripStopTimeCollection.put(10, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(11, firstTripLastStopTime);

        final SortedMap<Integer, StopTime> mockSecondTripStopTimeCollection = new TreeMap<>();

        final StopTime secondTripFirstStopTime = mock(StopTime.class);
        when(secondTripFirstStopTime.getArrivalTime()).thenReturn(NINE_AM_03_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripFirstStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_15_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripFirstStopTime.getStopSequence()).thenReturn(12);

        final StopTime secondTripLastStopTime = mock(StopTime.class);
        when(secondTripLastStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_16_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripLastStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_20_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripLastStopTime.getStopSequence()).thenReturn(13);

        mockSecondTripStopTimeCollection.put(12, secondTripFirstStopTime);
        mockSecondTripStopTimeCollection.put(13, secondTripLastStopTime);

        final SortedMap<Integer, StopTime> mockThirdTripStopTimeCollection = new TreeMap<>();
        final StopTime thirdTripFirstStopTime = mock(StopTime.class);
        when(thirdTripFirstStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON);
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
        when(mockTimeUtils.convertIntegerToHHMMSS(FIVE_AM_AS_SECS_BEFORE_NOON)).thenReturn("05:00:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(TEN_AM_AS_SECS_BEFORE_NOON)).thenReturn("10:00:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(NINE_AM_03_MIN_AS_SECS_BEFORE_NOON)).thenReturn("09:03:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(ELEVEN_AM_20_MIN_AS_SECS_BEFORE_NOON)).thenReturn("11:20:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(ELEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON)).thenReturn("11:40:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(1350)).thenReturn("13:50:00");
        when(mockTimeUtils.arePeriodsOverlapping(FIVE_AM_AS_SECS_BEFORE_NOON, TEN_AM_AS_SECS_BEFORE_NOON,
                NINE_AM_03_MIN_AS_SECS_BEFORE_NOON, ELEVEN_AM_20_MIN_AS_SECS_BEFORE_NOON)).thenReturn(true);

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E054 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();

        verify(firstMockTrip, times(4)).getTripId();

        verify(secondMockTrip, times(4)).getTripId();
        verify(secondMockTrip, times(1)).hasSameServiceId(firstMockTrip);

        verify(thirdMockTrip, times(4)).getTripId();
        verify(thirdMockTrip, times(1)).hasSameServiceId(firstMockTrip);
        verify(thirdMockTrip, times(1)).hasSameServiceId(secondMockTrip);

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(3)).getStopTimeByTripId("8");
        verify(mockDataRepo, times(1)).getCalendarAll();

        verify(firstTripFirstStopTime, times(2)).getArrivalTime();
        verify(firstTripFirstStopTime, times(2)).getArrivalTime();

        verify(secondTripFirstStopTime, times(4)).getArrivalTime();

        verify(thirdTripFirstStopTime, times(6)).getArrivalTime();

        verify(firstTripLastStopTime, times(2)).getDepartureTime();

        verify(secondTripLastStopTime, times(4)).getDepartureTime();
        verify(secondTripLastStopTime, times(4)).getDepartureTime();

        verify(thirdTripLastStopTime, times(6)).getDepartureTime();

        verify(mockTimeUtils, times(1)).convertIntegerToHHMMSS(FIVE_AM_AS_SECS_BEFORE_NOON);
        verify(mockTimeUtils, times(1)).convertIntegerToHHMMSS(TEN_AM_AS_SECS_BEFORE_NOON);
        verify(mockTimeUtils, times(1)).convertIntegerToHHMMSS(NINE_AM_03_MIN_AS_SECS_BEFORE_NOON);
        verify(mockTimeUtils, times(1)).convertIntegerToHHMMSS(ELEVEN_AM_20_MIN_AS_SECS_BEFORE_NOON);
        verify(mockTimeUtils, times(3))
                .arePeriodsOverlapping(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());

        final ArgumentCaptor<BlockTripsWithOverlappingStopTimesNotice> captor =
                ArgumentCaptor.forClass(BlockTripsWithOverlappingStopTimesNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<BlockTripsWithOverlappingStopTimesNotice> noticeList = captor.getAllValues();

        assertEquals(1, noticeList.size());
        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("2", noticeList.get(0).getEntityId());
        assertEquals("ERROR", noticeList.get(0).getLevel());
        assertEquals(54, noticeList.get(0).getCode());
        assertEquals("7", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_BLOCK_ID));
        assertEquals("5", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_PREVIOUS_TRIP_ID));
        assertEquals("05:00:00", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_FIRST_TIME));
        assertEquals("10:00:00", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_LAST_TIME));
        assertEquals("09:03:00", noticeList.get(0).getNoticeSpecific(Notice.KEY_PREVIOUS_TRIP_FIRST_TIME));
        assertEquals("11:20:00", noticeList.get(0).getNoticeSpecific(Notice.KEY_PREVIOUS_TRIP_LAST_TIME));

        verifyNoMoreInteractions(mockLogger, mockDataRepo, mockTimeUtils, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripSecondStopTime, firstTripThirdStopTime, firstTripLastStopTime,
                secondTripFirstStopTime, secondTripLastStopTime,
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
        // | 2      | 05:00       | 05:00         |  99    |  8            |
        // | 2      | 06:00       | 06:00         | 100    |  9            |
        // | 2      | 07:00       | 07:40         | 101    | 10            |
        // | 2      | 08:20       | 10:00         | 102    | 11            |
        // | 5      | 17:23       | 18:08         | 103    | 12            |
        // | 5      | 18:20       | 18:57         | 104    | 13            |
        // | 8      | 09:40       | 12:16         | 105    | 14            |
        // | 8      | 13:08       | 13:50         | 106    | 15            |

        // trips.txt + stop_times.txt
        // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
        // |---------|--------|-----------|---------|-----------------|----------------|
        // | 0       | 2      | a         | 7       | 05:00           | 10:00          |
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
        when(mockTimeUtils.convertIntegerToHHMMSS(FIVE_AM_AS_SECS_BEFORE_NOON)).thenReturn("05:00:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(TEN_AM_AS_SECS_BEFORE_NOON)).thenReturn("10:00:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(NINE_AM_40_MIN_AS_SECS_BEFORE_NOON)).thenReturn("09:40:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(1350)).thenReturn("13:50:00");
        when(mockTimeUtils.arePeriodsOverlapping(FIVE_AM_AS_SECS_BEFORE_NOON, TEN_AM_AS_SECS_BEFORE_NOON,
                NINE_AM_40_MIN_AS_SECS_BEFORE_NOON, 1350)).thenReturn(true);

        final Trip firstMockTrip = mock(Trip.class);
        when(firstMockTrip.getTripId()).thenReturn("2");
        when(firstMockTrip.getServiceId()).thenReturn("a");
        when(firstMockTrip.getBlockId()).thenReturn("7");
        when(firstMockTrip.hasSameServiceId(any())).thenReturn(false);

        final Trip secondMockTrip = mock(Trip.class);
        when(secondMockTrip.getTripId()).thenReturn("5");
        when(secondMockTrip.getServiceId()).thenReturn("b");
        when(secondMockTrip.getBlockId()).thenReturn("7");
        when(secondMockTrip.hasSameServiceId(any())).thenReturn(false);

        final Trip thirdMockTrip = mock(Trip.class);
        when(thirdMockTrip.getTripId()).thenReturn("8");
        when(thirdMockTrip.getServiceId()).thenReturn("c");
        when(thirdMockTrip.getBlockId()).thenReturn("7");
        when(thirdMockTrip.hasSameServiceId(any())).thenReturn(false);

        final List<Trip> mockTripCollection = new ArrayList<>();
        final Map<String, List<Trip>> mockTripPerBlockIdCollection = new HashMap<>();

        mockTripCollection.add(firstMockTrip);
        mockTripCollection.add(secondMockTrip);
        mockTripCollection.add(thirdMockTrip);
        mockTripPerBlockIdCollection.put("7", mockTripCollection);

        final SortedMap<Integer, StopTime> mockFirstTripStopTimeCollection = new TreeMap<>();

        final StopTime firstTripFirstStopTime = mock(StopTime.class);
        when(firstTripFirstStopTime.getArrivalTime()).thenReturn(FIVE_AM_AS_SECS_BEFORE_NOON);
        when(firstTripFirstStopTime.getDepartureTime()).thenReturn(FIVE_AM_AS_SECS_BEFORE_NOON);
        when(firstTripFirstStopTime.getStopSequence()).thenReturn(8);

        final StopTime firstTripSecondStopTime = mock(StopTime.class);
        when(firstTripSecondStopTime.getArrivalTime()).thenReturn(SIX_AM_AS_SECS_BEFORE_NOON);
        when(firstTripSecondStopTime.getDepartureTime()).thenReturn(SIX_AM_AS_SECS_BEFORE_NOON);
        when(firstTripSecondStopTime.getStopSequence()).thenReturn(9);

        final StopTime firstTripThirdStopTime = mock(StopTime.class);
        when(firstTripThirdStopTime.getArrivalTime()).thenReturn(SEVEN_AM_AS_SECS_BEFORE_NOON);
        when(firstTripThirdStopTime.getDepartureTime()).thenReturn(SEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON);
        when(firstTripThirdStopTime.getStopSequence()).thenReturn(10);

        final StopTime firstTripLastStopTime = mock(StopTime.class);
        when(firstTripLastStopTime.getArrivalTime()).thenReturn(EIGHT_AM_20_MIN_AS_SECS_BEFORE_NOON);
        when(firstTripLastStopTime.getDepartureTime()).thenReturn(TEN_AM_AS_SECS_BEFORE_NOON);
        when(firstTripLastStopTime.getStopSequence()).thenReturn(11);

        mockFirstTripStopTimeCollection.put(8, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(9, firstTripSecondStopTime);
        mockFirstTripStopTimeCollection.put(10, firstTripThirdStopTime);
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
        when(thirdTripFirstStopTime.getArrivalTime()).thenReturn(NINE_AM_40_MIN_AS_SECS_BEFORE_NOON);
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
        when(calendarForServiceA.isOverlapping(calendarForServiceB)).thenReturn(false);
        when(calendarForServiceA.isOverlapping(calendarForServiceC)).thenReturn(true);
        when(calendarForServiceB.isOverlapping(calendarForServiceC)).thenReturn(false);

        when(calendarForServiceA.getStartDate()).thenReturn(LocalDate.of(2020, 8, 1));
        when(calendarForServiceA.getEndDate()).thenReturn(LocalDate.of(2020, 8, 31));
        when(calendarForServiceB.getStartDate()).thenReturn(LocalDate.of(2020, 9, 1));
        when(calendarForServiceB.getEndDate()).thenReturn(LocalDate.of(2020, 9, 30));
        when(calendarForServiceC.getStartDate()).thenReturn(LocalDate.of(2020, 8, 1));
        when(calendarForServiceC.getEndDate()).thenReturn(LocalDate.of(2020, 8, 31));
        when(calendarForServiceA.getOverlappingDays(calendarForServiceC))
                .thenReturn(Set.of("tuesday"));

        when(mockDataRepo.getCalendarAll()).thenReturn(mockCalendarCollection);
        when(mockDataRepo.getCalendarByServiceId("a")).thenReturn(calendarForServiceA);
        when(mockDataRepo.getCalendarByServiceId("b")).thenReturn(calendarForServiceB);
        when(mockDataRepo.getCalendarByServiceId("c")).thenReturn(calendarForServiceC);

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E054 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getCalendarAll();
        verify(mockDataRepo, times(1)).getAllTripByBlockId();
        verify(mockDataRepo, times(2)).getCalendarByServiceId("a");
        verify(mockDataRepo, times(2)).getCalendarByServiceId("b");
        verify(mockDataRepo, times(2)).getCalendarByServiceId("c");

        verify(firstMockTrip, times(4)).getTripId();
        verify(firstMockTrip, times(2)).getServiceId();

        verify(secondMockTrip, times(4)).getTripId();
        verify(secondMockTrip, times(2)).getServiceId();
        verify(secondMockTrip, times(1)).hasSameServiceId(firstMockTrip);

        verify(thirdMockTrip, times(4)).getTripId();
        verify(thirdMockTrip, times(2)).getServiceId();
        verify(thirdMockTrip, times(1)).hasSameServiceId(firstMockTrip);
        verify(thirdMockTrip, times(1)).hasSameServiceId(secondMockTrip);

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(3)).getStopTimeByTripId("8");

        verify(firstTripFirstStopTime, times(2)).getArrivalTime();

        verify(secondTripFirstStopTime, times(3)).getArrivalTime();

        verify(thirdTripFirstStopTime, times(5)).getArrivalTime();

        verify(firstTripLastStopTime, times(2)).getDepartureTime();

        verify(secondTripLastStopTime, times(3)).getDepartureTime();

        verify(calendarForServiceA, times(1)).isOverlapping(calendarForServiceB);
        verify(calendarForServiceA, times(1)).isOverlapping(calendarForServiceC);
        verify(calendarForServiceB, times(1)).isOverlapping(calendarForServiceC);
        verify(calendarForServiceA, times(2)).getOverlappingDays(calendarForServiceC);

        verify(thirdTripLastStopTime, times(5)).getDepartureTime();
        verify(mockTimeUtils, times(1)).convertIntegerToHHMMSS(FIVE_AM_AS_SECS_BEFORE_NOON);
        verify(mockTimeUtils, times(1)).convertIntegerToHHMMSS(TEN_AM_AS_SECS_BEFORE_NOON);
        verify(mockTimeUtils, times(1)).convertIntegerToHHMMSS(NINE_AM_40_MIN_AS_SECS_BEFORE_NOON);
        verify(mockTimeUtils, times(1)).convertIntegerToHHMMSS(1350);
        verify(mockTimeUtils, times(1))
                .arePeriodsOverlapping(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());

        final ArgumentCaptor<BlockTripsWithOverlappingStopTimesNotice> captor =
                ArgumentCaptor.forClass(BlockTripsWithOverlappingStopTimesNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<BlockTripsWithOverlappingStopTimesNotice> noticeList = captor.getAllValues();

        assertEquals(1, noticeList.size());
        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("2", noticeList.get(0).getEntityId());
        assertEquals("ERROR", noticeList.get(0).getLevel());
        assertEquals(54, noticeList.get(0).getCode());
        assertEquals("7", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_BLOCK_ID));
        assertEquals("8", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_PREVIOUS_TRIP_ID));
        assertEquals("05:00:00", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_FIRST_TIME));
        assertEquals("10:00:00", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_LAST_TIME));
        assertEquals("09:40:00", noticeList.get(0).getNoticeSpecific(Notice.KEY_PREVIOUS_TRIP_FIRST_TIME));
        assertEquals("13:50:00", noticeList.get(0).getNoticeSpecific(Notice.KEY_PREVIOUS_TRIP_LAST_TIME));
        assertEquals(Set.of("tuesday"), noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_DATE_LIST));

        verifyNoMoreInteractions(mockLogger, mockDataRepo, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripSecondStopTime, firstTripThirdStopTime, firstTripLastStopTime,
                secondTripFirstStopTime, secondTripLastStopTime, thirdTripFirstStopTime, thirdTripLastStopTime,
                mockResultRepo, calendarForServiceA, calendarForServiceB, calendarForServiceC, mockTimeUtils);
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
        // | 2      | 05:00       | 05:00         |  99    |  8            |
        // | 2      | 06:00       | 06:00         | 100    |  9            |
        // | 2      | 07:00       | 07:40         | 101    | 10            |
        // | 2      | 08:20       | 10:00         | 102    | 11            |
        // | 5      | 11:03       | 11:15         | 103    | 12            |
        // | 5      | 11:16       | 11:20         | 104    | 13            |
        // | 8      | 09:40       | 10:20         | 105    | 14            |
        // | 8      | 10:30       | 13:50         | 106    | 15            |

        // trips.txt + stop_times.txt
        // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
        // |---------|--------|-----------|---------|-----------------|----------------|
        // | 0       | 2      | a         | 7       | 05:00           | 10:00          |
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
        // Service a starts at 05:00 and ends at 10:00 - and service c starts at 09:40 and ends at 13:50.
        // Which means that trips 2 and 8 overlap on 2020-07-23 between 09:40 and 10:00: that is an error and should
        // generate a notice.

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, List<Trip>> mockTripPerBlockIdCollection = new HashMap<>();
        final List<Trip> mockTripCollection = new ArrayList<>();

        final Trip firstMockTrip = mock(Trip.class);
        when(firstMockTrip.getTripId()).thenReturn("2");
        when(firstMockTrip.getServiceId()).thenReturn("a");
        when(firstMockTrip.getBlockId()).thenReturn("7");
        when(firstMockTrip.hasSameServiceId(any())).thenReturn(false);

        final Trip secondMockTrip = mock(Trip.class);
        when(secondMockTrip.getTripId()).thenReturn("5");
        when(secondMockTrip.getServiceId()).thenReturn("b");
        when(secondMockTrip.getBlockId()).thenReturn("7");
        when(secondMockTrip.hasSameServiceId(any())).thenReturn(false);

        final Trip thirdMockTrip = mock(Trip.class);
        when(thirdMockTrip.getTripId()).thenReturn("8");
        when(thirdMockTrip.getServiceId()).thenReturn("c");
        when(thirdMockTrip.getBlockId()).thenReturn("7");
        when(thirdMockTrip.hasSameServiceId(any())).thenReturn(false);

        mockTripCollection.add(firstMockTrip);
        mockTripCollection.add(secondMockTrip);
        mockTripCollection.add(thirdMockTrip);
        mockTripPerBlockIdCollection.put("7", mockTripCollection);

        final SortedMap<Integer, StopTime> mockFirstTripStopTimeCollection = new TreeMap<>();

        final StopTime firstTripFirstStopTime = mock(StopTime.class);
        when(firstTripFirstStopTime.getArrivalTime()).thenReturn(FIVE_AM_AS_SECS_BEFORE_NOON);
        when(firstTripFirstStopTime.getDepartureTime()).thenReturn(FIVE_AM_AS_SECS_BEFORE_NOON);
        when(firstTripFirstStopTime.getStopSequence()).thenReturn(8);

        final StopTime firstTripSecondStopTime = mock(StopTime.class);
        when(firstTripSecondStopTime.getArrivalTime()).thenReturn(SIX_AM_AS_SECS_BEFORE_NOON);
        when(firstTripSecondStopTime.getDepartureTime()).thenReturn(SIX_AM_AS_SECS_BEFORE_NOON);
        when(firstTripSecondStopTime.getStopSequence()).thenReturn(9);

        final StopTime firstTripThirdStopTime = mock(StopTime.class);
        when(firstTripThirdStopTime.getArrivalTime()).thenReturn(SEVEN_AM_AS_SECS_BEFORE_NOON);
        when(firstTripThirdStopTime.getDepartureTime()).thenReturn(SEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON);
        when(firstTripThirdStopTime.getStopSequence()).thenReturn(10);

        final StopTime firstTripLastStopTime = mock(StopTime.class);
        when(firstTripLastStopTime.getArrivalTime()).thenReturn(EIGHT_AM_20_MIN_AS_SECS_BEFORE_NOON);
        when(firstTripLastStopTime.getDepartureTime()).thenReturn(TEN_AM_AS_SECS_BEFORE_NOON);
        when(firstTripLastStopTime.getStopSequence()).thenReturn(11);

        mockFirstTripStopTimeCollection.put(10, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(11, firstTripLastStopTime);

        final SortedMap<Integer, StopTime> mockSecondTripStopTimeCollection = new TreeMap<>();

        final StopTime secondTripFirstStopTime = mock(StopTime.class);
        when(secondTripFirstStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_03_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripFirstStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_15_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripFirstStopTime.getStopSequence()).thenReturn(12);

        final StopTime secondTripLastStopTime = mock(StopTime.class);
        when(secondTripLastStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_16_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripLastStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_20_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripLastStopTime.getStopSequence()).thenReturn(13);

        mockSecondTripStopTimeCollection.put(12, secondTripFirstStopTime);
        mockSecondTripStopTimeCollection.put(13, secondTripLastStopTime);

        final SortedMap<Integer, StopTime> mockThirdTripStopTimeCollection = new TreeMap<>();
        final StopTime thirdTripFirstStopTime = mock(StopTime.class);
        when(thirdTripFirstStopTime.getArrivalTime()).thenReturn(NINE_AM_40_MIN_AS_SECS_BEFORE_NOON);
        when(thirdTripFirstStopTime.getDepartureTime()).thenReturn(TEN_AM_20_MIN_AS_SECS_BEFORE_NOON);
        when(thirdTripFirstStopTime.getStopSequence()).thenReturn(14);

        final StopTime thirdTripLastStopTime = mock(StopTime.class);
        when(thirdTripLastStopTime.getArrivalTime()).thenReturn(TEN_AM_30_MIN_AS_SECS_BEFORE_NOON);
        when(thirdTripLastStopTime.getDepartureTime()).thenReturn(1350);
        when(thirdTripLastStopTime.getStopSequence()).thenReturn(15);

        mockThirdTripStopTimeCollection.put(14, thirdTripFirstStopTime);
        mockThirdTripStopTimeCollection.put(15, thirdTripLastStopTime);

        when(mockDataRepo.getAllTripByBlockId()).thenReturn(mockTripPerBlockIdCollection);
        when(mockDataRepo.getStopTimeByTripId("2")).thenReturn(mockFirstTripStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("5")).thenReturn(mockSecondTripStopTimeCollection);
        when(mockDataRepo.getStopTimeByTripId("8")).thenReturn(mockThirdTripStopTimeCollection);

        final Map<String, Map<String, CalendarDate>> mockCalendarDateCollection = new HashMap<>();
        final HashMap<String, CalendarDate> calendarDateCollectionServiceA = new HashMap<>();
        final HashMap<String, CalendarDate> calendarDateCollectionServiceB = new HashMap<>();
        final HashMap<String, CalendarDate> calendarDateCollectionServiceC = new HashMap<>();
        final CalendarDate firstCalendarDateForServiceA = mock(CalendarDate.class);
        final CalendarDate secondCalendarDateForServiceA = mock(CalendarDate.class);
        final CalendarDate calendarDateForServiceB = mock(CalendarDate.class);
        final CalendarDate calendarDateForServiceC = mock(CalendarDate.class);
        calendarDateCollectionServiceA.put("2020-07-23", firstCalendarDateForServiceA);
        calendarDateCollectionServiceA.put("2020-08-01", secondCalendarDateForServiceA);
        calendarDateCollectionServiceB.put("2020-09-04", calendarDateForServiceB);
        calendarDateCollectionServiceC.put("2020-07-23", calendarDateForServiceC);

        when(firstCalendarDateForServiceA.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);
        when(firstCalendarDateForServiceA.getDate()).thenReturn(LocalDate.of(2020, 7, 23));

        when(secondCalendarDateForServiceA.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);
        when(secondCalendarDateForServiceA.getDate()).thenReturn(LocalDate.of(2020, 8, 1));

        when(calendarDateForServiceB.getExceptionType()).thenReturn(ExceptionType.REMOVED_SERVICE);
        when(calendarDateForServiceB.getDate()).thenReturn(LocalDate.of(2020, 9, 4));

        when(calendarDateForServiceC.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);
        when(calendarDateForServiceC.getDate()).thenReturn(LocalDate.of(2020, 7, 23));

        mockCalendarDateCollection.put("a", calendarDateCollectionServiceA);
        mockCalendarDateCollection.put("b", calendarDateCollectionServiceB);
        mockCalendarDateCollection.put("c", calendarDateCollectionServiceC);
        when(mockDataRepo.getCalendarDateAll()).thenReturn(mockCalendarDateCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtils = mock(TimeUtils.class);
        when(mockTimeUtils.convertIntegerToHHMMSS(FIVE_AM_AS_SECS_BEFORE_NOON)).thenReturn("05:00:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(TEN_AM_AS_SECS_BEFORE_NOON)).thenReturn("10:00:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(NINE_AM_40_MIN_AS_SECS_BEFORE_NOON)).thenReturn("09:40:00");
        when(mockTimeUtils.convertIntegerToHHMMSS(1350)).thenReturn("13:50:00");
        when(mockTimeUtils.arePeriodsOverlapping(FIVE_AM_AS_SECS_BEFORE_NOON, TEN_AM_AS_SECS_BEFORE_NOON,
                NINE_AM_40_MIN_AS_SECS_BEFORE_NOON, 1350)).thenReturn(true);

        final ValidateNoOverlappingStopTimeInTripBlock underTest =
                new ValidateNoOverlappingStopTimeInTripBlock(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils);

        underTest.execute();

        verify(mockLogger, times(1)).info("validating rule 'E054 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();
        verify(mockDataRepo, times(1)).getCalendarAll();

        verify(firstMockTrip, times(4)).getTripId();
        verify(firstMockTrip, times(2)).getServiceId();

        verify(secondMockTrip, times(4)).getTripId();
        verify(secondMockTrip, times(2)).getServiceId();
        verify(secondMockTrip, times(1)).hasSameServiceId(firstMockTrip);

        verify(thirdMockTrip, times(4)).getTripId();
        verify(thirdMockTrip, times(2)).getServiceId();
        verify(thirdMockTrip, times(1)).hasSameServiceId(firstMockTrip);
        verify(thirdMockTrip, times(1)).hasSameServiceId(secondMockTrip);

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(3)).getStopTimeByTripId("8");
        verify(mockDataRepo, times(6)).getCalendarDateAll();

        verify(calendarDateForServiceC, times(3)).getDate();
        verify(calendarDateForServiceB, times(1)).getExceptionType();
        verify(calendarDateForServiceC, times(2)).getExceptionType();

        verify(firstTripFirstStopTime, times(2)).getArrivalTime();

        verify(secondTripFirstStopTime, times(3)).getArrivalTime();

        verify(thirdTripFirstStopTime, times(5)).getArrivalTime();

        verify(firstTripLastStopTime, times(2)).getDepartureTime();

        verify(secondTripLastStopTime, times(3)).getDepartureTime();
        verify(secondTripLastStopTime, times(3)).getDepartureTime();

        verify(thirdTripLastStopTime, times(5)).getDepartureTime();

        final ArgumentCaptor<BlockTripsWithOverlappingStopTimesNotice> captor =
                ArgumentCaptor.forClass(BlockTripsWithOverlappingStopTimesNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<BlockTripsWithOverlappingStopTimesNotice> noticeList = captor.getAllValues();

        assertEquals(1, noticeList.size());
        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("2", noticeList.get(0).getEntityId());
        assertEquals("ERROR", noticeList.get(0).getLevel());
        assertEquals(54, noticeList.get(0).getCode());
        assertEquals("7", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_BLOCK_ID));
        assertEquals("8", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_PREVIOUS_TRIP_ID));
        assertEquals("05:00:00", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_FIRST_TIME));
        assertEquals("10:00:00", noticeList.get(0).getNoticeSpecific(Notice.KEY_TRIP_LAST_TIME));
        assertEquals("09:40:00", noticeList.get(0).getNoticeSpecific(Notice.KEY_PREVIOUS_TRIP_FIRST_TIME));
        assertEquals("13:50:00", noticeList.get(0).getNoticeSpecific(Notice.KEY_PREVIOUS_TRIP_LAST_TIME));
        assertEquals(Set.of("2020-07-23"), noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_DATE_LIST));

        verify(mockTimeUtils, times(1)).convertIntegerToHHMMSS(FIVE_AM_AS_SECS_BEFORE_NOON);
        verify(mockTimeUtils, times(1)).convertIntegerToHHMMSS(TEN_AM_AS_SECS_BEFORE_NOON);
        verify(mockTimeUtils, times(1)).convertIntegerToHHMMSS(NINE_AM_40_MIN_AS_SECS_BEFORE_NOON);
        verify(mockTimeUtils, times(1)).convertIntegerToHHMMSS(1350);
        verify(mockTimeUtils, times(1))
                .arePeriodsOverlapping(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());

        verifyNoMoreInteractions(mockLogger, mockDataRepo, mockResultRepo, firstMockTrip, secondMockTrip,
                thirdMockTrip, firstTripFirstStopTime, firstTripSecondStopTime, firstTripThirdStopTime,
                firstTripLastStopTime, secondTripFirstStopTime, secondTripLastStopTime, thirdTripFirstStopTime,
                thirdTripLastStopTime, firstCalendarDateForServiceA, secondCalendarDateForServiceA,
                calendarDateForServiceB, calendarDateForServiceC, mockTimeUtils);
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
        when(firstMockTrip.hasSameServiceId(any())).thenReturn(true);

        final Trip secondMockTrip = mock(Trip.class);
        when(secondMockTrip.getTripId()).thenReturn("5");
        when(secondMockTrip.getServiceId()).thenReturn("a");
        when(secondMockTrip.getBlockId()).thenReturn("7");
        when(secondMockTrip.hasSameServiceId(any())).thenReturn(true);

        final Trip thirdMockTrip = mock(Trip.class);
        when(thirdMockTrip.getTripId()).thenReturn("8");
        when(thirdMockTrip.getServiceId()).thenReturn("a");
        when(thirdMockTrip.getBlockId()).thenReturn("7");
        when(thirdMockTrip.hasSameServiceId(any())).thenReturn(true);

        mockTripCollection.add(firstMockTrip);
        mockTripCollection.add(secondMockTrip);
        mockTripCollection.add(thirdMockTrip);
        mockTripPerBlockIdCollection.put("7", mockTripCollection);

        final SortedMap<Integer, StopTime> mockFirstTripStopTimeCollection = new TreeMap<>();

        final StopTime firstTripFirstStopTime = mock(StopTime.class);
        when(firstTripFirstStopTime.getArrivalTime()).thenReturn(null);
        when(firstTripFirstStopTime.getDepartureTime()).thenReturn(SEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON);

        final StopTime firstTripLastStopTime = mock(StopTime.class);
        when(firstTripLastStopTime.getArrivalTime()).thenReturn(EIGHT_AM_20_MIN_AS_SECS_BEFORE_NOON);
        when(firstTripLastStopTime.getDepartureTime()).thenReturn(TEN_AM_AS_SECS_BEFORE_NOON);

        mockFirstTripStopTimeCollection.put(10, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(11, firstTripLastStopTime);

        final SortedMap<Integer, StopTime> mockSecondTripStopTimeCollection = new TreeMap<>();

        final StopTime secondTripFirstStopTime = mock(StopTime.class);
        when(secondTripFirstStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_03_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripFirstStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_15_MIN_AS_SECS_BEFORE_NOON);

        final StopTime secondTripLastStopTime = mock(StopTime.class);
        when(secondTripLastStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_16_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripLastStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_20_MIN_AS_SECS_BEFORE_NOON);

        mockSecondTripStopTimeCollection.put(12, secondTripFirstStopTime);
        mockSecondTripStopTimeCollection.put(13, secondTripLastStopTime);

        final SortedMap<Integer, StopTime> mockThirdTripStopTimeCollection = new TreeMap<>();
        final StopTime thirdTripFirstStopTime = mock(StopTime.class);
        when(thirdTripFirstStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON);
        when(thirdTripFirstStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_16_MIN_AS_SECS_BEFORE_NOON);

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

        verify(mockLogger, times(1)).info("validating rule 'E054 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();
        verify(mockDataRepo, times(1)).getCalendarAll();

        verify(firstMockTrip, times(3)).getTripId();

        verify(secondMockTrip, times(3)).getTripId();

        verify(thirdMockTrip, times(3)).getTripId();
        verify(thirdMockTrip, times(1)).hasSameServiceId(secondMockTrip);

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(1)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("8");

        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getArrivalTime();

        verify(secondTripFirstStopTime, times(2)).getArrivalTime();

        verify(thirdTripFirstStopTime, times(4)).getArrivalTime();

        verify(firstTripLastStopTime, times(1)).getDepartureTime();

        verify(secondTripLastStopTime, times(2)).getDepartureTime();

        verify(thirdTripLastStopTime, times(4)).getDepartureTime();

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
        when(firstMockTrip.hasSameServiceId(any())).thenReturn(true);

        final Trip secondMockTrip = mock(Trip.class);
        when(secondMockTrip.getTripId()).thenReturn("5");
        when(secondMockTrip.getServiceId()).thenReturn("a");
        when(secondMockTrip.getBlockId()).thenReturn("7");
        when(secondMockTrip.hasSameServiceId(any())).thenReturn(true);

        final Trip thirdMockTrip = mock(Trip.class);
        when(thirdMockTrip.getTripId()).thenReturn("8");
        when(thirdMockTrip.getServiceId()).thenReturn("a");
        when(thirdMockTrip.getBlockId()).thenReturn("7");
        when(thirdMockTrip.hasSameServiceId(any())).thenReturn(true);

        mockTripCollection.add(firstMockTrip);
        mockTripCollection.add(secondMockTrip);
        mockTripCollection.add(thirdMockTrip);
        mockTripPerBlockIdCollection.put("7", mockTripCollection);

        final SortedMap<Integer, StopTime> mockFirstTripStopTimeCollection = new TreeMap<>();

        final StopTime firstTripFirstStopTime = mock(StopTime.class);
        when(firstTripFirstStopTime.getArrivalTime()).thenReturn(SEVEN_AM_AS_SECS_BEFORE_NOON);
        when(firstTripFirstStopTime.getDepartureTime()).thenReturn(SEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON);

        final StopTime firstTripLastStopTime = mock(StopTime.class);
        when(firstTripLastStopTime.getArrivalTime()).thenReturn(EIGHT_AM_20_MIN_AS_SECS_BEFORE_NOON);
        when(firstTripLastStopTime.getDepartureTime()).thenReturn(null);

        mockFirstTripStopTimeCollection.put(10, firstTripFirstStopTime);
        mockFirstTripStopTimeCollection.put(11, firstTripLastStopTime);

        final SortedMap<Integer, StopTime> mockSecondTripStopTimeCollection = new TreeMap<>();

        final StopTime secondTripFirstStopTime = mock(StopTime.class);
        when(secondTripFirstStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_03_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripFirstStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_15_MIN_AS_SECS_BEFORE_NOON);

        final StopTime secondTripLastStopTime = mock(StopTime.class);
        when(secondTripLastStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_16_MIN_AS_SECS_BEFORE_NOON);
        when(secondTripLastStopTime.getDepartureTime()).thenReturn(ELEVEN_AM_20_MIN_AS_SECS_BEFORE_NOON);

        mockSecondTripStopTimeCollection.put(12, secondTripFirstStopTime);
        mockSecondTripStopTimeCollection.put(13, secondTripLastStopTime);

        final SortedMap<Integer, StopTime> mockThirdTripStopTimeCollection = new TreeMap<>();
        final StopTime thirdTripFirstStopTime = mock(StopTime.class);
        when(thirdTripFirstStopTime.getArrivalTime()).thenReturn(ELEVEN_AM_40_MIN_AS_SECS_BEFORE_NOON);
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

        verify(mockLogger, times(1)).info("validating rule 'E054 - Trips from same block" +
                " overlap'");

        verify(mockDataRepo, times(1)).getAllTripByBlockId();
        verify(mockDataRepo, times(1)).getCalendarAll();

        verify(firstMockTrip, times(3)).getTripId();

        verify(secondMockTrip, times(3)).getTripId();

        verify(thirdMockTrip, times(3)).getTripId();
        verify(thirdMockTrip, times(1)).hasSameServiceId(secondMockTrip);

        verify(mockDataRepo, times(1)).getStopTimeByTripId("2");
        verify(mockDataRepo, times(1)).getStopTimeByTripId("5");
        verify(mockDataRepo, times(2)).getStopTimeByTripId("8");

        verify(firstTripFirstStopTime, times(1)).getArrivalTime();
        verify(firstTripFirstStopTime, times(1)).getArrivalTime();

        verify(thirdTripFirstStopTime, times(4)).getArrivalTime();

        verify(firstTripLastStopTime, times(1)).getDepartureTime();

        verify(secondTripFirstStopTime, times(2)).getArrivalTime();
        verify(secondTripLastStopTime, times(2)).getDepartureTime();
        verify(secondTripLastStopTime, times(2)).getDepartureTime();

        verify(thirdTripLastStopTime, times(4)).getDepartureTime();

        verify(mockTimeUtils, times(1))
                .arePeriodsOverlapping(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockLogger, mockDataRepo, firstMockTrip, secondMockTrip, thirdMockTrip,
                firstTripFirstStopTime, firstTripLastStopTime, secondTripFirstStopTime, secondTripLastStopTime,
                thirdTripFirstStopTime, thirdTripLastStopTime, mockTimeUtils);
    }
}
