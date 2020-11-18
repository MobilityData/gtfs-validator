package org.mobilitydata.gtfsvalidator.notice;

import java.util.Map;

/**
 * Base class for all notices produced by GTFS validator.
 */
public abstract class Notice {
    private Map<String, Object> context;

    public Notice(Map<String, Object> context) {
        this.context = context;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    /**
     * Returns a descriptive type-specific name for this notice.
     *
     * @return notice code, e.g., "foreign_key_error".
     */
    public abstract String getCode();
}
