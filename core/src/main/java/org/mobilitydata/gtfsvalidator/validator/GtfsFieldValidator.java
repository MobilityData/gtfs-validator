/*
 * Copyright 2021 Google LLC
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

/**
 * Validator for a single field in a GTFS file.
 *
 * <p>All methods of this validator store notices in the provided container. If an error is stored,
 * then the value is considered invalid.
 */
public interface GtfsFieldValidator {

  /**
   * Validates a single field of any type.
   *
   * <p>This validation performs common checks that do not depend on field type.
   *
   * <p>This function returns a possibly updated and fixed content for that field, e.g., with
   * stripped trailing and leading whitespaces.
   */
  String validateField(
      String fieldValue, GtfsCellContext cellContext, NoticeContainer noticeContainer);

  /** Validates an ID field and adds notices to the container. */
  void validateId(String id, GtfsCellContext cellContext, NoticeContainer noticeContainer);

  /** Validates a URL field and adds notices to the container. */
  void validateUrl(String url, GtfsCellContext cellContext, NoticeContainer noticeContainer);

  /** Validates an e-mail field and adds notices to the container. */
  void validateEmail(String email, GtfsCellContext cellContext, NoticeContainer noticeContainer);

  /** Validates a phone number field and adds notices to the container. */
  void validatePhoneNumber(
      String phoneNumber, GtfsCellContext cellContext, NoticeContainer noticeContainer);
}
