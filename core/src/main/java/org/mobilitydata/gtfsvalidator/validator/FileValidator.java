/*
 * Copyright 2020 Google LLC
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

package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

/** Interface for validators that handle one as a whole or several files. */
public abstract class FileValidator {
  public abstract void validate(NoticeContainer noticeContainer);

  /**
   * Check if the validate method should be called. For example if the child field is a ForeignKey,
   * there's no point to validate if the child field column does not exist.
   *
   * @return true if the validate method should be called.
   */
  public Boolean shouldCallValidate() {
    return true;
  }
}
