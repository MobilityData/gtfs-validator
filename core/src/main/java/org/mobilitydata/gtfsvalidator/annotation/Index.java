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
 * Asks annotation processor to create an index for quick search on a given field. The field does not need to have
 * unique values.
 * <p>
 * Note that {@code PrimaryKey} already implies an index.
 * <p>
 * Example.
 *
 * <pre>
 *   @GtfsTable("stops.txt")
 *   public interface GtfsStopSchema extends GtfsEntity {
 *       @FieldType(FieldTypeEnum.ID)
 *       @Index
 *       String zoneId();
 *   }
 * </pre>
 * <p>
 * This generated the following method.
 *
 * <pre>
 *   public class GtfsStopTableContainer extends GtfsTableContainer<GtfsStop> {
 *       public List<GtfsStop> byZoneId(String key) {
 *           // ...
 *       }
 *   }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Index {
}
