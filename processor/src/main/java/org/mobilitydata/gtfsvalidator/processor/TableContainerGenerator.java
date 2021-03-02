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

package org.mobilitydata.gtfsvalidator.processor;

import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.byKeyMapName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.byKeyMethodName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.fieldNameField;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.hasMethodName;
import static org.mobilitydata.gtfsvalidator.processor.GtfsEntityClasses.TABLE_PACKAGE_NAME;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyError;
import org.mobilitydata.gtfsvalidator.notice.MoreThanOneEntityNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;

/**
 * Generates code for a container for a loaded GTFS table.
 *
 * <p>E.g., GtfsStopTableContainer class is generated for "stops.txt".
 */
public class TableContainerGenerator {

  private final GtfsFileDescriptor fileDescriptor;
  private final GtfsEntityClasses classNames;

  public TableContainerGenerator(GtfsFileDescriptor fileDescriptor) {
    this.fileDescriptor = fileDescriptor;
    this.classNames = new GtfsEntityClasses(fileDescriptor);
  }

  private static void addListMultimapWithGetters(
      TypeSpec.Builder typeSpec, GtfsFieldDescriptor indexField, TypeName entityTypeName) {
    addListMultimapWithGetters(typeSpec, indexField, null, entityTypeName);
  }

  private static void addListMultimapWithGetters(
      TypeSpec.Builder typeSpec, GtfsFieldDescriptor indexField, GtfsFieldDescriptor sequenceField,
      TypeName entityTypeName) {
    TypeName keyMapType =
        ParameterizedTypeName.get(
            ClassName.get(ListMultimap.class), TypeName.get(indexField.javaType()), entityTypeName);
    String methodName = byKeyMethodName(indexField.name());
    String fieldName = byKeyMapName(indexField.name());
    typeSpec.addField(
        FieldSpec.builder(keyMapType, fieldName, Modifier.PRIVATE)
            .initializer("$T.create()", ParameterizedTypeName.get(ArrayListMultimap.class))
            .build());
    String sortedBy = sequenceField != null ? " sorted by " + sequenceField.name() : "";
    typeSpec.addMethod(
        MethodSpec.methodBuilder(methodName)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(TypeName.get(indexField.javaType()), "key")
            .returns(ParameterizedTypeName.get(ClassName.get(List.class), entityTypeName))
            .addStatement("return $L.get(key)", fieldName)
            .addJavadoc("@return List of " + entityTypeName + sortedBy)
            .build());
    typeSpec.addMethod(
        MethodSpec.methodBuilder(methodName + "Map")
            .addModifiers(Modifier.PUBLIC)
            .returns(keyMapType)
            .addStatement("return $L", fieldName)
            .addJavadoc("@return ListMultimap keyed on " + indexField.name() + " with values that are Lists of " + entityTypeName + sortedBy)
            .build());
  }

  private static void addMapWithGetter(
      TypeSpec.Builder typeSpec, GtfsFieldDescriptor indexField, TypeName entityTypeName) {
    String methodName = byKeyMethodName(indexField.name());
    String fieldName = byKeyMapName(indexField.name());
    TypeName keyMapType =
        ParameterizedTypeName.get(
            ClassName.get(Map.class), TypeName.get(indexField.javaType()), entityTypeName);
    typeSpec.addField(
        FieldSpec.builder(keyMapType, fieldName, Modifier.PRIVATE)
            .initializer("new $T<>()", ParameterizedTypeName.get(HashMap.class))
            .build());
    typeSpec.addMethod(
        MethodSpec.methodBuilder(methodName)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(TypeName.get(indexField.javaType()), "key")
            .returns(entityTypeName)
            .addStatement("return $L.get(key)", fieldName)
            .build());
  }

  public JavaFile generateGtfsContainerJavaFile() {
    return JavaFile.builder(TABLE_PACKAGE_NAME, generateGtfsContainerClass()).build();
  }

