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
import org.mobilitydata.gtfsvalidator.domain.entity.StopOrPlatform;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

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

        Integer type = (Integer) validatedStopEntity.get("location_type");

        if (type != null) {
            switch (type) {
                case 0: // stop or platform
                    String id = validatedStopEntity.getEntityId();
                    String name = (String) validatedStopEntity.get("stop_name");
                    Float latitude = (Float) validatedStopEntity.get("stop_lat");
                    Float longitude = (Float) validatedStopEntity.get("stop_lon");

                    if (id != null && name != null && latitude != null && longitude != null) {
                        StopOrPlatform.StopOrPlatformBuilder builder = new StopOrPlatform.StopOrPlatformBuilder(
                                id,
                                name,
                                latitude,
                                longitude
                        );

                        builder.code((String) validatedStopEntity.get("stop_code"))
                                .description((String) validatedStopEntity.get("stop_desc"))
                                .zoneId((String) validatedStopEntity.get("zone_id"))
                                .url((String) validatedStopEntity.get("stop_url"))
                                .parentStation((String) validatedStopEntity.get("parent_station"))
                                .timezone((String) validatedStopEntity.get("stop_timezone"))
                                .levelId((String) validatedStopEntity.get("level_id"))
                                .platformCode((String) validatedStopEntity.get("platform_code"));

                        //TODO: ready to be built and added to gtfsDataRepo
                        //TODO: wheelchair value in a subsequent use case (FinalizeStopEntity)
                    } else {
                        //TODO: notice for cannnot build stop or platform
                    }
                    break;
            }

        } else {
            //TODO: add notice for null type
        }
    }
}
