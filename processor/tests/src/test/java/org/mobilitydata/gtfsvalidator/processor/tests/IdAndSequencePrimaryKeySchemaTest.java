/*
 * Copyright 2022 Google LLC
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
package org.mobilitydata.gtfsvalidator.processor.tests;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.table.IdAndSequencePrimaryKey;
import org.mobilitydata.gtfsvalidator.table.IdAndSequencePrimaryKeyTableContainer;
import org.mobilitydata.gtfsvalidator.table.IdAndSequencePrimaryKeyTableDescriptor;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

@RunWith(JUnit4.class)
public class IdAndSequencePrimaryKeySchemaTest {

  private static final String FILENAME = "id_and_sequence_primary_key.txt";

  private IdAndSequencePrimaryKeyTableDescriptor tableDescriptor;
  private LoadingHelper helper;

  @Before
  public void setup() {
    tableDescriptor = new IdAndSequencePrimaryKeyTableDescriptor();
    helper = new LoadingHelper();
  }

  @Test
  public void testTableContainer() throws ValidatorLoaderException {
    IdAndSequencePrimaryKeyTableContainer container =
        helper.load(
            tableDescriptor,
            "id,sequence,fruit",
            "a,1,apples",
            "a,2,bananas",
            "b,2,dates",
            "b,1,cherries");

    assertThat(container.getKeyColumnNames()).containsExactly("id", "sequence");

    assertThat(fruits(container.byId("a"))).containsExactly("apples", "bananas").inOrder();
    assertThat(fruits(container.byId("b"))).containsExactly("cherries", "dates").inOrder();

    assertThat(fruits(container.byIdMap().get("a"))).containsExactly("apples", "bananas").inOrder();
    assertThat(fruits(container.byIdMap().get("b"))).containsExactly("cherries", "dates").inOrder();

    assertThat(container.byTranslationKey("a", "1").get().fruit()).isEqualTo("apples");
  }

  @Test
  public void testDuplicates() throws ValidatorLoaderException {
    IdAndSequencePrimaryKeyTableContainer container =
        helper.load(tableDescriptor, "id,sequence,fruit", "a,1,apples", "a,1,bananas");

    assertThat(helper.getValidationNotices()).hasSize(1);
    assertThat(helper.getValidationNotices().get(0).getCode()).isEqualTo("duplicate_key");
  }

  /** Every duplicate is reported against the first row with that key, not against the previous. */
  @Test
  public void testDuplicates_moreThanTwoRowsWithTheSameKey() throws ValidatorLoaderException {
    helper.load(tableDescriptor, "id,sequence,fruit", "a,1,apples", "a,1,bananas", "a,1,cherries");

    assertThat(helper.getValidationNotices())
        .containsExactly(
            new DuplicateKeyNotice(FILENAME, 2, 3, "id,sequence", "a,1"),
            new DuplicateKeyNotice(FILENAME, 2, 4, "id,sequence", "a,1"))
        .inOrder();
  }

  /**
   * Duplicates are found by scanning the sorted groups of the id index, which are iterated in no
   * useful order, so the notices have to be sorted back into the order of the rows they report.
   */
  @Test
  public void testDuplicates_areReportedInFileOrder() throws ValidatorLoaderException {
    helper.load(
        tableDescriptor,
        "id,sequence,fruit",
        "a,1,apples", // row 2
        "b,1,bananas", // row 3
        "b,1,cherries", // row 4, duplicate of row 3
        "a,1,dates", // row 5, duplicate of row 2
        "b,2,elderberries", // row 6
        "b,2,figs"); // row 7, duplicate of row 6

    assertThat(helper.getValidationNotices())
        .containsExactly(
            new DuplicateKeyNotice(FILENAME, 3, 4, "id,sequence", "b,1"),
            new DuplicateKeyNotice(FILENAME, 2, 5, "id,sequence", "a,1"),
            new DuplicateKeyNotice(FILENAME, 6, 7, "id,sequence", "b,2"))
        .inOrder();
  }

  /** Rows without an id share the default value, and duplicates among them are still reported. */
  @Test
  public void testDuplicates_missingId() throws ValidatorLoaderException {
    helper.load(tableDescriptor, "id,sequence,fruit", ",1,apples", ",1,bananas");

    assertThat(helper.getValidationNotices())
        .containsExactly(new DuplicateKeyNotice(FILENAME, 2, 3, "sequence", 1));
  }

  @Test
  public void testByTranslationKey() throws ValidatorLoaderException {
    IdAndSequencePrimaryKeyTableContainer container =
        helper.load(
            tableDescriptor, "id,sequence,fruit", "a,1,apples", "a,1,bananas", "a,2,cherries");

    // As when the lookup map was filled while detecting duplicates, the first row with a given
    // key wins.
    assertThat(container.byTranslationKey("a", "1").get().fruit()).isEqualTo("apples");
    assertThat(container.byTranslationKey("a", "2").get().fruit()).isEqualTo("cherries");
    assertThat(container.byTranslationKey("a", "3").isPresent()).isFalse();
    assertThat(container.byTranslationKey("z", "1").isPresent()).isFalse();
    assertThat(container.byTranslationKey("a", "not a number").isPresent()).isFalse();
  }

  private List<String> fruits(List<IdAndSequencePrimaryKey> objects) {
    return objects.stream().map(IdAndSequencePrimaryKey::fruit).collect(Collectors.toList());
  }
}
