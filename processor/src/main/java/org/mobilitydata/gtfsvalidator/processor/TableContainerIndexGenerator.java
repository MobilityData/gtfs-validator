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
import com.squareup.javapoet.*;
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

  /**
   * Returns the primary key field on whose sorted index duplicate keys can be detected, if any.
   *
   * <p>A table qualifies when its primary key is made of exactly two fields, one of them annotated
   * with {@code @Index} and the other one being the sequence the index is sorted by. In that case
   * two entities share a primary key only if they sit next to each other in a sorted group of that
   * index, so scanning the groups detects duplicates without the map keyed on the composite key,
   * which costs about 60 bytes per row. Those are exactly the tables where that matters:
   * stop_times.txt and shapes.txt, which hold millions of rows on a large feed.
   *
   * <p>Every other table with a composite primary key keeps the map: they are small, and the
   * map-based detection is simpler.
   */
  private Optional<GtfsFieldDescriptor> resolveSortedGroupIndexField() {
    if (fileDescriptor.singleRow() || fileDescriptor.primaryKeys().size() != 2) {
      return Optional.empty();
    }
    for (GtfsFieldDescriptor primaryKey : fileDescriptor.primaryKeys()) {
      Optional<GtfsFieldDescriptor> sequenceField = resolveSequenceField(primaryKey);
      if (fileDescriptor.indices().contains(primaryKey)
          && sequenceField.isPresent()
          && sequenceField.get().javaType().getKind().isPrimitive()) {
        return Optional.of(primaryKey);
      }
    }
    return Optional.empty();
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

  private void addMapByCompositeKey(TypeSpec.Builder typeSpec, TypeName entityTypeName) {
    // Field: Map<CompositeKey, EntityType?> byCompositeKeyMap;
    TypeName keyMapType =
        ParameterizedTypeName.get(
            ClassName.get(Map.class), ClassName.get("", "CompositeKey"), entityTypeName);
    if (resolveSortedGroupIndexField().isEmpty()) {
      typeSpec.addField(
          FieldSpec.builder(keyMapType, BY_COMPOSITE_KEY_MAP_FIELD_NAME, Modifier.PRIVATE)
              .initializer("new $T<>()", ParameterizedTypeName.get(HashMap.class))
              .build());
      return;
    }
    if (!hasTranslationRecordId()) {
      // Nothing reads the map: duplicate keys are detected on the sorted index groups and this
      // table does not support translation lookups.
      return;
    }
    typeSpec.addField(
        FieldSpec.builder(keyMapType, BY_COMPOSITE_KEY_MAP_FIELD_NAME, Modifier.PRIVATE)
            .addAnnotation(Nullable.class)
            .addJavadoc("Built on first use, see {@link #$L()}.\n", BY_COMPOSITE_KEY_MAP_FIELD_NAME)
            .build());
    typeSpec.addMethod(generateByCompositeKeyMapMethod(keyMapType, entityTypeName));
  }

  /**
   * Generates the accessor that builds {@code byCompositeKeyMap} on first use.
   *
   * <p>This is only generated for the tables where duplicate keys are detected without the map (see
   * {@link #resolveSortedGroupIndexField()}), so that the map is paid for only by the feeds that
   * actually translate the file.
   */
  private MethodSpec generateByCompositeKeyMapMethod(TypeName keyMapType, TypeName entityTypeName) {
    return MethodSpec.methodBuilder(BY_COMPOSITE_KEY_MAP_FIELD_NAME)
        .addModifiers(Modifier.PRIVATE, Modifier.SYNCHRONIZED)
        .returns(keyMapType)
        .addJavadoc(
            "Returns the entities keyed on their composite primary key, building the map on first"
                + " use.\n"
                + "\n"
                + "<p>One entry per row costs about 60 bytes, which is too much for a table with"
                + " millions of rows, and only translation lookups need it. It is therefore built"
                + " lazily, and synchronized because multi-file validators may run on several"
                + " threads.\n")
        .beginControlFlow("if ($L == null)", BY_COMPOSITE_KEY_MAP_FIELD_NAME)
        .addStatement("$T map = new $T<>()", keyMapType, ParameterizedTypeName.get(HashMap.class))
        .beginControlFlow("for ($T entity : entities)", entityTypeName)
        // putIfAbsent: as when the map was filled while detecting duplicates, the first entity
        // with a given key wins.
        .addStatement(
            "map.putIfAbsent(CompositeKey.builder()\n$L\n.build(), entity)",
            compositeKeyBuilderSetters("entity"))
        .endControlFlow()
        .addStatement("$L = map", BY_COMPOSITE_KEY_MAP_FIELD_NAME)
        .endControlFlow()
        .addStatement("return $L", BY_COMPOSITE_KEY_MAP_FIELD_NAME)
        .build();
  }

  /** Returns the {@code CompositeKey.Builder} setters filled from the given entity variable. */
  private CodeBlock compositeKeyBuilderSetters(String entityVariable) {
    return fileDescriptor.primaryKeys().stream()
        .map(
            (field) ->
                CodeBlock.of(
                    ".$L($L.$L())",
                    FieldNameConverter.setterMethodName(field.name()),
                    entityVariable,
                    field.name()))
        .collect(CodeBlock.joining("\n"));
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

  /** Returns how to read the composite key map: the field itself, or the lazy accessor. */
  private String byCompositeKeyMapAccessor() {
    return resolveSortedGroupIndexField().isEmpty()
        ? BY_COMPOSITE_KEY_MAP_FIELD_NAME
        : BY_COMPOSITE_KEY_MAP_FIELD_NAME + "()";
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
              byCompositeKeyMapAccessor(),
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
    Optional<GtfsFieldDescriptor> sortedGroupIndexField = resolveSortedGroupIndexField();
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
    } else if (fileDescriptor.hasMultiColumnPrimaryKey() && sortedGroupIndexField.isEmpty()) {
      method
          .beginControlFlow("for ($T newEntity : entities)", gtfsEntityType)
          .addStatement(
              "CompositeKey key = CompositeKey.builder()\n$L\n.build()",
              compositeKeyBuilderSetters("newEntity"))
          .addStatement(
              "$T oldEntity = $L.getOrDefault(key, null)",
              classNames.entityImplementationTypeName(),
              BY_COMPOSITE_KEY_MAP_FIELD_NAME)
          .beginControlFlow("if (oldEntity != null)")
          .addStatement(
              "noticeContainer.addValidationNotice(new $T(\n"
                  + "gtfsFilename(), oldEntity.csvRowNumber(), newEntity.csvRowNumber(),\n"
                  + "key.getDefinedKeys(oldEntity), key.getDefinedValues(oldEntity)))",
              DuplicateKeyNotice.class)
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
                  + " oldEntity.csvRowNumber(), newEntity.csvRowNumber(), $T.$L, newEntity.$L()))",
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

    sortedGroupIndexField.ifPresent(
        (indexField) -> addSortedGroupDuplicateDetection(method, indexField));
    return method.build();
  }

  /**
   * Emits the detection of duplicate primary keys by a scan of the sorted groups of an index.
   *
   * <p>Runs after the index has been filled and sorted: entities sharing a primary key are then
   * adjacent inside a group, since the groups are keyed on one key field and sorted on the other.
   * See {@link #resolveSortedGroupIndexField()} for why this is worth the extra code.
   */
  private void addSortedGroupDuplicateDetection(
      MethodSpec.Builder method, GtfsFieldDescriptor indexField) {
    TypeName gtfsEntityType = classNames.entityImplementationTypeName();
    GtfsFieldDescriptor sequenceField = resolveSequenceField(indexField).get();
    String sequence = sequenceField.name();
    TypeName sequenceBoxedType = TypeName.get(sequenceField.javaType()).box();
    method
        .addComment("Entities are visited in the order they were loaded, and each one is looked up")
        .addComment("in its own group of the index sorted above: the leftmost entity of the group")
        .addComment("with the same sequence is the first one holding that primary key, so any")
        .addComment("other entity is a duplicate of it. This reports exactly what the map keyed on")
        .addComment("the composite key reported, without holding one entry per row.")
        .beginControlFlow("for ($T entity : entities)", gtfsEntityType)
        .addStatement(
            "List<$T> sortedGroup = $L.get(entity.$L())",
            gtfsEntityType,
            byKeyMapName(indexField.name()),
            indexField.name())
        .addComment("Leftmost binary search: the group is sorted by " + sequence + ".")
        .addStatement("int low = 0")
        .addStatement("int high = sortedGroup.size()")
        .beginControlFlow("while (low < high)")
        .addStatement("int middle = (low + high) >>> 1")
        .beginControlFlow(
            "if ($T.compare(sortedGroup.get(middle).$L(), entity.$L()) < 0)",
            sequenceBoxedType,
            sequence,
            sequence)
        .addStatement("low = middle + 1")
        .nextControlFlow("else")
        .addStatement("high = middle")
        .endControlFlow()
        .endControlFlow()
        .addStatement("$T firstEntity = sortedGroup.get(low)", gtfsEntityType)
        .beginControlFlow("if (firstEntity == entity)")
        .addStatement("continue")
        .endControlFlow()
        .addStatement(
            "CompositeKey key = CompositeKey.builder()\n$L\n.build()",
            compositeKeyBuilderSetters("firstEntity"))
        .addStatement(
            "noticeContainer.addValidationNotice(new $T(\n"
                + "gtfsFilename(), firstEntity.csvRowNumber(), entity.csvRowNumber(),\n"
                + "key.getDefinedKeys(firstEntity), key.getDefinedValues(firstEntity)))",
            DuplicateKeyNotice.class)
        .endControlFlow();
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

    // Generic method to return only defined keys (using has<FieldName>() checks)
    TypeName gtfsEntityType = classNames.entityImplementationTypeName();
    MethodSpec.Builder getDefinedKeysMethod =
        MethodSpec.methodBuilder("getDefinedKeys")
            .addModifiers(Modifier.PUBLIC)
            .returns(String.class)
            .addParameter(gtfsEntityType, "entity")
            .addStatement("List<String> keys = new ArrayList<>()");

    // Generic method to return only defined values (using has<FieldName>() checks)
    MethodSpec.Builder getDefinedValuesMethod =
        MethodSpec.methodBuilder("getDefinedValues")
            .addModifiers(Modifier.PUBLIC)
            .returns(Object.class)
            .addParameter(gtfsEntityType, "entity")
            .addStatement("List<Object> values = new ArrayList<>()");

    // Iterate over primary keys and use has<FieldName>() checks
    for (GtfsFieldDescriptor keyField : fileDescriptor.primaryKeys()) {
      String hasMethodName = FieldNameConverter.hasMethodName(keyField.name());
      String fieldGetter = keyField.name();
      String columnName = FieldNameConverter.fieldNameField(keyField.name());

      getDefinedKeysMethod
          .beginControlFlow("if (entity.$L())", hasMethodName)
          .addStatement(
              "keys.add($T.$L)",
              gtfsEntityType, // First placeholder for `$T`
              columnName // Second placeholder for `$L`
              )
          .endControlFlow();

      getDefinedValuesMethod
          .beginControlFlow("if (entity.$L())", hasMethodName)
          .addStatement(
              "values.add(entity.$L())", fieldGetter // Third placeholder for entity method call
              )
          .endControlFlow();
    }

    getDefinedKeysMethod.addStatement("return String.join(\",\", keys)");
    getDefinedValuesMethod.addStatement(
        "return values.stream().reduce((a, b) -> (a.toString() + \",\" + b.toString())).orElse(null)");

    keySpec.addMethod(getDefinedKeysMethod.build());
    keySpec.addMethod(getDefinedValuesMethod.build());

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
