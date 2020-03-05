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
import org.mobilitydata.gtfsvalidator.domain.entity.stops.*;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import static org.mobilitydata.gtfsvalidator.domain.entity.stops.LocationBase.*;

/*
 * This use case turns a parsed entity to a concrete class depending on the 'type' field
 * Further processing stop.txt related entities is required to validate parent stations <--> child stops relationships
 */
public class ProcessParsedStop {

    private final GtfsSpecRepository specRepo;
    private final ValidationResultRepository resultRepo;
    private final GtfsDataRepository gtfsRepo;

    public ProcessParsedStop(final GtfsSpecRepository specRepo,
                             final ValidationResultRepository resultRepo,
                             final GtfsDataRepository gtfsRepo) {
        this.specRepo = specRepo;
        this.resultRepo = resultRepo;
        this.gtfsRepo = gtfsRepo;
    }

    public void execute(final ParsedEntity validatedStopEntity) {

        Integer locationType = (Integer) validatedStopEntity.get("location_type");
        String stopId = validatedStopEntity.getEntityId();
        String stopName = (String) validatedStopEntity.get("stop_name");
        Float stopLat = (Float) validatedStopEntity.get("stop_lat");
        Float stopLon = (Float) validatedStopEntity.get("stop_lon");
        String stopCode = (String) validatedStopEntity.get("stop_code");
        String stopDesc = (String) validatedStopEntity.get("stop_desc");
        String zoneId = (String) validatedStopEntity.get("zone_id");
        String stopUrl = (String) validatedStopEntity.get("stop_url");
        String parentStation = (String) validatedStopEntity.get("parent_station");
        String stopTimezone = (String) validatedStopEntity.get("stop_timezone");
        Integer wheelchairBoarding = (Integer) validatedStopEntity.get("wheelchair_boarding");
        String levelId = (String) validatedStopEntity.get("level_id");
        String platformCode = (String) validatedStopEntity.get("platform_code");

        switch (LocationType.fromInt(locationType)) {
            case STOP_OR_PLATFORM: {
                StopOrPlatform.StopOrPlatformBuilder builder = new StopOrPlatform.StopOrPlatformBuilder(
                        stopId, stopName, stopLat, stopLon);

                builder.wheelchairBoarding(WheelchairBoarding.fromInt(wheelchairBoarding))
                        .stopCode(stopCode)
                        .stopDesc(stopDesc)
                        .zoneId(zoneId)
                        .stopUrl(stopUrl)
                        .parentStation(parentStation)
                        .stopTimezone(stopTimezone)
                        .levelId(levelId)
                        .platformCode(platformCode);

                //TODO: ready to be built and added to gtfsDataRepo
                //TODO: wheelchair value in a subsequent use case (FinalizeStopEntity)
                break;
            }
            case STATION: {
                Station.StationBuilder builder = new Station.StationBuilder(
                        stopId, stopName, stopLat, stopLon);

                builder.wheelchairBoarding(WheelchairBoarding.fromInt(wheelchairBoarding))
                        .stopCode(stopCode)
                        .stopDesc(stopDesc)
                        .zoneId(zoneId)
                        .stopUrl(stopUrl)
                        .parentStation(parentStation)
                        .stopTimezone(stopTimezone)
                        .levelId(levelId)
                        .platformCode(platformCode);
                //TODO: ready to be built and added to gtfsDataRepo
                //TODO: wheelchair value in a subsequent use case (FinalizeStopEntity)
                break;
            }
            case ENTRANCE_OR_EXIT: {
                EntranceOrExit.EntranceOrExitBuilder builder = new EntranceOrExit.EntranceOrExitBuilder(
                        stopId, stopName, stopLat, stopLon);

                builder.wheelchairBoarding(WheelchairBoarding.fromInt(wheelchairBoarding))
                        .stopCode(stopCode)
                        .stopDesc(stopDesc)
                        .zoneId(zoneId)
                        .stopUrl(stopUrl)
                        .parentStation(parentStation)
                        .stopTimezone(stopTimezone)
                        .levelId(levelId)
                        .platformCode(platformCode);
                //TODO: ready to be built and added to gtfsDataRepo
                //TODO: wheelchair value in a subsequent use case (FinalizeStopEntity)
                break;
            }
            case GENERIC_NODE: {
                GenericNode.GenericNodeBuilder builder = new GenericNode.GenericNodeBuilder(
                        stopId, parentStation);

                builder.stopName(stopName)
                        .stopLat(stopLat)
                        .stopLon(stopLon)
                        .stopCode(stopCode)
                        .stopDesc(stopDesc)
                        .zoneId(zoneId)
                        .stopUrl(stopUrl)
                        .stopTimezone(stopTimezone)
                        .levelId(levelId)
                        .platformCode(platformCode);
                //TODO: ready to be built and added to gtfsDataRepo
                //TODO: wheelchair value in a subsequent use case (FinalizeStopEntity)
                break;
            }
            case BOARDING_AREA: {
                BoardingArea.BoardingAreaBuilder builder = new BoardingArea.BoardingAreaBuilder(
                        stopId, parentStation);

                builder.stopName(stopName)
                        .stopLat(stopLat)
                        .stopLon(stopLon)
                        .stopCode(stopCode)
                        .stopDesc(stopDesc)
                        .zoneId(zoneId)
                        .stopUrl(stopUrl)
                        .stopTimezone(stopTimezone)
                        .levelId(levelId)
                        .platformCode(platformCode);
                //TODO: ready to be built and added to gtfsDataRepo
                //TODO: wheelchair value in a subsequent use case (FinalizeStopEntity)
                break;
            }
        }
    }
}
