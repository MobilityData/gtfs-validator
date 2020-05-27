/*
 *
 *  * Copyright (c) 2020. MobilityData IO.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.mobilitydata.gtfsvalidator.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetValidationUsecaseAll {
    private final Map<String, List<ValidationUsecase>> usecaseByFilename;

    public GetValidationUsecaseAll(final Map<String, List<ValidationUsecase>> usecaseByFilename) {
        this.usecaseByFilename = usecaseByFilename;
    }

    public List<ValidationUsecase> execute(final List<String> filenameListToProcess) {
        final List<ValidationUsecase> toReturn = new ArrayList<>();
        filenameListToProcess.forEach(filename ->
                toReturn.addAll(usecaseByFilename.get(filename)));
        return toReturn;
    }
}