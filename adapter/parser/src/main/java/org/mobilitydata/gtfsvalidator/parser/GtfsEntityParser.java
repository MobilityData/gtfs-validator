/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.parser;

import com.google.common.base.Strings;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.commons.validator.routines.FloatValidator;
import org.apache.commons.validator.routines.IntegerValidator;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.protos.GtfsSpecificationProto;
import org.mobilitydata.gtfsvalidator.usecase.notice.CannotParseDateNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.CannotParseFloatNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.CannotParseIntegerNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class GtfsEntityParser implements GtfsSpecRepository.RawEntityParser {
    private final GtfsSpecificationProto.CsvSpecProto fileSchema;
    private final RawFileInfo rawFileInfo;
    private final FloatValidator floatValidator;
    private final IntegerValidator integerValidator;
    private final DateValidator dateValidator;

    private static final String DATE_PATTERN = "yyyyMMdd";

    public GtfsEntityParser(GtfsSpecificationProto.CsvSpecProto fileSchema, RawFileInfo rawFileInfo,
                            FloatValidator floatValidator, IntegerValidator integerValidator,
                            DateValidator dateValidator) {
        this.fileSchema = fileSchema;
        this.rawFileInfo = rawFileInfo;
        this.floatValidator = floatValidator;
        this.integerValidator = integerValidator;
        if (!dateValidator.isStrict()) {
            throw new IllegalArgumentException("Date validator must be strict");
        }
        this.dateValidator = dateValidator;
    }

    /**
     * Returns a collection of errors regarding validity of non string types. That is validaiton of strings for
     * fields marked as FLOAT, INTEGER or DATE. See {@link GtfsSpecificationProto.ColumnInputType.InputType}
     *
     * @param toValidate the entity containing raw strings to validate
     * @return a list of encountered errors and warnings
     */
    @Override
    public Collection<ErrorNotice> validateNonStringTypes(RawEntity toValidate) {
        Collection<ErrorNotice> toReturn = new ArrayList<>();

        fileSchema.getColumnList().forEach(columnSpecProto -> {
            String rawField = toValidate.get(columnSpecProto.getName());

            //Skip values that weren't provided
            if (!Strings.isNullOrEmpty(rawField)) {

                if (columnSpecProto.getType().getType() == GtfsSpecificationProto.ColumnInputType.InputType.FLOAT) {

                    //FIXME: retrieve locale from agency_lang in agency.txt and if that doesn't exist,
                    //from feed_lang in feed_info.txt before defaulting to Locale.US
                    if (!floatValidator.isValid(rawField, Locale.US) || Float.isNaN(floatValidator.validate(rawField,
                            Locale.US))) {

                        toReturn.add(new CannotParseFloatNotice(
                                        fileSchema.getFilename(),
                                        columnSpecProto.getName(),
                                        toValidate.getIndex(),
                                        rawField
                                )
                        );
                    }
                } else if (columnSpecProto.getType().getType() ==
                        GtfsSpecificationProto.ColumnInputType.InputType.INTEGER) {

                    //FIXME: retrieve locale from agency_lang in agency.txt and if that doesn't exist,
                    //from feed_lang in feed_info.txt before defaulting to Locale.US
                    if (!integerValidator.isValid(rawField, Locale.US)) {

                        toReturn.add(new CannotParseIntegerNotice(
                                        fileSchema.getFilename(),
                                        columnSpecProto.getName(),
                                        toValidate.getIndex(),
                                        rawField
                                )
                        );
                    }
                } else if (columnSpecProto.getType().getType() ==
                        GtfsSpecificationProto.ColumnInputType.InputType.DATE) {

                    //FIXME: retrieve locale from agency_lang in agency.txt and if that doesn't exist,
                    //from feed_lang in feed_info.txt before defaulting to Locale.US
                    if (!dateValidator.isValid(rawField, DATE_PATTERN, Locale.US)) {
                        toReturn.add(new CannotParseDateNotice(
                                        fileSchema.getFilename(),
                                        columnSpecProto.getName(),
                                        toValidate.getIndex(),
                                        rawField
                                )
                        );
                    }
                }
            }
        });

        return toReturn;
    }

    @Override
    public ParsedEntity parse(RawEntity toParse) {
        Map<String, Object> contentByHeaderMap = new HashMap<>(fileSchema.getColumnCount());
        final String[] entityId = new String[1];

        fileSchema.getColumnList().forEach(columnSpecProto -> {
            String rawField = toParse.get(columnSpecProto.getName());

            if (!Strings.isNullOrEmpty(rawField)) {

                if (columnSpecProto.getType().getType() == GtfsSpecificationProto.ColumnInputType.InputType.FLOAT) {

                    //FIXME: retrieve locale from agency_lang in agency.txt and if that doesn't exist,
                    //from feed_lang in feed_info.txt before defaulting to Locale.US
                    if (floatValidator.isValid(rawField, Locale.US) && !Float.isNaN(floatValidator.validate(rawField,
                            Locale.US))) {
                        contentByHeaderMap.put(columnSpecProto.getName(), floatValidator.validate(rawField, Locale.US));
                    }

                } else if (columnSpecProto.getType().getType() ==
                        GtfsSpecificationProto.ColumnInputType.InputType.INTEGER) {

                    //FIXME: retrieve locale from agency_lang in agency.txt and if that doesn't exist,
                    //from feed_lang in feed_info.txt before defaulting to Locale.US
                    if (integerValidator.isValid(rawField, Locale.US)) {
                        contentByHeaderMap.put(columnSpecProto.getName(), integerValidator.validate(rawField,
                                Locale.US));
                    }

                } else if (columnSpecProto.getType().getType() ==
                        GtfsSpecificationProto.ColumnInputType.InputType.DATE) {

                    //FIXME: retrieve locale from agency_lang in agency.txt and if that doesn't exist,
                    //from feed_lang in feed_info.txt before defaulting to Locale.US
                    //FIXME: retrieve timezone from agency.txt
                    if (dateValidator.isValid(rawField, DATE_PATTERN, Locale.US)) {
                        contentByHeaderMap.put(
                                //https://programminghints.com/2017/05/still-using-java-util-date-dont/
                                columnSpecProto.getName(), LocalDateTime.ofInstant(
                                        dateValidator.validate(rawField, DATE_PATTERN, Locale.US).toInstant(),
                                        ZoneId.of("America/Montreal")
                                ));
                    }

                } else {
                    contentByHeaderMap.put(columnSpecProto.getName(), rawField);
                    if (columnSpecProto.getUniquevalues()) {
                        //Assuming there is only one field labelled unique per entity
                        entityId[0] = rawField;
                    }
                }
            }
        });

        return new ParsedEntity(entityId[0], contentByHeaderMap, rawFileInfo);
    }
}
