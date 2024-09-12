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

import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.fieldDefaultName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.getValueMethodName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.getterMethodName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.hasMethodName;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.Objects;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.columns.GtfsColumnAssignments;
import org.mobilitydata.gtfsvalidator.columns.GtfsColumnBasedEntity;
import org.mobilitydata.gtfsvalidator.columns.GtfsColumnStore;

/**
 * Generates a class that represent a single parsed row of a GTFS table.
 *
 * <p>E.g., GtfsStop class is generated for "stops.txt".
 */
public class ColumnBasedEntityImplementationGenerator {

  private static final String COLUMN_STORE_FIELD = "store";
  private static final String COLUMN_ASSIGNMENTS_FIELD = "assignments";
  private static final String ROW_INDEX_FIELD = "rowIndex";
  private static final Class ROW_INDEX_TYPE = int.class;

  private final GtfsFileDescriptor fileDescriptor;
  private final GtfsEntityClasses classNames;
  private final ColumnBasedEntityColumnAssignmentsGenerator assignmentGenerator;

  private final ColumnBasedEntityBuilderGenerator builderGenerator;

  public ColumnBasedEntityImplementationGenerator(
      GtfsFileDescriptor fileDescriptor, TypeMirror primitiveIntType) {
    this.fileDescriptor = fileDescriptor;
    this.classNames = new GtfsEntityClasses(fileDescriptor);
    this.assignmentGenerator =
        new ColumnBasedEntityColumnAssignmentsGenerator(fileDescriptor, this.classNames);
    this.builderGenerator =
        new ColumnBasedEntityBuilderGenerator(fileDescriptor, this.classNames, primitiveIntType);
  }

  public JavaFile generateGtfsEntityJavaFile() {
    return JavaFile.builder(fileDescriptor.packageName(), generateGtfsEntityClass()).build();
  }

  public TypeSpec generateGtfsEntityClass() {
    TypeSpec.Builder typeSpec =
        TypeSpec.classBuilder(classNames.columnBasedEntityImplementationSimpleName())
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(Generated.class)
            .superclass(ClassName.get(GtfsColumnBasedEntity.class))
            .addSuperinterface(classNames.entityTypeName());

    generateFields(typeSpec);
    typeSpec.addMethod(generateConstructor());
    typeSpec.addMethod(generateCreateMethod());
    typeSpec.addMethod(generateCsvRowNumberMethod());
    generateGetters(typeSpec);
    generateHasValueMethods(typeSpec);
    typeSpec.addMethod(generateAssignmentsMethod());
    typeSpec.addMethod(generateEqualsMethod());

    typeSpec.addType(assignmentGenerator.create());
    typeSpec.addType(builderGenerator.create());

    return typeSpec.build();
  }

  private void generateFields(TypeSpec.Builder typeSpec) {
    typeSpec.addField(
        ColumnBasedEntityColumnAssignmentsGenerator.TYPE_NAME,
        COLUMN_ASSIGNMENTS_FIELD,
        Modifier.PRIVATE,
        Modifier.FINAL);
  }

  private MethodSpec generateConstructor() {
    return MethodSpec.constructorBuilder()
        .addParameter(ClassName.get(GtfsColumnStore.class), COLUMN_STORE_FIELD)
        .addParameter(
            ColumnBasedEntityColumnAssignmentsGenerator.TYPE_NAME, COLUMN_ASSIGNMENTS_FIELD)
        .addParameter(ROW_INDEX_TYPE, ROW_INDEX_FIELD)
        .addStatement("super($L, $L)", COLUMN_STORE_FIELD, ROW_INDEX_FIELD)
        .addStatement("this.$L = $L", COLUMN_ASSIGNMENTS_FIELD, COLUMN_ASSIGNMENTS_FIELD)
        .build();
  }

  private MethodSpec generateCreateMethod() {
    return MethodSpec.methodBuilder("create")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(classNames.columnBasedEntityImplementationTypeName())
        .addParameter(ClassName.get(GtfsColumnStore.class), COLUMN_STORE_FIELD)
        .addParameter(GtfsColumnAssignments.class, COLUMN_ASSIGNMENTS_FIELD)
        .addParameter(ROW_INDEX_TYPE, ROW_INDEX_FIELD)
        .addStatement(
            "$T.checkState($L instanceof $T)",
            Preconditions.class,
            COLUMN_ASSIGNMENTS_FIELD,
            ColumnBasedEntityColumnAssignmentsGenerator.TYPE_NAME)
        .addStatement(
            "return new $T($L, ($T) $L, $L)",
            classNames.columnBasedEntityImplementationTypeName(),
            COLUMN_STORE_FIELD,
            ColumnBasedEntityColumnAssignmentsGenerator.TYPE_NAME,
            COLUMN_ASSIGNMENTS_FIELD,
            ROW_INDEX_FIELD)
        .build();
  }

  private MethodSpec generateCsvRowNumberMethod() {
    return MethodSpec.methodBuilder(getterMethodName("csvRowNumber"))
        .addModifiers(Modifier.PUBLIC)
        .returns(int.class)
        .addAnnotation(Override.class)
        .addStatement(
            "return $L.getInt($L.csvRowNumber(), $L, -1)",
            COLUMN_STORE_FIELD,
            COLUMN_ASSIGNMENTS_FIELD,
            ROW_INDEX_FIELD)
        .build();
  }

