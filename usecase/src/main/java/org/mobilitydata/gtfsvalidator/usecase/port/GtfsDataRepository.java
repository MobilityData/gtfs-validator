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

package org.mobilitydata.gtfsvalidator.usecase.port;

import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.*;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes.FareAttribute;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;

import java.time.LocalDate;
import java.util.Collection;
import java.util.TreeMap;

public interface GtfsDataRepository {
    Agency addAgency(final Agency newAgency) throws IllegalArgumentException;

    Agency getAgencyById(final String agencyId);

    Route addRoute(final Route newRoute) throws IllegalArgumentException;

    Collection<Route> getRouteAll();

    Route getRouteById(final String routeId);

    CalendarDate addCalendarDate(final CalendarDate newCalendarDate) throws IllegalArgumentException;

    CalendarDate getCalendarDateByServiceIdDate(final String serviceId, final LocalDate date);

    Level addLevel(final Level newLevel) throws IllegalArgumentException;

    Level getLevelById(final String levelId);

    Calendar addCalendar(final Calendar newCalendar) throws IllegalArgumentException;

    Calendar getCalendarByServiceId(final String serviceId);

    Collection<Calendar> getCalendarAll();

    Trip addTrip(final Trip newTrip) throws IllegalArgumentException;

    Trip getTripById(final String tripId);

    Transfer addTransfer(final Transfer newTransfer) throws IllegalArgumentException;

    Transfer getTransferByStopPair(final String fromStopId, final String toStopId);

    FeedInfo addFeedInfo(final FeedInfo newFeedInfo) throws IllegalArgumentException;

    FeedInfo getFeedInfoByFeedPublisherName(final String feedInfoPublisherName);

    FareAttribute addFareAttribute(final FareAttribute newFareAttribute);

    FareAttribute getFareAttributeById(final String fareId);

    FareRule addFareRule(final FareRule newFareRule) throws IllegalArgumentException;

    FareRule getFareRule(final String fareId, final String routeId, final String originId, final String destinationId,
                         final String containsId);

    /**
     * Add a {@link StopTime} representing a row from stop_times.txt to this {@link GtfsDataRepository}.
     * Return the entity added to the repository if the uniqueness constraint on rows from stop_times.txt is respected,
     * if this requirement is not met, returns null. This method adds the {@link StopTime} to this
     * {@link GtfsDataRepository} while maintaining the order according to the value of this {@link StopTime}
     * stop_sequence.
     *
     * @param newStopTime the internal representation of a row from stop_times.txt to be added to the repository.
     * @return Return the entity added to the repository if the uniqueness constraint on rows from stop_times.txt
     * is respected, if this requirement is not met, returns null. This method adds the {@link StopTime} to this
     * {@link GtfsDataRepository} while maintaining the order according to the value of this {@link StopTime}
     * stop_sequence.
     */
    StopTime addStopTime(final StopTime newStopTime) throws IllegalArgumentException;

    /**
     * Return the collection of {@link StopTime} from stop_times.txt related to the trip_id provided as parameter.
     * The returned collection is ordered by stop_sequence
     *
     * @param tripId  identifies a trip
     * @return  the StopTime representing a row from stop_times.txt related to the trip_id provided as parameter
     */
    TreeMap<Integer, StopTime> getStopTimeByTripId(final String tripId);
}
