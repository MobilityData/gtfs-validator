/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.notice.base;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Notice {
    public static final String KEY_FIELD_NAME = "fieldName";
    public static final String KEY_FOLDER_NAME = "folderName";
    public static final String KEY_LINE_NUMBER = "lineNumber";
    public static final String KEY_RAW_VALUE = "rawValue";
    public static final String KEY_RANGE_MIN = "rangeMin";
    public static final String KEY_RANGE_MAX = "rangeMax";
    public static final String KEY_ACTUAL_VALUE = "actualValue";
    public static final String KEY_COLOR_VALUE = "colorValue";
    public static final String KEY_EMAIL_VALUE = "emailValue";
    public static final String KEY_LANG_VALUE = "langValue";
    public static final String KEY_TIME_VALUE = "timeValue";
    public static final String KEY_URL_VALUE = "urlValue";
    public static final String KEY_ENUM_VALUE = "enumValue";
    public static final String KEY_TIMEZONE_VALUE = "timezoneValue";
    public static final String KEY_CONFLICTING_FIELD_NAME = "conflictingFieldName";
    public static final String KEY_CURRENCY_CODE = "currencyCode";
    public static final String KEY_ROW_INDEX = "rowIndex";
    public static final String KEY_EXPECTED_LENGTH = "expectedLength";
    public static final String KEY_ACTUAL_LENGTH = "actualLength";
    public static final String KEY_MISSING_HEADER_NAME = "missingHeaderName";
    public static final String KEY_EXTRA_HEADER_NAME = "extraHeaderName";
    public static final String KEY_CONTRAST_RATIO = "contrastRatio";
    public static final String KEY_SHORT_NAME_LENGTH = "shortNameLength";
    public static final String KEY_COMPOSITE_KEY_FIRST_PART = "compositeKeyFirstPart";
    public static final String KEY_COMPOSITE_KEY_SECOND_PART = "compositeKeySecondPart";
    public static final String KEY_COMPOSITE_KEY_FIRST_VALUE = "compositeKeyFirstValue";
    public static final String KEY_COMPOSITE_KEY_SECOND_VALUE = "compositeKeySecondValue";
    public static final String KEY_UNKNOWN_SHAPE_ID = "unknownShapeId";

    private final String filename;
    private final int code;
    private final String title;
    private final String description;
    protected final String entityId;
    private Map<String, Object> noticeSpecific = null;

    protected Notice(final String filename,
                     final int code,
                     final String title,
                     final String description,
                     final String entityId) {
        this.filename = filename;
        this.code = code;
        this.title = title;
        this.description = description;
        this.entityId = entityId != null ? entityId : "no id";
    }

    public abstract void export(final NoticeExporter exporter)
            throws IOException;

    protected void putNoticeSpecific(final String key, final Object extra) {
        if (noticeSpecific == null) {
            noticeSpecific = new HashMap<>();
        }

        noticeSpecific.put(key, extra);
    }

    public Object getNoticeSpecific(final String key) {
        return noticeSpecific.get(key);
    }

    public String getFilename() {
        return filename;
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getEntityId() {
        return entityId;
    }

    public Map<String, Object> getNoticeSpecificAll() {
        return noticeSpecific != null ? noticeSpecific : new HashMap<>();
    }
}
