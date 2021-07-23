/*
 * Copyright 2020 Google LLC
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

import static org.mobilitydata.gtfsvalidator.annotation.SchemaExport.BIGINT_DATA_TYPE;
import static org.mobilitydata.gtfsvalidator.annotation.SchemaExport.BLOB_DATA_TYPE;
import static org.mobilitydata.gtfsvalidator.annotation.SchemaExport.CLASS_SIMPLE_NAME_SEVERITY_LEVEL;
import static org.mobilitydata.gtfsvalidator.annotation.SchemaExport.INTEGER_DATA_TYPE;
import static org.mobilitydata.gtfsvalidator.annotation.SchemaExport.JSON_KEY_NAME;
import static org.mobilitydata.gtfsvalidator.annotation.SchemaExport.VARCHAR_DATA_TYPE;
import static org.mobilitydata.gtfsvalidator.annotation.SchemaExport.JSON_KEY_TYPE;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.reflect.ClassPath;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.mobilitydata.gtfsvalidator.annotation.SchemaExport;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Container for validation notices (errors and warnings).
 *
 * <p>This class is not intentionally not thread-safe to increase performance. Each thread has it's
 * own NoticeContainer, and after execution is complete the results are merged.
 */
public class NoticeContainer {
  private static final Gson DEFAULT_GSON =
      new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().create();
  private static final Gson PRETTY_GSON = DEFAULT_GSON.newBuilder().setPrettyPrinting().create();

  /** Limit on the amount of exported notices of the same type and severity. */
  private static final int MAX_EXPORTS_PER_NOTICE_TYPE = 100000;

  /**
   * Limit on the total amount of stored validation notices.
   *
   * <p>This is a measure to prevent OOM in the rare case when each row in a large file (such as
   * stop_times.txt or shapes.txt) produces a notice. Since this case is rare, we just introduce a
   * total limit on the amount of notices instead of counting amount of notices of each type.
   *
   * <p>Note that system errors are not limited since we don't expect to have a lot of them.
   */
  private static final int MAX_VALIDATION_NOTICES = 10000000;

  private final List<ValidationNotice> validationNotices = new ArrayList<>();
  private final List<SystemError> systemErrors = new ArrayList<>();
  private boolean hasValidationErrors = false;

  /** Adds a new validation notice to the container (if there is capacity). */
  public void addValidationNotice(ValidationNotice notice) {
    if (notice.isError()) {
      hasValidationErrors = true;
    }
    if (validationNotices.size() <= MAX_VALIDATION_NOTICES) {
      validationNotices.add(notice);
    }
  }

  /** Adds a new system error to the container (if there is capacity). */
  public void addSystemError(SystemError error) {
    systemErrors.add(error);
  }

  /**
   * Adds all validation notices and system errors from another container.
   *
   * <p>This is useful for multithreaded validation: each thread has its own notice container which
   * is merged into the global container when the thread finishes.
   *
   * @param otherContainer a container to take the notices from
   */
  public void addAll(NoticeContainer otherContainer) {
    validationNotices.addAll(otherContainer.validationNotices);
    systemErrors.addAll(otherContainer.systemErrors);
    hasValidationErrors |= otherContainer.hasValidationErrors;
  }

  /** Tells if this container has any {@code ValidationNotice} that is an error. */
  public boolean hasValidationErrors() {
    return hasValidationErrors;
  }

  /** Returns a list of all validation notices in the container. */
  public List<ValidationNotice> getValidationNotices() {
    return Collections.unmodifiableList(validationNotices);
  }

  /** Returns a list of all system errors in the container. */
  public List<SystemError> getSystemErrors() {
    return Collections.unmodifiableList(systemErrors);
  }

  /** Exports all validation notices as JSON. */
  public String exportValidationNotices(boolean isPretty) {
    return exportJson(validationNotices, isPretty);
  }

  /** Exports all system errors as JSON. */
  public String exportSystemErrors(boolean isPretty) {
    return exportJson(systemErrors, isPretty);
  }

