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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.*;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes.FareAttribute;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies.Frequency;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways.Pathway;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.LocationBase;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.Translation;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.util.*;

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

    // Map containing Trip entities. Entities are mapped on the value found in column block_id of GTFS file trips.txt
    private final Map<String, List<Trip>> tripPerBlockId = new HashMap<>();

    // Map containing Calendar entities. Entities are mapped on the value found in column service_id of GTFS file
    // calendar.txt.
    private final Map<String, Calendar> calendarPerServiceId = new HashMap<>();

    // CalendarDate entities container. The key for the outer map is the calendar_dates.txt service_id, with the key for
    // the inner map being the calendar_dates.txt date field
    private final Map<String, Map<String, CalendarDate>> calendarDatePerServiceIdAndDate = new HashMap<>();

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

    // Map containing FareRule entities. Entities are mapped on a composite key made of the values found in the
    // columns of GTFS file fare_rules.txt:
    // - fare_id
    // - route_id
    // - origin_id
    // - destination_id
    // - contains_id
    // Example of key after composition: fare_idroute_idorigin_iddestination_idcontains_id
    private final Map<String, FareRule> fareRuleCollection = new HashMap<>();

    // Map containing Frequency entities. Entities are mapped on a composite key made of the values found in the
    // columns of GTFS file frequencies.txt:
    // - trip_id
    // - start_time
    // Example of key after composition: trip_idstart_time
    private final Map<String, Frequency> frequencyPerTripIdStartTime = new HashMap<>();

    // Map containing Frequency entities. Entities are mapped on trip_id value. For each trip_id, Frequency entities are
    // stored based on their trip_id value.
    private final Map<String, List<Frequency>> frequencyPerTripId = new HashMap<>();

    // Map containing Pathway entities. Entities are mapped on the value found in column pathway_id of GTFS file
    // pathways.txt
    private final Map<String, Pathway> pathwayPerId = new HashMap<>();

    // Map containing Attribution entities. Entities are mapped on a composite key made of the values found in the
    // columns of GTFS file attributions.txt:
    // - attribution_id
    // - agency_id
    // - route_id
    // - trip_id
    // - organization_name
    // - is_producer
    // - is_operator
    // - is_authority
    // - attribution_url
    // - attribution_email
    // - attribution_phone
    // Example of key after composition: attribution_idagency_idroute_idtrip_idorganization_nameis_produceris_operatoris_authorityattribution_urlattribution_emailattribution_phone
    private final Map<String, Attribution> attributionCollection = new HashMap<>();

    // Map containing Shape Entities. A shape is a actually a collection of ShapePoint.
    // Entities are mapped on the values found in column shape_id  and shape_pt_sequence of GTFS file shapes.txt
    private final Map<String, SortedMap<Integer, ShapePoint>> shapePerIdShapePtSequence = new HashMap<>();

    // Map containing StopTime entities. Entities are mapped on a composite key made of the values found in the columns
    // of GTFS file stop_times.txt:
    // - trip_id
    // - stop_sequence
    private final Map<String, TreeMap<Integer, StopTime>> stopTimePerTripIdStopSequence = new HashMap<>();

    // Map containing Translation entities. Entities are mapped on the value found in column table_name, field_value and
    // language of GTFS file translations.txt.
    private final Map<String, Map<String, Map<String, Translation>>> translationPerTableName = new HashMap<>();

    // Map containing Stop entities. Entities are mapped on the value found in column stop_id of GTFS file stops.txt
    private final Map<String, LocationBase> stopPerId = new HashMap<>();

    /**
     * Add an Agency representing a row from agency.txt to this. Return the entity added to the repository if the
     * uniqueness constraint of agency based on agency_id is respected, if this requirement is not met, returns null.
     *
     * @param newAgency the internal representation of a row from agency.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of agency based on agency_id is
     * respected, if this requirement is not met returns null.
     */
    @Override
    public Agency addAgency(@NotNull final Agency newAgency, final Agency.AgencyBuilder builder)
            throws IllegalArgumentException {
        // todo: implement early fail when adding agency with agency_id already in data repository
        //noinspection ConstantConditions
        if (newAgency == null) {
            throw new IllegalArgumentException("Cannot add null agency to data repository");
        } else {
            final String agencyId = newAgency.getAgencyId();
            if (agencyId == null) {
                if (getAgencyCount() > 1) {
                    final Agency replacementAgency = (Agency) builder
                            .clear()
                            .agencyId(Agency.AgencyBuilder.DEFAULT_AGENCY_ID)
                            .agencyEmail(newAgency.getAgencyEmail())
                            .agencyFareUrl(newAgency.getAgencyUrl())
                            .agencyLang(newAgency.getAgencyLang())
                            .agencyName(newAgency.getAgencyName())
                            .agencyPhone(newAgency.getAgencyPhone())
                            .agencyUrl(newAgency.getAgencyUrl())
                            .build()
                            .getData();
                    return addAgency(replacementAgency, builder);
                } else {
                    agencyPerId.put(agencyId, newAgency);
                    return newAgency;
                }
            } else {
                if (agencyPerId.containsKey(agencyId)) {
                    return null;
                } else {
                    agencyPerId.put(agencyId, newAgency);
                    return newAgency;
                }
            }
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
     * Return an immutable map of Agency objects representing all the rows from agency.txt. Entities are mapped on field
     * `agency_id` of file `agency.txt`. Note that if a record from `
     *
     * @return a immutable map of Agency objects representing all the rows from agency.txt
     */
    @Override
    public Map<String, Agency> getAgencyAll() {
        return Collections.unmodifiableMap(agencyPerId);
    }

    /**
     * Return the number of {@link Agency} contained in this {@link GtfsDataRepository}
     *
     * @return the number of {@link Agency} contained in this {@link GtfsDataRepository}
     */
    @Override
    public int getAgencyCount() {
        return agencyPerId.size();
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
     * Return an unmodifiable map of Route objects representing all the rows from routes.txt. Entities are mapped on
     * value of field route_id of file `routes.txt`.
     *
     * @return an unmodifiable map of Route objects representing all the rows from routes.txt. Entities are mapped on
     * value of field route_id of file `routes.txt`.
     */
    @Override
    public Map<String, Route> getRouteAll() {
        return Collections.unmodifiableMap(routePerId);
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
                final String blockId = newTrip.getBlockId();
                if (blockId != null) {
                    if (tripPerBlockId.containsKey(blockId)) {
                        tripPerBlockId.get(blockId).add(newTrip);
                    } else {
                        final List<Trip> tripCollection = new ArrayList<>();
                        tripCollection.add(newTrip);
                        tripPerBlockId.put(blockId, tripCollection);
                    }
                }
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
     * Return an immutable map of Trip objects representing all the rows from trips.txt. Entities are mapped on trip_id
     * of file `trips.txt`.
     *
     * @return an immutable map of Trip objects representing all the rows from trips.txt. Entities are mapped on trip_id
     * of file `trips.txt`
     */
    @Override
    public Map<String, Trip> getTripAll() {
        return Collections.unmodifiableMap(tripPerId);
    }

    /**
     * Return an immutable map of {@link Trip} grouped by blockId value
     *
     * @return an immutable map of {@link Trip} grouped by blockId value
     */
    @Override
    public Map<String, List<Trip>> getAllTripByBlockId() {
        return Collections.unmodifiableMap(tripPerBlockId);
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
            final String serviceId = newCalendarDate.getServiceId();
            final String dateAsString = newCalendarDate.getDate().toString();
            if (calendarDatePerServiceIdAndDate.containsKey(serviceId)) {
                if (calendarDatePerServiceIdAndDate.get(serviceId).containsKey(dateAsString)) {
                    return null;
                } else {
                    calendarDatePerServiceIdAndDate.get(serviceId).put(dateAsString, newCalendarDate);
                    return newCalendarDate;
                }
            } else {
                final Map<String, CalendarDate> innerMap = new HashMap<>();
                innerMap.put(dateAsString, newCalendarDate);
                calendarDatePerServiceIdAndDate.put(serviceId, innerMap);
                return newCalendarDate;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null calendar date to data repository");
        }
    }

    /**
     * Return an immutable collection of {@code CalendarDate} objects representing all the rows from calendar_dates.txt.
     * Entities are mapped on service_id and date in a nested map.
     *
     * @return a immutable collection of {@code CalendarDate} objects representing all the rows from calendar_dates.txt.
     * Entities are mapped on service_id and date in a nested map.
     */
    public Map<String, Map<String, CalendarDate>> getCalendarDateAll() {
        return Collections.unmodifiableMap(calendarDatePerServiceIdAndDate);
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
     * Return an unmodifiable collection of Calendar objects representing all the rows from calendar.txt
     *
     * @return an unmodifiable collection of Calendar objects representing all the rows from calendar.txt
     */
    @Override
    public Map<String, Calendar> getCalendarAll() {
        return Collections.unmodifiableMap(calendarPerServiceId);
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
     *                   routes begins. Querying on {fromStopId}, the method will will only return records
     *                   containing {fromStopId} in the from_stop_id column of transfers.txt GTFS file. This
     *                   method will not return any records where {fromStopId} is in the to_stop_id column of the
     *                   same pre-mentioned GTFS file.
     * @param toStopId   second part of the composite key: identifies a stop or station where a connection between
     *                   routes ends. Querying on {toStopId}, the method will will only return records
     *                   containing {toStopId} in the to_stop_id column of transfers.txt GTFS file. This
     *                   method will not return any records where {toStopId} is in the from_stop_id column
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
     * @return the feedInfo representing a row from feed_info.txt related to the name provided as parameter
     */
    @Override
    public FeedInfo getFeedInfoByFeedPublisherName(final String feedPublisherName) {
        return feedInfoPerFeedPublisherName.get(feedPublisherName);
    }

    /**
     * Returns an unmodifiable  collection of {@link FeedInfo} entities
     *
     * @return an unmodifiable  collection of {@link FeedInfo} entities
     */
    @Override
    public Map<String, FeedInfo> getFeedInfoAll() {
        return Collections.unmodifiableMap(feedInfoPerFeedPublisherName);
    }

    /**
     * Add an FareAttribute representing a row from fare_attributes.txt to this {@code GtfsDataRepository}.
     * Return the entity added to the repository if the uniqueness constraint of agency based on fare_id is respected,
     * if this requirement is not met, returns null.
     *
     * @param newFareAttribute the internal representation of a row from fare_attributes.txt to be added to the
     *                         repository.
     * @return the entity added to the repository if the uniqueness constraint of fare_attribute based on fare_id is
     * respected, if this requirement is not met returns null.
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

    /**
     * Add a FareRule representing a row from fare_rules.txt to this {@link GtfsDataRepository}.
     * Return the entity added to the repository if the uniqueness constraint on rows from fare_rules.txt is respected,
     * if this requirement is not met, returns null.
     *
     * @param newFareRule the internal representation of a row from fare_rules.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint on rows from fare_rules.txt
     * is respected, if this requirement is not met, returns null.
     */
    @Override
    public FareRule addFareRule(final FareRule newFareRule) throws IllegalArgumentException {
        if (newFareRule != null) {
            final String key = newFareRule.getFareRuleMappingKey();
            if (fareRuleCollection.containsKey(key)) {
                return null;
            } else {
                fareRuleCollection.put(key, newFareRule);
                return newFareRule;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null FareRule to data repository");
        }
    }

    /**
     * Return the FareRule representing a row from fare_rules.txt related to the id provided as parameter
     *
     * @param fareId        1st part of the composite key: identifies a fare class
     * @param routeId       2nd part of the composite key: identifies a route associated with the fare class
     * @param originId      3rd part of the composite key: identifies an origin zone
     * @param destinationId 4th part of the composite key: identifies a destination zone
     * @param containsId    5th part ot the composite key: identifies the zones that a rider will enter while using a
     *                      given fare class
     * @return the FareRule representing a row from fare_rules.txt related to the id provided as parameter
     */
    @Override
    public FareRule getFareRule(final String fareId, final String routeId, final String originId,
                                final String destinationId, final String containsId) {
        return fareRuleCollection.get(FareRule.getFareRuleMappingKey(fareId, routeId, originId, destinationId,
                containsId));
    }

    /**
     * Add a Frequency representing a row from frequencies.txt to this {@code GtfsDataRepository}.
     * Return the entity added to the repository if the uniqueness constraint on rows from frequencies.txt
     * is respected, if this requirement is not met, returns null.
     *
     * @param newFrequency the internal representation of a row from frequencies.txt to be added to the
     *                     repository.
     * @return the entity added to the repository if the uniqueness constraint of frequency based on rows from
     * frequencies.txt is respected, if this requirement is not met returns null.
     * @throws IllegalArgumentException if the argument is null
     */
    @Override
    public Frequency addFrequency(final Frequency newFrequency) throws IllegalArgumentException {
        if (newFrequency != null) {
            final String key = newFrequency.getFrequencyMappingKey();
            if (frequencyPerTripIdStartTime.containsKey(key)) {
                return null;
            } else {
                final String tripId = newFrequency.getTripId();
                frequencyPerTripIdStartTime.put(key, newFrequency);
                if (frequencyPerTripId.containsKey(tripId)) {
                    frequencyPerTripId.get(tripId).add(newFrequency);
                } else {
                    final List<Frequency> frequencyCollection = new ArrayList<>();
                    frequencyCollection.add(newFrequency);
                    frequencyPerTripId.put(tripId, frequencyCollection);
                }
                return newFrequency;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null frequency to data repository");
        }
    }

    /**
     * Return the Frequency representing a row from frequencies.txt related to the id provided as parameter
     *
     * @param tripId    1st part of the composite key: identifies a fare class
     * @param startTime 2nd part of the composite key: identifies a route associated with the fare class
     * @return the Frequency representing a row from frequencies.txt related to the id provided as parameter
     */
    @Override
    public Frequency getFrequency(final String tripId, final Integer startTime) {
        return frequencyPerTripIdStartTime.get(Frequency.getFrequencyMappingKey(tripId, startTime));
    }

    /**
     * Return an unmodifiable map of Frequency objects from `frequencies.txt`. Entities are mapped on value of trip_id
     * of file `frequencies.txt`. Each trip_id may include multiple {@link Frequency} entries, therefore, a list of
     * these entries is returned.
     *
     * @return an unmodifiable map of Frequency objects from `frequencies.txt`. Entities are mapped on value of trip_id
     * of `frequencies.txt`. Each trip_id may include multiple {@link Frequency} entries, therefore, a list of these
     * entries is returned.
     */
    @Override
    public Map<String, List<Frequency>> getFrequencyAllByTripId() {
        return Collections.unmodifiableMap(frequencyPerTripId);
    }

    /**
     * Return an unmodifiable map of Frequency objects representing all the rows from frequencies.txt. Entities are
     * mapped on values of fields trip_id and start_time of file `frequencies.txt`. This composite key is generated via
     * method @see #Frequency#getFrequencyMappingKey().
     *
     * @return an unmodifiable map of Frequency objects representing all the rows from frequencies.txt. Entities are
     * mapped on values of fields trip_id and start_time of file `frequencies.txt`. This composite key is generated via
     * method @see #Frequency#getFrequencyMappingKey()
     */
    public Map<String, Frequency> getFrequencyAll() {
        return Collections.unmodifiableMap(frequencyPerTripIdStartTime);
    }

    /**
     * Add a Pathway representing a row from pathways.txt to this {@link GtfsDataRepository}.
     * Return the entity added to the repository if the uniqueness constraint of route based on pathway_id is respected,
     * if this requirement is not met, returns null.
     *
     * @param newPathway the internal representation of a row from routes.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of route based on pathway_id is
     * respected, if this requirement is not met returns null.
     */
    @Override
    public Pathway addPathway(final Pathway newPathway) throws IllegalArgumentException {
        if (newPathway != null) {
            final String pathwayId = newPathway.getPathwayId();
            if (pathwayPerId.containsKey(pathwayId)) {
                return null;
            } else {
                pathwayPerId.put(pathwayId, newPathway);
                return newPathway;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null pathway to data repository");
        }
    }

    /**
     * Return the Pathway representing a row from pathways.txt related to the id provided as parameter
     *
     * @param pathwayId the key from pathways.txt related to the Pathway to be returned
     * @return the Pathway representing a row from pathways.txt related to the id provided as parameter
     */
    @Override
    public Pathway getPathwayById(final String pathwayId) {
        return pathwayPerId.get(pathwayId);
    }

    /**
     * Add an Attribution representing a row from attributions.txt to this. Return the entity added to the repository if
     * the uniqueness constraint of rows f attributions.txt is respected, if this requirement is not met, returns null.
     *
     * @param newAttribution the internal representation of a row from attributions.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of rows f attributions.txt is respected,
     * if this requirement is not met, returns null.
     */
    @Override
    public Attribution addAttribution(final Attribution newAttribution) throws IllegalArgumentException {
        if (newAttribution != null) {
            final String key = newAttribution.getAttributionMappingKey();
            if (attributionCollection.containsKey(key)) {
                return null;
            } else {
                attributionCollection.put(key, newAttribution);
                return newAttribution;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null attribution to data repository");
        }
    }

    /**
     * Return the Attribution representing a row from attributions.txt related to the composite key provided as
     * parameter
     *
     * @param attributionId    identifies an attribution for the dataset or a subset of it
     * @param agencyId         agency to which the attribution applies
     * @param routeId          route to which the attribution applies
     * @param tripId           trip to which the attribution applies
     * @param organizationName name of the organization that the dataset is attributed to
     * @param isProducer       the role of the organization if producer
     * @param isOperator       the role of the organization if operator
     * @param isAuthority      the role of the organization if authority
     * @param attributionUrl   URL of the organization
     * @param attributionEmail email of the organization
     * @param attributionPhone phone number of the organization
     * @return the Attribution representing a row from attributions.txt related to the composite key provided as
     * parameter
     */
    @Override
    public Attribution getAttribution(final String attributionId, final String agencyId, final String routeId,
                                      final String tripId, final String organizationName, final boolean isProducer,
                                      final boolean isOperator, final boolean isAuthority, final String attributionUrl,
                                      final String attributionEmail, final String attributionPhone) {
        return attributionCollection.get(Attribution.getAttributionMappingKey(attributionId, agencyId, routeId, tripId,
                organizationName, isProducer, isOperator, isAuthority, attributionUrl, attributionEmail,
                attributionPhone));
    }

    /**
     * Add a {@link ShapePoint} to a shape. A shape is a list of{@link ShapePoint} whereas a {@link ShapePoint}
     * represents a row from shapes.txt. Return the entity added to the repository if the entity was
     * successfully added, and returns null if the provided newShapePoint already exists in the repository. This method
     * adds the {@link ShapePoint} to this {@link GtfsDataRepository} while maintaining the order according to the
     * value of this {@link ShapePoint} shape_pt_sequence.
     *
     * @param newShapePoint the internal representation of a row from shapes.txt to be added to the repository.
     * @return Return the entity added to the repository if the entity was successfully added, and returns null if the
     * provided newShapePoint already exists in the repository.  This method adds the {@link ShapePoint} to this
     * {@link GtfsDataRepository} while maintaining the order according to the value of this {@link ShapePoint}
     * shape_pt_sequence.
     * @throws IllegalArgumentException if the shape point passed as argument is null
     */
    @Override
    public ShapePoint addShapePoint(final ShapePoint newShapePoint) throws IllegalArgumentException {
        if (newShapePoint != null) {
            final String shapeId = newShapePoint.getShapeId();
            if (shapePerIdShapePtSequence.containsKey(shapeId)) {
                if (!shapePerIdShapePtSequence.get(shapeId).containsKey(newShapePoint.getShapePtSequence())) {
                    shapePerIdShapePtSequence.get(shapeId).put(newShapePoint.getShapePtSequence(), newShapePoint);
                } else {
                    return null;
                }
            } else {
                final SortedMap<Integer, ShapePoint> innerMap = new TreeMap<>();
                innerMap.put(newShapePoint.getShapePtSequence(), newShapePoint);
                shapePerIdShapePtSequence.put(shapeId, innerMap);
            }
            return newShapePoint;
        } else {
            throw new IllegalArgumentException("Cannot add null shape point to data repository");
        }
    }

    /**
     * Return an immutable map of shape points from shapes.txt related to the id provided as parameter; which represents
     * a shape object. The returned map is ordered by shape_pt_sequence.
     *
     * @param shapeId the key from shapes.txt related to the Route to be returned
     * @return an immutable map of shape points from shapes.txt related to the id provided as parameter; which
     * represents a shape object. The returned map is ordered by shape_pt_sequence.
     */
    @Override
    public SortedMap<Integer, ShapePoint> getShapeById(final String shapeId) {
        try {
            return Collections.unmodifiableSortedMap(shapePerIdShapePtSequence.get(shapeId));
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Return an immutable map representing all records from shapes.txt. The key values for the returned map are
     * shape_id and the value is another map, which keys are shape_pt_sequence and values are {@link ShapePoint}.
     * Note that those are ordered by ascending shape_pt_sequence.
     *
     * @return an immutable map representing all records from shapes.txt
     */
    @Override
    public Map<String, SortedMap<Integer, ShapePoint>> getShapeAll() {
        return Collections.unmodifiableMap(shapePerIdShapePtSequence);
    }

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
    @Override
    public StopTime addStopTime(final StopTime newStopTime) throws IllegalArgumentException {
        if (newStopTime != null) {
            final String tripId = newStopTime.getTripId();
            final Integer stopSequence = newStopTime.getStopSequence();
            if (stopTimePerTripIdStopSequence.containsKey(tripId)) {
                if (!stopTimePerTripIdStopSequence.get(tripId).containsKey(stopSequence)) {
                    stopTimePerTripIdStopSequence.get(tripId).put(stopSequence, newStopTime);
                } else {
                    return null;
                }
            } else {
                final TreeMap<Integer, StopTime> innerMap = new TreeMap<>();
                innerMap.put(stopSequence, newStopTime);
                stopTimePerTripIdStopSequence.put(tripId, innerMap);
            }
            return newStopTime;
        } else {
            throw new IllegalArgumentException("Cannot add null StopTime to data repository");
        }
    }

    /**
     * Return an immutable map of {@link StopTime} from stop_times.txt related to the trip_id provided as parameter.
     * The returned map is ordered by stop_sequence
     *
     * @param tripId identifies a trip
     * @return an immutable map of {@link StopTime} from stop_times.txt related to the trip_id provided as parameter
     */
    @Override
    public SortedMap<Integer, StopTime> getStopTimeByTripId(final String tripId) {
        return Collections.unmodifiableSortedMap(stopTimePerTripIdStopSequence.get(tripId));
    }

    /**
     * Return an immutable map representing all records from stop_times.txt. The key values for the returned map are
     * trip_id and the value is another map, which keys are stop_sequence and values are {@link StopTime}. Note that
     * those are ordered by ascending stop_sequence.
     *
     * @return an immutable map representing all records from stop_times.txt
     */
    public Map<String, TreeMap<Integer, StopTime>> getStopTimeAll() {
        return Collections.unmodifiableMap(stopTimePerTripIdStopSequence);
    }

    /**
     * Add a {@code Translation} representing a row from translations.txt to this {@link GtfsDataRepository}.
     * Return the entity added to the repository if the uniqueness constraint on rows from translations.txt is
     * respected. If this requirement is not met, returns null.
     *
     * @param newTranslationTable the internal representation of a row from translations.txt to be added to the
     *                            repository
     * @return the entity added to the repository if the uniqueness constraint on rows from translations.txt is
     * respected. If this requirement is not met, returns null,
     * @throws IllegalArgumentException if the argument is null
     */
    @Override
    public Translation addTranslation(final Translation newTranslationTable) throws IllegalArgumentException {
        if (newTranslationTable != null) {
            final String tableName = newTranslationTable.getTableName().toString().toLowerCase();
            final String fieldName = newTranslationTable.getFieldName();
            final String language = newTranslationTable.getLanguage();
            if (translationPerTableName.containsKey(tableName)) {
                if (translationPerTableName.get(tableName).containsKey(fieldName)) {
                    if (translationPerTableName.get(tableName).get(fieldName).containsKey(language)) {
                        return null;
                    } else {
                        translationPerTableName.get(tableName).get(fieldName).put(language, newTranslationTable);
                    }
                } else {
                    final Map<String, Translation> firstLevelMap = new TreeMap<>();
                    final Map<String, Map<String, Translation>> secondLevelMap = new TreeMap<>();
                    secondLevelMap.put(language, firstLevelMap);
                    translationPerTableName.put(fieldName, secondLevelMap);
                }
            } else {
                final Map<String, Translation> firstLevelMap = new TreeMap<>();
                firstLevelMap.put(language, newTranslationTable);
                final Map<String, Map<String, Translation>> secondLevelMap = new TreeMap<>();
                secondLevelMap.put(fieldName, firstLevelMap);
                translationPerTableName.put(tableName, secondLevelMap);
            }
            return newTranslationTable;
        } else {
            throw new IllegalArgumentException("Cannot add null Translation to data repository");
        }
    }

    /**
     * Return the list of {@link Translation} from translations.txt related to the table_name, field_value and language
     * provided as parameter
     *
     * @param tableName the name of the table to retrieve translations from
     * @param fieldName the name of the field to be translated
     * @param language  the language of translation
     * @return the list of {@link Translation} from translations.txt related to the table_name, field_value and language
     * provided as parameter
     */
    @Override
    public Translation getTranslationByTableNameFieldValueLanguage(final String tableName,
                                                                   final String fieldName,
                                                                   final String language) {
        return translationPerTableName.get(tableName).get(fieldName).get(language);
    }

    /**
     * Add a stop representing a row from stops.txt to this {@link GtfsDataRepository}. Return the entity added to the
     * repository if the uniqueness constraint of stop based on stop_id is respected. Otherwise returns null.
     *
     * @param newStop the internal representation. A subclass of {@link LocationBase}
     *                of a row from stops.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of stop based on stop_id is
     * respected. Otherwise returns null.
     * @throws IllegalArgumentException if the given parameter is null
     */
    @Override
    public LocationBase addStop(final LocationBase newStop) throws IllegalArgumentException {
        if (newStop != null) {
            final String stopId = newStop.getStopId();
            if (stopPerId.containsKey(stopId)) {
                return null;
            } else {
                stopPerId.put(stopId, newStop);
                return newStop;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null stop to data repository");
        }
    }

    /**
     * Return an immutable map of {@link LocationBase} objects representing all the rows from stops.txt,
     * with stop_id as the key
     *
     * @return an immutable map of {@link LocationBase} objects representing all the rows from stops.txt,
     * with stop_id as the key
     */
    @Override
    public Map<String, LocationBase> getStopAll() {
        return Collections.unmodifiableMap(stopPerId);
    }

    /**
     * Return the {@link LocationBase} representing a row from stops.txt related to the id provided as parameter
     *
     * @param stopId the stop_id from stops.txt related to the {@link LocationBase} to be returned
     * @return the {@link LocationBase} representing a row from stops.txt related to the id provided as parameter.
     * Null if the id couldn't be found.
     */
    @Override
    public LocationBase getStopById(final String stopId) {
        return stopPerId.get(stopId);
    }

    /**
     * Returns `feed_info.feed_publisher_name` value if file `feed_info.txt` was provided, otherwise returns an empty
     * String
     *
     * @return `feed_info.feed_publisher_name` value if file `feed_info.txt` was provided, otherwise returns an empty
     * String
     */
    @Override
    public String getFeedPublisherName() {
        if (feedInfoPerFeedPublisherName.size() == 0) {
            return "";
        } else {
            return feedInfoPerFeedPublisherName.keySet().stream().findFirst().get();
        }
    }
}
