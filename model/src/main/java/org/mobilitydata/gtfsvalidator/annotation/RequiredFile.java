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
 * Adds a validation that the field or a file is required.
 *
 * <p>In the case of a field, both the column header and a value for each row is required.
 *
 * <p>Example.
 *
 * <pre>
 *   {@literal @}GtfsTable("agency.txt")
 *   public interface GtfsAgencySchema extends GtfsEntity {
 *       {@literal @}FieldType(FieldTypeEnum.ID)
 *       {@literal @}PrimaryKey
 *       String agencyId();
 *
 *       {@literal @}RequiredFile String agencyName();
 *   }
 * </pre>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface RequiredFile {}
