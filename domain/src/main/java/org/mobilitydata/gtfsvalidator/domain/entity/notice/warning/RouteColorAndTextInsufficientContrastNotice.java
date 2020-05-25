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

package org.mobilitydata.gtfsvalidator.domain.entity.notice.warning;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.WarningNotice;

import java.io.IOException;

public class RouteColorAndTextInsufficientContrastNotice extends WarningNotice {
    private String contrastRatio;

    public RouteColorAndTextInsufficientContrastNotice(final String filename, final String entityId, final String contrastRatio) {
        super(filename, W_006,
                "Route color and text have insufficient contrast",
                "Contrast ratio should be >= 4.5 but was " + contrastRatio + " for Route:" + entityId + " in file:" + filename,
                entityId);
        this.contrastRatio = contrastRatio;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getContrastRatio() {
        return contrastRatio;
    }

    @Override
    public void export(final NoticeExporter exporter)
            throws IOException {
        exporter.export(this);
    }
}