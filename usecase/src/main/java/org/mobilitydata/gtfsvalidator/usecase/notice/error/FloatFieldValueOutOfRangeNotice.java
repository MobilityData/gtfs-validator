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

import java.io.IOException;

public class FloatFieldValueOutOfRangeNotice extends ErrorNotice {
    private String fieldName;
    private String entityId;
    private float rangeMin;
    private float rangeMax;
    private float actualValue;

    public FloatFieldValueOutOfRangeNotice(
            String filename,
            String fieldName,
            String entityId,
            float rangeMin,
            float rangeMax,
            float actualValue) {
        super(filename, E_011,
                "Out of range float value",
                "Invalid value for field:" + fieldName + " of entity with id:" + entityId +
                        " -- min:" + rangeMin + " max:" + rangeMax + " actual:" + actualValue);
        this.rangeMax = rangeMax;
        this.rangeMin = rangeMin;
        this.entityId = entityId != null ? entityId : "no id";
        this.fieldName = fieldName;
        this.actualValue = actualValue;
    }

    @Override
    public void export(ValidationResultRepository.NoticeExporter exporter)
            throws IOException {
        exporter.export(this);
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getEntityId() {
        return entityId;
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