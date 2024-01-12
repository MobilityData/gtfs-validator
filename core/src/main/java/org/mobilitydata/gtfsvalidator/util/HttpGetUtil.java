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

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

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
     * @throws IOException if no file could not be found at the specified location
     * @throws URISyntaxException if URL is malformed
     */
    public static void loadFromUrl(URL sourceUrl, OutputStream outputStream, String validatorVersion)
            throws IOException, URISyntaxException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(sourceUrl.toURI());
            httpGet.setHeader("User-Agent", getUserAgent(validatorVersion));
            try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
                httpResponse.getEntity().writeTo(outputStream);
            }
        }
    }
}
