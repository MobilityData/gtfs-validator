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

package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.geometry.S2LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.ClassGraphDiscovery;

/** Exports schema describing all possible notices and their contexts. */
public class NoticeSchemaGenerator {

  /**
   * Exports JSON schema for all notices in given packages. This includes notices that are declared
   * inside validators.
   *
   * <p>See https://json-schema.org/ for more information on JSON schema.
   *
   * <p>The returned object looks like that:
   *
   * <pre>{@code
   * {
   *   "attribution_without_role": {
   *     "type": "object",
   *     "properties": {
   *       "attributionId": {
   *         "type": "string"
   *       },
   *       "csvRowNumber": {
   *         "type": "integer"
   *       }
   *     }
   *   },
   *   "duplicate_fare_rule_zone_id_fields": {
   *     "type": "object",
   *     "properties": {
   *       "csvRowNumber": {
   *         "type": "integer"
   *       },
   *       "fareId": {
   *         "type": "string"
   *       },
   *       "previousCsvRowNumber": {
   *         "type": "integer"
   *       },
   *       "previousFareId": {
   *         "type": "string"
   *       }
   *     }
   *   },
   *   ...
   * }
   * }</pre>
   *
   * @param packages List of packages where notices are declared. Use {@link
   *     ClassGraphDiscovery#DEFAULT_NOTICE_PACKAGES} and add your custom Java packages here, if any
   * @return a {@link JsonObject} describing all notices in given packages (see above)
   * @throws IOException
   */
  public static JsonObject jsonSchemaForPackages(List<String> packages) throws IOException {
    JsonObject schema = new JsonObject();
    for (Map.Entry<String, Map<String, Class<?>>> entry :
        contextFieldsInPackages(packages).entrySet()) {
      schema.add(entry.getKey(), jsonSchemaForNotice(entry.getKey(), entry.getValue()));
    }
    return schema;
  }

  /**
   * Convenient function to find all notices in given packages and describe their fields.
   *
   * <p>The returned map has looks this way:
   *
   * <pre>{@code
   * {
   *   "AttributionWithoutRoleNotice": {
   *     "attributionId": String.class,
   *     "csvRowNumber": Long.class,
   *   }
   * }
   * }</pre>
   *
   * @param packages List of packages where notices are declared
   * @return a map describing all notices in given packages (see above)
   * @throws IOException
   */
  @VisibleForTesting
  static Map<String, Map<String, Class<?>>> contextFieldsInPackages(List<String> packages)
      throws IOException {
    // Return a sorted TreeMap for stable results.
    Map<String, Map<String, Class<?>>> contextFieldsByNotice = new TreeMap<>();

    for (Class<Notice> noticeClass : ClassGraphDiscovery.discoverNoticeSubclasses(packages)) {
      contextFieldsByNotice.put(Notice.getCode(noticeClass), contextFieldsForNotice(noticeClass));
    }

    return contextFieldsByNotice;
  }

  @VisibleForTesting
  static Map<String, Class<?>> contextFieldsForNotice(Class<? extends Notice> noticeClass) {
    // Return a sorted TreeMap for stable results.
    Map<String, Class<?>> fields = new TreeMap<>();
    for (Field field : noticeClass.getDeclaredFields()) {
      fields.put(field.getName(), field.getType());
    }
    return fields;
  }

  private static boolean isSubclassOf(Class<?> parent, Class<?> child) {
    return !child.equals(parent) && parent.isAssignableFrom(child);
  }

  private static final class JsonTypes {

    private static final String NUMBER = "number";
    private static final String INTEGER = "integer";
    private static final String STRING = "string";
    private static final String BOOLEAN = "boolean";
    private static final String ARRAY = "array";
  }

  static JsonObject s2LatLngToJsonType() {
    JsonObject contains = new JsonObject();
    contains.addProperty("type", JsonTypes.NUMBER);

    JsonObject typeDef = new JsonObject();
    typeDef.addProperty("type", JsonTypes.ARRAY);
    typeDef.add("contains", contains);
    typeDef.addProperty("minItems", 2);
    typeDef.addProperty("maxItems", 2);
    return typeDef;
  }

  static JsonArray objectToJsonType() {
    JsonArray array = new JsonArray();
    array.add(JsonTypes.STRING);
    array.add(JsonTypes.INTEGER);
    array.add(JsonTypes.NUMBER);
    return array;
  }

  static JsonElement javaTypeToJson(Class<?> type) {
    if (type == int.class
        || type == long.class
        || type == short.class
        || type == byte.class
        || type == Integer.class
        || type == Long.class
        || type == Short.class
        || type == Byte.class) {
      return new JsonPrimitive(JsonTypes.INTEGER);
    }
    if (type == double.class
        || type == float.class
        || type == Double.class
        || type == Float.class) {
      return new JsonPrimitive(JsonTypes.NUMBER);
    }
    if (type == boolean.class || type == Boolean.class) {
      return new JsonPrimitive(JsonTypes.BOOLEAN);
    }
    if (type == String.class
        || type == GtfsColor.class
        || type == GtfsDate.class
        || type == GtfsTime.class) {
      return new JsonPrimitive(JsonTypes.STRING);
    }
    if (type == Object.class) {
      return objectToJsonType();
    }
    throw new IllegalArgumentException(String.format("Unsupported Java type for JSON: %s", type));
  }

  static JsonObject fieldTypeSchema(Class<?> fieldType) {
    if (fieldType == S2LatLng.class) {
      return s2LatLngToJsonType();
    }
    JsonObject schema = new JsonObject();
    schema.add("type", javaTypeToJson(fieldType));
    return schema;
  }

  @VisibleForTesting
  static JsonObject jsonSchemaForNotice(String noticeClass, Map<String, Class<?>> fields) {
    JsonObject properties = new JsonObject();

    for (Map.Entry<String, Class<?>> field : fields.entrySet()) {
      try {
        properties.add(field.getKey(), fieldTypeSchema(field.getValue()));
      } catch (IllegalArgumentException e) {
        throw new IllegalStateException(
            String.format("Cannot generate schema for %s.%s", noticeClass, field.getKey()), e);
      }
    }
    JsonObject schema = new JsonObject();
    schema.addProperty("type", "object");
    schema.add("properties", properties);
    return schema;
  }
}