  /**
   * Exports notices as JSON.
   *
   * <p>Up to {@link #MAX_EXPORTS_PER_NOTICE_TYPE} is exported per each type+severity.
   */
  public static <T extends Notice> String exportJson(List<T> notices, boolean isPretty) {
    JsonObject root = new JsonObject();
    JsonArray jsonNotices = new JsonArray();
    root.add("notices", jsonNotices);
    Gson gson = isPretty ? PRETTY_GSON : DEFAULT_GSON;

    for (Collection<T> noticesOfType : groupNoticesByTypeAndSeverity(notices).asMap().values()) {
      JsonObject noticesOfTypeJson = new JsonObject();
      jsonNotices.add(noticesOfTypeJson);
      T firstNotice = noticesOfType.iterator().next();
      noticesOfTypeJson.addProperty("code", firstNotice.getCode());
      noticesOfTypeJson.addProperty("severity", firstNotice.getSeverityLevel().toString());
      noticesOfTypeJson.addProperty("totalNotices", noticesOfType.size());
      JsonArray noticesArrayJson = new JsonArray();
      noticesOfTypeJson.add("notices", noticesArrayJson);
      int i = 0;
      for (T notice : noticesOfType) {
        ++i;
        if (i > MAX_EXPORTS_PER_NOTICE_TYPE) {
          // Do not export too many notices for this type.
          break;
        }
        noticesArrayJson.add(gson.toJsonTree(notice.getContext()));
      }
    }

    return gson.toJson(root);
  }

  private static <T extends Notice> ListMultimap<String, T> groupNoticesByTypeAndSeverity(
      List<T> notices) {
    ListMultimap<String, T> noticesByType = MultimapBuilder.treeKeys().arrayListValues().build();
    for (T notice : notices) {
      noticesByType.put(notice.getCode() + notice.getSeverityLevel().ordinal(), notice);
    }
    return noticesByType;
  }

  /**
   * Exports notices information as a json file.
   *
   * @param isPretty will beautify the output if set to true
   * @param noticePackageName the name of the package that contains the notices
   * @param validatorPackageName the name of the package that contains the {@code GtfsValidator}s
   * @return the stringified json file that contains information about all {@code ValidationNotice}s
   * @throws IOException if the attempt to read class path resources (jar files or directories)
   * failed.
   * */
  public static String exportNoticesSchema(boolean isPretty, String noticePackageName,
      String validatorPackageName)
      throws IOException {
    Gson gson = isPretty ? PRETTY_GSON : DEFAULT_GSON;
    JsonObject coreNoticeProperties = extractCoreNoticesProperties(noticePackageName);
    JsonObject mainNoticesProperties = extractMainNoticesProperties(validatorPackageName);
    JsonObject root =
        mergeGsonObjects(ImmutableList.of(coreNoticeProperties, mainNoticesProperties));
    return gson.toJson(root);
  }

  /**
   * Merge multiple {@code com.google.gson.JsonObject} into one.
   *
   * @param gsonObjects {@code com.google.gson.JsonObject}s to merge
   * @return the merged {@code com.google.gson.JsonObject}
   */
  private static JsonObject mergeGsonObjects(ImmutableList<JsonObject> gsonObjects) {
    JsonObject toReturn = new JsonObject();
    gsonObjects.forEach(gsonObject -> {
      gsonObject.entrySet().forEach(entry -> {
        toReturn.add(entry.getKey(), entry.getValue());
      });
    });
    return toReturn;
  }

  /**
   * Extract information from notices defined in the core module.
   *
   * @param noticePackageName the name of the package that contains the notices
   * @return the {@code com.google.gson.JsonObject} that contains the {@code ValidationNotice} class
   * simple name, and the type of each parameter
   * @throws IOException if the attempt to read class path resources (jar files or directories)
   * failed.
   */
  private static JsonObject extractCoreNoticesProperties(String noticePackageName)
      throws IOException {
    Class<?> clazz;
    JsonObject toReturn = new JsonObject();
    for (ClassPath.ClassInfo noticeClass :
        ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClasses(noticePackageName)) {
      clazz = noticeClass.load();
      if (!ValidationNotice.class.isAssignableFrom(clazz)) {
        continue;
      }
      if (clazz.isEnum()) {
        continue;
      }
      if (clazz.getSimpleName().equals(NoticeContainer.class.getSimpleName())) {
        continue;
      }
      if (clazz.getSimpleName().equals(ValidationNotice.class.getSimpleName())) {
        continue;
      }
      toReturn.add(
          noticeClass.getSimpleName(),
          extractNoticeProperties(clazz)
      );
    }
    return toReturn;
  }

  /**
   * Extract information from notices defined in the main module.
   *
   * @param validatorPackageName the name of the package that contains the {@code GtfsValidator}s
   * @return the {@code com.google.gson.JsonObject} that contains the {@code ValidationNotice} class
   * simple name, and the type of each parameter
   * @throws IOException if the attempt to read class path resources (jar files or directories)
   * failed.
   */
  private static JsonObject extractMainNoticesProperties(String validatorPackageName)
      throws IOException {
    JsonObject toReturn = new JsonObject();
    for (ClassPath.ClassInfo validatorClass : ClassPath.from(ClassLoader.getSystemClassLoader())
            .getTopLevelClasses(validatorPackageName)) {
      for (Class<?> innerNoticeClass : validatorClass.load().getDeclaredClasses()) {
        if (ValidationNotice.class.isAssignableFrom(innerNoticeClass))
        toReturn.add(
            innerNoticeClass.getSimpleName(),
            extractNoticeProperties(innerNoticeClass)
        );
      }
    }
    return toReturn;
  }

