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

import static javax.lang.model.util.ElementFilter.methodsIn;
import static org.mobilitydata.gtfsvalidator.processor.EnumGenerator.createEnumName;
import static org.mobilitydata.gtfsvalidator.processor.GtfsEntityClasses.entityImplementationSimpleName;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Locale;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;
import org.mobilitydata.gtfsvalidator.annotation.*;
import org.mobilitydata.gtfsvalidator.parsing.RowParser.NumberBounds;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Analyses annotations on Java interfaces that define GTFS schema and translates them to
 * descriptors ({@code GtfsFileDescriptor} etc.).
 *
 * <p>Code generators (such as {@code TableContainerGenerator}) use descriptors instead of analysing
 * Java annotations directly. This makes the code of generators much simpler, makes testing easier
 * and also allows to extend GTFS schema by adding new Java interfaces that describe the same
 * tables.
 */
public class Analyser {

  public GtfsFileDescriptor analyzeGtfsFileType(TypeElement type) {
    GtfsFileDescriptor.Builder fileBuilder = GtfsFileDescriptor.builder();
    GtfsTable gtfsFileAnnotation = type.getAnnotation(GtfsTable.class);
    fileBuilder.setFilename(gtfsFileAnnotation.value().toLowerCase());
    fileBuilder.setSingleRow(gtfsFileAnnotation.singleRow());
    if (gtfsFileAnnotation.maxCharsPerColumn() != 0) {
      fileBuilder.setMaxCharsPerColumn(gtfsFileAnnotation.maxCharsPerColumn());
    }
    fileBuilder.interfacesBuilder().add(type.asType());
    fileBuilder.setClassName(entityImplementationSimpleName(type.getSimpleName().toString()));
    fileBuilder.setRecommended(type.getAnnotation(Recommended.class) != null);
    fileBuilder.setRequired(type.getAnnotation(Required.class) != null);
    for (ExecutableElement method : methodsIn(type.getEnclosedElements())) {
      GtfsFieldDescriptor.Builder fieldBuilder = GtfsFieldDescriptor.builder();
      fieldBuilder.setName(method.getSimpleName().toString());
      fieldBuilder.setJavaType(method.getReturnType());
      FieldType fieldTypeAnnotation = method.getAnnotation(FieldType.class);
      fieldBuilder.setType(
          fieldTypeAnnotation != null
              ? fieldTypeAnnotation.value()
              : javaTypeToGtfsType(method.getReturnType()));
      fieldBuilder.setRecommended(method.getAnnotation(Recommended.class) != null);
      fieldBuilder.setColumnRequired(method.getAnnotation(RequiredColumn.class) != null);
      fieldBuilder.setValueRequired(method.getAnnotation(Required.class) != null);
      fieldBuilder.setMixedCase(method.getAnnotation(MixedCase.class) != null);
      PrimaryKey primaryKey = method.getAnnotation(PrimaryKey.class);
      if (primaryKey != null) {
        fieldBuilder.setPrimaryKey(primaryKey);
      }
      fieldBuilder.setIndex(method.getAnnotation(Index.class) != null);
      fieldBuilder.setCached(method.getAnnotation(CachedField.class) != null);

      if (method.getAnnotation(Positive.class) != null) {
        fieldBuilder.setNumberBounds(NumberBounds.POSITIVE);
      } else if (method.getAnnotation(NonNegative.class) != null) {
        fieldBuilder.setNumberBounds(NumberBounds.NON_NEGATIVE);
      } else if (method.getAnnotation(NonZero.class) != null) {
        fieldBuilder.setNumberBounds(NumberBounds.NON_ZERO);
      }

      ForeignKey foreignKey = method.getAnnotation(ForeignKey.class);
      if (foreignKey != null) {
        fieldBuilder.setForeignKey(
            ForeignKeyDescriptor.create(
                foreignKey.table(), FieldNameConverter.javaFieldName(foreignKey.field())));
      }

      DefaultValue defaultValue = method.getAnnotation(DefaultValue.class);
      if (defaultValue != null && defaultValue.value() != null) {
        fieldBuilder.setDefaultValue(defaultValue.value());
      }

      EndRange endRange = method.getAnnotation(EndRange.class);
      if (endRange != null) {
        fieldBuilder.setEndRange(
            EndRangeDescriptor.create(
                FieldNameConverter.javaFieldName(endRange.field()), endRange.allowEqual()));
      }

      CurrencyAmount currencyAmount = method.getAnnotation(CurrencyAmount.class);
      if (currencyAmount != null) {
        fieldBuilder.setCurrencyFieldReference(currencyAmount.currencyField());
      }

      fileBuilder.fieldsBuilder().add(fieldBuilder.build());
    }
    return fileBuilder.build();
  }

  FieldTypeEnum javaTypeToGtfsType(TypeMirror javaType) {
    return javaType.accept(
        new SimpleTypeVisitor8<FieldTypeEnum, Void>() {
          @Override
          public FieldTypeEnum visitPrimitive(PrimitiveType t, Void p) {
            switch (t.getKind()) {
              case INT:
                return FieldTypeEnum.INTEGER;
              case DOUBLE:
                return FieldTypeEnum.FLOAT;
              default:
                throw new AssertionError();
            }
          }

          @Override
          public FieldTypeEnum visitDeclared(DeclaredType t, Void p) {
            String name = ((TypeElement) t.asElement()).getQualifiedName().toString();
            if (name.equals(String.class.getCanonicalName())) {
              return FieldTypeEnum.TEXT;
            }
            if (name.equals(ZoneId.class.getCanonicalName())) {
              return FieldTypeEnum.TIMEZONE;
            }
            if (name.equals(Locale.class.getCanonicalName())) {
              return FieldTypeEnum.LANGUAGE_CODE;
            }
            if (name.equals(Currency.class.getCanonicalName())) {
              return FieldTypeEnum.CURRENCY_CODE;
            }
            if (name.equals(GtfsDate.class.getCanonicalName())) {
              return FieldTypeEnum.DATE;
            }
            if (name.equals(GtfsTime.class.getCanonicalName())) {
              return FieldTypeEnum.TIME;
            }
            if (name.equals(GtfsColor.class.getCanonicalName())) {
              return FieldTypeEnum.COLOR;
            }
            if (name.equals(BigDecimal.class.getCanonicalName())) {
              return FieldTypeEnum.DECIMAL;
            }
            return FieldTypeEnum.ENUM;
          }

          @Override
          protected FieldTypeEnum defaultAction(TypeMirror e, Void p) {
            return FieldTypeEnum.ENUM;
          }
        },
        null);
  }

  public GtfsEnumDescriptor analyzeGtfsEnumType(TypeElement type) {
    GtfsEnumDescriptor.Builder enumBuilder = GtfsEnumDescriptor.builder();
    enumBuilder.setName(createEnumName(type.getSimpleName().toString()));
    GtfsEnumValues valuesAnnotation = type.getAnnotation(GtfsEnumValues.class);
    if (valuesAnnotation != null) {
      // A typical enum has multiple values.
      for (GtfsEnumValue value : valuesAnnotation.value()) {
        addEnumValue(value, enumBuilder);
      }
    } else {
      // A trivial enum has a single values.
      addEnumValue(type.getAnnotation(GtfsEnumValue.class), enumBuilder);
    }
    return enumBuilder.build();
  }

  private void addEnumValue(GtfsEnumValue value, GtfsEnumDescriptor.Builder enumBuilder) {
    enumBuilder
        .valuesBuilder()
        .add(GtfsEnumValueDescriptor.create(value.name().toUpperCase(), value.value()));
  }
}
