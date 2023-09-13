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

import static org.mobilitydata.gtfsvalidator.processor.GtfsEntityClasses.TABLE_PACKAGE_NAME;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableDescriptor;

/**
 * Generates code for a container for a loaded GTFS table.
 *
 * <p>E.g., GtfsStopTableContainer class is generated for "stops.txt".
 */
public class TableContainerGenerator {

  private final GtfsFileDescriptor fileDescriptor;
  private final GtfsEntityClasses classNames;
  private final TableContainerIndexGenerator indexGenerator;

  private final ParameterizedTypeName tableDescriptorType;

  public TableContainerGenerator(GtfsFileDescriptor fileDescriptor) {
    this.fileDescriptor = fileDescriptor;
    this.classNames = new GtfsEntityClasses(fileDescriptor);
    this.indexGenerator = new TableContainerIndexGenerator(fileDescriptor);
    this.tableDescriptorType =
        ParameterizedTypeName.get(
            ClassName.get(GtfsTableDescriptor.class), classNames.entityImplementationTypeName());
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
            .addStatement("return $T.FILENAME", classNames.entityImplementationTypeName())
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

    typeSpec.addMethod(generateConstructorWithEntities());
    typeSpec.addMethod(generateConstructorWithStatus());
    typeSpec.addMethod(generateForHeaderAndEntitiesMethod());
    typeSpec.addMethod(generateForEntitiesMethod());
    typeSpec.addMethod(generateForStatusMethod());

    indexGenerator.generateMethods(typeSpec);

    return typeSpec.build();
  }

  private MethodSpec generateConstructorWithEntities() {
    return MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PRIVATE)
        .addParameter(tableDescriptorType, "descriptor")
        .addParameter(CsvHeader.class, "header")
        .addParameter(
            ParameterizedTypeName.get(
                ClassName.get(List.class), classNames.entityImplementationTypeName()),
            "entities")
        .addStatement("super(descriptor, TableStatus.PARSABLE_HEADERS_AND_ROWS, header)")
        .addStatement("this.entities = entities")
        .build();
  }

  private MethodSpec generateConstructorWithStatus() {
    return MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(tableDescriptorType, "descriptor")
        .addParameter(GtfsTableContainer.TableStatus.class, "tableStatus")
        .addStatement("super(descriptor, tableStatus, $T.EMPTY)", CsvHeader.class)
        .addStatement("this.entities = new $T<>()", ArrayList.class)
        .build();
  }

  private MethodSpec generateForHeaderAndEntitiesMethod() {
    TypeName tableContainerTypeName = classNames.tableContainerTypeName();
    return MethodSpec.methodBuilder("forHeaderAndEntities")
        .returns(tableContainerTypeName)
        .addJavadoc("Creates a table with given header and entities")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(tableDescriptorType, "descriptor")
        .addParameter(CsvHeader.class, "header")
        .addParameter(
            ParameterizedTypeName.get(
                ClassName.get(List.class), classNames.entityImplementationTypeName()),
            "entities")
        .addParameter(NoticeContainer.class, "noticeContainer")
        .addStatement(
            "$T table = new $T(descriptor, header, entities)",
            tableContainerTypeName,
            tableContainerTypeName)
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
            "return forHeaderAndEntities(new $T(), $T.EMPTY, entities, noticeContainer)",
            classNames.tableDescriptorTypeName(),
            CsvHeader.class)
        .build();
  }

  private MethodSpec generateForStatusMethod() {
    TypeName tableContainerTypeName = classNames.tableContainerTypeName();
    return MethodSpec.methodBuilder("forStatus")
        .returns(tableContainerTypeName)
        .addJavadoc(
            "Creates a table with the given TableStatus. This method is intended to be"
                + " used in tests.")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(GtfsTableContainer.TableStatus.class, "tableStatus")
        .addStatement(
            "return new $T(new $T(), tableStatus)",
            tableContainerTypeName,
            classNames.tableDescriptorTypeName())
        .build();
  }
}
