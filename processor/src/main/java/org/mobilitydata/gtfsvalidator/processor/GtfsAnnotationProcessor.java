package org.mobilitydata.gtfsvalidator.processor;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.squareup.javapoet.JavaFile;
import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValues;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static javax.lang.model.util.ElementFilter.typesIn;

/**
 * Processor that generates data classes, loaders and validators based on annotations on GTFS schema interfaces.
 */
@AutoService(Processor.class)
public class GtfsAnnotationProcessor extends AbstractProcessor {

    private Analyser analyser = new Analyser();

    /**
     * Sanitizes the result of {@link RoundEnvironment#getElementsAnnotatedWith}, which otherwise
     * can contain elements annotated with annotations of ERROR type.
     *
     * <p>The canonical example is forgetting to import &#64;Nullable.
     */
    private static Set<? extends Element> annotatedElementsIn(
            RoundEnvironment roundEnv, Class<? extends Annotation> a) {
        return Sets.filter(roundEnv.getElementsAnnotatedWith(a),
                element -> element.getAnnotation(a) != null);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(GtfsTable.class.getName(), GtfsEnumValues.class.getName());
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
        ForeignKeyValidatorGenerator foreignKeyValidatorGenerator = new ForeignKeyValidatorGenerator(fileDescriptors);
        for (JavaFile javaFile : foreignKeyValidatorGenerator.generateValidatorFiles()) {
            writeJavaFile(javaFile);
        }
        return false;
    }

    private void writeJavaFile(JavaFile javaFile) {
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR, String.format("failed to generate output file: %s", e.getMessage()));
        }
    }

}
