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

package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.notice.EmptyFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

/** Runs GtfsShapeTableLoader on test CSV data. */
@RunWith(JUnit4.class)
public class GtfsShapeTableLoaderTest {
  private static final GtfsFeedName FEED_NAME = GtfsFeedName.parseString("au-sydney-buses");

  @Test
  public void validFileShouldNotGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader =
        new StringReader(
            "shape_id,shape_pt_lat,shape_pt_lon,shape_pt_sequence"
                + System.lineSeparator()
                + "shape id value,30.34,10.76,20");
    GtfsShapeTableLoader loader = new GtfsShapeTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsShapeTableContainer tableContainer =
        (GtfsShapeTableContainer) loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);
    reader.close();

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
    assertThat(tableContainer.entityCount()).isEqualTo(1);
    GtfsShape shape = tableContainer.byShapeId("shape id value").get(0);
    assertThat(shape).isNotNull();
    assertThat(shape.shapeId()).matches("shape id value");
    assertThat(shape.shapePtLat()).isEqualTo(30.34);
    assertThat(shape.shapePtLon()).isEqualTo(10.76);
    assertThat(shape.shapePtSequence()).isEqualTo(20);
  }

  @Test
  public void missingRequiredFieldShouldGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader =
        new StringReader(
            "shape_id,shape_pt_lat,shape_pt_lon,shape_pt_sequence"
                + System.lineSeparator()
                + "shape id value,,10.76,20");
    GtfsShapeTableLoader loader = new GtfsShapeTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsShapeTableContainer tableContainer =
        (GtfsShapeTableContainer) loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);
    reader.close();

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new MissingRequiredFieldError("shapes.txt", 2, "shape_pt_lat"));
    assertThat(tableContainer.entityCount()).isEqualTo(0);
  }

  @Test
  public void emptyFileShouldGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader = new StringReader("");
    GtfsShapeTableLoader loader = new GtfsShapeTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();

    loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);
    reader.close();

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new EmptyFileNotice("shapes.txt"));
  }
}
