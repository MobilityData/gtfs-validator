/*
 * Copyright 2020 Google LLC, MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnusedShapeNotice;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

public class ShapeUsageValidatorTest {

  public static GtfsShape createShapePoint(
      long csvRowNumber,
      String shapeId,
      double shapePtLat,
      double shapePtLon,
      int shapePtSequence,
      double shapeDistTraveled) {
    return new GtfsShape.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setShapeId(shapeId)
        .setShapePtLat(shapePtLat)
        .setShapePtLon(shapePtLon)
        .setShapePtSequence(shapePtSequence)
        .setShapeDistTraveled(shapeDistTraveled)
        .build();
  }

  public static GtfsTrip createTrip(
      long csvRowNumber, String routeId, String serviceId, String tripId, String shapeId) {
    return new GtfsTrip.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setRouteId(routeId)
        .setServiceId(serviceId)
        .setTripId(tripId)
        .setShapeId(shapeId)
        .build();
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsShape> shapes, List<GtfsTrip> trips) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new ShapeUsageValidator(
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsShapeTableContainer.forEntities(shapes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void allShapeUsedShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createShapePoint(1, "first shape id", 45.0d, 45.0d, 2, 40.0d),
                    createShapePoint(3, "second shape id", 45.0d, 45.0d, 2, 40.0d)),
                ImmutableList.of(
                    createTrip(
                        2, "route id value", "service id value", "first trip id", "first shape id"),
                    createTrip(
                        4,
                        "other route id value",
                        "other service id value",
                        "second trip id",
                        "second shape id"))))
        .isEmpty();
  }

  @Test
  public void unusedShapeShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createShapePoint(1, "first shape id", 45.0d, 45.0d, 2, 40.0d),
                    createShapePoint(3, "second shape id", 45.0d, 45.0d, 2, 40.0d)),
                ImmutableList.of(
                    createTrip(
                        2,
                        "route id value",
                        "service id value",
                        "first trip id",
                        "first shape id"))))
        .containsExactly(new UnusedShapeNotice("second shape id", 3));
  }
}
