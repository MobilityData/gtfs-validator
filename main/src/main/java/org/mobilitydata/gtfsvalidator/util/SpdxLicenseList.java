/*
 * Copyright 2026 MobilityData IO
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

import com.google.common.base.Ascii;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * The SPDX license list, loaded from the bundled copy of {@code licenses.json} from <a
 * href="https://github.com/spdx/license-list-data">spdx/license-list-data</a>.
 *
 * <p>License identifiers are matched case-insensitively, as required by the SPDX specification.
 */
public final class SpdxLicenseList {

  /** Classpath location of the bundled SPDX license list. */
  static final String RESOURCE_PATH = "/spdx/licenses.json";

  private static final SpdxLicenseList INSTANCE = load();

  /** Whether each license is deprecated, keyed by lowercased license identifier. */
  private final ImmutableMap<String, Boolean> deprecatedByLowercaseId;

  private final String version;

  private SpdxLicenseList(Map<String, Boolean> deprecatedByLowercaseId, String version) {
    this.deprecatedByLowercaseId = ImmutableMap.copyOf(deprecatedByLowercaseId);
    this.version = version;
  }

  /** Returns the bundled SPDX license list. */
  public static SpdxLicenseList getInstance() {
    return INSTANCE;
  }

  /** Tells whether {@code licenseId} is in the SPDX license list, deprecated or not. */
  public boolean isKnown(String licenseId) {
    return deprecatedByLowercaseId.containsKey(Ascii.toLowerCase(licenseId));
  }

  /** Tells whether {@code licenseId} is in the SPDX license list and marked deprecated. */
  public boolean isDeprecated(String licenseId) {
    return deprecatedByLowercaseId.getOrDefault(Ascii.toLowerCase(licenseId), false);
  }

  /** The number of license identifiers in the list, deprecated ones included. */
  int size() {
    return deprecatedByLowercaseId.size();
  }

  /** The version of the bundled SPDX license list, e.g. {@code 3.29.0}. */
  public String getVersion() {
    return version;
  }

  private static SpdxLicenseList load() {
    try (InputStream inputStream = SpdxLicenseList.class.getResourceAsStream(RESOURCE_PATH)) {
      if (inputStream == null) {
        throw new IllegalStateException("Missing SPDX license list resource " + RESOURCE_PATH);
      }
      try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        Map<String, Boolean> deprecatedByLowercaseId = new HashMap<>();
        for (JsonElement element : root.getAsJsonArray("licenses")) {
          JsonObject license = element.getAsJsonObject();
          deprecatedByLowercaseId.put(
              Ascii.toLowerCase(license.get("licenseId").getAsString()),
              license.get("isDeprecatedLicenseId").getAsBoolean());
        }
        return new SpdxLicenseList(
            deprecatedByLowercaseId, root.get("licenseListVersion").getAsString());
      }
    } catch (IOException e) {
      throw new UncheckedIOException("Cannot read SPDX license list " + RESOURCE_PATH, e);
    }
  }
}
