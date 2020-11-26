package org.mobilitydata.gtfsvalidator.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * Generates class names for a given GTFS table.
 *
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
