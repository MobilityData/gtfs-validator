package org.mobilitydata.gtfsvalidator.processor;

import com.squareup.javapoet.*;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.time.ZoneId;
import java.util.TimeZone;

import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.*;
import static org.mobilitydata.gtfsvalidator.processor.GtfsEntityClasses.TABLE_PACKAGE_NAME;

/**
 * Generates a class that represent a single parsed row of a GTFS table.
 *
 * E.g., GtfsStop class is generated for "stops.txt".
 */
public class EntityImplementationGenerator {
    private static final String CSV_ROW_NUMBER = "csvRowNumber";
    private final GtfsFileDescriptor fileDescriptor;
    private final GtfsEntityClasses classNames;

    public EntityImplementationGenerator(GtfsFileDescriptor fileDescriptor) {
        this.fileDescriptor = fileDescriptor;
        this.classNames = new GtfsEntityClasses(fileDescriptor);
    }

    private static String maskForFieldNumber(int fieldNumber) {
        return "0x" + Integer.toHexString(1 << fieldNumber);
    }

    private static CodeBlock getDefaultValue(GtfsFieldDescriptor field) {
        if (field.defaultValue().isPresent()) {
            String valueString = field.defaultValue().get();
            switch (field.type()) {
                case BOOLEAN:
                    return CodeBlock.of(
                            Boolean.parseBoolean(valueString) || (Integer.parseInt(valueString) == 1) ?
                                    "true" : "false");
                case INTEGER:
                case ENUM:
                    return CodeBlock.of(Integer.toString(Integer.parseInt(valueString)));
                case FLOAT:
                case LATITUDE:
                case LONGITUDE:
                    return CodeBlock.of(Double.toString(Double.parseDouble(valueString)));
                case COLOR:
                    return CodeBlock.of("$T.fromInt(0x$L)",
                            GtfsColor.class, Integer.toHexString(Integer.parseInt(valueString, 16)));
                case TEXT:
                    return CodeBlock.of("$S", valueString);
                default:
                    // TODO: Support all types or throw an exception.
                    break;
            }
        }
        switch (field.type()) {
            case BOOLEAN:
                return CodeBlock.of("false");
            case ENUM:
            case INTEGER:
            case FLOAT:
            case LATITUDE:
            case LONGITUDE:
                return CodeBlock.of("0");
            case COLOR:
                return CodeBlock.of("$T.fromInt(0)", GtfsColor.class);
            case TEXT:
            case URL:
            case PHONE_NUMBER:
            case ID:
            case EMAIL:
                return CodeBlock.of("\"\"");
            case DATE:
                return CodeBlock.of("$T.fromEpochDay(0)", GtfsDate.class);
            case TIME:
                return CodeBlock.of("$T.fromSecondsSinceMidnight(0)", GtfsTime.class);
            case TIMEZONE:
                return CodeBlock.of("$T.getTimeZone($T.of(\"UTC\"))", TimeZone.class, ZoneId.class);
            case CURRENCY_CODE:
            case LANGUAGE_CODE:
            default:
                return CodeBlock.of("null");
        }
    }

    private static TypeName getClassFieldType(GtfsFieldDescriptor field) {
        if (field.type() == FieldTypeEnum.ENUM) {
            return TypeName.INT;
        }
        return TypeName.get(field.javaType());
    }

    public JavaFile generateGtfsEntityJavaFile() {
        return JavaFile.builder(
                TABLE_PACKAGE_NAME, generateGtfsEntityClass()).build();
    }

    public TypeSpec generateGtfsEntityClass() {
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(classNames.entityImplementationSimpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(Generated.class)
                .addSuperinterface(GtfsEntity.class);

        for (TypeMirror superinterface : fileDescriptor.interfaces()) {
            typeSpec.addSuperinterface(superinterface);
        }

        typeSpec.addField(long.class, "csvRowNumber", Modifier.PRIVATE);
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            typeSpec.addField(getClassFieldType(field), field.name(), Modifier.PRIVATE);
        }
        typeSpec.addField(FieldSpec.builder(int.class, "bitField0_", Modifier.PRIVATE)
                .initializer("0").build());

        int fieldNumber = 0;

        typeSpec.addMethod(MethodSpec.methodBuilder("csvRowNumber")
                .addModifiers(Modifier.PUBLIC)
                .returns(long.class)
                .addAnnotation(Override.class)
                .addStatement("return csvRowNumber")
                .build());
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            typeSpec.addMethod(generateGetterMethod(field));
            maybeAddEnumValueGetter(field, typeSpec);
            typeSpec.addMethod(generateHasMethod(field, fieldNumber));
            ++fieldNumber;
        }

