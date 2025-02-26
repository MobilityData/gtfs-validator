/*
 * Copyright 2023 MobilityData
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
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

public class HttpGetUtil {

  public static final String USER_AGENT_PREFIX = "MobilityData GTFS-Validator";

  /**
   * @param validatorVersion version of the validator
   * @return the user agent string in the format: "MobilityData GTFS-Validator/{validatorVersion}
   *     (Java {java version})"
   */
  public static String getUserAgent(String validatorVersion) {
    return USER_AGENT_PREFIX
        + "/"
        + (validatorVersion != null ? validatorVersion : "")
        + " (Java "
        + System.getProperty("java.version")
        + ")";
  }

  /**
   * Downloads data from network.
   *
   * @param sourceUrl the fully qualified URL
   * @param outputStream the output stream
   * @param validatorVersion the version of the validator
   */
  public static void loadFromUrl(URL sourceUrl, OutputStream outputStream, String validatorVersion)
      throws IOException, URISyntaxException {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpGet request = new HttpGet(sourceUrl.toString());
      request.addHeader("User-Agent", getUserAgent(validatorVersion));
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        response.getEntity().writeTo(outputStream);
      }
    }
  }
}
