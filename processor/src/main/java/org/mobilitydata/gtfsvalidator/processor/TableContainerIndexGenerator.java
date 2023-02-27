package org.mobilitydata.gtfsvalidator.processor;

import static org.mobilitydata.gtfsvalidator.annotation.TranslationRecordIdType.RECORD_ID;
import static org.mobilitydata.gtfsvalidator.annotation.TranslationRecordIdType.RECORD_SUB_ID;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.byKeyMapName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.byKeyMethodName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.fieldNameField;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.hasMethodName;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.TranslationRecordIdType;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.MoreThanOneEntityNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/** Generates code in a container class for @Index and @PrimaryKey annotations. */
class TableContainerIndexGenerator {

  private static final String BY_COMPOSITE_KEY_MAP_FIELD_NAME = "byCompositeKeyMap";

  private static final String KEY_COLUMN_NAMES_FIELD_NAME = "KEY_COLUMN_NAMES";

  private final GtfsFileDescriptor fileDescriptor;
  private final GtfsEntityClasses classNames;

  TableContainerIndexGenerator(GtfsFileDescriptor fileDescriptor) {
    this.fileDescriptor = fileDescriptor;
    this.classNames = new GtfsEntityClasses(fileDescriptor);
  }

  void generateMethods(TypeSpec.Builder typeSpec) {
    if (fileDescriptor.singleRow()) {
      typeSpec.addMethod(
          MethodSpec.methodBuilder("getSingleEntity")
              .addModifiers(Modifier.PUBLIC)
              .returns(
                  ParameterizedTypeName.get(
                      ClassName.get(Optional.class), classNames.entityImplementationTypeName()))
              .addStatement(
                  "return entities.isEmpty() ? Optional.empty() : Optional.of(entities.get(0))")
              .build());
    } else if (fileDescriptor.hasSingleColumnPrimaryKey()) {
      addMapWithGetter(
          typeSpec,
          fileDescriptor.getSingleColumnPrimaryKey(),
          classNames.entityImplementationTypeName());
    } else if (fileDescriptor.hasMultiColumnPrimaryKey()) {
      addMapByCompositeKey(typeSpec, classNames.entityImplementationTypeName());
    }

    for (GtfsFieldDescriptor indexField : fileDescriptor.indices()) {
      addListMultimapWithGetters(
          typeSpec,
          indexField,
          resolveSequenceField(indexField),
          classNames.entityImplementationTypeName());
    }

    typeSpec.addMethod(generateByTranslationKeyMethod());

    typeSpec.addField(generateKeyColumnNames());
    typeSpec.addMethod(generateGetKeyColumnNames());
    typeSpec.addMethod(generateSetupIndicesMethod());

    if (fileDescriptor.hasMultiColumnPrimaryKey()) {
      typeSpec.addType(compositeKeyClass());
    }
  }

  private Optional<GtfsFieldDescriptor> resolveSequenceField(GtfsFieldDescriptor indexField) {
    if (indexField.primaryKey().isPresent()) {
      for (GtfsFieldDescriptor field : fileDescriptor.primaryKeys()) {
        if (field != indexField
            && field.primaryKey().isPresent()
            && field.primaryKey().get().isSequenceUsedForSorting()) {
          return Optional.of(field);
        }
      }
    }
    return Optional.empty();
  }