        typeSpec.addType(generateGtfsEntityBuilderClass());

        return typeSpec.build();
    }

    private MethodSpec generateGetterMethod(GtfsFieldDescriptor field) {
        MethodSpec.Builder method = MethodSpec.methodBuilder(getterMethodName(field.name()))
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(field.javaType()));
        if (field.type() == FieldTypeEnum.ENUM) {
            method.addStatement("$T result = $T.forNumber($L)",
                    field.javaType(), field.javaType(), field.name())
                    .addStatement("return result == null ? $T.UNRECOGNIZED : result", field.javaType());
        } else {
            method.addStatement("return $L", field.name());
        }
        return method.build();
    }

    private void maybeAddEnumValueGetter(GtfsFieldDescriptor field, TypeSpec.Builder typeSpec) {
        if (field.type() != FieldTypeEnum.ENUM) {
            return;
        }
        typeSpec.addMethod(MethodSpec.methodBuilder(getValueMethodName(field.name()))
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addStatement("return $L", field.name()).build());
    }

    private MethodSpec generateHasMethod(GtfsFieldDescriptor field, int fieldNumber) {
        return MethodSpec.methodBuilder(hasMethodName(field.name()))
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return (bitField0_ & $L) != 0", maskForFieldNumber(fieldNumber))
                .build();
    }

    public TypeSpec generateGtfsEntityBuilderClass() {
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder("Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);

        typeSpec.addField(long.class, CSV_ROW_NUMBER, Modifier.PRIVATE);
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            typeSpec.addField(getClassFieldType(field), field.name(), Modifier.PRIVATE);
        }
        typeSpec.addField(FieldSpec.builder(int.class, "bitField0_", Modifier.PRIVATE)
                .initializer("0").build());

        typeSpec.addMethod(MethodSpec.methodBuilder(getterMethodName(CSV_ROW_NUMBER))
                .addModifiers(Modifier.PUBLIC)
                .returns(long.class)
                .addStatement("return $L", CSV_ROW_NUMBER).build());
        typeSpec.addMethod(MethodSpec.methodBuilder(setterMethodName(CSV_ROW_NUMBER))
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(long.class, "value")
                .addStatement("$L = value", CSV_ROW_NUMBER)
                .build());

        int fieldNumber = 0;
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            typeSpec.addMethod(MethodSpec.methodBuilder(getterMethodName(field.name()))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(getClassFieldType(field))
                    .addStatement("return $L", field.name()).build());
            typeSpec.addMethod(MethodSpec.methodBuilder(setterMethodName(field.name()))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(getClassFieldType(field).box(), "value")
                    .beginControlFlow("if (value == null)")
                    .addStatement("$L = $L", field.name(), getDefaultValue(field))
                    .addStatement("bitField0_ &= ~$L", maskForFieldNumber(fieldNumber))
                    .addStatement("return")
                    .endControlFlow()
                    .addStatement("$L = value", field.name())
                    .addStatement("bitField0_ |= $L", maskForFieldNumber(fieldNumber))
                    .build());
            ++fieldNumber;
        }

        typeSpec.addMethod(generateBuilderBuildMethod());
        typeSpec.addMethod(generateBuilderClearMethod());

        return typeSpec.build();
    }

    private MethodSpec generateBuilderBuildMethod() {
        TypeName gtfsEntityType = classNames.entityImplementationTypeName();
        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(gtfsEntityType)
                .addStatement("$T entity = new $T()", gtfsEntityType, gtfsEntityType)
                .addStatement("entity.csvRowNumber = csvRowNumber")
                .addStatement("entity.bitField0_ = bitField0_");
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            buildMethod.addStatement("entity.$L = $L", field.name(), field.name());
        }
        buildMethod.addStatement("return entity");

        return buildMethod.build();
    }

    private MethodSpec generateBuilderClearMethod() {
        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("clear")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement("csvRowNumber = 0")
                .addStatement("bitField0_ = 0");
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            buildMethod.addStatement("$L = $L", field.name(), getDefaultValue(field));
        }
        return buildMethod.build();
    }
}
