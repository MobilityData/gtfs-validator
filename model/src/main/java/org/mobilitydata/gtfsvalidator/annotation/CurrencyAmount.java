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

package org.mobilitydata.gtfsvalidator.annotation;

/**
 * Generates a org.mobilitydata.gtfsvalidator.notice.InvalidCurrencyAmountNotice for a
 * currency amount value that does not match the specification of its currency code.
 *
 * <p>Example.
 *
 * <pre>
 *   {@literal @}GtfsTable("fare_products.txt")
 *   public interface GtfsExampleFaresSchema extends GtfsEntity {
 *       {@literal @}CurrencyAmount(currencyField="currency")
 *       BigDecimal amount();
 *
 *       Currency currency();
 *   }
 * </pre>
 */
public @interface CurrencyAmount {

  /**
   * The name of a field in the table that specifies the currency code for this currency amount.
   * Used to determine the proper format of the currency amount.
   */
  String currencyField();
}
