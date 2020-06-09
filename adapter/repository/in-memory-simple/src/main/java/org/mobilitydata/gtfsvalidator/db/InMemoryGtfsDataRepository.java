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

package org.mobilitydata.gtfsvalidator.db;

import org.jetbrains.annotations.NotNull;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.FeedInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Level;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes.FareAttribute;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This holds an internal representation of gtfs entities: each row of each file from a GTFS dataset is represented here
 */
public class InMemoryGtfsDataRepository implements GtfsDataRepository {
    // Map containing Agency entities. Entities are mapped on the value found in the column agency_id of GTFS file
    // agency.txt
    private final Map<String, Agency> agencyPerId = new HashMap<>();

    // Map containing Route entities. Entities are mapped on the value found in the column route_id of GTFS file
    // routes.txt
    private final Map<String, Route> routePerId = new HashMap<>();

    // Map containing Trip entities. Entities are mapped on the value found in column trip_id of GTFS file trips.txt
    private final Map<String, Trip> tripPerId = new HashMap<>();

    // Map containing Calendar entities. Entities are mapped on the value found in column service_id of GTFS file
    // calendar.txt.
    private final Map<String, Calendar> calendarPerServiceId = new HashMap<>();

    // CalendarDate entities container. Entities are mapped on key resulting from the concatenation of the values
    // contained in the following columns (found in calendar_dates.txt gtfs file):
    // - service_id
    // - date
    private final Map<String, CalendarDate> calendarDatePerServiceIdAndDate = new HashMap<>();

    // Map containing Level entities. Entities are mapped on the value found in column level_id of GTFS file levels.txt
    private final Map<String, Level> levelPerId = new HashMap<>();

    // Map containing FareAttribute entities. Entities are mapped on the value found in column fare_id of gtfs file
    // fare_attributes.txt
    private final Map<String, FareAttribute> fareAttributePerFareId = new HashMap<>();

    // map storing feedInfo entities on key feed_publisher_name found in file feed_info.txt
    private final Map<String, FeedInfo> feedInfoPerFeedPublisherName = new HashMap<>();

    // Map containing Transfer entities. Entities are mapped on a first key which is the value found in the column
    // from_stop_id of GTFS file transfers.txt; the second key is the value found in the column to_stop_id of the same
    // file.
    private final Map<String, Map<String, Transfer>> transferPerStopPair = new HashMap<>();

