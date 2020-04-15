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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.UnexpectedValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.sql.SQLIntegrityConstraintViolationException;

public class ProcessParsedTransfer {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final Transfer.TransferBuilder builder;

    public ProcessParsedTransfer(final ValidationResultRepository resultRepository,
                                 final GtfsDataRepository gtfsDataRepository,
                                 final Transfer.TransferBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    public void execute(final ParsedEntity validatedParsedTransfer) throws IllegalArgumentException,
            SQLIntegrityConstraintViolationException {

        String fromStopId = (String) validatedParsedTransfer.get("from_stop_id");
        String toStopId = (String) validatedParsedTransfer.get("to_stop_id");
        Integer transferType = (Integer) validatedParsedTransfer.get("transfer_type");
        Integer minTransferTime = (Integer) validatedParsedTransfer.get("min_transfer_time");

        try {
            builder.fromStopId(fromStopId)
                    .toStopId(toStopId)
                    .transferType(transferType)
                    .minTransferTime(minTransferTime);

            gtfsDataRepository.addTransfer(builder.build());

        } catch (IllegalArgumentException e) {
            if (fromStopId == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("transfers.txt",
                        "from_stop_id", validatedParsedTransfer.getEntityId()));
            }
            if (toStopId == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("transfers.txt",
                        "to_stop_id", validatedParsedTransfer.getEntityId()));
            }
            if (transferType < 1 || transferType > 3) {
                resultRepository.addNotice(new UnexpectedValueNotice("transfers.txt", "transfer_type",
                        validatedParsedTransfer.getEntityId(), transferType));
            }
            if (minTransferTime != null && minTransferTime < 0) {
                resultRepository.addNotice(new IntegerFieldValueOutOfRangeNotice("transfers.txt",
                        "transfer_type", validatedParsedTransfer.getEntityId(), 0, Integer.MAX_VALUE,
                        minTransferTime));
            }
            throw e;
        } catch (SQLIntegrityConstraintViolationException e) {
            resultRepository.addNotice(new EntityMustBeUniqueNotice("transfers.txt", "from_stop_id",
                    validatedParsedTransfer.getEntityId()));
            throw e;
        }
    }
}