  private static void addListMultimapWithGetters(
      TypeSpec.Builder typeSpec,
      GtfsFieldDescriptor indexField,
      Optional<GtfsFieldDescriptor> sequenceField,
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
        sequenceField
            .map((f) -> " sorted by " + FieldNameConverter.gtfsColumnName(f.name()))
            .orElse("");
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
            .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), entityTypeName))
            .addStatement("return Optional.ofNullable($L.getOrDefault(key, null))", fieldName)
            .build());
  }

  private static void addMapByCompositeKey(TypeSpec.Builder typeSpec, TypeName entityTypeName) {
    // Field: Map<CompositeKey, EntityType?> byCompositeKeyMap;
    TypeName keyMapType =
        ParameterizedTypeName.get(
            ClassName.get(Map.class), ClassName.get("", "CompositeKey"), entityTypeName);
    typeSpec.addField(
        FieldSpec.builder(keyMapType, BY_COMPOSITE_KEY_MAP_FIELD_NAME, Modifier.PRIVATE)
            .initializer("new $T<>()", ParameterizedTypeName.get(HashMap.class))
            .build());
  }

  private FieldSpec generateKeyColumnNames() {
    FieldSpec.Builder field =
        FieldSpec.builder(
            ParameterizedTypeName.get(ImmutableList.class, String.class),
            KEY_COLUMN_NAMES_FIELD_NAME,
            Modifier.PRIVATE,
            Modifier.STATIC,
            Modifier.FINAL);
    field.initializer(
        "ImmutableList.of($L)",
        fileDescriptor.primaryKeys().stream()
            .map(
                (f) ->
                    CodeBlock.of(
                        "$T.$L",
                        classNames.entityImplementationTypeName(),
                        fieldNameField(f.name())))
            .collect(CodeBlock.joining(",\n")));
    return field.build();
  }

  private MethodSpec generateGetKeyColumnNames() {

    return MethodSpec.methodBuilder("getKeyColumnNames")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(ParameterizedTypeName.get(ImmutableList.class, String.class))
        .addStatement("return $L", KEY_COLUMN_NAMES_FIELD_NAME)
        .build();
  }

  private boolean hasTranslationRecordId() {
    return fileDescriptor.primaryKeys().stream()
        .anyMatch((f) -> f.primaryKey().get().translationRecordIdType() == RECORD_ID);
  }

  private MethodSpec generateByTranslationKeyMethod() {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("byTranslationKey")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get(Optional.class), classNames.entityImplementationTypeName()))
            .addParameter(String.class, "recordId")
            .addParameter(String.class, "recordSubId");
    if (fileDescriptor.hasSingleColumnPrimaryKey()) {
      method.addStatement(
          "return Optional.ofNullable($L.getOrDefault(recordId, null))",
          byKeyMapName(fileDescriptor.getSingleColumnPrimaryKey().name()));
    } else if (fileDescriptor.hasMultiColumnPrimaryKey() && hasTranslationRecordId()) {
      ImmutableMap<TranslationRecordIdType, String> recordIdTypes =
          ImmutableMap.of(RECORD_ID, "recordId", RECORD_SUB_ID, "recordSubId");
      List<CodeBlock> keyBuilderSetters = new ArrayList<>();
      for (GtfsFieldDescriptor field : fileDescriptor.primaryKeys()) {
        // Currently, translations.txt only supports lookup of entities with at most two primary
        // keys via record_id and record_sub_id, even if those entities technically have more than
        // two primary keys (e.g. transfers.txt).  To support this use case, we need to be careful
        // about the construction of the CompositeKey.  In the "setupIndices" method, CompositeKey
        // is built with a value for each key field of an entity, even if the entity returns a
        // default value.  We need to follow that same behavior here, using entity default values
        // for fields that aren't filled directly from `record_id` or `record_sub_id`.
        CodeBlock accessor = EntityImplementationGenerator.getDefaultValue(field);
        String parameterName =
            recordIdTypes.get(field.primaryKey().get().translationRecordIdType());
        if (parameterName != null) {
          accessor =
              CodeBlock.of(
                  // We also use the entity default value if the record id is empty.
                  "$T.isNullOrEmpty($L) ? $L : $L",
                  ClassName.get(Strings.class),
                  parameterName,
                  accessor,
                  wrapStringAccessorWithTypeConversion(field, CodeBlock.of(parameterName)));
        }
        keyBuilderSetters.add(
            CodeBlock.of(".$L($L)", FieldNameConverter.setterMethodName(field.name()), accessor));
      }
      method
          .beginControlFlow("try")
          .addStatement(
              "return Optional.ofNullable($L.getOrDefault(CompositeKey.builder()\n$L.\nbuild(), null))",
              BY_COMPOSITE_KEY_MAP_FIELD_NAME,
              CodeBlock.join(keyBuilderSetters, "\n"))
          .nextControlFlow("catch (NumberFormatException ex)")
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

  private CodeBlock wrapStringAccessorWithTypeConversion(
      GtfsFieldDescriptor field, CodeBlock accessor) {
    switch (field.type()) {
      case INTEGER:
        return CodeBlock.of("$T.parseInt($L)", TypeName.get(field.javaType()).box(), accessor);
      case DATE:
        return CodeBlock.of("$T.fromString($L)", ClassName.get(GtfsDate.class), accessor);
      case TIME:
        return CodeBlock.of("$T.fromString($L)", ClassName.get(GtfsTime.class), accessor);
      case LANGUAGE_CODE:
        return CodeBlock.of("$T.forLanguageTag($L)", ClassName.get(Locale.class), accessor);
      default:
        return accessor;
    }
  }

  private MethodSpec generateSetupIndicesMethod() {
    TypeName gtfsEntityType = classNames.entityImplementationTypeName();
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
    } else if (fileDescriptor.hasMultiColumnPrimaryKey()) {
      method
          .beginControlFlow("for ($T newEntity : entities)", gtfsEntityType)
          .addStatement(
              "CompositeKey key = CompositeKey.builder()\n$L\n.build()",
              fileDescriptor.primaryKeys().stream()
                  .map(
                      (field) ->
                          CodeBlock.of(
                              ".$L(newEntity.$L())",
                              FieldNameConverter.setterMethodName(field.name()),
                              field.name()))
                  .collect(CodeBlock.joining("\n")))
          .addStatement(
              "$T oldEntity = $L.getOrDefault(key, null)",
              classNames.entityImplementationTypeName(),
              BY_COMPOSITE_KEY_MAP_FIELD_NAME)
          .beginControlFlow("if (oldEntity != null)")
          .addStatement(
              "noticeContainer.addValidationNotice(new $T(\n"
                  + "gtfsFilename(), newEntity.csvRowNumber(), "
                  + "oldEntity.csvRowNumber(),\n"
                  + "$L,\n$L))",
              DuplicateKeyNotice.class,
              fileDescriptor.primaryKeys().stream()
                  .map(
                      (field) ->
                          CodeBlock.of("$T.$L", gtfsEntityType, fieldNameField(field.name())))
                  .collect(CodeBlock.joining(" + \",\" + \n")),
              fileDescriptor.primaryKeys().stream()
                  .map((field) -> CodeBlock.of("oldEntity.$L()", field.name()))
                  .collect(CodeBlock.joining(" + \",\" + \n")))
          .nextControlFlow("else")
          .addStatement("$L.put(key, newEntity)", BY_COMPOSITE_KEY_MAP_FIELD_NAME)
          .endControlFlow()
          .endControlFlow();
    } else if (fileDescriptor.hasSingleColumnPrimaryKey()) {
      GtfsFieldDescriptor primaryKey = fileDescriptor.getSingleColumnPrimaryKey();
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
              gtfsEntityType,
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
      for (GtfsFieldDescriptor indexField : fileDescriptor.indices()) {
        Optional<GtfsFieldDescriptor> sequenceField = resolveSequenceField(indexField);
        if (sequenceField.isPresent()) {
          method
              .beginControlFlow(
                  "for (List<$T> entityList: $T.asMap($L).values())",
                  gtfsEntityType,
                  Multimaps.class,
                  byKeyMapName(indexField.name()))
              .addStatement(
                  "entityList.sort((entity1, entity2) -> $T.compare(entity1.$L(), entity2.$L()))",
                  TypeName.get(sequenceField.get().javaType()).box(),
                  sequenceField.get().name(),
                  sequenceField.get().name())
              .endControlFlow();
        }
      }
    }
    return method.build();
  }

  private TypeSpec compositeKeyClass() {
    // We generate an @AutoValue object to contain the values of the key.  @AutoValue automatically
    // generates equals() and hashCode() methods.
    TypeSpec.Builder keySpec =
        TypeSpec.classBuilder("CompositeKey")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT)
            .addAnnotation(ClassName.get("com.google.auto.value", "AutoValue"));

    // Getters for each field.
    for (GtfsFieldDescriptor keyField : fileDescriptor.primaryKeys()) {
      MethodSpec.Builder getter =
          MethodSpec.methodBuilder(keyField.name())
              .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
              .returns(TypeName.get(keyField.javaType()));
      if (isKeyTypeNullable(keyField)) {
        getter.addAnnotation(Nullable.class);
      }
      keySpec.addMethod(getter.build());
    }

    TypeSpec.Builder valueBuilderTypeSpec =
        TypeSpec.classBuilder("Builder")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT)
            .addAnnotation(ClassName.get("com.google.auto.value", "AutoValue.Builder"));
    for (GtfsFieldDescriptor keyField : fileDescriptor.primaryKeys()) {
      ParameterSpec.Builder param =
          ParameterSpec.builder(TypeName.get(keyField.javaType()), keyField.name());
      if (isKeyTypeNullable(keyField)) {
        param.addAnnotation(Nullable.class);
      }
      valueBuilderTypeSpec.addMethod(
          MethodSpec.methodBuilder(FieldNameConverter.setterMethodName(keyField.name()))
              .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
              .addParameter(param.build())
              .returns(ClassName.get("", "CompositeKey.Builder"))
              .build());
    }
    valueBuilderTypeSpec.addMethod(
        MethodSpec.methodBuilder("build")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(ClassName.get("", "CompositeKey"))
            .build());
    keySpec.addType(valueBuilderTypeSpec.build());

    // The builder() method.
    keySpec.addMethod(
        MethodSpec.methodBuilder("builder")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("", "Builder"))
            .addStatement(
                "return new AutoValue_$L_CompositeKey.Builder()",
                classNames.tableContainerSimpleName())
            .build());

    return keySpec.build();
  }

  /**
   * When generating the CompositeKey type, @AutoValue expects non-primitive fields to be non-null
   * by default. However, in practice, field values can be null if a feed doesn't specify a value.
   * While these feeds may ultimately fail validation for missing a required value, we still want to
   * be able to construct a valid CompositeKey that accepts null values.
   */
  private static boolean isKeyTypeNullable(GtfsFieldDescriptor field) {
    return !field.javaType().getKind().isPrimitive();
  }
}
