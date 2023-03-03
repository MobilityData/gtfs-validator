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
import org.mobilitydata.gtfsvalidator.table.MultiColumnLocalePrimaryKeyTableContainer;
import org.mobilitydata.gtfsvalidator.table.MultiColumnLocalePrimaryKeyTableDescriptor;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

@RunWith(JUnit4.class)
public class MultiColumnLocalePrimaryKeySchemaTest {

  private MultiColumnLocalePrimaryKeyTableDescriptor tableDescriptor;
  private LoadingHelper helper;

  @Before
  public void setup() {
    tableDescriptor = new MultiColumnLocalePrimaryKeyTableDescriptor();
    helper = new LoadingHelper();
  }

  /**
   * This test focuses on the edge case of a GTFS field with a nullable default value. This
   * typically includes non-primitive, non-string fields (e.g. Locale). We encountered a bug with
   * key construction when one such value was missing.
   */
  @Test
  public void testNullKeys() throws ValidatorLoaderException {
    MultiColumnLocalePrimaryKeyTableContainer container =
        helper.load(tableDescriptor, "id,language,translation", "a1,,apples");

    MultiColumnLocalePrimaryKeyTableContainer.CompositeKey key =
        MultiColumnLocalePrimaryKeyTableContainer.CompositeKey.builder().setId("a1").build();
    assertThat(container.byTranslationKey("a1", "").get().translation()).isEqualTo("apples");
  }
}
