/*
 * Copyright 2021 MobilityData IO
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.jdi.InvalidTypeException;
import java.io.IOException;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.mobilitydata.gtfsvalidator.annotation.SchemaExport;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * RTTI machine for schema generation.
 */
public class NoticeSchemaGenerator {

  private static final Gson DEFAULT_GSON =
      new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().create();
  private static final Gson PRETTY_GSON = DEFAULT_GSON.newBuilder().setPrettyPrinting().create();
  private static final String SEVERITY_LEVEL_PARAMETER_NAME = "severityLevel";
  private static final String JSON_KEY_NAME = "name";
  private static final String JSON_KEY_TYPE = "type";
  private static final String BLOB_DATA_TYPE = "BLOB";
  private static final String VARCHAR_DATA_TYPE = "VARCHAR";
  private static final String BIGINT_DATA_TYPE = "BIGINT";
  private static final String INTEGER_DATA_TYPE = "INTEGER";
  /**
   * Map of data type to Oracle's JDBC data types.
   */
  private static final ImmutableMap<Class<?>, String> typeMap =
      new ImmutableMap.Builder<Class<?>, String>()
          .put(int.class, INTEGER_DATA_TYPE)
          .put(GtfsColor.class, VARCHAR_DATA_TYPE)
          .put(String.class, VARCHAR_DATA_TYPE)
          .put(long.class, BIGINT_DATA_TYPE)
          .put(SeverityLevel.class, VARCHAR_DATA_TYPE)
          .put(GtfsDate.class, BLOB_DATA_TYPE)
          .put(GtfsTime.class, BLOB_DATA_TYPE)
          .put(Object.class, BLOB_DATA_TYPE)
          .build();

