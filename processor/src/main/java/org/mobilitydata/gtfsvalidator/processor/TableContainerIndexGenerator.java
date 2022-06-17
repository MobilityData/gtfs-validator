package org.mobilitydata.gtfsvalidator.processor;

import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.byKeyMapName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.byKeyMethodName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.fieldNameField;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.hasMethodName;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
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
              .returns(classNames.entityImplementationTypeName())
              .addStatement("return entities.isEmpty() ? null : entities.get(0)")
              .build());
    } else if (fileDescriptor.hasMultiColumnPrimaryKey()) {
      addMapByCompositeKey(
          typeSpec, fileDescriptor.primaryKeys(), classNames.entityImplementationTypeName());
    } else if (fileDescriptor.hasSingleColumnPrimaryKey()) {
      addMapWithGetter(
          typeSpec,
          fileDescriptor.getSingleColumnPrimaryKey(),
          classNames.entityImplementationTypeName());
    }
    for (GtfsFieldDescriptor indexField : fileDescriptor.indices()) {
      addListMultimapWithGetters(
          typeSpec,
          indexField,
          resolveSequenceField(indexField),
          classNames.entityImplementationTypeName());
    }

    typeSpec.addField(generateKeyColumnNames());
    typeSpec.addMethod(generateGetKeyColumnNames());
    typeSpec.addMethod(generateByPrimaryKey());
    typeSpec.addMethod(generateSetupIndicesMethod());

    if (fileDescriptor.hasMultiColumnPrimaryKey()) {
      typeSpec.addType(compositeKeyClass());
    }
  }

  private Optional<GtfsFieldDescriptor> resolveSequenceField(GtfsFieldDescriptor indexField) {
    if (indexField.primaryKey()) {
      for (GtfsFieldDescriptor field : fileDescriptor.primaryKeys()) {
        if (field != indexField && field.sequenceKey()) {
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

  private static void addMapByCompositeKey(
      TypeSpec.Builder typeSpec, ImmutableList<GtfsFieldDescriptor> keys, TypeName entityTypeName) {

    // Field: Map<CompsiteKey, EntityType?> byCompositeKeyMap;
    TypeName keyMapType =
        ParameterizedTypeName.get(
            ClassName.get(Map.class), ClassName.get("", "CompositeKey"), entityTypeName);
    typeSpec.addField(
        FieldSpec.builder(keyMapType, BY_COMPOSITE_KEY_MAP_FIELD_NAME, Modifier.PRIVATE)
            .initializer("new $T<>()", ParameterizedTypeName.get(HashMap.class))
            .build());

    // Method: byCompositeKey(TypeA paramA, TypeB paramB, ...) { }
    String methodName = "byCompositeKey";
    MethodSpec.Builder m = MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC);
    String paramNames =
        keys.stream().map(GtfsFieldDescriptor::name).collect(Collectors.joining(", "));
    for (GtfsFieldDescriptor keyField : keys) {
      m.addParameter(TypeName.get(keyField.javaType()), keyField.name());
    }
    typeSpec.addMethod(
        m.returns(entityTypeName)
            .addStatement(
                "return $L.get(CompositeKey.create($L))",
                BY_COMPOSITE_KEY_MAP_FIELD_NAME,
                paramNames)
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
                        "$T.$L", classNames.tableLoaderTypeName(), fieldNameField(f.name())))
            .collect(CodeBlock.joining(", ")));
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

  private MethodSpec generateByPrimaryKey() {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("byPrimaryKey")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get(Optional.class), classNames.entityImplementationTypeName()))
            .addParameter(
                ParameterizedTypeName.get(
                    ClassName.get(ImmutableList.class), ClassName.get(String.class)),
                "ids");
    if (fileDescriptor.hasSingleColumnPrimaryKey()) {
      method.addStatement(
          "return Optional.ofNullable($L.getOrDefault(ids.get(0), null))",
          byKeyMapName(fileDescriptor.getSingleColumnPrimaryKey().name()));
    } else if (fileDescriptor.hasMultiColumnPrimaryKey()) {
      // We potentially need to perform type conversion on the input ids.
      List<CodeBlock> accessors = new ArrayList<>();
      for (int i = 0; i < fileDescriptor.primaryKeys().size(); ++i) {
        GtfsFieldDescriptor field = fileDescriptor.primaryKeys().get(i);
        CodeBlock accessor = CodeBlock.of("ids.get($L)", i);
        if (field.type() == FieldTypeEnum.INTEGER) {
          accessor =
              CodeBlock.of("$T.parseInt($L)", TypeName.get(field.javaType()).box(), accessor);
        } else if (field.type() == FieldTypeEnum.DATE) {
          accessor = CodeBlock.of("$T.fromString($L)", TypeName.get(GtfsDate.class), accessor);
        } else if (field.type() == FieldTypeEnum.TIME) {
          accessor = CodeBlock.of("$T.fromString($L)", TypeName.get(GtfsTime.class), accessor);
        }
        accessors.add(accessor);
      }
      method
          .beginControlFlow("try")
          .addStatement(
              "return Optional.ofNullable($L.getOrDefault(CompositeKey.create($L), null))",
              BY_COMPOSITE_KEY_MAP_FIELD_NAME,
              CodeBlock.join(accessors, ", "))
          .nextControlFlow("catch (NumberFormatException e)")
          .addStatement("return Optional.empty()")
          .nextControlFlow("catch (IllegalArgumentException e)")
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
    } else if (fileDescriptor.hasMultiColumnPrimaryKey()) {
      method
          .beginControlFlow("for ($T newEntity : entities)", gtfsEntityType)
          .addStatement(
              "CompositeKey key = CompositeKey.create($L)",
              fileDescriptor.primaryKeys().stream()
                  .map((field) -> CodeBlock.of("newEntity.$L()", field.name()))
                  .collect(CodeBlock.joining(", ")))
          .addStatement(
              "$T oldEntity = $L.getOrDefault(key, null)",
              classNames.entityImplementationTypeName(),
              BY_COMPOSITE_KEY_MAP_FIELD_NAME)
          .beginControlFlow("if (oldEntity != null)")
          .addStatement(
              "noticeContainer.addValidationNotice(new $T("
                  + "gtfsFilename(), newEntity.csvRowNumber(), "
                  + "oldEntity.csvRowNumber(), "
                  + "$L, $L))",
              DuplicateKeyNotice.class,
              fileDescriptor.primaryKeys().stream()
                  .map((field) -> CodeBlock.of("$T.$L", loaderType, fieldNameField(field.name())))
                  .collect(CodeBlock.joining(" + \",\" + ")),
              fileDescriptor.primaryKeys().stream()
                  .map((field) -> CodeBlock.of("oldEntity.$L()", field.name()))
                  .collect(CodeBlock.joining(" + \",\" + ")))
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
            .addModifiers(Modifier.STATIC, Modifier.ABSTRACT)
            .addAnnotation(ClassName.get("com.google.auto.value", "AutoValue"));

    // Getters for each field.
    for (GtfsFieldDescriptor keyField : fileDescriptor.primaryKeys()) {
      keySpec.addMethod(
          MethodSpec.methodBuilder(keyField.name())
              .addModifiers(Modifier.ABSTRACT)
              .returns(TypeName.get(keyField.javaType()))
              .build());
    }

    // The create() method.
    MethodSpec.Builder m =
        MethodSpec.methodBuilder("create")
            .addModifiers(Modifier.STATIC)
            .returns(ClassName.get("", "CompositeKey"));
    for (GtfsFieldDescriptor keyField : fileDescriptor.primaryKeys()) {
      m.addParameter(TypeName.get(keyField.javaType()), keyField.name());
    }
    String paramNames =
        fileDescriptor.primaryKeys().stream()
            .map(GtfsFieldDescriptor::name)
            .collect(Collectors.joining(", "));
    m.addStatement(
        "return new AutoValue_$L_CompositeKey($L)",
        classNames.tableContainerSimpleName(),
        paramNames);
    keySpec.addMethod(m.build());

    return keySpec.build();
  }
}
