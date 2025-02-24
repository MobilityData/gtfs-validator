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
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.InvalidCurrencyAmountNotice;
import org.mobilitydata.gtfsvalidator.table.CurrencyAmountTableDescriptor;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.CurrencyAmountCurrencyAmountValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

@RunWith(JUnit4.class)
public class CurrencyAmountSchemaTest {

  private CurrencyAmountTableDescriptor tableDescriptor;
  private LoadingHelper helper;

  @Before
  public void setup() throws ValidatorLoaderException {
    tableDescriptor = new CurrencyAmountTableDescriptor();
    helper = new LoadingHelper();
    helper.setValidatorLoader(
        ValidatorLoader.createForClasses(
            ImmutableList.of(CurrencyAmountCurrencyAmountValidator.class)));
  }

  @Test
  public void testValidCurrencyUSD() throws ValidatorLoaderException {

    helper.load(tableDescriptor, "amount,currency", "1.50,USD");

    assertThat(helper.getValidationNotices()).isEmpty();
  }

  @Test
  public void testInvalidCurrencyUSD() throws ValidatorLoaderException {
    helper.load(tableDescriptor, "amount,currency", "1.5,USD");

    assertThat(helper.getValidationNotices())
        .containsExactly(
            new InvalidCurrencyAmountNotice(
                "currency_amount.txt", 2, "USD", new BigDecimal("1.5")));
  }

  @Test
  public void testValidCurrencyISK() throws ValidatorLoaderException {
    // Icelandic króna expects no digits after decimal separator.
    helper.load(tableDescriptor, "amount,currency", "5,ISK");

    assertThat(helper.getValidationNotices()).isEmpty();
  }

  @Test
  public void testInvalidCurrencyISK() throws ValidatorLoaderException {
    // Icelandic króna expects no digits after decimal separator.
    helper.load(tableDescriptor, "amount,currency", "5.0,ISK");

    assertThat(helper.getValidationNotices())
        .containsExactly(
            new InvalidCurrencyAmountNotice(
                "currency_amount.txt", 2, "ISK", new BigDecimal("5.0")));
  }
}