  public TypeSpec generateGtfsContainerClass() {
    TypeName gtfsEntityType = classNames.entityImplementationTypeName();
    TypeSpec.Builder typeSpec =
        TypeSpec.classBuilder(classNames.tableContainerSimpleName())
            .superclass(
                ParameterizedTypeName.get(ClassName.get(GtfsTableContainer.class), gtfsEntityType))
            .addAnnotation(Generated.class)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

    typeSpec.addMethod(
        MethodSpec.methodBuilder("getEntityClass")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(ParameterizedTypeName.get(ClassName.get(Class.class), gtfsEntityType))
            .addStatement("return $T.class", gtfsEntityType)
            .build());

    typeSpec.addMethod(
        MethodSpec.methodBuilder("gtfsFilename")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(String.class)
            .addStatement("return $T.FILENAME", classNames.tableLoaderTypeName())
            .build());

    typeSpec.addMethod(
        MethodSpec.methodBuilder("isRequired")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(boolean.class)
            .addStatement("return $L", fileDescriptor.required())
            .build());

    typeSpec.addField(
        ParameterizedTypeName.get(ClassName.get(List.class), gtfsEntityType),
        "entities",
        Modifier.PRIVATE);

    typeSpec.addMethod(
        MethodSpec.methodBuilder("getEntities")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(ParameterizedTypeName.get(ClassName.get(List.class), gtfsEntityType))
            .addStatement("return entities")
            .build());

    if (fileDescriptor.singleRow()) {
      typeSpec.addMethod(
          MethodSpec.methodBuilder("getSingleEntity")
              .addModifiers(Modifier.PUBLIC)
              .returns(classNames.entityImplementationTypeName())
              .addStatement("return entities.isEmpty() ? null : entities.get(0)")
              .build());
    } else if (fileDescriptor.sequenceKey().isPresent()) {
      addListMultimapWithGetters(
          typeSpec, fileDescriptor.firstKey().get(), fileDescriptor.sequenceKey().get(), classNames.entityImplementationTypeName());
    } else if (fileDescriptor.primaryKey().isPresent()) {
      addMapWithGetter(
          typeSpec, fileDescriptor.primaryKey().get(), classNames.entityImplementationTypeName());
    }
    for (GtfsFieldDescriptor indexField : fileDescriptor.indices()) {
      addListMultimapWithGetters(typeSpec, indexField, classNames.entityImplementationTypeName());
    }

    typeSpec.addMethod(generateConstructorWithEntities());
    typeSpec.addMethod(generateConstructorWithStatus());
    typeSpec.addMethod(generateSetupIndicesMethod());
    typeSpec.addMethod(generateForEntitiesMethod());

    return typeSpec.build();
  }

