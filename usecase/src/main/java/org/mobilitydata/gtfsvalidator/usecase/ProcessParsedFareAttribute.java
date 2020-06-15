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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes.FareAttribute;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

/**
 * This use case turns a parsed entity representing a row from fare_attributes.txt into a concrete class
 */
public class ProcessParsedFareAttribute {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final FareAttribute.FareAttributeBuilder builder;

    public ProcessParsedFareAttribute(final ValidationResultRepository resultRepository,
                                      final GtfsDataRepository gtfsDataRepository,
                                      final FareAttribute.FareAttributeBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from fare_attributes.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code FareAttribute} object if the
     * requirements from the official GTFS specification are met. When these requirements are not met, related notices
     * generated in {@code FareAttribute.FareAttributeBuilder} are added to the result repository provided in the
     * constructor. This use case also adds a {@code EntityMustBeUniqueNotice} to said repository if the uniqueness
     * constraint on fare attribute entities is not respected.
     *
     * @param validatedFareAttribute entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedFareAttribute) {
        final String fareId = (String) validatedFareAttribute.get("fare_id");
        final Float price = (Float) validatedFareAttribute.get("price");
        final String currencyType = (String) validatedFareAttribute.get("currency_type");
        final Integer paymentMethod = (Integer) validatedFareAttribute.get("payment_method");
        final Integer transfers = (Integer) validatedFareAttribute.get("transfers");
        final String agencyId = (String) validatedFareAttribute.get("agency_id");
        final Integer transferDuration = (Integer) validatedFareAttribute.get("transfer_duration");

        builder.fareId(fareId)
                .price(price)
                .currencyType(currencyType)
                .paymentMethod(paymentMethod)
                .transfers(transfers)
                .agencyId(agencyId)
                .transferDuration(transferDuration);

        final EntityBuildResult<?> fareAttribute = builder.build();

        if (fareAttribute.isSuccess()) {
            if (gtfsDataRepository.addFareAttribute((FareAttribute) fareAttribute.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("fare_attributes.txt",
                        "fare_id", validatedFareAttribute.getEntityId()));
            }
        } else {
            // at this step it is certain that calling getData method will return a list of notices, therefore there is
            // no need for cast check
            //noinspection unchecked
            ((List<Notice>) fareAttribute.getData()).forEach(resultRepository::addNotice);
        }
    }
}