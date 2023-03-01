/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds a validation that the column and header must be present. A value for the field may be
 * optional.
 *
 * <p>Example.
 *
 * <pre>
 *    {@literal @}GtfsTable("transfers.txt")
 *    public interface GtfsTransferSchema extends GtfsEntity {
 *        {@literal @}FieldType(FieldTypeEnum.ID)
 *        {@literal @}Required
 *        {@literal @}ForeignKey(table = "stops.txt", field = "stop_id")
 *        {@literal @}PrimaryKey(translationRecordIdType = RECORD_ID)
 *        String fromStopId();
 *
 *        {@literal @}FieldType(FieldTypeEnum.ID)
 *        {@literal @}Required
 *        {@literal @}ForeignKey(table = "stops.txt", field = "stop_id")
 *        {@literal @}PrimaryKey(translationRecordIdType = RECORD_SUB_ID)
 *        String toStopId();
 *
 *        {@literal @}RequiredColumn
 *        GtfsTransferType transferType();
 *    }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface RequiredColumn {}
