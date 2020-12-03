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
 * <p>
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

    private static int lastBitFieldNumber(int fieldCount) {
        // Each bitField has 32 bits. We need bitField0_ to store 1..32 fields,
        // bitField0_ and bitField1_ for 33..64 fields etc.
        return (fieldCount - 1) / 32;
    }

    /**
     * Returns name of a bitField with the given index.
     * <p>
     * Example.
     * <p>
     * bitFieldName(1) == "bitField1_"
     *
     * @param i number of a bitField, starting from 0.
     * @return name of a bitField, e.g., bitField0_.
     */
    private static String bitFieldName(int i) {
        return "bitField" + i + "_";
    }

    /**
     * Returns name of a bitField to store bit for GTFS field with a given number.
     * <p>
     * Examples.
     * * bitFieldName(1) == "bitField0_"
     * * bitFieldName(32) == "bitField1_"
     *
     * @param fieldNumber number of a GTFS field, starting from 0.
     * @return name of a bitField, e.g., bitField0_.
     */
    private static String bitFieldForFieldNumber(int fieldNumber) {
        // Bits for fields 0..31 are stored in bitField0_,
        // for fields 32..63 - in bitField1_ etc.
        return bitFieldName(fieldNumber / 32);
    }

    private static String maskForFieldNumber(int fieldNumber) {
        return "0x" + Integer.toHexString(1 << (fieldNumber % 32));
    }

    private static CodeBlock getDefaultValue(GtfsFieldDescriptor field) {
        if (field.defaultValue().isPresent()) {
            String valueString = field.defaultValue().get();
            switch (field.type()) {
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

    private void addEntityOrBuilderFields(TypeSpec.Builder typeSpec) {
        typeSpec.addField(long.class, CSV_ROW_NUMBER, Modifier.PRIVATE);
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            typeSpec.addField(getClassFieldType(field), field.name(), Modifier.PRIVATE);
        }
        for (int i = 0; i <= lastBitFieldNumber(fileDescriptor.fields().size()); ++i) {
            typeSpec.addField(FieldSpec.builder(int.class, "bitField" + i + "_", Modifier.PRIVATE)
                    .initializer("0").build());
        }
    }

    public TypeSpec generateGtfsEntityClass() {
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(classNames.entityImplementationSimpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(Generated.class)
                .addSuperinterface(GtfsEntity.class);

        for (TypeMirror superinterface : fileDescriptor.interfaces()) {
            typeSpec.addSuperinterface(superinterface);
        }

        addEntityOrBuilderFields(typeSpec);

        int fieldNumber = 0;

        typeSpec.addMethod(MethodSpec.methodBuilder(getterMethodName(CSV_ROW_NUMBER))
                .addModifiers(Modifier.PUBLIC)
                .returns(long.class)
                .addAnnotation(Override.class)
                .addStatement("return $L", CSV_ROW_NUMBER)
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
                .returns(TypeName.get(field.javaType()))
                .addAnnotation(Override.class);
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
                .addStatement("return ($L & $L) != 0", bitFieldForFieldNumber(fieldNumber), maskForFieldNumber(fieldNumber))
                .build();
    }

    public TypeSpec generateGtfsEntityBuilderClass() {
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder("Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);

        addEntityOrBuilderFields(typeSpec);

        typeSpec.addMethod(MethodSpec.methodBuilder(getterMethodName(CSV_ROW_NUMBER))
                .addModifiers(Modifier.PUBLIC)
                .returns(long.class)
                .addStatement("return $L", CSV_ROW_NUMBER).build());
        typeSpec.addMethod(MethodSpec.methodBuilder(setterMethodName(CSV_ROW_NUMBER))
                .addModifiers(Modifier.PUBLIC)
                .returns(classNames.entityBuilderTypeName())
                .addParameter(long.class, "value")
                .addStatement("$L = value", CSV_ROW_NUMBER)
                .addStatement("return this")
                .build());

        int fieldNumber = 0;
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            typeSpec.addMethod(MethodSpec.methodBuilder(getterMethodName(field.name()))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(getClassFieldType(field))
                    .addStatement("return $L", field.name()).build());
            typeSpec.addMethod(MethodSpec.methodBuilder(setterMethodName(field.name()))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(classNames.entityBuilderTypeName())
                    .addParameter(getClassFieldType(field).box(), "value")
                    .beginControlFlow("if (value == null)")
                    .addStatement("$L = $L", field.name(), getDefaultValue(field))
                    .addStatement("$L &= ~$L", bitFieldForFieldNumber(fieldNumber), maskForFieldNumber(fieldNumber))
                    .addStatement("return this")
                    .endControlFlow()
                    .addStatement("$L = value", field.name())
                    .addStatement("$L |= $L", bitFieldForFieldNumber(fieldNumber), maskForFieldNumber(fieldNumber))
                    .addStatement("return this")
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
                .addStatement("entity.$L = this.$L", CSV_ROW_NUMBER, CSV_ROW_NUMBER);
        for (int i = 0; i <= lastBitFieldNumber(fileDescriptor.fields().size()); ++i) {
            buildMethod.addStatement("entity.$L = this.$L", bitFieldName(i), bitFieldName(i));
        }
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            buildMethod.addStatement("entity.$L = this.$L", field.name(), field.name());
        }
        buildMethod.addStatement("return entity");

        return buildMethod.build();
    }

    private MethodSpec generateBuilderClearMethod() {
        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("clear")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement("$L = 0", CSV_ROW_NUMBER);
        for (int i = 0; i <= lastBitFieldNumber(fileDescriptor.fields().size()); ++i) {
            buildMethod.addStatement("$L = 0", bitFieldName(i));
        }
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            buildMethod.addStatement("$L = $L", field.name(), getDefaultValue(field));
        }
        return buildMethod.build();
    }
}
