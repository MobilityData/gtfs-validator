/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.SuspiciousMinTransferTimeNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/**
 * Use case to validate that all rows of file `transfers.txt` that have a value for field `min_transfer_time` is within
 * a certain range. The range is specified by the user of the software by execution parameters that are passed as
 * command line arguments or defined within file `execution-parameter.json`. If this range is not specified, the
 * use case will consider default values for said thresholds:
 * - range min: 0 sec
 * - range max: 86400 sec (24 hours)
 *
 * This use case is triggered after defining the content of the {@code GtfsDataRepository} provided in the constructor.
 * At this step it it assumed that all values of `min_transfer_time` are positive integers.
 */
public class ValidateMinTransferTimeValue {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final ExecParamRepository execParamRepo;
    private final Logger logger;

    /**
     * @param dataRepo        a repository storing the data of a GTFS dataset
     * @param resultRepo      a repository storing information about the validation process
     * @param execParamRepo   a repository storing the execution parameters
     * @param logger          a logger displaying information about the validation process
     */
    public ValidateMinTransferTimeValue(final GtfsDataRepository dataRepo,
                                        final ValidationResultRepository resultRepo,
                                        final ExecParamRepository execParamRepo,
                                        final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.execParamRepo = execParamRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if every record of file `transfers.txt` having a non null value for field
     * min_transfer_time are within a certain range. This range is specified by the user of the software by execution
     * parameters that are passed as command line arguments or defined within file `execution-parameter.json`.
     * If this range is not specified, the use case will consider default values for said thresholds:
     * - range min: 0 sec
     * - range max: 86400 sec (24 hours)
     */
    public void execute() {
        logger.info("Validating rule 'W009 - `min_transfer_time` is outside allowed range" + System.lineSeparator());
        final int minTransferTimeRangeMin =
                Integer.parseInt(
                        execParamRepo.getExecParamValue(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MIN));
        final int minTransferTimeRangeMax =
                Integer.parseInt(
                        execParamRepo.getExecParamValue(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MAX));
        dataRepo.getTransferAll().values()
                .forEach(transferCollection ->
                        transferCollection.values().stream()
                                .filter(transfer -> transfer.getMinTransferTime() != null &&
                                        (transfer.getMinTransferTime() < minTransferTimeRangeMin ||
                                                transfer.getMinTransferTime() > minTransferTimeRangeMax))
                                .forEach(transfer -> resultRepo.addNotice(
                                        new SuspiciousMinTransferTimeNotice(minTransferTimeRangeMin,
                                                minTransferTimeRangeMax,
                                                transfer.getMinTransferTime(), "from_stop_id",
                                                "to_stop_id",
                                                transfer.getFromStopId(),
                                                transfer.getToStopId()))
                                ));
    }
}
