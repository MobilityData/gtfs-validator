/*
 * Copyright 2021 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.validator.sample;

import com.google.common.collect.ImmutableMap;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.SchemaExport;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.validator.FileValidator;

/**
 * Sample validator for test to be used in {@code NoticeContainerTest#exportNoticesSchema}. No
 * validation rule is implemented here.
 */
@GtfsValidator
public class SampleTestValidator extends FileValidator {

  @Inject
  SampleTestValidator() {
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
  }

  static class NestedTestValidatorNotice extends ValidationNotice {

    @SchemaExport
    NestedTestValidatorNotice(String tripId, long csvRowNumber) {
      super(new ImmutableMap.Builder<String, Object>()
              .put("tripId", tripId)
              .put("csvRowNumber", csvRowNumber)
              .build(),
          SeverityLevel.WARNING);
    }
  }
}
