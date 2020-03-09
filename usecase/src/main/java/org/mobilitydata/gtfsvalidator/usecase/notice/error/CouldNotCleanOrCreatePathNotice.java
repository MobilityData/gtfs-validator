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

package org.mobilitydata.gtfsvalidator.usecase.notice.error;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

public class CouldNotCleanOrCreatePathNotice extends ErrorNotice {

    private String pathToCleanOrCreate;

    public CouldNotCleanOrCreatePathNotice(final String pathToCleanOrCreate) {
        super("",
                E_009,
                "Path cleaning or creation error",
                "An error occurred while trying clean or create path: " + pathToCleanOrCreate);
        this.pathToCleanOrCreate = pathToCleanOrCreate;
    }

    @Override
    public void export(ValidationResultRepository.NoticeExporter exporter) {
        exporter.export(this);
    }

    public String getPathToCleanOrCreate() {
        return pathToCleanOrCreate;
    }
}