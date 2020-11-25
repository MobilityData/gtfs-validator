package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.base.Joiner;

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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Notice) {
            return context.equals(((Notice) other).context);
        }
        return false;
    }

    @Override
    public String toString() {
        return getCode() + " " + Joiner.on(",").withKeyValueSeparator("=").join(context);
    }
}
