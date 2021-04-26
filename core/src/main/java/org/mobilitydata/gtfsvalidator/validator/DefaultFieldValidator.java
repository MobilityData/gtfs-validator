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

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.notice.InvalidEmailNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidPhoneNumberNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidUrlNotice;
import org.mobilitydata.gtfsvalidator.notice.LeadingOrTrailingWhitespacesNotice;
import org.mobilitydata.gtfsvalidator.notice.NewLineInValueNotice;
import org.mobilitydata.gtfsvalidator.notice.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

/** Default implementation of {@link GtfsFieldValidator}. */
public class DefaultFieldValidator implements GtfsFieldValidator {

  private final CountryCode countryCode;

  public DefaultFieldValidator(CountryCode countryCode) {
    this.countryCode = countryCode;
  }

  @Override
  public String validateField(
      String fieldValue, GtfsCellContext cellContext, NoticeContainer noticeContainer) {
    if (fieldValue.indexOf('\n') != -1 || fieldValue.indexOf('\r') != -1) {
      noticeContainer.addValidationNotice(
          new NewLineInValueNotice(
              cellContext.filename(),
              cellContext.csvRowNumber(),
              cellContext.fieldName(),
              fieldValue));
    }
    final String trimmed = fieldValue.trim();
    if (trimmed.length() < fieldValue.length()) {
      noticeContainer.addValidationNotice(
          new LeadingOrTrailingWhitespacesNotice(
              cellContext.filename(),
              cellContext.csvRowNumber(),
              cellContext.fieldName(),
              fieldValue));
      return trimmed;
    }
    return fieldValue;
  }

  @Override
  public boolean validateId(
      String id, GtfsCellContext cellContext, NoticeContainer noticeContainer) {
    if (!hasOnlyPrintableAscii(id)) {
      noticeContainer.addValidationNotice(
          new NonAsciiOrNonPrintableCharNotice(
              cellContext.filename(), cellContext.csvRowNumber(), cellContext.fieldName(), id));
      return false;
    }
    return true;
  }

  @Override
  public boolean validateUrl(
      String url, GtfsCellContext cellContext, NoticeContainer noticeContainer) {
    if (!UrlValidator.getInstance().isValid(url)) {
      noticeContainer.addValidationNotice(
          new InvalidUrlNotice(
              cellContext.filename(), cellContext.csvRowNumber(), cellContext.fieldName(), url));
      return false;
    }
    return true;
  }

  @Override
  public boolean validateEmail(
      String email, GtfsCellContext cellContext, NoticeContainer noticeContainer) {
    if (!EmailValidator.getInstance().isValid(email)) {
      noticeContainer.addValidationNotice(
          new InvalidEmailNotice(
              cellContext.filename(), cellContext.csvRowNumber(), cellContext.fieldName(), email));
      return false;
    }
    return true;
  }

  @Override
  public boolean validatePhoneNumber(
      String phoneNumber, GtfsCellContext cellContext, NoticeContainer noticeContainer) {
    if (!PhoneNumberUtil.getInstance()
        .isPossibleNumber(phoneNumber, countryCode.getCountryCode())) {
      noticeContainer.addValidationNotice(
          new InvalidPhoneNumberNotice(
              cellContext.filename(),
              cellContext.csvRowNumber(),
              cellContext.fieldName(),
              phoneNumber));
      return false;
    }
    return true;
  }

  static boolean hasOnlyPrintableAscii(String s) {
    for (int i = 0, n = s.length(); i < n; ++i) {
      if (!(s.charAt(i) >= 32 && s.charAt(i) < 127)) {
        return false;
      }
    }
    return true;
  }
}
