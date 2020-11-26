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

package org.mobilitydata.gtfsvalidator.processor;

import com.google.auto.value.AutoValue;

/**
 * Describes a foreign key relation in a GTFS table.
 */
@AutoValue
public abstract class ForeignKeyDescriptor {
    public static ForeignKeyDescriptor create(String table, String field) {
        return new AutoValue_ForeignKeyDescriptor(table, field);
    }

    public abstract String table();

    public abstract String field();
}
