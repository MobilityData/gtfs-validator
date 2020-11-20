package org.mobilitydata.gtfsvalidator.processor;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

/**
 * Collection of utility functions to generate names of getters and setters, fields and other related staff for a given
 * GTFS field name.
 */
public final class FieldNameConverter {
    public static String getterMethodName(String field) {
        return field;
    }

    public static String getValueMethodName(String field) {
        return field + "Value";
    }

    public static String setterMethodName(String field) {
        return "set" + StringUtils.capitalize(field);
    }

    public static String hasMethodName(String field) {
        return "has" + StringUtils.capitalize(field);
    }

    public static String byKeyMethodName(String field) {
        return "by" + StringUtils.capitalize(field);
    }

    public static String byKeyMapName(String field) {
        return "by" + StringUtils.capitalize(field) + "Map";
    }

    public static String gtfsColumnName(String javaFieldName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, javaFieldName);
    }

    public static String javaFieldName(String gtfsColumnName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, gtfsColumnName);
    }

    public static String fieldNameField(String field) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, field) + "_FIELD_NAME";
    }
}
