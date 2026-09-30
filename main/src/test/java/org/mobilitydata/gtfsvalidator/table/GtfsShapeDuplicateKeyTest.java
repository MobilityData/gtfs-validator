/*
 * Copyright 2026 MobilityData
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
package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;

import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

/**
 * Duplicate primary keys in shapes.txt, which is one of the two tables where they are detected by
 * scanning the sorted groups of the shape_id index instead of a map keyed on the composite key.
 */
@RunWith(JUnit4.class)
public class GtfsShapeDuplicateKeyTest {

  private static final String FILENAME = "shapes.txt";
  private static final String HEADER = "shape_id,shape_pt_lat,shape_pt_lon,shape_pt_sequence";

  private LoadingHelper helper;
  private GtfsShapeTableDescriptor tableDescriptor;

  @Before
  public void setup() {
    tableDescriptor = new GtfsShapeTableDescriptor();
    helper = new LoadingHelper();
  }

  @Test
  public void distinctKeys_noNotice() throws ValidatorLoaderException {
    helper.load(
        tableDescriptor, HEADER, "shape1,45.0,9.0,1", "shape1,45.1,9.1,2", "shape2,45.0,9.0,1");

    assertThat(helper.getValidationNotices()).isEmpty();
  }

  @Test
  public void duplicateKeys_reportedInFileOrderAgainstTheFirstRow()
      throws ValidatorLoaderException {
    helper.load(
        tableDescriptor,
        HEADER,
        "shape1,45.0,9.0,1", // row 2
        "shape2,45.0,9.0,1", // row 3
        "shape2,45.1,9.1,1", // row 4, duplicate of row 3
        "shape1,45.1,9.1,1", // row 5, duplicate of row 2
        "shape1,45.2,9.2,1"); // row 6, duplicate of row 2 as well

    assertThat(helper.getValidationNotices())
        .containsExactly(
            new DuplicateKeyNotice(FILENAME, 3, 4, "shape_id,shape_pt_sequence", "shape2,1"),
            new DuplicateKeyNotice(FILENAME, 2, 5, "shape_id,shape_pt_sequence", "shape1,1"),
            new DuplicateKeyNotice(FILENAME, 2, 6, "shape_id,shape_pt_sequence", "shape1,1"))
        .inOrder();
  }

  /** The index the duplicate detection relies on is still filled and sorted. */
  @Test
  public void byShapeId_isSortedByShapePtSequence() throws ValidatorLoaderException {
    GtfsShapeTableContainer container =
        helper.load(
            tableDescriptor, HEADER, "shape1,45.2,9.2,3", "shape1,45.0,9.0,1", "shape1,45.1,9.1,2");

    assertThat(helper.getValidationNotices()).isEmpty();
    assertThat(
            container.byShapeId("shape1").stream()
                .map(GtfsShape::shapePtSequence)
                .collect(Collectors.toList()))
        .containsExactly(1, 2, 3)
        .inOrder();
  }
}
