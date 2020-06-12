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
    public static final String NOTICE_SPECIFIC_KEY__FIELD_NAME = "fieldName";
    public static final String NOTICE_SPECIFIC_KEY__FOLDER_NAME = "folderName";
    public static final String NOTICE_SPECIFIC_KEY__LINE_NUMBER = "lineNumber";
    public static final String NOTICE_SPECIFIC_KEY__RAW_VALUE = "rawValue";
    public static final String NOTICE_SPECIFIC_KEY__RANGE_MIN = "rangeMin";
    public static final String NOTICE_SPECIFIC_KEY__RANGE_MAX = "rangeMax";
    public static final String NOTICE_SPECIFIC_KEY__ACTUAL_VALUE = "actualValue";
    public static final String NOTICE_SPECIFIC_KEY__COLOR_VALUE = "colorValue";
    public static final String NOTICE_SPECIFIC_KEY__EMAIL_VALUE = "emailValue";
    public static final String NOTICE_SPECIFIC_KEY__LANG_VALUE = "langValue";
    public static final String NOTICE_SPECIFIC_KEY__TIME_VALUE = "timeValue";
    public static final String NOTICE_SPECIFIC_KEY__URL_VALUE = "urlValue";
    public static final String NOTICE_SPECIFIC_KEY__ENUM_VALUE = "enumValue";
    public static final String NOTICE_SPECIFIC_KEY__TIMEZONE_VALUE = "timezoneValue";
    public static final String NOTICE_SPECIFIC_KEY__CONFLICTING_FIELD_NAME = "conflictingFieldName";
    public static final String NOTICE_SPECIFIC_KEY__CURRENCY_CODE = "currencyCode";
    public static final String NOTICE_SPECIFIC_KEY__ROW_INDEX = "rowIndex";
    public static final String NOTICE_SPECIFIC_KEY__EXPECTED_LENGTH = "expectedLength";
    public static final String NOTICE_SPECIFIC_KEY__ACTUAL_LENGTH = "actualLength";
    public static final String NOTICE_SPECIFIC_KEY__MISSING_HEADER_NAME = "missingHeaderName";
    public static final String NOTICE_SPECIFIC_KEY__EXTRA_HEADER_NAME = "extraHeaderName";
    public static final String NOTICE_SPECIFIC_KEY__CONTRAST_RATIO = "contrastRatio";
    public static final String NOTICE_SPECIFIC_KEY__SHORT_NAME_LENGTH = "shortNameLength";

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
