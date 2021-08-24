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
import com.google.common.collect.ImmutableList;
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
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.MoreThanOneEntityNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
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
      TypeSpec.Builder typeSpec,
      GtfsFieldDescriptor indexField,
      @Nullable GtfsFieldDescriptor sequenceField,
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
    String sortedBy =
        sequenceField != null
            ? " sorted by " + FieldNameConverter.gtfsColumnName(sequenceField.name())
            : "";
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
            .addJavadoc(
                "@return ListMultimap keyed on "
                    + FieldNameConverter.gtfsColumnName(indexField.name())
                    + " with values that are Lists of "
                    + entityTypeName
                    + sortedBy)
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

  private static void addMapByCompositeKey(
      TypeSpec.Builder typeSpec,
      GtfsFieldDescriptor firstKey,
      GtfsFieldDescriptor sequenceKey,
      TypeName entityTypeName) {
    String methodName = byKeyMethodName(firstKey.name(), sequenceKey.name());
    String fieldName = byKeyMapName(firstKey.name(), sequenceKey.name());
    TypeName keyMapType =
        ParameterizedTypeName.get(
            ClassName.get(Map.class), ClassName.get("", "CompositeKey"), entityTypeName);
    typeSpec.addField(
        FieldSpec.builder(keyMapType, fieldName, Modifier.PRIVATE)
            .initializer("new $T<>()", ParameterizedTypeName.get(HashMap.class))
            .build());
    typeSpec.addMethod(
        MethodSpec.methodBuilder(methodName)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(TypeName.get(firstKey.javaType()), firstKey.name())
            .addParameter(TypeName.get(sequenceKey.javaType()), sequenceKey.name())
            .returns(entityTypeName)
            .addStatement(
                "return $L.get(new CompositeKey($L, $L))",
                fieldName,
                firstKey.name(),
                sequenceKey.name())
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
    } else if (hasCompositeKey()) {
      addListMultimapWithGetters(
          typeSpec,
          fileDescriptor.firstKey().get(),
          fileDescriptor.sequenceKey().get(),
          classNames.entityImplementationTypeName());
      addMapByCompositeKey(
          typeSpec,
          fileDescriptor.firstKey().get(),
          fileDescriptor.sequenceKey().get(),
          classNames.entityImplementationTypeName());
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
    typeSpec.addMethod(generateForHeaderAndEntitiesMethod());
    typeSpec.addMethod(generateForEntitiesMethod());
    typeSpec.addMethod(generateGetKeyColumnNames());
    typeSpec.addMethod(generateByPrimaryKey());
    if (hasCompositeKey()) {
      typeSpec.addType(compositeKeyClass());
    }

    return typeSpec.build();
  }

  private boolean hasCompositeKey() {
    return fileDescriptor.sequenceKey().isPresent() && fileDescriptor.firstKey().isPresent();
  }

  private TypeSpec compositeKeyClass() {
    TypeSpec.Builder keySpec = TypeSpec.classBuilder("CompositeKey").addModifiers(Modifier.STATIC);
    TypeName firstKeyType = TypeName.get(fileDescriptor.firstKey().get().javaType());
    TypeName sequenceKeyType = TypeName.get(fileDescriptor.sequenceKey().get().javaType());
    keySpec.addField(firstKeyType, "firstKey", Modifier.FINAL, Modifier.PRIVATE);
    keySpec.addField(sequenceKeyType, "sequenceKey", Modifier.FINAL, Modifier.PRIVATE);

    keySpec.addMethod(
        MethodSpec.constructorBuilder()
            .addParameter(firstKeyType, "firstKey")
            .addParameter(sequenceKeyType, "sequenceKey")
            .addStatement("this.firstKey = firstKey")
            .addStatement("this.sequenceKey = sequenceKey")
            .build());

    keySpec.addMethod(
        MethodSpec.methodBuilder("equals")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .addParameter(Object.class, "obj")
            .returns(boolean.class)
            .beginControlFlow("if (obj == this)")
            .addStatement("return true")
            .endControlFlow()
            .beginControlFlow("if (obj instanceof CompositeKey)")
            .addStatement("CompositeKey other = (CompositeKey) obj")
            .addStatement(
                "return $T.equals(firstKey, other.firstKey) && sequenceKey == other.sequenceKey",
                Objects.class)
            .endControlFlow()
            .addStatement("return false")
            .build());

    keySpec.addMethod(
        MethodSpec.methodBuilder("hashCode")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(int.class)
            .addStatement("return $T.hash(firstKey, sequenceKey)", Objects.class)
            .build());

    return keySpec.build();
  }

  private MethodSpec generateGetKeyColumnNames() {
    return MethodSpec.methodBuilder("getKeyColumnNames")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(ParameterizedTypeName.get(ImmutableList.class, String.class))
        .addStatement("return $T.KEY_COLUMN_NAMES", classNames.tableLoaderTypeName())
        .build();
  }

  private MethodSpec generateByPrimaryKey() {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("byPrimaryKey")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get(Optional.class), classNames.entityImplementationTypeName()))
            .addParameter(String.class, "id")
            .addParameter(String.class, "subId");
    if (fileDescriptor.primaryKey().isPresent()) {
      method.addStatement(
          "return Optional.ofNullable($L.getOrDefault(id, null))",
          byKeyMapName(fileDescriptor.primaryKey().get().name()));
    } else if (hasCompositeKey()) {
      GtfsFieldDescriptor firstKey = fileDescriptor.firstKey().get();
      GtfsFieldDescriptor sequenceKey = fileDescriptor.sequenceKey().get();
      method
          .beginControlFlow("try")
          .addStatement(
              "return Optional.ofNullable($L.getOrDefault(new CompositeKey(id,"
                  + " $T.parseInt(subId)), null))",
              byKeyMapName(firstKey.name(), sequenceKey.name()),
              TypeName.get(sequenceKey.javaType()).box())
          .nextControlFlow("catch (NumberFormatException e)")
          .addStatement("return Optional.empty()")
          .endControlFlow();
    } else if (fileDescriptor.singleRow()) {
      method.addStatement(
          "return entities.isEmpty() ? Optional.empty() : Optional.of(entities.get(0))");
    } else {
      method.addStatement("return Optional.empty()");
    }
    return method.build();
  }

  private MethodSpec generateConstructorWithEntities() {
    return MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PRIVATE)
        .addParameter(CsvHeader.class, "header")
        .addParameter(
            ParameterizedTypeName.get(
                ClassName.get(List.class), classNames.entityImplementationTypeName()),
            "entities")
        .addStatement("super(TableStatus.PARSABLE_HEADERS_AND_ROWS, header)")
        .addStatement("this.entities = entities")
        .build();
  }

  private MethodSpec generateConstructorWithStatus() {
    return MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(GtfsTableContainer.TableStatus.class, "tableStatus")
        .addStatement("super(tableStatus, $T.EMPTY)", CsvHeader.class)
        .addStatement("this.entities = new $T<>()", ArrayList.class)
        .build();
  }

  private MethodSpec generateForHeaderAndEntitiesMethod() {
    TypeName tableContainerTypeName = classNames.tableContainerTypeName();
    return MethodSpec.methodBuilder("forHeaderAndEntities")
        .returns(tableContainerTypeName)
        .addJavadoc("Creates a table with given header and entities")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(CsvHeader.class, "header")
        .addParameter(
            ParameterizedTypeName.get(
                ClassName.get(List.class), classNames.entityImplementationTypeName()),
            "entities")
        .addParameter(NoticeContainer.class, "noticeContainer")
        .addStatement(
            "$T table = new $T(header, entities)", tableContainerTypeName, tableContainerTypeName)
        .addStatement("table.setupIndices(noticeContainer)")
        .addStatement("return table")
        .build();
  }

  private MethodSpec generateForEntitiesMethod() {
    TypeName tableContainerTypeName = classNames.tableContainerTypeName();
    return MethodSpec.methodBuilder("forEntities")
        .returns(tableContainerTypeName)
        .addJavadoc(
            "Creates a table with given entities and empty header. This method is intended to be"
                + " used in tests.")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(
            ParameterizedTypeName.get(
                ClassName.get(List.class), classNames.entityImplementationTypeName()),
            "entities")
        .addParameter(NoticeContainer.class, "noticeContainer")
        .addStatement(
            "return forHeaderAndEntities($T.EMPTY, entities, noticeContainer)", CsvHeader.class)
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
    } else if (hasCompositeKey()) {
      GtfsFieldDescriptor firstKey = fileDescriptor.firstKey().get();
      GtfsFieldDescriptor sequenceKey = fileDescriptor.sequenceKey().get();
      String byCompositeKeyMap = byKeyMapName(firstKey.name(), sequenceKey.name());
      method
          .beginControlFlow("for ($T newEntity : entities)", gtfsEntityType)
          .addStatement(
              "CompositeKey key = new CompositeKey(newEntity.$L(), newEntity.$L())",
              fileDescriptor.firstKey().get().name(),
              fileDescriptor.sequenceKey().get().name())
          .addStatement(
              "$T oldEntity = $L.getOrDefault(key, null)",
              classNames.entityImplementationTypeName(),
              byCompositeKeyMap)
          .beginControlFlow("if (oldEntity != null)")
          .addStatement(
              "noticeContainer.addValidationNotice(new $T("
                  + "gtfsFilename(), newEntity.csvRowNumber(), "
                  + "oldEntity.csvRowNumber(), "
                  + "$T.$L, oldEntity.$L(), "
                  + "$T.$L, oldEntity.$L()))",
              DuplicateKeyNotice.class,
              loaderType,
              fieldNameField(firstKey.name()),
              firstKey.name(),
              loaderType,
              fieldNameField(sequenceKey.name()),
              sequenceKey.name())
          .nextControlFlow("else")
          .addStatement("$L.put(key, newEntity)", byCompositeKeyMap)
          .endControlFlow()
          .endControlFlow();

      String byFirstKeyMap = byKeyMapName(firstKey.name());
      method.beginControlFlow("for ($T entity : entities)", gtfsEntityType);
      method.addStatement("$L.put(entity.$L(), entity)", byFirstKeyMap, firstKey.name());
      method.endControlFlow();

      method
          .beginControlFlow(
              "for (List<$T> entityList: $T.asMap($L).values())",
              gtfsEntityType,
              Multimaps.class,
              byFirstKeyMap)
          .addStatement(
              "entityList.sort((entity1, entity2) -> $T.compare(entity1.$L(), entity2.$L()))",
              TypeName.get(sequenceKey.javaType()).box(),
              sequenceKey.name(),
              sequenceKey.name())
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
              DuplicateKeyNotice.class,
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
