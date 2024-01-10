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
package org.mobilitydata.gtfsvalidator.testing;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.DateForValidation;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.AnyTableLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableDescriptor;
import org.mobilitydata.gtfsvalidator.validator.DefaultValidatorProvider;
import org.mobilitydata.gtfsvalidator.validator.ValidationContext;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;

/** Convenience methods for loading files in unit-tests. */
public class LoadingHelper {

  private CountryCode countryCode = CountryCode.forStringOrUnknown("ca");
  private LocalDate dateForValidation = LocalDate.of(2021, 1, 1);

  private NoticeContainer noticeContainer = new NoticeContainer();

  /**
   * We explicitly do not scan and load all validators by default, because we want to keep the set
   * of validators used in unit-tests minimal and stable.
   */
  private ValidatorLoader validatorLoader = ValidatorLoader.createEmpty();

  public List<ValidationNotice> getValidationNotices() {
    return noticeContainer.getValidationNotices();
  }

  public void setValidatorLoader(ValidatorLoader validatorLoader) {
    this.validatorLoader = validatorLoader;
  }

  public <X extends GtfsEntity, Y extends GtfsTableContainer<X>> Y load(
      GtfsTableDescriptor<X> tableDescriptor, String... lines) throws ValidatorLoaderException {
    String content = Arrays.stream(lines).collect(Collectors.joining("\n"));
    InputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    noticeContainer = new NoticeContainer(); // reset notices if reloading helper

    ValidationContext context =
        ValidationContext.builder()
            .setCountryCode(countryCode)
            .setDateForValidation(new DateForValidation(dateForValidation))
            .build();
    ValidatorProvider provider = new DefaultValidatorProvider(context, validatorLoader);
    return (Y) AnyTableLoader.load(tableDescriptor, provider, in, noticeContainer);
  }
}