    /**
     * Add an Agency representing a row from agency.txt to this. Return the entity added to the repository if the
     * uniqueness constraint of agency based on agency_id is respected, if this requirement is not met, returns null.
     *
     * @param newAgency the internal representation of a row from agency.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of agency based on agency_id is
     * respected, if this requirement is not met returns null.
     */
    @Override
    public Agency addAgency(@NotNull final Agency newAgency) throws IllegalArgumentException {
        //noinspection ConstantConditions
        if (newAgency != null) {
            if (agencyPerId.containsKey(newAgency.getAgencyId())) {
                return null;
            } else {
                agencyPerId.put(newAgency.getAgencyId(), newAgency);
                return newAgency;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null agency to data repository");
        }
    }

    /**
     * Return the Agency representing a row from agency.txt related to the id provided as parameter
     *
     * @param agencyId the key from agency.txt related to the Agency to be returned
     * @return the Agency representing a row from agency.txt related to the id provided as parameter
     */
    @Override
    public Agency getAgencyById(final String agencyId) {
        return agencyPerId.get(agencyId);
    }

    /**
     * Add a Route representing a row from routes.txt to this. Return the entity added to the repository if the
     * uniqueness constraint of route based on route_id is respected, if this requirement is not met, returns null.
     *
     * @param newRoute the internal representation of a row from routes.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of route based on route_id is
     * respected, if this requirement is not met returns null.
     */
    @Override
    public Route addRoute(@NotNull final Route newRoute) throws IllegalArgumentException {
        //noinspection ConstantConditions
        if (newRoute != null) {
            if (routePerId.containsKey(newRoute.getRouteId())) {
                return null;
            } else {
                routePerId.put(newRoute.getRouteId(), newRoute);
                return newRoute;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null route to data repository");
        }
    }

    /**
     * Return a collection of Route objects representing all the rows from routes.txt
     *
     * @return a collection of Route objects representing all the rows from routes.txt
     */
    @Override
    public Collection<Route> getRouteAll() {
        return routePerId.values();
    }

    /**
     * Return the Route representing a row from routes.txt related to the id provided as parameter
     *
     * @param routeId the key from routes.txt related to the Route to be returned
     * @return the Route representing a row from routes.txt related to the id provided as parameter
     */
    @Override
    public Route getRouteById(final String routeId) {
        return routePerId.get(routeId);
    }

    /**
     * Add a trip representing a row from trip.txt to this {@link GtfsDataRepository}. Return the entity added to the
     * repository if the uniqueness constraint of trip based on trip_id is respected, if this requirement is not met,
     * returns null.
     *
     * @param newTrip the internal representation of a row from trips.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of trip based on trip_id is
     * respected, if this requirement is not met returns null.
     */
    @Override
    public Trip addTrip(final Trip newTrip) throws IllegalArgumentException {
        if (newTrip != null) {
            if (tripPerId.containsKey(newTrip.getTripId())) {
                return null;
            } else {
                final String tripId = newTrip.getTripId();
                tripPerId.put(tripId, newTrip);
                return newTrip;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null trip to data repository");
        }
    }

    /**
     * Return the Trip representing a row from trips.txt related to the id provided as parameter
     *
     * @param tripId the key from trips.txt related to the Trip to be returned
     * @return the Trip representing a row from trips.txt related to the id provided as parameter
     */
    @Override
    public Trip getTripById(final String tripId) {
        return tripPerId.get(tripId);
    }


    /**
     * Add a CalendarDate representing a row from calendar_dates.txt to this. Return the entity added to the repository
     * if the uniqueness constraint of route based on service_id and date is respected, if this requirement is not met,
     * returns null.
     *
     * @param newCalendarDate the internal representation of a row from calendar_dates.txt to be added to the repository
     * @return the entity added to the repository if the uniqueness constraint of route based on service_id is
     * respected, if this requirement is not met returns null.
     */
    @Override
    public CalendarDate addCalendarDate(@NotNull final CalendarDate newCalendarDate) throws IllegalArgumentException {
        // suppressed warning regarding nullability of parameter newCalendarDate, since it can be null even if it should
        // not be
        //noinspection ConstantConditions
        if (newCalendarDate != null) {
            if (calendarDatePerServiceIdAndDate.containsKey(newCalendarDate.getCalendarDateMappingKey())) {
                return null;
            } else {
                calendarDatePerServiceIdAndDate.put(newCalendarDate.getCalendarDateMappingKey(), newCalendarDate);
                return newCalendarDate;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null calendar date to data repository");
        }
    }

    /**
     * Return the CalendarDate representing a row from calendar_dates.txt related to the id provided as parameter
     *
     * @param serviceId  first part of the composite key used to map rows from calendar_dates.txt
     * @param date       second part of the composite key used to map rows from calendar_dates.txt
     * @return the CalendarDate representing a row from calendar_dates.txt related to the composite key provided as
     * parameter
     */
    @Override
    public CalendarDate getCalendarDateByServiceIdDate(final String serviceId, final LocalDateTime date) {
        return calendarDatePerServiceIdAndDate.get(serviceId + date.toString());
    }

    /**
     * Add a Level representing a row from levels.txt to this {@link GtfsDataRepository}. Return the entity added to the
     * repository if the uniqueness constraint of route based on level_id is respected, if this requirement is not met,
     * returns null.
     *
     * @param newLevel the internal representation of a row from levels.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of route based on level_id is
     * respected, if this requirement is not met returns null.
     */
    @Override
    public Level addLevel(final Level newLevel) throws IllegalArgumentException {
        if (newLevel != null) {
            if (levelPerId.containsKey(newLevel.getLevelId())) {
                return null;
            } else {
                final String levelId = newLevel.getLevelId();
                levelPerId.put(levelId, newLevel);
                return newLevel;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null level to data repository");
        }
    }

    /**
     * Return the Route representing a row from levels.txt related to the id provided as parameter
     *
     * @param levelId the key from levels.txt related to the Level to be returned
     * @return the Level representing a row from levels.txt related to the id provided as parameter
     */
    @Override
    public Level getLevelById(final String levelId) {
        return levelPerId.get(levelId);
    }

    /**
     * Add an Calendar representing a row from calendar.txt to this. Return the entity added to the repository if the
     * uniqueness constraint of agency based on service_id is respected, if this requirement is not met, returns null.
     * This method throws {@code IllegalArgumentException} if the entity is already present in the data repository.
     *
     * @param newCalendar the internal representation of a row from calendar.txt to be added to the repository.
     * @return the entity added to the repository if the
     * uniqueness constraint of agency based on service_id is respected, if this requirement is not met, returns null.
     * @throws IllegalArgumentException if the entity is already present in the data repository.
     */
    @Override
    public Calendar addCalendar(final Calendar newCalendar) throws IllegalArgumentException {
        if (newCalendar != null) {
            if (calendarPerServiceId.containsKey(newCalendar.getServiceId())) {
                return null;
            } else {
                final String serviceId = newCalendar.getServiceId();
                calendarPerServiceId.put(serviceId, newCalendar);
                return newCalendar;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null route to data repository");
        }
    }

    /**
     * Return the entity representing a row from calendar.txt related to the id provided as parameter
     *
     * @param serviceId the key from calendar.txt related to the Calendar to be returned
     * @return the {@link Calendar} representing a row from calendar.txt related to the id provided as parameter
     */
    @Override
    public Calendar getCalendarByServiceId(final String serviceId) {
        return calendarPerServiceId.get(serviceId);
    }

    /**
     * Add a Transfer representing a row from transfers.txt to this {@link GtfsDataRepository}. Return the entity added
     * to the repository if the uniqueness constraint of route based on composite key from_stop_id and to_stop_id is
     * respected, if this requirement is not met, returns null.
     *
     * @param newTransfer the internal representation of a row from transfers.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of route based on composite key
     * from_stop_id and to_stop_id is respected, if this requirement is not met, returns null.
     */
    @Override
    public Transfer addTransfer(final Transfer newTransfer) throws IllegalArgumentException {
        if (newTransfer != null) {
            // check that that from_stop_id is not already in collection. It if is, check that to_stop_id is not in the
            // associated map
            if ((transferPerStopPair.containsKey(newTransfer.getFromStopId())) &&
                    (transferPerStopPair.get(newTransfer.getFromStopId())
                            .containsKey(newTransfer.getToStopId()))) {
                return null;
            } else {
                final String fromStopId = newTransfer.getFromStopId();
                final String toStopId = newTransfer.getToStopId();
                if (!transferPerStopPair.containsKey(newTransfer.getFromStopId())) {
                    final Map<String, Transfer> innerMap = new HashMap<>();
                    innerMap.put(toStopId, newTransfer);
                    transferPerStopPair.put(fromStopId, innerMap);
                } else {
                    transferPerStopPair.get(newTransfer.getFromStopId())
                            .put(newTransfer.getToStopId(), newTransfer);
                }
                return newTransfer;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null transfer to data repository");
        }
    }

    /**
     * Return the Transfer representing a row from transfers.txt related to the composite key provided as parameter.
     *
     * @param fromStopId first part of the composite key: identifies a stop or station where a connection between
     *                   routes begins. Querying on {@param fromStopId}, the method will will only return records
     *                   containing {@param fromStopId} in the from_stop_id column of transfers.txt GTFS file. This
     *                   method will not return any records where {@param fromStopId} is in the to_stop_id column of the
     *                   same pre-mentioned GTFS file.
     *
     * @param toStopId   second part of the composite key: identifies a stop or station where a connection between
     *                   routes ends. Querying on {@param toStopId}, the method will will only return records
     *                   containing {@param toStopId} in the to_stop_id column of transfers.txt GTFS file. This
     *                   method will not return any records where {@param toStopId} is in the from_stop_id column
     *                   of the same pre-mentioned GTFS file.
     * @return the Transfer representing a row from transfers.txt related to the composite key provided as parameter
     */
    @Override
    public Transfer getTransferByStopPair(final String fromStopId, final String toStopId) {
        return transferPerStopPair.get(fromStopId).get(toStopId);
    }

    /**
     * Add a FeedInfo representing a row from feed_info.txt to this {@link GtfsDataRepository}. Return the entity added
     * to the repository if the uniqueness constraint of feed_info based on feed_publisher_name is respected,
     * if this requirement is not met,returns null.
     *
     * @param newFeedInfo the internal representation of a row from feed_info.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of route based on feed_publisher_name is
     * respected, if this requirement is not met returns null.
     */
    @Override
    public FeedInfo addFeedInfo(final FeedInfo newFeedInfo) throws IllegalArgumentException {
        if (newFeedInfo != null) {
            if (feedInfoPerFeedPublisherName.containsKey(newFeedInfo.getFeedPublisherName())) {
                return null;
            } else {
                feedInfoPerFeedPublisherName.put(newFeedInfo.getFeedPublisherName(), newFeedInfo);
                return newFeedInfo;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null feedInfo to data repository");
        }
    }

    /**
     * Return the FeedInfo representing a row from feed_info.txt related to the name provided as parameter
     *
     * @param feedPublisherName the key from feed_info.txt related to the FeedInfo to be returned
     * @return the eedInfo representing a row from feed_info.txt related to the name provided as parameter
     */
    @Override
    public FeedInfo getFeedInfoByFeedPublisherName(final String feedPublisherName) {
        return feedInfoPerFeedPublisherName.get(feedPublisherName);
    }

    /**
     * Add an FareAttribute representing a row from fare_attributes.txt to this {@code GtfsDataRepository}.
     * Return the entity added to the repository if the uniqueness constraint of agency based on fare_id is respected,
     * if this requirement is not met, returns null.
     *
     * @param newFareAttribute the internal representation of a row from fare_attributes.txt to be added to the
     *                         repository.
     * @return the entity added to the repository if the uniqueness constraint of agency based on fare_id is respected,
     * if this requirement is not met returns null.
     * @throws IllegalArgumentException if the argument is null
     */
    @Override
    public FareAttribute addFareAttribute(final FareAttribute newFareAttribute) throws IllegalArgumentException {
        if (newFareAttribute != null) {
            if (fareAttributePerFareId.containsKey(newFareAttribute.getFareId())) {
                return null;
            } else {
                fareAttributePerFareId.put(newFareAttribute.getFareId(), newFareAttribute);
                return newFareAttribute;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null fare attribute to data repository");
        }
    }

    /**
     * Return the FareAttribute representing a row from fare_attributes.txt related to the id provided as parameter
     *
     * @param fareId the key from fare_attributes.txt related to the FareAttribute to be returned
     * @return the FareAttribute representing a row from fare_attributes.txt related to the id provided as parameter
     */
    @Override
    public FareAttribute getFareAttributeById(final String fareId) {
        return fareAttributePerFareId.get(fareId);
    }
}