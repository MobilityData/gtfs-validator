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

import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.ParentStationInvalidLocationTypeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.StationWithParentStationNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.stops.*;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/*
 * This use case turns a parsed entity to a concrete class depending on the 'type' field
 * Further processing stop.txt related entities is required to validate parent stations <--> child stops relationships
 */
public class ProcessParsedStop {

    /**
     * This enum matches types that can be found in the location_type field of stops.txt
     * // see https://gtfs.org/reference/static#stopstxt
     * It's used to decide which concrete type derived from {@link LocationBase} to instantiate
     */
    private enum LocationType {
        STOP_OR_PLATFORM(0),
        STATION(1),
        ENTRANCE(2),
        GENERIC_NODE(3),
        BOARDING_AREA(4);

        private int value;

        LocationType(int value) {
            this.value = value;
        }

        static public LocationType fromInt(Integer locationTypeAsInt) {
            if (locationTypeAsInt == null) {
                return STOP_OR_PLATFORM;
            }
            return Stream.of(LocationType.values())
                    .filter(enumItem -> enumItem.value == locationTypeAsInt)
                    .findAny()
                    .orElse(STOP_OR_PLATFORM);
        }
    }

    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsRepo;
    private final StopOrPlatform.StopOrPlatformBuilder stopOrPlatformBuilder;
    private final Station.StationBuilder stationBuilder;
    private final Entrance.EntranceBuilder entranceBuilder;
    private final GenericNode.GenericNodeBuilder genericNodeBuilder;
    private final BoardingArea.BoardingAreaBuilder boardingAreaBuilder;

    public ProcessParsedStop(final ValidationResultRepository resultRepo,
                             final GtfsDataRepository gtfsRepo,
                             final Station.StationBuilder stationBuilder,
                             final StopOrPlatform.StopOrPlatformBuilder stopOrPlatformBuilder,
                             final Entrance.EntranceBuilder entranceBuilder,
                             final GenericNode.GenericNodeBuilder genericNodeBuilder,
                             final BoardingArea.BoardingAreaBuilder boardingAreaBuilder) {
        this.resultRepository = resultRepo;
        this.gtfsRepo = gtfsRepo;
        this.stopOrPlatformBuilder = stopOrPlatformBuilder;
        this.stationBuilder = stationBuilder;
        this.entranceBuilder = entranceBuilder;
        this.genericNodeBuilder = genericNodeBuilder;
        this.boardingAreaBuilder = boardingAreaBuilder;
    }

