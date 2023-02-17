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

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MixedCaseNotice;
import org.mobilitydata.gtfsvalidator.table.MixedCase;
import org.mobilitydata.gtfsvalidator.table.MixedCaseTableDescriptor;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.MixedCaseMixedCaseValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class MixedCaseSchemaTest {

    private MixedCaseTableDescriptor tableDescriptor;
    private LoadingHelper helper;

    @Before
    public void setup() throws ValidatorLoaderException {
        tableDescriptor = new MixedCaseTableDescriptor();
        helper = new LoadingHelper();
        helper.setValidatorLoader(
                ValidatorLoader.createForClasses(
                        ImmutableList.of(MixedCaseMixedCaseValidator.class)));
    }
    @Test
    public void testValidMixedCase() throws ValidatorLoaderException {

        helper.load(tableDescriptor, MixedCase.MIXED_CASE_FIELD_NAME, "MixedCase");

        assertThat(helper.getValidationNotices()).isEmpty();
    }
    @Test
    public void testInvalidMixedCase() throws ValidatorLoaderException {

        helper.load(tableDescriptor, MixedCase.MIXED_CASE_FIELD_NAME, "lowercase");

        assertThat(helper.getValidationNotices())
                .containsExactly(
                        new MixedCaseNotice(
                                MixedCase.FILENAME, MixedCase.MIXED_CASE_FIELD_NAME, 2));
    }

}
