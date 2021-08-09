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
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/** Base class for all notices produced by GTFS validator. */
public abstract class Notice {
  public static final Gson GSON =
      new GsonBuilder()
          .setExclusionStrategies(new NoticeExclusionStrategy())
          .registerTypeAdapter(GtfsColor.class, new GtfsColorSerializer())
          .registerTypeAdapter(GtfsDate.class, new GtfsDateSerializer())
          .registerTypeAdapter(GtfsTime.class, new GtfsTimeSerializer())
          .serializeSpecialFloatingPointValues()
          .create();

  private static final String NOTICE_SUFFIX = "_notice";

  private SeverityLevel severityLevel;

  public Notice(SeverityLevel severityLevel) {
    this.severityLevel = severityLevel;
  }

  public JsonElement getContext() {
    return GSON.toJsonTree(this);
  }

  public SeverityLevel getSeverityLevel() {
    return this.severityLevel;
  }

  /**
   * Returns a descriptive type-specific name for this notice based on the class simple name.
   *
   * @return notice code, e.g., "foreign_key_violation".
   */
  public String getCode() {
    return StringUtils.removeEnd(
        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, getClass().getSimpleName()),
        NOTICE_SUFFIX);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Notice) {
      Notice otherNotice = (Notice) other;
      return getClass().equals(otherNotice.getClass())
          && severityLevel.equals(otherNotice.severityLevel)
          && getContext().equals(otherNotice.getContext());
    }
    return false;
  }

  @Override
  public String toString() {
    return String.format("%s %s %s", getCode(), getSeverityLevel(), getContext());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getClass(), getContext(), getSeverityLevel());
  }

  /**
   * Tells if this notice is an {@code ERROR}.
   *
   * <p>This method is preferred to checking {@code severityLevel} directly since more levels may be
   * added in the future.
   *
   * @return true if this notice is an error, false otherwise
   */
  public boolean isError() {
    return getSeverityLevel().ordinal() >= SeverityLevel.ERROR.ordinal();
  }

  /** JSON exclusion strategy for notice context. It skips {@link Notice#severityLevel}. */
  private static class NoticeExclusionStrategy implements ExclusionStrategy {
    public boolean shouldSkipClass(Class<?> clazz) {
      return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {
      return f.getName().equals("severityLevel");
    }
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
}
