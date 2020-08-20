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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.OverlappingTripsInBlockNotice;
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

    public void execute() {
        logger.info("validating rule 'E052 - Trips from same block overlap'");

        final Map<String, List<Trip>> tripPerBlockId = dataRepo.getAllTripByBlockId();

        // for each block id apply the following process:
        tripPerBlockId.forEach((blockId, tripCollectionFromSameBlockId) -> {
            final List<String> visitedTripIdCollection = new ArrayList<>();
            tripCollectionFromSameBlockId.forEach(currentTrip -> {

                final SortedMap<Integer, StopTime> stopTimeCollectionForCurrentTrip =
                        dataRepo.getStopTimeByTripId(currentTrip.getTripId());

                final StopTime currentTripFirstStopTimeInSequence =
                        stopTimeCollectionForCurrentTrip.get(stopTimeCollectionForCurrentTrip.firstKey());
                final StopTime currentTripLastStopTimeInSequence =
                        stopTimeCollectionForCurrentTrip.get(stopTimeCollectionForCurrentTrip.lastKey());
                final Integer currentTripFirstTime = currentTripFirstStopTimeInSequence.getArrivalTime();
                final Integer currentTripLastTime = currentTripLastStopTimeInSequence.getDepartureTime();

                visitedTripIdCollection.add(currentTrip.getTripId());

                tripCollectionFromSameBlockId.forEach(unvisitedTrip -> {
                    if (currentTripFirstTime != null && currentTripLastTime != null) {
                        if (!visitedTripIdCollection.contains(unvisitedTrip.getTripId())) {
                            // if trips have the same service_id
                            // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
                            // |---------|--------|-----------|---------|-----------------|----------------|
                            // | 0       | 2      | a         | 5       | 07:00           | 10:00          |
                            // | 0       | 5      | a         | 5       | 08:45           | 11:20          |
                            // | 0       | 8      | a         | 5       | 11:40           | 13:50          |
                            if (unvisitedTrip.getServiceId().equals(currentTrip.getServiceId())) {
                                // fetch related StopTime for unvisitedTrip
                                final SortedMap<Integer, StopTime> stopTimesForUnvisitedTrip =
                                        dataRepo.getStopTimeByTripId(unvisitedTrip.getTripId());

                                final StopTime unvisitedTripFirstStopTimeInSequence =
                                        stopTimesForUnvisitedTrip.get(stopTimesForUnvisitedTrip.firstKey());
                                final StopTime unvisitedTripLastStopTimeInSequence =
                                        stopTimesForUnvisitedTrip.get(stopTimesForUnvisitedTrip.lastKey());

                                final Integer unvisitedTripFirstTime =
                                        unvisitedTripFirstStopTimeInSequence.getArrivalTime();
                                final Integer unvisitedTripLastTime =
                                        unvisitedTripLastStopTimeInSequence.getDepartureTime();

                                if (unvisitedTripFirstTime != null && unvisitedTripLastTime != null) {

                                    // arrival_time and departure_time are supposed to be ordered
                                    if (currentTripFirstTime <= currentTripLastTime &&
                                            unvisitedTripFirstTime <= unvisitedTripLastTime) {
                                        // if times overlap
                                        if (!(currentTripLastTime <= unvisitedTripFirstTime) ||
                                                !(currentTripFirstTime <= unvisitedTripLastTime)) {
                                            resultRepo.addNotice(new OverlappingTripsInBlockNotice(
                                                    currentTrip.getTripId(),
                                                    currentTrip.getBlockId(),
                                                    currentTripFirstStopTimeInSequence.getStopSequence(),
                                                    currentTripLastStopTimeInSequence.getStopSequence(),
                                                    timeUtils.convertIntegerToHMMSS(currentTripFirstTime),
                                                    timeUtils.convertIntegerToHMMSS(currentTripLastTime),
                                                    unvisitedTrip.getTripId(),
                                                    unvisitedTripFirstStopTimeInSequence.getStopSequence(),
                                                    unvisitedTripLastStopTimeInSequence.getStopSequence(),
                                                    timeUtils.convertIntegerToHMMSS(unvisitedTripFirstTime),
                                                    timeUtils.convertIntegerToHMMSS(unvisitedTripLastTime)
                                            ));
                                        }
                                    }
                                }
                            } else {
                                // if trips have different service_id additional check needs to be done to determine
                                // whether they operate on the same day or noy
                                // This is done by checking files `calendar.txt` and `calendar_dates.txt
                                // Note that both files can be provided, but also only one of these files can be
                                // provided.

                                // | routeId | tripId | serviceId | blockId | first stop time | last stop time |
                                // |---------|--------|-----------|---------|-----------------|----------------|
                                // | 0       | 2      | a         | 5       | 07:00           | 10:00          |
                                // | 0       | 5      | b         | 5       | 08:45           | 11:20          |
                                // | 0       | 8      | c         | 5       | 11:40           | 13:50          |
                                final boolean areCalendarProvided = dataRepo.getCalendarAll() != null;
                                Calendar currentTripCalendar = null;
                                Calendar unvisitedTripCalendar = null;
                                Map<String, CalendarDate> currentTripCalendarDatePossibilityCollection = null;
                                Map<String, CalendarDate> unvisitedTripCalendarDatePossibilityCollection = null;
                                if (areCalendarProvided) {
                                    currentTripCalendar = dataRepo.getCalendarByServiceId(currentTrip.getServiceId());
                                    unvisitedTripCalendar =
                                            dataRepo.getCalendarByServiceId(unvisitedTrip.getServiceId());
                                } else {
                                    currentTripCalendarDatePossibilityCollection =
                                            dataRepo.getCalendarDateAll().get(currentTrip.getServiceId());
                                    unvisitedTripCalendarDatePossibilityCollection =
                                            dataRepo.getCalendarDateAll().get(unvisitedTrip.getServiceId());
                                }
                                // interpret data from `calendar.txt` to determine if currentTrip and unvisitedTrip
                                // operate on same days
                                if (areCalendarProvided) {
                                    // first check if `start_date` and `end_date` overlap for both trips
                                    if (currentTripCalendar.areCalendarOverlapping(unvisitedTripCalendar)) {
                                        // determine the if the trips operate on same days
                                        if (currentTripCalendar.getCalendarsCommonOperationDayCollection(
                                                unvisitedTripCalendar).size() >= 1) {
                                            //
                                            final SortedMap<Integer, StopTime> stopTimesForUnvisitedTrip =
                                                    dataRepo.getStopTimeByTripId(tripCollectionFromSameBlockId.get(0)
                                                            .getTripId());

                                            final StopTime unvisitedTripFirstStopTimeInSequence =
                                                    stopTimesForUnvisitedTrip.get(stopTimesForUnvisitedTrip.firstKey());
                                            final StopTime unvisitedTripFastStopTimeInSequence =
                                                    stopTimesForUnvisitedTrip.get(stopTimesForUnvisitedTrip.lastKey());

                                            final Integer unvisitedTripFirstTime =
                                                    currentTripFirstStopTimeInSequence.getArrivalTime();
                                            final Integer unvisitedTripLastTime =
                                                    currentTripLastStopTimeInSequence.getArrivalTime();

                                            if (unvisitedTripFirstTime != null && unvisitedTripLastTime != null) {

                                                if (currentTripFirstTime <= currentTripLastTime &&
                                                        unvisitedTripFirstTime <= unvisitedTripLastTime) {
                                                    // if times overlap
                                                    if (!(currentTripLastTime <= unvisitedTripFirstTime) ||
                                                            !(unvisitedTripLastTime <= currentTripFirstTime)) {
                                                        resultRepo.addNotice(new OverlappingTripsInBlockNotice(
                                                                currentTrip.getTripId(),
                                                                currentTrip.getBlockId(),
                                                                currentTripFirstStopTimeInSequence.getStopSequence(),
                                                                currentTripLastStopTimeInSequence.getStopSequence(),
                                                                timeUtils.convertIntegerToHMMSS(currentTripFirstTime),
                                                                timeUtils.convertIntegerToHMMSS(currentTripLastTime),
                                                                unvisitedTrip.getTripId(),
                                                                unvisitedTripFirstStopTimeInSequence.getStopSequence(),
                                                                unvisitedTripFastStopTimeInSequence.getStopSequence(),
                                                                timeUtils.convertIntegerToHMMSS(unvisitedTripFirstTime),
                                                                timeUtils.convertIntegerToHMMSS(unvisitedTripLastTime),
                                                                currentTripCalendar
                                                                        .getCalendarsCommonOperationDayCollection(
                                                                                unvisitedTripCalendar)
                                                        ));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // interpret data from `calendar_dates.txt` to determine if currentTrip and
                                    // unvisitedTrip operate on same days
                                    final Set<String> currentTripDatePossibilityCollection = new HashSet<>();
                                    currentTripCalendarDatePossibilityCollection.forEach((date, calendarDate) -> {
                                        if (calendarDate.getExceptionType() == ExceptionType.ADDED_SERVICE) {
                                            currentTripDatePossibilityCollection.add(date);
                                        }
                                    });

                                    final Set<String> unvisitedTripDatePossibilityCollection = new HashSet<>();
                                    unvisitedTripCalendarDatePossibilityCollection.forEach((date, calendarDate) -> {
                                        if (calendarDate.getExceptionType() == ExceptionType.ADDED_SERVICE) {
                                            unvisitedTripDatePossibilityCollection.add(date);
                                        }
                                    });

                                    // get the list of common dates during which trips with different service_id
                                    // operate: this represent the list of dates where there could be potential time
                                    // overlap.

                                    final Set<String> potentialConflictingDates =
                                            Set.copyOf(currentTripDatePossibilityCollection);
                                    potentialConflictingDates.retainAll(unvisitedTripDatePossibilityCollection);

                                    if (potentialConflictingDates.size() >= 1) {
                                        final SortedMap<Integer, StopTime> stopTimesForUnvisitedTrip =
                                                dataRepo.getStopTimeByTripId(tripCollectionFromSameBlockId.get(0)
                                                        .getTripId());

                                        final StopTime unvisitedTripFirstStopTimeInSequence =
                                                stopTimesForUnvisitedTrip.get(stopTimesForUnvisitedTrip.firstKey());
                                        final StopTime unvisitedTripFastStopTimeInSequence =
                                                stopTimesForUnvisitedTrip.get(stopTimesForUnvisitedTrip.lastKey());

                                        final Integer unvisitedTripFirstTime =
                                                currentTripFirstStopTimeInSequence.getArrivalTime();
                                        final Integer unvisitedTripLastTime =
                                                currentTripLastStopTimeInSequence.getArrivalTime();

                                        if (unvisitedTripFirstTime != null && unvisitedTripLastTime != null) {

                                            if (currentTripFirstTime <= currentTripLastTime &&
                                                    unvisitedTripFirstTime <= unvisitedTripLastTime) {
                                                // if times overlap
                                                if (!(currentTripLastTime <= unvisitedTripFirstTime) ||
                                                        !(unvisitedTripLastTime <= currentTripFirstTime)) {
                                                    resultRepo.addNotice(new OverlappingTripsInBlockNotice(
                                                            currentTrip.getTripId(),
                                                            currentTrip.getBlockId(),
                                                            currentTripFirstStopTimeInSequence.getStopSequence(),
                                                            currentTripLastStopTimeInSequence.getStopSequence(),
                                                            timeUtils.convertIntegerToHMMSS(currentTripFirstTime),
                                                            timeUtils.convertIntegerToHMMSS(currentTripLastTime),
                                                            unvisitedTrip.getTripId(),
                                                            unvisitedTripFirstStopTimeInSequence.getStopSequence(),
                                                            unvisitedTripFastStopTimeInSequence.getStopSequence(),
                                                            timeUtils.convertIntegerToHMMSS(unvisitedTripFirstTime),
                                                            timeUtils.convertIntegerToHMMSS(unvisitedTripLastTime),
                                                            currentTripCalendar
                                                                    .getCalendarsCommonOperationDayCollection(
                                                                            unvisitedTripCalendar)
                                                    ));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            });
        });
    }
}
