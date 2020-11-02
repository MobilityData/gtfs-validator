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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.ExceptionType;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.BlockTripsWithOverlappingStopTimesNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.info.UnsupportedGtfsStructureNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;

import java.util.*;

/**
 * Use case to validate that for a given `trip_id` there is no overlapping {@code StopTime}.
 */
public class ValidateNoOverlappingStopTimeInTripBlock {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;
    private final TimeUtils timeUtils;

    public ValidateNoOverlappingStopTimeInTripBlock(final GtfsDataRepository dataRepo,
                                                    final ValidationResultRepository resultRepo,
                                                    final Logger logger,
                                                    final TimeUtils timeUtils) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
        this.timeUtils = timeUtils;
    }

    /**
     * Use case execution method: checks for each trip of the same block that no stop times overlap:
     * - trips from same block with same service id that overlap should generate a notice
     * - trips from same block with same service_id that do not overlap should not generate notice
     * - trips from same block with with different service_id that overlap should generate notice whether calendar or
     * calendar_dates is provided
     * - trips from same block with with different service_id that do not overlap should not generate notice whether
     * calendar or calendar_dates is provided
     * Note that at present, this use case does not consider the corner cases where files `calendar.txt` and
     * `calendar_dates.txt` are both provided in the GTFS archive.
     */
    public void execute() {
        logger.info("validating rule 'E054 - Trips from same block overlap'");

        final Map<String, List<Trip>> tripPerBlockId = dataRepo.getAllTripByBlockId();
        final boolean isCalendarProvided = dataRepo.getCalendarAll().size() != 0;
        tripPerBlockId.forEach((blockId, trips) -> {
            final Map<String, Trip> visitedTripIdCollection = new HashMap();
            trips.forEach(currentTrip -> {
                final String currentTripId = currentTrip.getTripId();
                final SortedMap<Integer, StopTime> tripStopTimes = dataRepo.getStopTimeByTripId(currentTripId);

                final StopTime tripFirstStopTime = tripStopTimes.get(tripStopTimes.firstKey());
                final StopTime tripLastStopTime = tripStopTimes.get(tripStopTimes.lastKey());
                final Integer tripFirstTime = tripFirstStopTime.getArrivalTime();
                final Integer tripLastTime = tripLastStopTime.getDepartureTime();
                visitedTripIdCollection.put(currentTripId, currentTrip);
                if (tripFirstTime == null || tripLastTime == null) {
                    return;
                }
                if (!areTripTimesValid(tripFirstStopTime, tripLastStopTime)) {
                    return;
                }
                trips.forEach(unvisitedTrip -> {
                    final String unvisitedTripId = unvisitedTrip.getTripId();
                    if (visitedTripIdCollection.containsKey(unvisitedTripId)) {
                        return;
                    }
                    // if trips have the same service_id
                    // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
                    // |---------|--------|-----------|---------|-----------------|----------------|
                    // | 0       | 2      | a         | 5       | 07:00           | 10:00          |
                    // | 0       | 5      | a         | 5       | 08:45           | 11:20          |
                    // | 0       | 8      | a         | 5       | 11:40           | 13:50          |

                    // fetch related StopTime for unvisitedTrip
                    final SortedMap<Integer, StopTime> unvisitedTripStopTimes =
                            dataRepo.getStopTimeByTripId(unvisitedTripId);
                    final StopTime unvisitedTripFirstStopTime =
                            unvisitedTripStopTimes.get(unvisitedTripStopTimes.firstKey());
                    final StopTime unvisitedTripLastStopTime =
                            unvisitedTripStopTimes.get(unvisitedTripStopTimes.lastKey());

                    final Integer unvisitedTripFirstTime = unvisitedTripFirstStopTime.getArrivalTime();
                    final Integer unvisitedTripLastTime = unvisitedTripLastStopTime.getDepartureTime();
                    if (unvisitedTrip.hasSameServiceId(currentTrip)) {
                        checkOverlappingForTripsWithSameServiceId(
                                blockId,
                                currentTripId,
                                unvisitedTripId,
                                tripFirstTime,
                                tripLastTime,
                                unvisitedTripFirstTime,
                                unvisitedTripLastTime,
                                unvisitedTripFirstStopTime,
                                unvisitedTripLastStopTime);
                    } else {
                        // if trips have different service_id additional check needs to be done to determine
                        // whether they operate on the same day or not
                        // This is done by checking files `calendar.txt` and `calendar_dates.txt

                        // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
                        // |---------|--------|-----------|---------|-----------------|----------------|
                        // | 0       | 2      | a         | 5       | 07:00           | 10:00          |
                        // | 0       | 5      | b         | 5       | 08:45           | 11:20          |
                        // | 0       | 8      | c         | 5       | 11:40           | 13:50          |
                        if (isCalendarProvided) {
                            checkOverlappingTripsDifferentServiceIdCalendarProvided(
                                    blockId,
                                    currentTrip,
                                    currentTripId,
                                    unvisitedTrip,
                                    unvisitedTripId,
                                    tripFirstTime,
                                    tripLastTime,
                                    unvisitedTripFirstTime,
                                    unvisitedTripLastTime,
                                    unvisitedTripFirstStopTime,
                                    unvisitedTripLastStopTime);
                        } else {
                            checkOverlappingTripsDifferentServiceIdCalendarNotProvided(
                                    blockId,
                                    currentTrip,
                                    currentTripId,
                                    unvisitedTrip,
                                    unvisitedTripId,
                                    tripFirstTime,
                                    tripLastTime,
                                    unvisitedTripFirstTime,
                                    unvisitedTripLastTime,
                                    unvisitedTripFirstStopTime,
                                    unvisitedTripLastStopTime);
                        }
                    }
                });
            });
        });
    }

    /**
     * Determines if {@code Trip} times are valid. Returns true if
     * tripFirstStopTime.arrivalTime < tripLastStopTime.departureTime, otherwise returns false
     *
     * @param tripFirstStopTime first {@link StopTime} of trip
     * @param tripLastStopTime  last {@link StopTime} of trip
     * @return true if {@param tripFirstStopTime} arrival time is before {@param tripLastStopTime} departure time,
     * otherwise returns false.
     */
    private boolean areTripTimesValid(final StopTime tripFirstStopTime, final StopTime tripLastStopTime) {
        final Integer arrivalTime = tripFirstStopTime.getArrivalTime();
        final Integer departureTime = tripLastStopTime.getDepartureTime();
        if (arrivalTime != null & departureTime != null) {
            return arrivalTime < departureTime;
        } else {
            return false;
        }
    }

    /**
     * Determines if trips with same service_od are overlapping. A notice is generated and added to the
     * {@link ValidationResultRepository} provided in the constructor each time trips with same service id overlap
     * in time.
     *
     * @param blockId                id of the block
     * @param tripId                 id of {@link Trip}
     * @param otherTripId            other {@link Trip} id
     * @param tripFirstTime          {@link Trip} first time
     * @param tripLastTime           {@link Trip} last time
     * @param otherTripFirstTime     other {@link Trip} first time
     * @param otherTripLastTime      other {@link Trip} last time
     * @param otherTripFirstStopTime other {@link Trip} first {@link StopTime}
     * @param otherTripLastStopTime  other {@link Trip} last {@link StopTime}
     */
    private void checkOverlappingForTripsWithSameServiceId(final String blockId,
                                                           final String tripId,
                                                           final String otherTripId,
                                                           final int tripFirstTime,
                                                           final int tripLastTime,
                                                           final Integer otherTripFirstTime,
                                                           final Integer otherTripLastTime,
                                                           final StopTime otherTripFirstStopTime,
                                                           final StopTime otherTripLastStopTime) {
        // stop_times arrival_time and departure_time should not be null to perform the
        // subsequent operations
        if (otherTripFirstTime == null || otherTripLastTime == null) {
            return;
        }
        // stop_times arrival_time and departure_time are supposed to be ordered
        if (!areTripTimesValid(otherTripFirstStopTime, otherTripLastStopTime)) {
            return;
        }
        // if times overlap
        if (!timeUtils.arePeriodsOverlapping(tripFirstTime, tripLastTime,
                otherTripFirstTime, otherTripLastTime)) {
            return;
        }
        resultRepo.addNotice(new BlockTripsWithOverlappingStopTimesNotice(
                tripId,
                timeUtils.convertIntegerToHHMMSS(tripFirstTime),
                timeUtils.convertIntegerToHHMMSS(tripLastTime),
                otherTripId,
                timeUtils.convertIntegerToHHMMSS(otherTripFirstTime),
                timeUtils.convertIntegerToHHMMSS(otherTripLastTime),
                blockId));
    }

    /**
     * Determines if trips are overlapping when trips do not have the same service_id and file `calendar.txt` is provided
     * in the GTFS archive. A notice is generate and added to the {@link ValidationResultRepository} provided in the
     * constructor each time said trips overlap in time.
     *
     * @param blockId                id of the block
     * @param trip                   first {@link Trip} to analyze
     * @param tripId                 id of {@link Trip}
     * @param otherTrip              other {@link Trip} to analyze
     * @param otherTripId            other {@link Trip} id
     * @param tripFirstTime          {@link Trip} first time
     * @param tripLastTime           {@link Trip} last time
     * @param otherTripFirstTime     other {@link Trip} first time
     * @param otherTripLastTime      other {@link Trip} last time
     * @param otherTripFirstStopTime other {@link Trip} first {@link StopTime}
     * @param otherTripLastStopTime  other {@link Trip} last {@link StopTime}
     */
    private void checkOverlappingTripsDifferentServiceIdCalendarProvided(final String blockId,
                                                                         final Trip trip,
                                                                         final String tripId,
                                                                         final Trip otherTrip,
                                                                         final String otherTripId,
                                                                         final int tripFirstTime,
                                                                         final int tripLastTime,
                                                                         final Integer otherTripFirstTime,
                                                                         final Integer otherTripLastTime,
                                                                         final StopTime otherTripFirstStopTime,
                                                                         final StopTime otherTripLastStopTime) {
        // interpret data from `calendar.txt` to determine if currentTrip and otherTrip
        // operate on same days
        Calendar tripCalendar = dataRepo.getCalendarByServiceId(trip.getServiceId());
        Calendar unvisitedTripCalendar = dataRepo.getCalendarByServiceId(otherTrip.getServiceId());
        if (tripCalendar == null || unvisitedTripCalendar == null) {
            resultRepo.addNotice(
                    new UnsupportedGtfsStructureNotice(
                            tripId,
                            otherTripId,
                            trip.getServiceId(),
                            otherTrip.getServiceId()
                    ));
            return;
        }
        // first check if `start_date` and `end_date` overlap for both trips
        if (!tripCalendar.isOverlapping(unvisitedTripCalendar)) {
            return;
        }
        // determine the if the trips operate on same days
        if (tripCalendar.getOverlappingDays(unvisitedTripCalendar).size() == 0) {
            return;
        }
        if (otherTripFirstTime == null || otherTripLastTime == null) {
            return;
        }
        if (!areTripTimesValid(otherTripFirstStopTime, otherTripLastStopTime)) {
            return;
        }
        if (!timeUtils.arePeriodsOverlapping(tripFirstTime, tripLastTime,
                otherTripFirstTime, otherTripLastTime)) {
            return;
        }
        // if times overlap
        resultRepo.addNotice(
                new BlockTripsWithOverlappingStopTimesNotice(
                        tripId,
                        timeUtils.convertIntegerToHHMMSS(tripFirstTime),
                        timeUtils.convertIntegerToHHMMSS(tripLastTime),
                        otherTripId,
                        timeUtils.convertIntegerToHHMMSS(otherTripFirstTime),
                        timeUtils.convertIntegerToHHMMSS(otherTripLastTime),
                        blockId,
                        tripCalendar.getOverlappingDays(unvisitedTripCalendar)));
    }

    /**
     * Determines if trips are overlapping when trips do not have the same service_id and file `calendar.txt` is not
     * provided in the GTFS archive. A notice is generate and added to the {@link ValidationResultRepository} provided
     * in the constructor each time said trips overlap in time.
     *
     * @param blockId                id of the block
     * @param trip                   first {@link Trip} to analyze
     * @param tripId                 id of {@link Trip}
     * @param otherTrip              other {@link Trip} to analyze
     * @param otherTripId            other {@link Trip} id
     * @param tripFirstTime          {@link Trip} first time
     * @param tripLastTime           {@link Trip} last time
     * @param otherTripFirstTime     other {@link Trip} first time
     * @param otherTripLastTime      other {@link Trip} last time
     * @param otherTripFirstStopTime other {@link Trip} first {@link StopTime}
     * @param otherTripLastStopTime  other {@link Trip} last {@link StopTime}
     */
    private void checkOverlappingTripsDifferentServiceIdCalendarNotProvided(final String blockId,
                                                                            final Trip trip,
                                                                            final String tripId,
                                                                            final Trip otherTrip,
                                                                            final String otherTripId,
                                                                            final int tripFirstTime,
                                                                            final int tripLastTime,
                                                                            final Integer otherTripFirstTime,
                                                                            final Integer otherTripLastTime,
                                                                            final StopTime otherTripFirstStopTime,
                                                                            final StopTime otherTripLastStopTime) {
        // interpret data from `calendar_dates.txt` to determine if trip and otherTrip operate on same days
        final Set<String> tripCalendarDateCollection = new HashSet<>();
        dataRepo.getCalendarDateAll().get(trip.getServiceId())
                .forEach((date, calendarDate) -> tripCalendarDateCollection.add(date));
        final Set<CalendarDate> unvisitedTripCalendarDateCollection = new HashSet<>();
        dataRepo.getCalendarDateAll().get(otherTrip.getServiceId()).forEach((date, calendarDate) ->
                unvisitedTripCalendarDateCollection.add(calendarDate));
        // get the list of common dates during which trips with different service_id operate: this represent the list of
        // dates where there could be potential time overlap.
        final Set<String> potentialConflictingDates = new HashSet<>();
        unvisitedTripCalendarDateCollection.forEach(calendarDate -> {
            if (calendarDate.getExceptionType() == ExceptionType.ADDED_SERVICE) {
                if (tripCalendarDateCollection.contains(calendarDate.getDate().toString())) {
                    potentialConflictingDates.add(calendarDate.getDate().toString());
                }
            }
        });
        if (potentialConflictingDates.size() == 0) {
            return;
        }
        if (otherTripFirstTime == null || otherTripLastTime == null) {
            return;
        }
        if (!areTripTimesValid(otherTripFirstStopTime, otherTripLastStopTime)) {
            return;
        }
        if (!timeUtils.arePeriodsOverlapping(tripFirstTime, tripLastTime, otherTripFirstTime, otherTripLastTime)) {
            return;
        }
        // if times overlap
        resultRepo.addNotice(
                new BlockTripsWithOverlappingStopTimesNotice(
                        tripId,
                        timeUtils.convertIntegerToHHMMSS(tripFirstTime),
                        timeUtils.convertIntegerToHHMMSS(tripLastTime),
                        otherTripId,
                        timeUtils.convertIntegerToHHMMSS(otherTripFirstTime),
                        timeUtils.convertIntegerToHHMMSS(otherTripLastTime),
                        blockId,
                        potentialConflictingDates)
        );
    }
}
