package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

/**
 * A notice that the file is expected to have a single entity but has more (e.g., "feed_info.txt").
 */
public class MoreThanOneEntityNotice extends Notice {
    public MoreThanOneEntityNotice(String filename, long entityCount) {
        super(ImmutableMap.of("filename", filename, "entityCount", entityCount));
    }

    @Override
    public String getCode() {
        return "more_than_one_entity";
    }
}