  private MethodSpec generateConstructorWithEntities() {
    return MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PRIVATE)
        .addParameter(
            ParameterizedTypeName.get(
                ClassName.get(List.class), classNames.entityImplementationTypeName()),
            "entities")
        .addStatement("super(TableStatus.PARSABLE_HEADERS_AND_ROWS)")
        .addStatement("this.entities = entities")
        .build();
  }

  private MethodSpec generateConstructorWithStatus() {
    return MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(GtfsTableContainer.TableStatus.class, "tableStatus")
        .addStatement("super(tableStatus)")
        .addStatement("this.entities = new $T<>()", ArrayList.class)
        .build();
  }

  private MethodSpec generateForEntitiesMethod() {
    TypeName tableContainerTypeName = classNames.tableContainerTypeName();
    return MethodSpec.methodBuilder("forEntities")
        .returns(tableContainerTypeName)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(
            ParameterizedTypeName.get(
                ClassName.get(List.class), classNames.entityImplementationTypeName()),
            "entities")
        .addParameter(NoticeContainer.class, "noticeContainer")
        .addStatement("$T table = new $T(entities)", tableContainerTypeName, tableContainerTypeName)
        .addStatement("table.setupIndices(noticeContainer)")
        .addStatement("return table")
        .build();
  }

  private MethodSpec generateSetupIndicesMethod() {
    TypeName gtfsEntityType = classNames.entityImplementationTypeName();
    TypeName loaderType = classNames.tableLoaderTypeName();
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("setupIndices")
            .addModifiers(Modifier.PRIVATE)
            .addParameter(NoticeContainer.class, "noticeContainer")
            .returns(void.class);

    if (fileDescriptor.singleRow()) {
      method
          .beginControlFlow("if (entities.size() > 1)")
          .addStatement(
              "noticeContainer.addValidationNotice(new $T(gtfsFilename(), entities.size()))",
              MoreThanOneEntityNotice.class)
          .endControlFlow();
    } else if (fileDescriptor.sequenceKey().isPresent() && fileDescriptor.firstKey().isPresent()) {
      GtfsFieldDescriptor firstKey = fileDescriptor.firstKey().get();
      GtfsFieldDescriptor sequenceKey = fileDescriptor.sequenceKey().get();
      String byKeyMap = byKeyMapName(firstKey.name());
      method.beginControlFlow("for ($T entity : entities)", gtfsEntityType);
      method.addStatement("$L.put(entity.$L(), entity)", byKeyMap, firstKey.name());
      method.endControlFlow();

      method
          .beginControlFlow(
              "for (List<$T> entityList: $T.asMap($L).values())",
              gtfsEntityType,
              Multimaps.class,
              byKeyMap)
          .addStatement(
              "entityList.sort((entity1, entity2) -> Integer.compare(entity1.$L(), entity2.$L()))",
              sequenceKey.name(),
              sequenceKey.name())
          .beginControlFlow("for (int i = 1; i < entityList.size(); ++i)")
          .addStatement("$T a = entityList.get(i - 1)", gtfsEntityType)
          .addStatement("$T b = entityList.get(i)", gtfsEntityType)
          .beginControlFlow("if (a.$L() == b.$L())", sequenceKey.name(), sequenceKey.name())
          .addStatement(
              "noticeContainer.addValidationNotice(new $T("
                  + "gtfsFilename(), a.csvRowNumber(), "
                  + "b.csvRowNumber(), "
                  + "$T.$L, a.$L(), "
                  + "$T.$L, a.$L()))",
              DuplicateKeyError.class,
              loaderType,
              fieldNameField(firstKey.name()),
              firstKey.name(),
              loaderType,
              fieldNameField(sequenceKey.name()),
              sequenceKey.name())
          .endControlFlow()
          .endControlFlow()
          .endControlFlow();
    } else if (fileDescriptor.primaryKey().isPresent()) {
      GtfsFieldDescriptor primaryKey = fileDescriptor.primaryKey().get();
      String byKeyMap = byKeyMapName(primaryKey.name());
      method.beginControlFlow("for ($T newEntity : entities)", gtfsEntityType);
      method
          .beginControlFlow("if (!newEntity.$L())", hasMethodName(primaryKey.name()))
          .addStatement("continue")
          .endControlFlow()
          .addStatement(
              "$T oldEntity = $L.getOrDefault(newEntity.$L(), null)",
              classNames.entityImplementationTypeName(),
              byKeyMap,
              primaryKey.name())
          .beginControlFlow("if (oldEntity != null)")
          .addStatement(
              "noticeContainer.addValidationNotice(new $T(gtfsFilename(),"
                  + " newEntity.csvRowNumber(), oldEntity.csvRowNumber(), $T.$L, newEntity.$L()))",
              DuplicateKeyError.class,
              loaderType,
              fieldNameField(primaryKey.name()),
              primaryKey.name())
          .nextControlFlow("else")
          .addStatement("$L.put(newEntity.$L(), newEntity)", byKeyMap, primaryKey.name())
          .endControlFlow();
      method.endControlFlow();
    }

    if (!fileDescriptor.indices().isEmpty()) {
      method.beginControlFlow("for ($T entity : entities)", gtfsEntityType);
      for (GtfsFieldDescriptor indexField : fileDescriptor.indices()) {
        method.addStatement(
            "$L.put(entity.$L(), entity)", byKeyMapName(indexField.name()), indexField.name());
      }
      method.endControlFlow();
    }
    return method.build();
  }
}
