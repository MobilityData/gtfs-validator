/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.notice.warning;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.WarningNotice;

import java.io.IOException;

public class DuplicateRouteLongNameRouteShortNameCombinationNotice extends WarningNotice {
    public DuplicateRouteLongNameRouteShortNameCombinationNotice(final String routeId,
                                                                 final String conflictingRouteId,
                                                                 final String duplicateRouteLongName,
                                                                 final String duplicateRouteShortName) {
        super("routes.txt",
                W_016,
                "Duplicate `route_long_name` `route_short_name` combination",
                String.format("Routes with `route_id`: `%s` and `%s` have same `route_long_name`: `%s` and " +
                                "`route_short_name`: `%s`. The combination of these two fields should not be used " +
                                "more than once",
                        routeId,
                        conflictingRouteId,
                        duplicateRouteLongName,
                        duplicateRouteShortName),
                conflictingRouteId);
        putNoticeSpecific(KEY_ROUTE_CONFLICTING_ROUTE_ID, routeId);
        putNoticeSpecific(KEY_ROUTE_DUPLICATE_ROUTE_SHORT_NAME, duplicateRouteShortName);
        putNoticeSpecific(KEY_ROUTE_DUPLICATE_ROUTE_LONG_NAME, duplicateRouteLongName);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
