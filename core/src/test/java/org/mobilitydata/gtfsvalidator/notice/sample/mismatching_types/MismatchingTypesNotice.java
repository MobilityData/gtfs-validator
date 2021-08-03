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

package org.mobilitydata.gtfsvalidator.notice.sample.mismatching_types;

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.annotation.SchemaExport;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;

/**
 * {@code ValidationNotice} defined for tests purpose: two constructors that define the same
 * parameter with different types.
 */
public class MismatchingTypesNotice extends ValidationNotice {

  @SchemaExport
  public MismatchingTypesNotice(String filename, long csvRowNumber,
      Integer parameterWithWrongType) {
    super(new ImmutableMap.Builder<String, Object>()
            .put("filename", filename)
            .put("csvRowNumber", csvRowNumber)
            .put("parameterWithWrongType", parameterWithWrongType)
            .build(),
        SeverityLevel.WARNING);
  }

  @SchemaExport
  public MismatchingTypesNotice(String filename, long csvRowNumber, String parameterWithWrongType) {
    super(new ImmutableMap.Builder<String, Object>()
            .put("filename", filename)
            .put("csvRowNumber", csvRowNumber)
            .put("parameterWithWrongType", parameterWithWrongType)
            .build(),
        SeverityLevel.WARNING);
  }
}