    public void execute(final Map<String, ParsedEntity> parsedEntityByStopId) {
        parsedEntityByStopId.values().forEach(stop -> {
            Integer locationType = (Integer) stop.get("location_type");
            String stopId = stop.getEntityId();
            String stopName = (String) stop.get("stop_name");
            Float stopLat = (Float) stop.get("stop_lat");
            Float stopLon = (Float) stop.get("stop_lon");
            String stopCode = (String) stop.get("stop_code");
            String stopDesc = (String) stop.get("stop_desc");
            String zoneId = (String) stop.get("zone_id");
            String stopUrl = (String) stop.get("stop_url");
            String parentStation = (String) stop.get("parent_station");
            String stopTimezone = (String) stop.get("stop_timezone");
            Integer wheelchairBoarding = (Integer) stop.get("wheelchair_boarding");
            String levelId = (String) stop.get("level_id");
            String platformCode = (String) stop.get("platform_code");

            EntityBuildResult<?> stopEntityBuildResult = null;

            switch (LocationType.fromInt(locationType)) {
                case STOP_OR_PLATFORM: {
                    if (parentStation != null) {
                        ParsedEntity parent = parsedEntityByStopId.get(parentStation);
                        Integer parentLocationType = (Integer) parent.get("location_type");

                        if (LocationType.fromInt(parentLocationType) != LocationType.STATION) {
                            resultRepository.addNotice(
                                    new ParentStationInvalidLocationTypeNotice(
                                            stopId, locationType, parentStation, 1,
                                            parentLocationType
                                    )
                            );
                        }

                        if (WheelchairBoarding.fromInt(wheelchairBoarding) ==
                                WheelchairBoarding.UNKNOWN_WHEELCHAIR_BOARDING) {
                            wheelchairBoarding = (Integer) parent.get("wheelchair_boarding");
                        }
                    }

                    stopOrPlatformBuilder.clear()
                            .parentStation(parentStation)
                            .wheelchairBoarding(wheelchairBoarding)
                            .platformCode(platformCode)
                            .stopId(stopId)
                            .stopName(stopName)
                            .stopLat(stopLat)
                            .stopLon(stopLon)
                            .stopCode(stopCode)
                            .stopDesc(stopDesc)
                            .zoneId(zoneId)
                            .stopUrl(stopUrl)
                            .stopTimezone(stopTimezone);

                    stopEntityBuildResult = stopOrPlatformBuilder.build();
                    break;
                }
                case STATION: {
                    if (parentStation != null) {
                        resultRepository.addNotice(
                                new StationWithParentStationNotice(stopId, 1, parentStation)
                        );
                    }

                    stationBuilder.clear()
                            .wheelchairBoarding(wheelchairBoarding)
                            .levelId(levelId)
                            .stopId(stopId)
                            .stopName(stopName)
                            .stopLat(stopLat)
                            .stopLon(stopLon)
                            .stopCode(stopCode)
                            .stopDesc(stopDesc)
                            .zoneId(zoneId)
                            .stopUrl(stopUrl)
                            .stopTimezone(stopTimezone);

                    stopEntityBuildResult = stationBuilder.build();
                    break;
                }
                case ENTRANCE: {
                    if (parentStation != null) {
                        ParsedEntity parent = parsedEntityByStopId.get(parentStation);
                        Integer parentLocationType = (Integer) parent.get("location_type");

                        if (LocationType.fromInt(parentLocationType) != LocationType.STATION) {
                            resultRepository.addNotice(
                                    new ParentStationInvalidLocationTypeNotice(
                                            stopId, locationType, parentStation, 1,
                                            parentLocationType
                                    )
                            );
                        }

                        if (WheelchairBoarding.fromInt(wheelchairBoarding) ==
                                WheelchairBoarding.UNKNOWN_WHEELCHAIR_BOARDING) {
                            wheelchairBoarding = (Integer) parent.get("wheelchair_boarding");
                        }
                    }

                    entranceBuilder.clear()
                            .parentStation(parentStation)
                            .wheelchairBoarding(wheelchairBoarding)
                            .stopId(stopId)
                            .stopName(stopName)
                            .stopLat(stopLat)
                            .stopLon(stopLon)
                            .stopCode(stopCode)
                            .stopDesc(stopDesc)
                            .zoneId(zoneId)
                            .stopUrl(stopUrl)
                            .stopTimezone(stopTimezone);

                    stopEntityBuildResult = entranceBuilder.build();
                    break;
                }
                case GENERIC_NODE: {
                    if (parentStation != null) {
                        ParsedEntity parent = parsedEntityByStopId.get(parentStation);
                        Integer parentLocationType = (Integer) parent.get("location_type");

                        if (LocationType.fromInt(parentLocationType) != LocationType.STATION) {
                            resultRepository.addNotice(
                                    new ParentStationInvalidLocationTypeNotice(
                                            stopId, locationType, parentStation, 1,
                                            parentLocationType
                                    )
                            );
                        }
                    }

                    genericNodeBuilder.clear()
                            .parentStation(parentStation)
                            .stopId(stopId)
                            .stopName(stopName)
                            .stopLat(stopLat)
                            .stopLon(stopLon)
                            .stopCode(stopCode)
                            .stopDesc(stopDesc)
                            .zoneId(zoneId)
                            .stopUrl(stopUrl)
                            .stopTimezone(stopTimezone);

                    stopEntityBuildResult = genericNodeBuilder.build();
                    break;
                }
                case BOARDING_AREA: {
                    if (parentStation != null) {
                        ParsedEntity parent = parsedEntityByStopId.get(parentStation);
                        Integer parentLocationType = (Integer) parent.get("location_type");

                        if (LocationType.fromInt(parentLocationType) != LocationType.STOP_OR_PLATFORM) {
                            resultRepository.addNotice(
                                    new ParentStationInvalidLocationTypeNotice(
                                            stopId, locationType, parentStation, 0,
                                            parentLocationType
                                    )
                            );
                        }
                    }

                    boardingAreaBuilder.clear()
                            .parentStation(parentStation)
                            .stopId(stopId)
                            .stopName(stopName)
                            .stopLat(stopLat)
                            .stopLon(stopLon)
                            .stopCode(stopCode)
                            .stopDesc(stopDesc)
                            .zoneId(zoneId)
                            .stopUrl(stopUrl)
                            .stopTimezone(stopTimezone);

                    stopEntityBuildResult = boardingAreaBuilder.build();
                    break;
                }
            }

            if (stopEntityBuildResult.isSuccess()) {
                if (gtfsRepo.addStop((LocationBase) stopEntityBuildResult.getData()) == null) {
                    resultRepository.addNotice(new DuplicatedEntityNotice("stops.txt",
                            "stop_id", stopId));
                }
            } else {
                //noinspection unchecked
                ((List<Notice>) stopEntityBuildResult.getData()).forEach(resultRepository::addNotice);
            }
        });
    }
}
