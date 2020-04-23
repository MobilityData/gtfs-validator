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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes.FareAttribute;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.UnexpectedValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.sql.SQLIntegrityConstraintViolationException;

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

    public void execute(final ParsedEntity validatedFareAttribute) throws IllegalArgumentException,
            SQLIntegrityConstraintViolationException {

        final String fareId = (String) validatedFareAttribute.get("fare_id");
        final Float price = (Float) validatedFareAttribute.get("price");
        final String currencyType = (String) validatedFareAttribute.get("currency_type");
        final Integer paymentMethod = (Integer) validatedFareAttribute.get("payment_method");
        final Integer transfers = (Integer) validatedFareAttribute.get("transfers");
        final String agencyId = (String) validatedFareAttribute.get("agency_id");
        final Integer transferDuration = (Integer) validatedFareAttribute.get("transfer_duration");

        try {
            builder.fareId(fareId)
                    .price(price)
                    .currencyType(currencyType)
                    .paymentMethod(paymentMethod)
                    .transfers(transfers)
                    .agencyId(agencyId)
                    .transferDuration(transferDuration);

            gtfsDataRepository.addFareAttribute(builder.build());

        } catch (IllegalArgumentException e) {

            if (fareId == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("fare_attributes.txt",
                        "fare_id", validatedFareAttribute.getEntityId()));
            }
            if (price == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("fare_attributes.txt",
                        "price", validatedFareAttribute.getEntityId()));
            }
            if (currencyType == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("fare_attributes.txt",
                        "currency_type", validatedFareAttribute.getEntityId()));
            }
            if (paymentMethod == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("fare_attributes.txt",
                        "payment_method", validatedFareAttribute.getEntityId()));
            } else if (paymentMethod < 0 || paymentMethod > 1) {
                resultRepository.addNotice(new UnexpectedValueNotice("fare_attributes.txt",
                        "payment_method", validatedFareAttribute.getEntityId(), paymentMethod));
            }
            if (transfers > 2) {
                resultRepository.addNotice(new UnexpectedValueNotice("fare_attributes.txt",
                        "transfers", validatedFareAttribute.getEntityId(), transfers));
            }
            throw e;
        } catch (SQLIntegrityConstraintViolationException e) {
            resultRepository.addNotice(new EntityMustBeUniqueNotice("fare_attributes.txt", "fare_id",
                    validatedFareAttribute.getEntityId()));
            throw e;
        }
    }
}
