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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies.Frequency;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways.Pathway;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.LocationBase;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.Translation;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public interface GtfsDataRepository {
    Agency addAgency(final Agency newAgency, final Agency.AgencyBuilder builder) throws IllegalArgumentException;

    Agency getAgencyById(final String agencyId);

    int getAgencyCount();

    Map<String, Agency> getAgencyAll();

    Route addRoute(final Route newRoute) throws IllegalArgumentException;

    Map<String, Route> getRouteAll();

    Route getRouteById(final String routeId);

    CalendarDate addCalendarDate(final CalendarDate newCalendarDate) throws IllegalArgumentException;

    Map<String, Map<String, CalendarDate>> getCalendarDateAll();

    Level addLevel(final Level newLevel) throws IllegalArgumentException;

    Level getLevelById(final String levelId);

    Calendar addCalendar(final Calendar newCalendar) throws IllegalArgumentException;

    Calendar getCalendarByServiceId(final String serviceId);

    Map<String, Calendar> getCalendarAll();

    Trip addTrip(final Trip newTrip) throws IllegalArgumentException;

    Trip getTripById(final String tripId);

    Map<String, Trip> getTripAll();

    Map<String, List<Trip>> getAllTripByBlockId();

    Transfer addTransfer(final Transfer newTransfer) throws IllegalArgumentException;

    Transfer getTransferByStopPair(final String fromStopId, final String toStopId);

    FeedInfo addFeedInfo(final FeedInfo newFeedInfo) throws IllegalArgumentException;

    Map<String, FeedInfo> getFeedInfoAll();

    FeedInfo getFeedInfoByFeedPublisherName(final String feedInfoPublisherName);

    FareAttribute addFareAttribute(final FareAttribute newFareAttribute);

    FareAttribute getFareAttributeById(final String fareId);

    FareRule addFareRule(final FareRule newFareRule) throws IllegalArgumentException;

    FareRule getFareRule(final String fareId, final String routeId, final String originId, final String destinationId,
                         final String containsId);

    Frequency addFrequency(final Frequency newFrequency);

    Frequency getFrequency(final String tripId, final Integer startTime);

    Map<String, Frequency> getFrequencyAll();

    Map<String, List<Frequency>> getFrequencyAllByTripId();

    Pathway addPathway(final Pathway newPathway) throws IllegalArgumentException;

    Pathway getPathwayById(final String pathwayId);

    Attribution addAttribution(final Attribution newAttribution) throws IllegalArgumentException;

    Attribution getAttribution(final String attributionId, final String agencyId, final String routeId,
                               final String tripId, final String organizationName, final boolean isProducer,
                               final boolean isOperator, final boolean isAuthority, final String attributionUrl,
                               final String attributionEmail, final String attributionPhone);

    /**
     * Add a {@link ShapePoint} to a shape. A shape is a list of{@link ShapePoint} whereas a {@link ShapePoint}
     * represents a row from shapes.txt to this. Return the entity added to the repository if the entity was
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
    ShapePoint addShapePoint(final ShapePoint newShapePoint) throws IllegalArgumentException;

    /**
     * Return an immutable map of shape points from shapes.txt related to the id provided as parameter; which represents
     * a shape object. The returned map is ordered by shape_pt_sequence.
     *
     * @param shapeId the key from shapes.txt related to the Route to be returned
     * @return an immutable map of shape points from shapes.txt related to the id provided as parameter; which
     * represents a shape object. The returned map is ordered by shape_pt_sequence.
     */
    SortedMap<Integer, ShapePoint> getShapeById(final String shapeId);

    /**
     * Return an immutable map representing all records from shapes.txt. The key values for the returned map are
     * shape_id and the value is another map, which keys are shape_pt_sequence and values are {@link ShapePoint}.
     * Note that those are ordered by ascending shape_pt_sequence.
     *
     * @return an immutable map representing all records from shapes.txt
     */
    Map<String, SortedMap<Integer, ShapePoint>> getShapeAll();

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
     * Return an immutable map of {@link StopTime} from stop_times.txt related to the trip_id provided as parameter.
     * The returned map is ordered by stop_sequence
     *
     * @param tripId identifies a trip
     * @return an immutable map of {@link StopTime} from stop_times.txt related to the trip_id provided as parameter
     */
    SortedMap<Integer, StopTime> getStopTimeByTripId(final String tripId);

    /**
     * Return an immutable map representing all records from stop_times.txt. Elements of this map are mapped by
     * "trip_id" and ordered by ascending by stop_sequence
     *
     * @return an immutable map representing all records from stop_times.txt
     */
    Map<String, TreeMap<Integer, StopTime>> getStopTimeAll();

    Translation getTranslationByTableNameFieldValueLanguage(final String tableName,
                                                            final String fieldValue,
                                                            final String language);

    Translation addTranslation(final Translation newTranslationTable);

    LocationBase addStop(final LocationBase newStop);

    LocationBase getStopById(final String stopId);

    Map<String, LocationBase> getStopAll();

    /**
     * Returns `feed_info.feed_publisher_name` value if file `feed_info.txt` was provided, otherwise returns an empty
     * String
     *
     * @return `feed_info.feed_publisher_name` value if file `feed_info.txt` was provided, otherwise returns an empty
     * String
     */
    String getFeedPublisherName();
}
