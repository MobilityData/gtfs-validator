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

import org.mobilitydata.gtfsvalidator.domain.entity.notice.info.ValidatorCrashNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

public class GenerateValidatorCrashNotice {
    private final ValidationResultRepository resultRepo;

    public GenerateValidatorCrashNotice(final ValidationResultRepository resultRepo) {
        this.resultRepo = resultRepo;
    }

    public void execute(final String exceptionMessage) {
        resultRepo.addNotice(new ValidatorCrashNotice(exceptionMessage));
    }
}
