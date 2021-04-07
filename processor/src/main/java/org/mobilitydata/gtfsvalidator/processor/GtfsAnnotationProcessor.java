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

import static javax.lang.model.util.ElementFilter.typesIn;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.squareup.javapoet.JavaFile;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;
import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValues;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;

/**
 * Processor that generates data classes, loaders and validators based on annotations on GTFS schema
 * interfaces.
 */
@AutoService(Processor.class)
public class GtfsAnnotationProcessor extends AbstractProcessor {

  private final Analyser analyser = new Analyser();

  /**
   * Sanitizes the result of {@link RoundEnvironment#getElementsAnnotatedWith}, which otherwise can
   * contain elements annotated with annotations of ERROR type.
   *
   * <p>The canonical example is forgetting to import &#64;Nullable.
   */
  private static Set<? extends Element> annotatedElementsIn(
      RoundEnvironment roundEnv, Class<? extends Annotation> a) {
    return Sets.filter(
        roundEnv.getElementsAnnotatedWith(a), element -> element.getAnnotation(a) != null);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return ImmutableSet.of(
        GtfsTable.class.getName(), GtfsEnumValues.class.getName(), GtfsEnumValue.class.getName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    List<GtfsEnumDescriptor> enumDescriptors = new ArrayList<>();
    for (TypeElement type : typesIn(annotatedElementsIn(roundEnv, GtfsEnumValues.class))) {
      enumDescriptors.add(analyser.analyzeGtfsEnumType(type));
    }
    // Support enums that have a single value.
    for (TypeElement type : typesIn(annotatedElementsIn(roundEnv, GtfsEnumValue.class))) {
      enumDescriptors.add(analyser.analyzeGtfsEnumType(type));
    }
    for (GtfsEnumDescriptor enumDescriptor : enumDescriptors) {
      writeJavaFile(new EnumGenerator(enumDescriptor).generateEnumJavaFile());
    }

    List<GtfsFileDescriptor> fileDescriptors = new ArrayList<>();
    for (TypeElement type : typesIn(annotatedElementsIn(roundEnv, GtfsTable.class))) {
      fileDescriptors.add(analyser.analyzeGtfsFileType(type));
    }
    for (GtfsFileDescriptor fileDescriptor : fileDescriptors) {
      writeJavaFile(new EntityImplementationGenerator(fileDescriptor).generateGtfsEntityJavaFile());
      writeJavaFile(new TableLoaderGenerator(fileDescriptor).generateGtfsTableLoaderJavaFile());
      writeJavaFile(new TableContainerGenerator(fileDescriptor).generateGtfsContainerJavaFile());
    }
    for (JavaFile javaFile :
        new ForeignKeyValidatorGenerator(fileDescriptors).generateValidatorFiles()) {
      writeJavaFile(javaFile);
    }
    for (JavaFile javaFile :
        new EndRangeValidatorGenerator(fileDescriptors).generateValidatorFiles()) {
      writeJavaFile(javaFile);
    }
    for (JavaFile javaFile :
        new LatLonValidatorGenerator(fileDescriptors).generateValidatorFiles()) {
      writeJavaFile(javaFile);
    }
    return false;
  }

  private void writeJavaFile(JavaFile javaFile) {
    try {
      javaFile.writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      processingEnv
          .getMessager()
          .printMessage(
              Diagnostic.Kind.ERROR,
              String.format("failed to generate output file: %s", e.getMessage()));
    }
  }
}
