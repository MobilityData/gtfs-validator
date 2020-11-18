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

import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FatalInternalErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Arrays;

/**
 * Use case to handle unsupported exceptions.The goal is to handle these gracefully. On exceptions,
 * a notice will be generated to inform the users that something wrong prevent the validator from finishing its
 * execution.
 */
public class HandleUnsupportedException {
    private final ValidationResultRepository resultRepo;

    /**
     * @param resultRepo       a repository storing information about the validation process
     */
    public HandleUnsupportedException(final ValidationResultRepository resultRepo) {
        this.resultRepo = resultRepo;
    }

    /**
     * Will add a {@code FatalInternalErrorNotice} to the {@code ValidationResultRepository} provided in the constructor
     * @param exception  the exception that is not supported by the validator
     */
    public void execute(final Exception exception) {
        resultRepo.addNotice(
                new FatalInternalErrorNotice(
                        exception.getMessage(),
                        Arrays.toString((exception.getStackTrace()))
                ));
    }
}
