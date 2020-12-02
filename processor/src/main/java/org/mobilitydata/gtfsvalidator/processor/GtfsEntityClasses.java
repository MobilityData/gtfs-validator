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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * Generates class names for a given GTFS table.
 * <p>
 * This class is used by multiple code generators.
 */
public final class GtfsEntityClasses {
    public static final String TABLE_PACKAGE_NAME = "org.mobilitydata.gtfsvalidator.table";
    public static final String SCHEMA_SUFFIX = "Schema";

    /**
     * Upper camelcase name, e.g., GtfsStopTime.
     */
    private final String className;

    public GtfsEntityClasses(String className) {
        this.className = className;
    }

    public GtfsEntityClasses(GtfsFileDescriptor fileDescriptor) {
        this(fileDescriptor.className());
    }

    public static String entityImplementationSimpleName(String schemaName) {
        if (!schemaName.endsWith(SCHEMA_SUFFIX)) {
            throw new IllegalArgumentException("Schema interface must end with " + SCHEMA_SUFFIX);
        }
        return schemaName.substring(0, schemaName.length() - SCHEMA_SUFFIX.length());
    }


    public String entityImplementationSimpleName() {
        return className;
    }

    public String tableLoaderSimpleName() {
        return className + "TableLoader";
    }

    public String tableContainerSimpleName() {
        return className + "TableContainer";
    }

    public TypeName entityImplementationTypeName() {
        return ClassName.get(TABLE_PACKAGE_NAME, entityImplementationSimpleName());
    }

    public TypeName entityBuilderTypeName() {
        return ClassName.get(TABLE_PACKAGE_NAME, entityImplementationSimpleName() + ".Builder");
    }

    public TypeName tableLoaderTypeName() {
        return ClassName.get(TABLE_PACKAGE_NAME, tableLoaderSimpleName());
    }

    public TypeName tableContainerTypeName() {
        return ClassName.get(TABLE_PACKAGE_NAME, tableContainerSimpleName());
    }

}
