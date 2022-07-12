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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.table.SingleColumnPrimaryKeyTableContainer;
import org.mobilitydata.gtfsvalidator.table.SingleColumnPrimaryKeyTableLoader;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

@RunWith(JUnit4.class)
public class SingleColumnPrimaryKeySchemaTest {

  private SingleColumnPrimaryKeyTableLoader loader;
  private LoadingHelper helper;

  @Before
  public void setup() {
    loader = new SingleColumnPrimaryKeyTableLoader();
    helper = new LoadingHelper();
  }

  @Test
  public void testTableContainer() throws ValidatorLoaderException {
    SingleColumnPrimaryKeyTableContainer container =
        helper.load(loader, "id,fruit", "a,apples", "b,bananas", "c,cherries");

    assertThat(container.getKeyColumnNames()).containsExactly("id");

    assertThat(container.byId("a").get().fruit()).isEqualTo("apples");
    assertThat(container.byId("b").get().fruit()).isEqualTo("bananas");

    assertThat(container.byPrimaryKey("a").get().fruit()).isEqualTo("apples");
  }

  @Test
  public void testDuplicates() throws ValidatorLoaderException {
    SingleColumnPrimaryKeyTableContainer container =
        helper.load(loader, "id,fruit", "a,apples", "a,bananas");

    assertThat(helper.getValidationNotices()).hasSize(1);
    assertThat(helper.getValidationNotices().get(0).getCode()).isEqualTo("duplicate_key");
  }
}
