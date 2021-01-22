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
 * Annotates an interface that defines schema for a single GTFS table, such as "stops.txt".
 *
 * <p>Set {@code singleRow = true} if the table may have a single row, such as "feed_info.txt".
 *
 * <p>Example.
 *
 * <pre>
 *   @GtfsTable("routes.txt")
 *   public interface GtfsRouteSchema extends GtfsEntity {
 *       @DefaultValue("FFFFFF")
 *       GtfsColor routeColor();
 *   }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GtfsTable {
  String value();

  boolean singleRow() default false;
}
