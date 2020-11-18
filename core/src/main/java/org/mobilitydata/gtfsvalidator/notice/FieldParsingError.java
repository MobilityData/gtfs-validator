package org.mobilitydata.gtfsvalidator.notice;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class FieldParsingError extends Notice {

    public FieldParsingError(String filename, long csvRowNumber, String fieldName, String fieldType, @Nullable String fieldValue) {
        super(createContext(filename, csvRowNumber, fieldName, fieldType, fieldValue));
    }

    private static Map<String, Object> createContext(String filename, long csvRowNumber, String fieldName, String fieldType, @Nullable String fieldValue) {
        // ImmutableMap does not support null values, so we have to use a HashMap here.
        Map<String, Object> map = new HashMap<>();
        map.put("filename", filename);
        map.put("csvRowNumber", csvRowNumber);
        map.put("fieldName", fieldName);
        map.put("fieldType", fieldType);
        map.put("fieldValue", fieldValue);
        return map;
    }

    @Override
    public String getCode() {
        return "field_parsing_error";
    }
}

