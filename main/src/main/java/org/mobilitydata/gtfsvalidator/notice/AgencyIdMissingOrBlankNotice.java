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

package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

/**
 * `agency.agency_id` should be provided even if `agency.txt` only counts one unique row.
 *
 * <p>Severity: {@code SeverityLevel.WARNING}
 */
public class AgencyIdMissingOrBlankNotice extends ValidationNotice {
  public AgencyIdMissingOrBlankNotice(long csvRowNumber) {
    super(
        new ImmutableMap.Builder<String, Object>().put("csvRowNumber", csvRowNumber).build(),
        SeverityLevel.WARNING);
  }

  @Override
  public String getCode() {
    return "agency_id_missing_or_blank";
  }
}
