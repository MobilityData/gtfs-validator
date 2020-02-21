package org.mobilitydata.gtfsvalidator.domain.entity;

import java.util.Map;

public class ParsedEntity {

    private final RawFileInfo rawFileInfo;
    private final String entityId;
    private final Map<String, Object> contentByHeaderMap;

    public ParsedEntity(String id, Map<String, Object> contentByHeaderMap, RawFileInfo rawFileInfo) {
        this.contentByHeaderMap = contentByHeaderMap;
        this.rawFileInfo = rawFileInfo;
        this.entityId = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public Object get(final String header) {
        return contentByHeaderMap.get(header);
    }

    public RawFileInfo getRawFileInfo() {
        return rawFileInfo;
    }
}
