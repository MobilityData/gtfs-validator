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

package org.mobilitydata.gtfsvalidator.usecase.notice.warning;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

public class NonStandardHeaderNotice extends WarningNotice {

    private String extraHeader;

    public NonStandardHeaderNotice(String filename, String extra) {
        super(filename, W_002,
                "Non standard header",
                "Unexpected header:" + extra + " in file:" + filename);
        this.extraHeader = extra;
    }

    public String getExtraHeader() {
        return extraHeader;
    }

    @Override
    public void export(ValidationResultRepository.NoticeExporter exporter) {
        exporter.export(this);
    }
}