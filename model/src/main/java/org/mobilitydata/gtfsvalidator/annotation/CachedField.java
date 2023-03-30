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
/**
 * Enables caching of values for a given field to optimize memory usage.
 *
 * <p>See {@code FieldCache} for details.
 *
 * <p>Note that caching is already automatically enabled for certain field types (ID, date, time,
 * color, language code).
 *
 * <p>Example.
 *
 * <pre>
 *   {@literal @}GtfsTable("stop_times.txt")
 *   {@literal @}Required
 *   public interface GtfsStopTimeSchema extends GtfsEntity {
 *       {@literal @}CachedField
 *       String stopHeadsign();
 *   }
 * </pre>
 */
public @interface CachedField {}
