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

public class IntegerFieldValueOutOfRangeNotice extends ErrorNotice {
    private String fieldName;
    private String entityId;
    private int rangeMin;
    private int rangeMax;
    private int actualValue;

    public IntegerFieldValueOutOfRangeNotice(
            String filename,
            String fieldName,
            String entityId,
            int rangeMin,
            int rangeMax,
            int actualValue) {
        super(filename, E_010,
                "Out of range integer value",
                "Invalid value for field:" + fieldName + " of entity with id:" + entityId +
                        " -- min:" + rangeMin + " max:" + rangeMax + " actual:" + actualValue);
        this.fieldName = fieldName;
        this.entityId = entityId;
        this.rangeMax = rangeMax;
        this.rangeMin = rangeMin;
        this.actualValue = actualValue;
    }

    @Override
    public void export(ValidationResultRepository.NoticeExporter exporter) {
        exporter.export(this);
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getEntityId() {
        return entityId;
    }

    public int getRangeMin() {
        return rangeMin;
    }

    public int getRangeMax() {
        return rangeMax;
    }

    public int getActualValue() {
        return actualValue;
    }
}