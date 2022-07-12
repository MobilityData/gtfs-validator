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
 *
 * <p>This also adds a validation that all values are unique.
 *
 * <p>Note that {@code @PrimaryKey} does not imply that the field is required and you need to put an
 * extra {@code @Required} annotation in this case.
 *
 * <p>Example.
 *
 * <pre>
 *   {@literal @}GtfsTable("stops.txt")
 *   public interface GtfsStopSchema extends GtfsEntity {
 *       {@literal @}FieldType(FieldTypeEnum.ID)
 *       {@literal @}Required
 *       {@literal @}PrimaryKey
 *       String stopId();
 *   }
 * </pre>
 *
 * <p>The {@code PrimaryKey} annotation can be specified for multiple fields if the file has a
 * multi-column key.
 *
 * <p>For single-field primary keys, a lookup method will be added to the container class to find an
 * entity by its primary key.
 *
 * <pre>
 *   {@literal @}Generated
 *   public class GtfsStopTableContainer extends GtfsTableContainer&lt;GtfsStop&gt; {
 *       public GtfsStop byStopId(String key) {
 *           // ...
 *       }
 *   }
 * </pre>
 *
 * <p>A lookup method will not automatically be generated for multi-field primary keys. Instead, use
 * the {@code @Index} annotation to specify which individual fields should have lookup methods.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface PrimaryKey {

  /**
   * Typically used in a multi-column primary key situation. When true, if an index is generated for
   * other primary key fields in the file, the associated list of values will be sorted by the
   * sequence field. Classic examples include stop_times.txt, where the primary key is (trip_id,
   * stop_sequence) and a byTripId(...) method returns a list of stop-times that are sorted by their
   * `stop_sequence` value.
   */
  boolean isSequenceUsedForSorting() default false;
}
