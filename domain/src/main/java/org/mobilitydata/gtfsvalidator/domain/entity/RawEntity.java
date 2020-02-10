package org.mobilitydata.gtfsvalidator.domain.entity;

import java.util.Map;

public class RawEntity {

    private final Map<String, String> contentByHeaderMap;
    private final int entityIndex;

    public RawEntity(Map<String, String> contentByHeaderMap, int entityIndex) {
        this.contentByHeaderMap = contentByHeaderMap;
        this.entityIndex = entityIndex;
    }

    public String get(final String header) {
        return contentByHeaderMap.get(header);
    }

    public int size() {
        return contentByHeaderMap.size();
    }

    public int getIndex() {
        return entityIndex;
    }
}
