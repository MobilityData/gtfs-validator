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

package org.mobilitydata.gtfsvalidator.domain.entity.notice.error;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;

import java.io.IOException;

public class RouteColorAndTextInsufficientContrastNotice extends ErrorNotice {

    public RouteColorAndTextInsufficientContrastNotice(
            final String filename,
            final String entityId,
            final double contrastRatio) {
        super(filename, E_025,
                "Route color and text have insufficient contrast",
                "Contrast ratio should be >= 4.5 but was " + contrastRatio + " for Route:" + entityId + " in file:" + filename +
                        ". The route_text_color and route_color should be set to contrasting colors, as they are used " +
                        "as the text and background color (respectively) for displaying route names.  When left blank, " +
                        "route_text_color defaults to 000000 (black) and route_color defaults to FFFFFF (white).  A common " +
                        "source of issues here is setting route_color to a dark color, while leaving route_text_color set " +
                        "to black. In this case, route_text_color should be set to a lighter color like FFFFFF to ensure " +
                        "a legible contrast between the two. The contrast ratio formula used can be found here : " +
                        "https://www.w3.org/TR/WCAG20-TECHS/G17.html#G17-procedure",
                entityId);
        putExtra(NOTICE_SPECIFIC_KEY__CONTRAST_RATIO, contrastRatio);
    }

    @Override
    public void export(final NoticeExporter exporter)
            throws IOException {
        exporter.export(this);
    }
}