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
 * Specifies the primary key in a GTFS table.
 * <p>
 * This also adds a validation that all values are unique.
 * <p>
 * Note that {@code @PrimaryKey} does not imply that the field is required and you need to put an extra
 * {@code @Required} annotation in this case.
 * <p>
 * Example.
 *
 * <pre>
 *   @GtfsTable("stops.txt")
 *   public interface GtfsStopSchema extends GtfsEntity {
 *       @FieldType(FieldTypeEnum.ID)
 *       @Required
 *       @PrimaryKey
 *       String stopId();
 *   }
 * </pre>
 * <p>
 * This also adds a method to the container class to find an entity by its primary key.
 *
 * <pre>
 *   @Generated
 *   public class GtfsStopTableContainer extends GtfsTableContainer<GtfsStop> {
 *       public GtfsStop byStopId(String key) {
 *           // ...
 *       }
 *   }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface PrimaryKey {
}
