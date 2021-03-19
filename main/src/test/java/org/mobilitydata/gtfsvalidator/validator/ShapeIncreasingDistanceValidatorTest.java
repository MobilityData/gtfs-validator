/*
 * Copyright 2020 Google LLC
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
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.validator.ShapeIncreasingDistanceValidator.DecreasingOrEqualShapeDistanceNotice;

public class ShapeIncreasingDistanceValidatorTest {
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

  private static List<ValidationNotice> generateNotices(List<GtfsShape> shapes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new ShapeIncreasingDistanceValidator(
            GtfsShapeTableContainer.forEntities(shapes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void increasingDistanceAlongShapeShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createShapePoint(1, "first shape", 30.0d, 45, 1, 10.0d),
                    createShapePoint(2, "first shape", 31.0d, 42, 2, 45.0d),
                    createShapePoint(3, "first shape", 29.0d, 46, 3, 64.0d))))
        .isEmpty();
  }

  @Test
  public void lastShapeWithDecreasingDistanceAlongShapeShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createShapePoint(1, "first shape", 30.0d, 45, 1, 10.0d),
                    createShapePoint(2, "first shape", 31.0d, 42, 2, 45.0d),
                    createShapePoint(3, "first shape", 29.0d, 46, 3, 40.0))))
        .containsExactly(
            new DecreasingOrEqualShapeDistanceNotice("first shape", 3, 40.0d, 3, 2, 45.0d, 2));
  }

  @Test
  public void oneIntermediateShapeWithDecreasingDistanceAlongShapeShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createShapePoint(1, "first shape", 30.0d, 45, 1, 10.0d),
                    createShapePoint(2, "first shape", 31.0d, 42, 2, 9.0d),
                    createShapePoint(3, "first shape", 45.0d, 46, 3, 40.0))))
        .containsExactly(
            new DecreasingOrEqualShapeDistanceNotice("first shape", 2, 9.0d, 2, 1, 10.0d, 1));
  }

  @Test
  public void shapeWithEqualDistanceAlongShapeShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createShapePoint(1, "first shape", 30.0d, 45, 1, 10.0d),
                    createShapePoint(2, "first shape", 31.0d, 42, 2, 45.0d),
                    createShapePoint(3, "first shape", 29.0d, 46, 3, 45.0))))
        .containsExactly(
            new DecreasingOrEqualShapeDistanceNotice("first shape", 3, 45.0d, 3, 2, 45.0d, 2));
  }
}
