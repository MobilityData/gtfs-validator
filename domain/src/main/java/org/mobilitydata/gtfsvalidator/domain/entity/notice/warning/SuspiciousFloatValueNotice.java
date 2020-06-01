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

package org.mobilitydata.gtfsvalidator.domain.entity.notice.warning;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.WarningNotice;

import java.io.IOException;

public class SuspiciousFloatValueNotice extends WarningNotice {
    private final String fieldName;
    private final float rangeMin;
    private final float rangeMax;
    private final float actualValue;

    public SuspiciousFloatValueNotice(
            final String filename,
            final String fieldName,
            final String entityId,
            final float rangeMin,
            final float rangeMax,
            final float actualValue) {
        super(filename, W_006,
                "Suspicious float value",
                "Suspicious value for field:" + fieldName + " of entity with id:" + entityId +
                        " -- min:" + rangeMin + " max:" + rangeMax + " actual:" + actualValue,
                entityId);
        this.fieldName = fieldName;
        this.rangeMax = rangeMax;
        this.rangeMin = rangeMin;
        this.actualValue = actualValue;
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }

    public String getFieldName() {
        return fieldName;
    }

    public float getRangeMin() {
        return rangeMin;
    }

    public float getRangeMax() {
        return rangeMax;
    }

    public float getActualValue() {
        return actualValue;
    }
}
