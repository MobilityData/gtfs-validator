package org.mobilitydata.gtfsvalidator.processor;

import static org.mobilitydata.gtfsvalidator.processor.GtfsEntityClasses.TABLE_PACKAGE_NAME;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.table.GtfsTableDescriptor;
import org.mobilitydata.gtfsvalidator.table.GtfsTableRegistry;

public class TableRegistryGenerator {
  private final List<GtfsFileDescriptor> fileDescriptors;

  public TableRegistryGenerator(List<GtfsFileDescriptor> fileDescriptors) {
    this.fileDescriptors = fileDescriptors;
  }

  public JavaFile generateRegistry() {
    return JavaFile.builder(TABLE_PACKAGE_NAME, generateTableRegistryClass()).build();
  }

  private TypeSpec generateTableRegistryClass() {
    return TypeSpec.classBuilder("DefaultTableRegistry")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Generated.class)
        .addSuperinterface(GtfsTableRegistry.class)
        .addMethod(generateGetTableDescriptorsMethod())
        .build();
  }

  private MethodSpec generateGetTableDescriptorsMethod() {
    TypeName loaderType =
        ParameterizedTypeName.get(
            ClassName.get(GtfsTableDescriptor.class), WildcardTypeName.subtypeOf(Object.class));
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("getTableDescriptors")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(ParameterizedTypeName.get(ClassName.get(ImmutableList.class), loaderType));
    method.addStatement(
        "$T.Builder<$T> builder = $T.builder()",
        ImmutableList.class,
        loaderType,
        ImmutableList.class);
    for (GtfsFileDescriptor fileDescriptor : fileDescriptors) {
      method.addStatement(
          "builder.add(new $T())", new GtfsEntityClasses(fileDescriptor).tableDescriptorTypeName());
    }
    method.addStatement("return builder.build()");
    return method.build();
  }
}
