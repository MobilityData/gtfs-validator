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

package org.mobilitydata.gtfsvalidator.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.validator.routines.UrlValidator;

/** Utility functions for validating urls */
public class UrlUtil {
  private static final int LOWER_BOUND_RESPONSE_CODE = 200;
  private static final int UPPER_BOUND_RESPONSE_CODE = 399;

  public enum UrlStatus {
    VALID,
    INVALID,
    NOT_FOUND
  }
  /**
   * Validates the provided URL for correctness and accessibility.
   *
   * <p>This method first checks if the provided URL string is in a valid format, then checks if the
   * URL is accessible by making an HTTP connection and checking the response code. The status of
   * the URL is determined based on these checks and returned as a UrlStatus enum value.
   *
   * @param urlString The URL string to be validated.
   * @return UrlStatus - Returns the status of the URL. - INVALID: If the URL is not in a valid
   *     format. - NOT_FOUND: If the URL is in a valid format but is not accessible (i.e., it does
   *     not respond or throws an exception). - VALID: If the URL is in a valid format and is
   *     accessible.
   */
  public static UrlStatus validateUrl(String urlString) {
    if (!UrlValidator.getInstance().isValid(urlString)) {
      return UrlStatus.INVALID;
    }
    try {
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      int responseCode = connection.getResponseCode();
      return responseCode >= LOWER_BOUND_RESPONSE_CODE && responseCode <= UPPER_BOUND_RESPONSE_CODE
          ? UrlStatus.VALID
          : UrlStatus.NOT_FOUND;
    } catch (MalformedURLException e) {
      return UrlStatus.INVALID;
    } catch (IOException e) {
      return UrlStatus.NOT_FOUND;
    }
  }

  private UrlUtil() {}
}
