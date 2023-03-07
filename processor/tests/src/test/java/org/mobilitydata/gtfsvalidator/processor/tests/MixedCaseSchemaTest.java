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

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MixedCaseRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.table.MixedCaseTest;
import org.mobilitydata.gtfsvalidator.table.MixedCaseTestTableDescriptor;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.MixedCaseTestMixedCaseValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

@RunWith(JUnit4.class)
public class MixedCaseSchemaTest {

  private MixedCaseTestTableDescriptor tableDescriptor;
  private LoadingHelper helper;

  @Before
  public void setup() throws ValidatorLoaderException {
    tableDescriptor = new MixedCaseTestTableDescriptor();
    helper = new LoadingHelper();
    helper.setValidatorLoader(
        ValidatorLoader.createForClasses(ImmutableList.of(MixedCaseTestMixedCaseValidator.class)));
  }

  @Test
  public void testValidMixedCase() throws ValidatorLoaderException {

    helper.load(tableDescriptor, MixedCaseTest.SOME_FIELD_FIELD_NAME, "MixedCaseValue");

    assertThat(helper.getValidationNotices()).isEmpty();

    helper.load(tableDescriptor, MixedCaseTest.SOME_FIELD_FIELD_NAME, "Mixed Case Value");

    assertThat(helper.getValidationNotices()).isEmpty();

    helper.load(tableDescriptor, MixedCaseTest.SOME_FIELD_FIELD_NAME, "Another good value");

    assertThat(helper.getValidationNotices()).isEmpty();
  }

  @Test
  public void testInvalidMixedCases() throws ValidatorLoaderException {

    helper.load(tableDescriptor, MixedCaseTest.SOME_FIELD_FIELD_NAME, "lowercase");

    assertThat(helper.getValidationNotices())
        .containsExactly(
            new MixedCaseRecommendedFieldNotice(
                MixedCaseTest.FILENAME, MixedCaseTest.SOME_FIELD_FIELD_NAME, 2));

    helper.load(tableDescriptor, MixedCaseTest.SOME_FIELD_FIELD_NAME, "UPPERCASE");

    assertThat(helper.getValidationNotices())
        .containsExactly(
            new MixedCaseRecommendedFieldNotice(
                MixedCaseTest.FILENAME, MixedCaseTest.SOME_FIELD_FIELD_NAME, 2));

    helper.load(tableDescriptor, MixedCaseTest.SOME_FIELD_FIELD_NAME, "snake_case");

    assertThat(helper.getValidationNotices())
        .containsExactly(
            new MixedCaseRecommendedFieldNotice(
                MixedCaseTest.FILENAME, MixedCaseTest.SOME_FIELD_FIELD_NAME, 2));

    helper.load(tableDescriptor, MixedCaseTest.SOME_FIELD_FIELD_NAME, "kebab-case");

    assertThat(helper.getValidationNotices())
        .containsExactly(
            new MixedCaseRecommendedFieldNotice(
                MixedCaseTest.FILENAME, MixedCaseTest.SOME_FIELD_FIELD_NAME, 2));

    helper.load(tableDescriptor, MixedCaseTest.SOME_FIELD_FIELD_NAME, "UPPER-CASE");

    assertThat(helper.getValidationNotices())
        .containsExactly(
            new MixedCaseRecommendedFieldNotice(
                MixedCaseTest.FILENAME, MixedCaseTest.SOME_FIELD_FIELD_NAME, 2));

    helper.load(tableDescriptor, MixedCaseTest.SOME_FIELD_FIELD_NAME, "lower case space");

    assertThat(helper.getValidationNotices())
        .containsExactly(
            new MixedCaseRecommendedFieldNotice(
                MixedCaseTest.FILENAME, MixedCaseTest.SOME_FIELD_FIELD_NAME, 2));
  }
}
