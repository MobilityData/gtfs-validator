///*
// *  Copyright (c) 2020. MobilityData IO.
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//
//package org.mobilitydata.gtfsvalidator.domain.entity.notice.error;
//
//import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
//import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;
//
//import java.io.IOException;
//
//public class DecreasingShapeDistanceNotice extends ErrorNotice {
//    public DecreasingShapeDistanceNotice(final String shapeId,
//                                         final int shapePtSequence,
//                                         final Float shapeDistTraveled,
//                                         final int previousShapePtSequence,
//                                         final Float previousShapeDistTraveled) {
//        super("shapes.txt", E_058,
//                "Decreasing `shape_dist_traveled` values",
//                String.format("`shape_id`: `%s` `shape_pt_sequence`: `%s` has a larger `shape_dist_traveled` (`%s`) than" +
//                                " `shape_pt_sequence`: `%s` (`%s`). `shape_dist_traveled` must increase with" +
//                                " `shape_pt_sequence`.",
//                        shapeId, shapePtSequence, shapeDistTraveled, previousShapePtSequence,
//                        previousShapeDistTraveled),
//                shapeId);
//        putNoticeSpecific(KEY_SHAPE_PT_SEQUENCE, shapePtSequence);
//        putNoticeSpecific(KEY_SHAPE_DIST_TRAVELED, shapeDistTraveled);
//        putNoticeSpecific(KEY_SHAPE_PREVIOUS_SHAPE_PT_SEQUENCE, previousShapePtSequence);
//        putNoticeSpecific(KEY_SHAPE_PREVIOUS_SHAPE_DIST_TRAVELED, previousShapeDistTraveled);
//    }
//
//    @Override
//    public void export(final NoticeExporter exporter) throws IOException {
//        exporter.export(this);
//    }
//}