  /**
   * Exports notices information as a json file.
   *
   * @param isPretty                     will beautify the output if set to true
   * @param validationNoticePackageNames the list of validation notices package names
   * @param validatorPackageNames        the list of validation package names
   * @return the json string file that contains information about all {@code ValidationNotice}s
   * @throws IOException if the attempt to read class path resources (jar files or directories)
   *                     failed.
   * @throws InvalidTypeException if two notice constructors defines the same parameter with
   * different types.
   */
  public static String export(boolean isPretty, List<String> validationNoticePackageNames,
      List<String> validatorPackageNames)
      throws IOException, InvalidTypeException {
    Gson gson = isPretty ? PRETTY_GSON : DEFAULT_GSON;
    JsonObject coreNoticeProperties = new JsonObject();
    JsonObject mainNoticesProperties = new JsonObject();
    for (String validationNoticePackageName : validationNoticePackageNames) {
      coreNoticeProperties = mergeGsonObjects(ImmutableList
          .of(extractCoreNoticesProperties(validationNoticePackageName), coreNoticeProperties));
    }
    for (String validatorPackageName : validatorPackageNames) {
      mainNoticesProperties = mergeGsonObjects(ImmutableList
          .of(extractMainNoticesProperties(validatorPackageName), mainNoticesProperties));
    }
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
   * Processes a notice {@code ClassInfo}. Returns a {@code JsonObject} that contains information
   * about the type of each parameter of said notice whose {@code ClassInfo} was passed as
   * parameter
   *
   * @param noticeClassInfo the {@code ClassInfo} to extract information from
   * @return a {@code JsonObject} that contains information about the type of each parameter of said
   * notice whose {@code ClassInfo} was passed as parameter
   */
  private static JsonObject processNoticeClass(ClassInfo noticeClassInfo)
      throws InvalidTypeException {
    return processNoticeClass(noticeClassInfo.load());
  }

  /**
   * Processes a notice {@code Class}. Returns a {@code JsonObject} that contains information about
   * the type of each parameter of said notice whose {@code Class} was passed as parameter
   *
   * @param noticeClass the {@code Class} to extract information from
   * @return a {@code JsonObject} that contains information about the type of each parameter of said
   * notice whose {@code Class} was passed as parameter
   */
  private static JsonObject processNoticeClass(Class<?> noticeClass) throws InvalidTypeException {
    JsonObject toReturn = new JsonObject();
    toReturn.add(Notice.getCode(noticeClass.getSimpleName()), extractNoticeProperties(noticeClass));
    return toReturn;
  }

  /**
   * Extract information from notices defined in the core module.
   *
   * @param noticePackageName the name of the package that contains the notices
   * @return the {@code com.google.gson.JsonObject} that contains the {@code ValidationNotice} class
   * simple name, and the type of each parameter
   * @throws IOException if the attempt to read class path resources (jar files or directories)
   *                     failed.
   * @throws InvalidTypeException if two notice constructors defines the same parameter with
   * different types.
   */
  private static JsonObject extractCoreNoticesProperties(String noticePackageName)
      throws IOException, InvalidTypeException {
    Class<?> clazz;
    JsonObject toReturn = new JsonObject();
    for (ClassPath.ClassInfo noticeClass :
        ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClasses(noticePackageName)) {
      clazz = noticeClass.load();
      if (ValidationNotice.class.isAssignableFrom(clazz)) {
        // do not iterate over ValidationNotice.java which is the base class for all validation
        // notices
        if (clazz.equals(ValidationNotice.class)) {
          continue;
        }
        toReturn = mergeGsonObjects(ImmutableList.of(processNoticeClass(noticeClass), toReturn));
      }
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
   *                     failed.
   * @throws InvalidTypeException if two notice constructors defines the same parameter with
   * different types.
   */
  private static JsonObject extractMainNoticesProperties(String validatorPackageName)
      throws IOException, InvalidTypeException {
    JsonObject toReturn = new JsonObject();
    for (ClassPath.ClassInfo validatorClass : ClassPath.from(ClassLoader.getSystemClassLoader())
        .getTopLevelClasses(validatorPackageName)) {
      for (Class<?> innerNoticeClass : validatorClass.load().getDeclaredClasses()) {
        if (ValidationNotice.class.isAssignableFrom(innerNoticeClass)) {
          toReturn = mergeGsonObjects(
              ImmutableList.of(processNoticeClass(innerNoticeClass), toReturn));
        }
      }
    }
    return toReturn;
  }

  /**
   * Return a {@code JsonArray} that contains information about the type of each parameter of a
   * {@code ValidationNotice} using the constructors of the class that are annotated by {@code
   * SchemaExport}.
   *
   * @param validationNoticeSubClass the {@code ValidationNotice} sub class to extract information
   *                                 from
   * @return a {@code JsonArray} that contains information about the type of each parameter of said
   * {@code ValidationNotice} using the constructors of the class that are annotated by {@code
   * SchemaExport}.
   * @throws InvalidTypeException if two notice constructors defines the same parameter with
   * different types.
   */
  private static JsonArray extractNoticeProperties(Class<?> validationNoticeSubClass)
      throws InvalidTypeException {
    List<Constructor<?>> constructors = getAnnotatedConstructors(validationNoticeSubClass);
    JsonArray parametersAsJsonArray = new JsonArray();
    Map<String, Parameter> parameterMap = new TreeMap<>();
    for (Constructor<?> constructor : constructors) {
      for (Parameter parameter : constructor.getParameters()) {
        Parameter existingParameter = parameterMap.get(parameter.getName());
        if (existingParameter != null) {
          if (!(existingParameter.getType().equals(parameter.getType()))) {
            throw new InvalidTypeException(
                String.format(
                    "Validation notice %s defines parameter %s with different types "
                        + "in its constructors.",
                    validationNoticeSubClass.getSimpleName(),
                    existingParameter.getName()
                ));
          }
        }
        parameterMap.put(parameter.getName(), parameter);
      }
    }
    for (Entry<String, Parameter> entry : parameterMap.entrySet()) {
      JsonObject parameterDetails = new JsonObject();
      parameterDetails.addProperty(JSON_KEY_NAME, entry.getKey());
      parameterDetails.addProperty(JSON_KEY_TYPE, mapDataType(entry.getValue()));
      parametersAsJsonArray.add(parameterDetails);
    }
    if (parameterMap.get(SEVERITY_LEVEL_PARAMETER_NAME) != null) {
      JsonObject severityDetails = new JsonObject();
      severityDetails.addProperty(JSON_KEY_NAME, SEVERITY_LEVEL_PARAMETER_NAME);
      severityDetails.addProperty(JSON_KEY_TYPE, VARCHAR_DATA_TYPE);
      parametersAsJsonArray.add(severityDetails);
    }
    return parametersAsJsonArray;
  }

  /**
   * Maps Java data type to Oracle's JDBC data types. Said types definitions can be found at the
   * following url: https://docs.oracle.com/cd/E19830-01/819-4721/beajw/index.html.
   *
   * @param parameter the parameter whose JDBC data type has to be inferred
   * @return the JDBC data type that is used in mapping Java data fields to SQL types
   */
  private static String mapDataType(Parameter parameter) {
    return JDBCType.valueOf(
        typeMap.getOrDefault(
            parameter.getType(),
            parameter.getType().getSimpleName().toUpperCase()))
        .getName();
  }

  /**
   * Returns the annotated constructors of a {@code ValidationNotice} subclass. Throws {@code
   * AnnotationFormatError} if the {@code ValidationNotice} subclass does not define a constructor
   * for schema export i.e. no constructor for the given class uses {@code SchemaExport}
   * annotation.
   *
   * @param clazz the class to extract the annotated constructor from
   * @return the annotated constructors of a {@code ValidationNotice} subclass. Throws {@code
   * AnnotationFormatError} if the {@code ValidationNotice} subclass does not define a constructor
   * for schema export i.e. no constructor for the given class uses {@code SchemaExport}
   * annotation.
   */
  private static List<Constructor<?>> getAnnotatedConstructors(Class<?> clazz) {
    List<Constructor<?>> constructors = new ArrayList<>();
    for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
      if (constructor.isAnnotationPresent(SchemaExport.class)) {
        constructors.add(constructor);
      }
    }
    if (constructors.size() == 0) {
      throw new AnnotationFormatError(
          String.format(
              "Validation notice %s does not define constructor for schema export",
              clazz.getSimpleName()));
    }
    return constructors;
  }
}
