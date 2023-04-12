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

package org.mobilitydata.gtfsvalidator.notice.schema;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.flogger.FluentLogger;
import com.google.common.geometry.S2LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Level;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.NoticeDocComments;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.schema.ReferencesSchema.UrlReference;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;
import org.mobilitydata.gtfsvalidator.table.GtfsEnum;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.ClassGraphDiscovery;

/** Exports schema describing all possible notices and their fields, as serialized to JSON . */
public class NoticeSchemaGenerator {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private static Gson GSON = new GsonBuilder().create();

  /**
   * Convenient function to find all notices in given packages and describe their fields.
   *
   * @param packages List of packages where notices are declared
   * @return a map describing all notices in given packages, keyed by notice code (see above)
   */
  public static Map<String, NoticeSchema> generateSchemasForNoticesInPackages(
      List<String> packages) {
    // Return a sorted TreeMap for stable results.
    Map<String, NoticeSchema> noticesByCode = new TreeMap<>();
    for (Class<Notice> noticeClass : ClassGraphDiscovery.discoverNoticeSubclasses(packages)) {
      noticesByCode.put(Notice.getCode(noticeClass), generateSchemaForNotice(noticeClass));
    }

    return noticesByCode;
  }

  @VisibleForTesting
  static NoticeSchema generateSchemaForNotice(Class<? extends Notice> noticeClass) {
    SeverityLevel severity = SeverityLevel.INFO;
    GtfsValidationNotice noticeAnnotation = noticeClass.getAnnotation(GtfsValidationNotice.class);
    if (noticeAnnotation != null) {
      severity = noticeAnnotation.severity();
    }

    NoticeSchema schema = new NoticeSchema(Notice.getCode(noticeClass), severity);

    NoticeDocComments comments = loadComments(noticeClass);
    if (comments.getDocComment() != null) {
      schema.setDescription(comments.getDocComment());
    }

    if (noticeAnnotation != null) {
      ReferencesSchema references = generateReferences(noticeAnnotation);
      if (!references.isEmpty()) {
        schema.setReferences(references);
      }
    }

    for (Field field : noticeClass.getDeclaredFields()) {
      FieldSchema fieldSchema =
          new FieldSchema(
              fieldTypeSchema(field.getType()),
              field.getName(),
              comments.getFieldComment(field.getName()));
      schema.addField(fieldSchema);
    }
    return schema;
  }

  private static NoticeDocComments loadComments(Class<?> noticeClass) {
    String resourceName = NoticeDocComments.getResourceNameForClass(noticeClass);
    InputStream is = noticeClass.getResourceAsStream(resourceName);
    if (is == null) {
      return new NoticeDocComments();
    }
    try (Reader reader = new InputStreamReader(is)) {
      return GSON.fromJson(reader, NoticeDocComments.class);
    } catch (IOException e) {
      logger.at(Level.WARNING).withCause(e).log(
          "Unable to read NoticeDocComments for notice class %s", noticeClass.getName());
      return new NoticeDocComments();
    }
  }

  private static ReferencesSchema generateReferences(GtfsValidationNotice noticeAnnotation) {
    ReferencesSchema schema = new ReferencesSchema();
    Arrays.stream(noticeAnnotation.files().value())
        .map(NoticeSchemaGenerator::getFileIdForTableClass)
        .flatMap(Optional::stream)
        .forEach(schema::addFileReference);
    Arrays.stream(noticeAnnotation.bestPractices().value())
        .map(NoticeSchemaGenerator::getFileIdForTableClass)
        .flatMap(Optional::stream)
        .forEach(schema::addBestPracticesFileReference);
    Arrays.stream(noticeAnnotation.urls())
        .map(NoticeSchemaGenerator::convertUrlRef)
        .forEach(schema::addUrlReference);
    return schema;
  }

  private static Optional<String> getFileIdForTableClass(Class<? extends GtfsEntity> tableClass) {
    GtfsTable table = tableClass.getAnnotation(GtfsTable.class);
    return Optional.ofNullable(table).map(GtfsTable::value);
  }

  private static UrlReference convertUrlRef(UrlRef ref) {
    return new UrlReference(ref.label(), ref.url());
  }

  static FieldTypeSchema fieldTypeSchema(Class<?> type) {
    if (type == int.class
        || type == long.class
        || type == short.class
        || type == byte.class
        || type == Integer.class
        || type == Long.class
        || type == Short.class
        || type == Byte.class) {
      return FieldTypeSchema.INTEGER;
    }
    if (type == double.class
        || type == float.class
        || type == Double.class
        || type == Float.class) {
      return FieldTypeSchema.NUMBER;
    }
    if (type == boolean.class || type == Boolean.class) {
      return FieldTypeSchema.BOOLEAN;
    }
    if (GtfsEnum.class.isAssignableFrom(type)) {
      return FieldTypeSchema.ENUM;
    }
    if (type == String.class
        || type == GtfsColor.class
        || type == GtfsDate.class
        || type == GtfsTime.class) {
      return FieldTypeSchema.STRING;
    }
    if (type == S2LatLng.class) {
      return FieldTypeSchema.array(FieldTypeSchema.NUMBER, 2, 2);
    }
    if (type == Object.class) {
      return FieldTypeSchema.OBJECT;
    }
    throw new IllegalArgumentException(String.format("Unsupported Java type for JSON: %s", type));
  }
}