  /**
   * Return a {@code JsonArray} that contains information about the type of each parameter of
   * a {@code ValidationNotice} using the constructor of the class that is annotated by
   * {@code  NoticeExport}.
   *
   * @param validationNoticeSubClass the {@code ValidationNotice} sub class to extract information
   *                                from
   * @return a {@code JsonArray} that contains information about the type of each parameter of
   * said {@code ValidationNotice} using the constructor of the class that is annotated by
   * {@code NoticeExport}.
   */
  private static JsonArray extractNoticeProperties(Class<?> validationNoticeSubClass) {
    Constructor<?> constructor = getAnnotatedConstructor(validationNoticeSubClass);
    JsonArray parametersAsJsonArray = new JsonArray();
    Arrays.stream(constructor.getParameters()).forEach(parameter -> {
      JsonObject parameterDetails = new JsonObject();
      parameterDetails.addProperty(JSON_KEY_NAME, parameter.getName());
        parameterDetails.addProperty(JSON_KEY_TYPE, mapDataType(parameter));
      parametersAsJsonArray.add(parameterDetails);
    });
    if (!noticeConstructorHasSeverityLevelParameter(constructor)) {
      JsonObject severityDetails = new JsonObject();
      severityDetails.addProperty(JSON_KEY_NAME, CLASS_SIMPLE_NAME_SEVERITY_LEVEL);
      severityDetails.addProperty(JSON_KEY_TYPE, BLOB_DATA_TYPE);
      parametersAsJsonArray.add(severityDetails);
    }
    return parametersAsJsonArray;
  }

  /**
   * Return true if a notice constructor includes a parameter of type {@code SeverityLevel},
   * otherwise returns false.
   *
   * @param constructor the constructor to analyze
   * @return true if a notice constructor includes a parameter of type {@code SeverityLevel},
   * otherwise returns false.
   */
  private static boolean noticeConstructorHasSeverityLevelParameter(Constructor<?> constructor) {
    for (Parameter parameter : constructor.getParameters()) {
      if (parameter.getType().getSimpleName().equals(CLASS_SIMPLE_NAME_SEVERITY_LEVEL)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Maps Java data type to Oracle's JDBC data types. Said types definitions can be found at the
   * following url: https://docs.oracle.com/cd/E19830-01/819-4721/beajw/index.html.
   *
   * @param parameter the parameter whose JDBC data type has to be inferred
   * @return the JDBC data type that is used in mapping Java data fields to SQL types
   */
  private static String mapDataType(Parameter parameter) {
    ImmutableMap.Builder<Class<?>, String> builder = ImmutableMap.builder();
    builder.put(int.class, INTEGER_DATA_TYPE);
    builder.put(GtfsColor.class, VARCHAR_DATA_TYPE);
    builder.put(String.class, VARCHAR_DATA_TYPE);
    builder.put(long.class, BIGINT_DATA_TYPE);
    builder.put(SeverityLevel.class, BLOB_DATA_TYPE);
    builder.put(GtfsDate.class, BLOB_DATA_TYPE);
    builder.put(GtfsTime.class, BLOB_DATA_TYPE);
    builder.put(Object.class, BLOB_DATA_TYPE);
    ImmutableMap<Class<?>, String> typeMap = builder.build();
    return JDBCType.valueOf(
        typeMap.getOrDefault(
            parameter.getType(),
            parameter.getType().getSimpleName().toUpperCase()))
        .getName();
  }

  /**
   * Returns the annotated constructor of a {@code ValidationNotice} subclass.
   * Throws {@code AnnotationFormatError} if the {@code ValidationNotice} subclass does not define a
   * constructor for schema export i.e. no constructor for the given class ses {@code NoticeExport}
   * annotation.
   *
   * @param clazz the class to extract the annotated constructor from
   * @return the annotated constructor of a {@code ValidationNotice} subclass.
   * Throws {@code AnnotationFormatError} if the {@code ValidationNotice} subclass does not define a
   * constructor for schema export i.e. no constructor for the given class uses {@code NoticeExport}
   * annotation.
   */
  private static Constructor<?> getAnnotatedConstructor(Class<?> clazz) {
    for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
      if (constructor.isAnnotationPresent(SchemaExport.class)) {
        return constructor;
      }
    }
    throw new AnnotationFormatError(
        String.format(
            "Validation Notice %s does not define constructor for schema export", clazz.getSimpleName()));
  }
}