  private void generateGetters(TypeSpec.Builder typeSpec) {
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      typeSpec.addMethod(generateGetterMethod(field));
      if (field.type() == FieldTypeEnum.ENUM) {
        typeSpec.addMethod(generateEnumValueGetterMethod(field));
      }
    }
  }

  private MethodSpec generateGetterMethod(GtfsFieldDescriptor field) {
    CodeBlock valueGetter = generateValueGetterCodeBlock(field);
    if (field.type() == FieldTypeEnum.ENUM) {
      valueGetter = CodeBlock.of("$L.forNumber($L)", field.javaType(), valueGetter);
    }
    return MethodSpec.methodBuilder(getterMethodName(field.name()))
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.get(field.javaType()))
        .addAnnotation(EntityGenerator.nullabilityAnnotation(field))
        .addAnnotation(Override.class)
        .addStatement("return $L", valueGetter)
        .build();
  }

  private MethodSpec generateEnumValueGetterMethod(GtfsFieldDescriptor field) {
    CodeBlock valueGetter = generateValueGetterCodeBlock(field);
    return MethodSpec.methodBuilder(getValueMethodName(field.name()))
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.INT)
        .addAnnotation(EntityGenerator.nullabilityAnnotation(field))
        .addAnnotation(Override.class)
        .addStatement("return $L", valueGetter)
        .build();
  }

  private CodeBlock generateValueGetterCodeBlock(GtfsFieldDescriptor field) {
    String columnGetterName = getFieldTypeColumn(field);
    return CodeBlock.of(
        "$L.get$L($L.$L(), $L, $T.$L)",
        COLUMN_STORE_FIELD,
        columnGetterName,
        COLUMN_ASSIGNMENTS_FIELD,
        field.name(),
        ROW_INDEX_FIELD,
        classNames.entityTypeName(),
        fieldDefaultName(field.name()));
  }

  private void generateHasValueMethods(TypeSpec.Builder typeSpec) {
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      String columnGetterName = getFieldTypeColumn(field);
      CodeBlock hasCodeBlock =
          CodeBlock.of(
              "return $L.has$L(assignments.$L(), $L)",
              COLUMN_STORE_FIELD,
              columnGetterName,
              field.name(),
              ROW_INDEX_FIELD);

      typeSpec.addMethod(
          MethodSpec.methodBuilder(hasMethodName(field.name()))
              .addModifiers(Modifier.PUBLIC)
              .returns(boolean.class)
              .addAnnotation(Override.class)
              .addStatement(hasCodeBlock)
              .build());
    }
  }

  private MethodSpec generateAssignmentsMethod() {
    return MethodSpec.methodBuilder("getAssignments")
        .addModifiers(Modifier.PROTECTED)
        .addAnnotation(Override.class)
        .returns(GtfsColumnAssignments.class)
        .addStatement("return $L", COLUMN_ASSIGNMENTS_FIELD)
        .build();
  }

  private MethodSpec generateEqualsMethod() {
    return MethodSpec.methodBuilder("equals")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .addParameter(Object.class, "rhs")
        .returns(boolean.class)
        .beginControlFlow("if (this == rhs)")
        .addStatement("return true")
        .endControlFlow()
        .beginControlFlow("if (rhs == null || getClass() != rhs.getClass())")
        .addStatement("return false")
        .endControlFlow()
        .addStatement(
            "$T entity = ($T) rhs",
            classNames.columnBasedEntityImplementationTypeName(),
            classNames.columnBasedEntityImplementationTypeName())
        .addCode("return $T.equals(this.store, entity.store)", Objects.class)
        .addCode("  && $T.equals(this.assignments, entity.assignments)", Objects.class)
        .addStatement("  && this.rowIndex == entity.rowIndex", Objects.class)
        .build();
  }

  static String getFieldTypeColumn(GtfsFieldDescriptor field) {
    if (field.enumDescriptor().isPresent()) {
      return capitalize(field.enumDescriptor().get().fieldType().toString());
    }
    if (field.javaType().getKind().isPrimitive()) {
      return capitalize(field.javaType().getKind().toString());
    } else if (field.javaType().getKind() == TypeKind.DECLARED) {
      return ((DeclaredType) field.javaType()).asElement().getSimpleName().toString();
    } else {
      throw new UnsupportedOperationException();
    }
  }

  private static String capitalize(String name) {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
  }

  static class FieldTypeGetterName extends SimpleTypeVisitor8<String, Void> {
    @Override
    public String visitPrimitive(PrimitiveType t, Void p) {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, t.getKind().toString());
    }

    @Override
    public String visitDeclared(DeclaredType t, Void p) {
      return t.asElement().getSimpleName().toString();
    }

    @Override
    public String visitError(ErrorType t, Void unused) {
      return t.asElement().getSimpleName().toString();
    }

    @Override
    protected String defaultAction(TypeMirror e, Void p) {
      return "Int";
    }
  }
}
