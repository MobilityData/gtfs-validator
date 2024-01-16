package org.mobilitydata.gtfsvalidator.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.columns.GtfsColumnAssignments;

public class ColumnBasedEntityColumnAssignmentsGenerator {

  static final String CLASS_NAME = "ColumnAssignments";

  static final ClassName TYPE_NAME = ClassName.get("", CLASS_NAME);

  private final GtfsFileDescriptor fileDescriptor;
  private final GtfsEntityClasses classNames;

  public ColumnBasedEntityColumnAssignmentsGenerator(
      GtfsFileDescriptor fileDescriptor, GtfsEntityClasses classNames) {
    this.fileDescriptor = fileDescriptor;
    this.classNames = classNames;
  }

  public TypeSpec create() {
    TypeSpec.Builder typeBuilder =
        TypeSpec.classBuilder(CLASS_NAME)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addSuperinterface(GtfsColumnAssignments.class);
    generateColumnIndexFields(typeBuilder);
    return typeBuilder.build();
  }

  private void generateColumnIndexFields(TypeSpec.Builder typeBuilder) {
    List<String> fieldNames = new ArrayList<>();
    fieldNames.add("csvRowNumber");
    fieldNames.addAll(
        fileDescriptor.fields().stream()
            .map(GtfsFieldDescriptor::name)
            .collect(Collectors.toList()));
    for (String fieldName : fieldNames) {
      typeBuilder.addField(
          FieldSpec.builder(int.class, fieldName, Modifier.PRIVATE).initializer("-1").build());
    }
    for (String fieldName : fieldNames) {
      typeBuilder.addMethod(
          MethodSpec.methodBuilder(fieldName)
              .addModifiers(Modifier.PUBLIC)
              .returns(int.class)
              .addStatement("return $L", fieldName)
              .build());
    }
    for (String fieldName : fieldNames) {
      typeBuilder.addMethod(
          MethodSpec.methodBuilder(FieldNameConverter.setterMethodName(fieldName))
              .addModifiers(Modifier.PUBLIC)
              .addParameter(int.class, "value")
              .addStatement("$L = value", fieldName)
              .build());
    }
  }
}
