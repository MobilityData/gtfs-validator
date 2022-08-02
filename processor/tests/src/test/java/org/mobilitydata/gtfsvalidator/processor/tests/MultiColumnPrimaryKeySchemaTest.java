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
import static com.google.common.truth.Truth8.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.table.MultiColumnPrimaryKey;
import org.mobilitydata.gtfsvalidator.table.MultiColumnPrimaryKeyTableContainer;
import org.mobilitydata.gtfsvalidator.table.MultiColumnPrimaryKeyTableLoader;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

@RunWith(JUnit4.class)
public class MultiColumnPrimaryKeySchemaTest {

  private MultiColumnPrimaryKeyTableLoader loader;
  private LoadingHelper helper;

  @Before
  public void setup() {
    loader = new MultiColumnPrimaryKeyTableLoader();
    helper = new LoadingHelper();
  }

  @Test
  public void testTableContainer() throws ValidatorLoaderException {
    MultiColumnPrimaryKeyTableContainer container =
        helper.load(
            loader,
            "id_a,id_b,id_c,fruit",
            "a1,a2,a3,apples",
            "a1,a2x,a3x,apricots",
            "b1,b2,b3,bananas",
            "c1,c2,c3,cherries",
            "d1,d2,,dates");

    assertThat(container.getKeyColumnNames()).containsExactly("id_a", "id_b", "id_c");

    assertThat(fruits(container.byIdA("a1"))).containsExactly("apples", "apricots");
    assertThat(fruits(container.byIdA("b1"))).containsExactly("bananas");

    assertThat(container.byTranslationKey("a1", "a2")).isEmpty();
    assertThat(container.byTranslationKey("d1", "d2").get().fruit()).isEqualTo("dates");
  }

  @Test
  public void testDuplicates() throws ValidatorLoaderException {
    helper.load(loader, "id_a,id_b,id_c,fruit", "a1,a2,a3,apples", "a1,a2,a3,apricots");

    assertThat(helper.getValidationNotices()).hasSize(1);
    assertThat(helper.getValidationNotices().get(0).getCode()).isEqualTo("duplicate_key");
  }

  private List<String> fruits(List<MultiColumnPrimaryKey> objects) {
    return objects.stream().map(MultiColumnPrimaryKey::fruit).collect(Collectors.toList());
  }
}
