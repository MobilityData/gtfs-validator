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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

/**
 * This use case turns a parsed entity representing a row from transfers.txt into a concrete class
 */
public class ProcessParsedTransfer {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final ExecParamRepository execParamRepo;
    private final Transfer.TransferBuilder builder;

    public ProcessParsedTransfer(final ValidationResultRepository resultRepository,
                                 final GtfsDataRepository gtfsDataRepository,
                                 final ExecParamRepository execParamRepo,
                                 final Transfer.TransferBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.execParamRepo = execParamRepo;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from transfers.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code Transfer} object if the
     * requirements from the official GTFS specification are met. When these requirements are mot met, related notices
     * generated in {@code Transfer.TransferBuilder} are added to the result repository provided to the constructor.
     * This use case also adds a {@code DuplicatedEntityNotice} to said repository if the uniqueness constraint on
     * route entities is not respected.
     *
     * @param validatedParsedTransfer entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedParsedTransfer) {
        final String fromStopId = (String) validatedParsedTransfer.get("from_stop_id");
        final String toStopId = (String) validatedParsedTransfer.get("to_stop_id");
        final Integer transferType = (Integer) validatedParsedTransfer.get("transfer_type");
        final Integer minTransferTime = (Integer) validatedParsedTransfer.get("min_transfer_time");

        builder.fromStopId(fromStopId)
                .toStopId(toStopId)
                .transferType(transferType)
                .minTransferTime(minTransferTime);

        final EntityBuildResult<?> transfer = builder.build(
                Integer.parseInt(execParamRepo.getExecParamValue(ExecParamRepository.LOWER_BOUND_MIN_TRANSFER_TIME)),
                Integer.parseInt(execParamRepo.getExecParamValue(ExecParamRepository.UPPER_BOUND_MIN_TRANSFER_TIME))
        );

        if (transfer.isSuccess()) {
            if (gtfsDataRepository.addTransfer((Transfer) transfer.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("transfers.txt",
                        "from_stop_id;to_stop_id", validatedParsedTransfer.getEntityId()));
            }
        } else {
            //noinspection unchecked to avoid lint
            ((List<Notice>) transfer.getData()).forEach(resultRepository::addNotice);
        }
    }
}
