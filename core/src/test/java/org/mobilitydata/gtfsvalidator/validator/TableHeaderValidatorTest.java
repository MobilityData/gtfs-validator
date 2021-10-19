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

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.DuplicatedColumnNotice;
import org.mobilitydata.gtfsvalidator.notice.EmptyColumnNameNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredColumnNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnknownColumnNotice;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;

@RunWith(JUnit4.class)
public class TableHeaderValidatorTest {
  @Test
  public void expectedColumns() {
    NoticeContainer container = new NoticeContainer();
    new DefaultTableHeaderValidator()
        .validate(
            "stops.txt",
            new CsvHeader(new String[] {"stop_id", "stop_name"}),
            ImmutableSet.of("stop_id", "stop_name", "stop_lat", "stop_lon"),
            ImmutableSet.of("stop_id"),
            container);

    assertThat(container.getValidationNotices()).isEmpty();
    assertThat(container.hasValidationErrors()).isFalse();
  }

  @Test
  public void unknownColumnShouldGenerateNotice() {
    NoticeContainer container = new NoticeContainer();
    new DefaultTableHeaderValidator()
        .validate(
            "stops.txt",
            new CsvHeader(new String[] {"stop_id", "stop_name", "stop_extra"}),
            ImmutableSet.of("stop_id", "stop_name"),
            ImmutableSet.of("stop_id"),
            container);

    assertThat(container.getValidationNotices())
        .containsExactly(new UnknownColumnNotice("stops.txt", "stop_extra", 3));
    assertThat(container.hasValidationErrors()).isFalse();
  }

  @Test
  public void missingRequiredColumnShouldGenerateNotice() {
    NoticeContainer container = new NoticeContainer();
    new DefaultTableHeaderValidator()
        .validate(
            "stops.txt",
            new CsvHeader(new String[] {"stop_name"}),
            ImmutableSet.of("stop_id", "stop_name"),
            ImmutableSet.of("stop_id"),
            container);

    assertThat(container.getValidationNotices())
        .containsExactly(new MissingRequiredColumnNotice("stops.txt", "stop_id"));
    assertThat(container.hasValidationErrors()).isTrue();
  }

  @Test
  public void duplicatedColumnShouldGenerateNotice() {
    NoticeContainer container = new NoticeContainer();
    new DefaultTableHeaderValidator()
        .validate(
            "stops.txt",
            new CsvHeader(new String[] {"stop_id", "stop_name", "stop_id"}),
            ImmutableSet.of("stop_id", "stop_name"),
            ImmutableSet.of("stop_id"),
            container);

    assertThat(container.getValidationNotices())
        .containsExactly(new DuplicatedColumnNotice("stops.txt", "stop_id", 1, 3));
    assertThat(container.hasValidationErrors()).isTrue();
  }

  @Test
  public void emptyFileShouldNotGenerateNotice() {
    NoticeContainer container = new NoticeContainer();
    new DefaultTableHeaderValidator()
        .validate(
            "stops.txt",
            CsvHeader.EMPTY,
            ImmutableSet.of("stop_id", "stop_name"),
            ImmutableSet.of("stop_id"),
            container);

    assertThat(container.getValidationNotices()).isEmpty();
    assertThat(container.hasValidationErrors()).isFalse();
  }

  @Test
  public void emptyColumnNameShouldGenerateNotice() {
    NoticeContainer container = new NoticeContainer();
    new DefaultTableHeaderValidator()
        .validate(
            "stops.txt",
            new CsvHeader(new String[] {"stop_id", null, "stop_name", ""}),
            ImmutableSet.of("stop_id", "stop_name"),
            ImmutableSet.of("stop_id"),
            container);

    assertThat(container.getValidationNotices())
        .containsExactly(
            new EmptyColumnNameNotice("stops.txt", 2), new EmptyColumnNameNotice("stops.txt", 4));
    assertThat(container.hasValidationErrors()).isTrue();
  }
}
