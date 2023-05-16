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
import java.util.Arrays;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mobilitydata.gtfsvalidator.notice.MixedCaseRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.table.MixedCaseTest;
import org.mobilitydata.gtfsvalidator.table.MixedCaseTestTableDescriptor;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.MixedCaseTestMixedCaseValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

@RunWith(Parameterized.class)
public class MixedCaseSchemaTest {
  private final String value;
  private final boolean isValid;

  public MixedCaseSchemaTest(String value, boolean isValid) {
    this.value = value;
    this.isValid = isValid;
  }

  private MixedCaseTestTableDescriptor tableDescriptor;
  private LoadingHelper helper;

  @Before
  public void setup() throws ValidatorLoaderException {
    tableDescriptor = new MixedCaseTestTableDescriptor();
    helper = new LoadingHelper();
    helper.setValidatorLoader(
        ValidatorLoader.createForClasses(ImmutableList.of(MixedCaseTestMixedCaseValidator.class)));
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(
        new Object[][] {
          // valid values
          {"Mixed-Case", true},
          {"Mixed_Case", true},
          {"Mixed Case", true},
          {"22222", true},
          {"A1", true},
          {"ZA112", true},
          {"301", true},
          {"RTE 30", true},
          {"급 행 12", true},
          {"급행12", true},
          {"東西線", true},
          {"101B", true},
          {"A14C", true},
          {"A14c", true},
          {"A14-C", true},
          {"A14_C", true},
          {"A14 C", true},
          {"Route 1", true},
          {"Route 1 Boulevard", true},
          {"ZA12", true},
          {"Avenue des Champs-Élysées", true},
          // invalid values
          {"lowercase", false},
          {"UPPERCASE", false},
          {"snake_case", false},
          {"kebab-case", false},
          {"UPPER-CASE", false},
          {"lower case space", false},
          {"ROUTE 22", false},
          {"34broadst", false},
          {"ROUTE 1", false},
          {"route 1 Boulevard", false},
          {"Another bad value", false},
          {"MixedCaseButSingleWord", false},
        });
  }

  @Test
  public void testMixedCase() throws ValidatorLoaderException {
    helper.load(tableDescriptor, MixedCaseTest.SOME_FIELD_FIELD_NAME, value);
    if (isValid) {
      assertThat(helper.getValidationNotices()).isEmpty();
    } else {
      assertThat(helper.getValidationNotices())
          .containsExactly(
              new MixedCaseRecommendedFieldNotice(
                  MixedCaseTest.FILENAME, MixedCaseTest.SOME_FIELD_FIELD_NAME, value, 2));
    }
  }
}
