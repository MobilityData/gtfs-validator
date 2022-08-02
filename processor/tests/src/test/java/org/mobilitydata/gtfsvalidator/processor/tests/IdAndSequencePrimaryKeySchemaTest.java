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
import org.mobilitydata.gtfsvalidator.table.IdAndSequencePrimaryKey;
import org.mobilitydata.gtfsvalidator.table.IdAndSequencePrimaryKeyTableContainer;
import org.mobilitydata.gtfsvalidator.table.IdAndSequencePrimaryKeyTableLoader;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

@RunWith(JUnit4.class)
public class IdAndSequencePrimaryKeySchemaTest {

  private IdAndSequencePrimaryKeyTableLoader loader;
  private LoadingHelper helper;

  @Before
  public void setup() {
    loader = new IdAndSequencePrimaryKeyTableLoader();
    helper = new LoadingHelper();
  }

  @Test
  public void testTableContainer() throws ValidatorLoaderException {
    IdAndSequencePrimaryKeyTableContainer container =
        helper.load(
            loader, "id,sequence,fruit", "a,1,apples", "a,2,bananas", "b,2,dates", "b,1,cherries");

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
        helper.load(loader, "id,sequence,fruit", "a,1,apples", "a,1,bananas");

    assertThat(helper.getValidationNotices()).hasSize(1);
    assertThat(helper.getValidationNotices().get(0).getCode()).isEqualTo("duplicate_key");
  }

  private List<String> fruits(List<IdAndSequencePrimaryKey> objects) {
    return objects.stream().map(IdAndSequencePrimaryKey::fruit).collect(Collectors.toList());
  }
}
