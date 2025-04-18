/*
 * Copyright 2020 Google LLC, MobilityData IO
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

import com.google.common.base.CaseFormat;
import com.google.common.geometry.S2LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/** Base class for all notices produced by GTFS validator. */
public abstract class Notice {
  public static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(GtfsColor.class, new GtfsColorSerializer())
          .registerTypeAdapter(GtfsDate.class, new GtfsDateSerializer())
          .registerTypeAdapter(GtfsTime.class, new GtfsTimeSerializer())
          .registerTypeAdapter(S2LatLng.class, new S2LatLngSerializer())
          .serializeSpecialFloatingPointValues()
          .create();

  private static final String NOTICE_SUFFIX = "Notice";

  public JsonElement toJsonTree() {
    return GSON.toJsonTree(this);
  }

  public List<String> getAllFields() {
    return Arrays.stream(this.getClass().getDeclaredFields())
        .map(Field::getName) // Extract the name of each field
        .collect(Collectors.toList()); // Collect as a list of strings
  }

  /**
   * Returns a descriptive type-specific name for this notice based on the class simple name.
   *
   * @return notice code, e.g., "foreign_key_violation".
   */
  public String getCode() {
    return getCode(getClass());
  }

  /**
   * Returns a descriptive type-specific name for this notice class.
   *
   * @return notice code, e.g., "foreign_key_violation".
   */
  public static String getCode(Class<?> noticeClass) {
    return CaseFormat.UPPER_CAMEL.to(
        CaseFormat.LOWER_UNDERSCORE,
        StringUtils.removeEnd(noticeClass.getSimpleName(), NOTICE_SUFFIX));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    Notice notice = (Notice) o;

    JsonElement lhsJson = this.toJsonTree();
    JsonElement rhsJson = notice.toJsonTree();
    return lhsJson.equals(rhsJson);
  }

  @Override
  public String toString() {
    return toJsonTree().toString();
  }

  @Override
  public int hashCode() {
    return toJsonTree().hashCode();
  }

  private static class GtfsColorSerializer implements JsonSerializer<GtfsColor> {
    @Override
    public JsonElement serialize(GtfsColor src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.toHtmlColor());
    }
  }

  private static class GtfsDateSerializer implements JsonSerializer<GtfsDate> {
    @Override
    public JsonElement serialize(GtfsDate src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.toYYYYMMDD());
    }
  }

  private static class GtfsTimeSerializer implements JsonSerializer<GtfsTime> {
    @Override
    public JsonElement serialize(GtfsTime src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.toHHMMSS());
    }
  }

  /** Serializes {@link S2LatLng} as {@code [latDegrees, lngDegrees]} array. */
  private static class S2LatLngSerializer implements JsonSerializer<S2LatLng> {
    @Override
    public JsonElement serialize(S2LatLng src, Type typeOfSrc, JsonSerializationContext context) {
      JsonArray latLng = new JsonArray(2);
      latLng.add(src.latDegrees());
      latLng.add(src.lngDegrees());
      return latLng;
    }
  }
}
