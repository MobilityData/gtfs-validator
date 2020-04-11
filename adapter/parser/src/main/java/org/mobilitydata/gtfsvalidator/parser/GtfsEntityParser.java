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
import org.jetbrains.annotations.NotNull;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.protos.GtfsSpecificationProto;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.CannotParseDateNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.CannotParseFloatNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.CannotParseIntegerNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * This provides methods to parse data from a GTFS CSV file.
 */
public class GtfsEntityParser implements GtfsSpecRepository.RawEntityParser {
    private final GtfsSpecificationProto.CsvSpecProto fileSchema;
    private final RawFileInfo rawFileInfo;
    private final FloatValidator floatValidator;
    private final IntegerValidator integerValidator;
    private final DateValidator dateValidator;

    private static final String DATE_PATTERN = "yyyyMMdd";

    public GtfsEntityParser(@NotNull GtfsSpecificationProto.CsvSpecProto fileSchema,
                            @NotNull RawFileInfo rawFileInfo,
                            @NotNull FloatValidator floatValidator,
                            @NotNull IntegerValidator integerValidator,
                            @NotNull DateValidator dateValidator) {
        this.fileSchema = fileSchema;
        this.rawFileInfo = rawFileInfo;
        this.floatValidator = floatValidator;
        this.integerValidator = integerValidator;
        if (!dateValidator.isStrict()) {
            //see https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/AbstractFormatValidator.html#isStrict()
            throw new IllegalArgumentException("Date validator must be strict");
        }
        this.dateValidator = dateValidator;
    }

    /**
     * Validates numeric types for a provided {@link RawEntity} from information stored in the
     * {@link GtfsSpecificationProto.CsvSpecProto} provided in the constructor. If a NaN value is encountered or if
     * the value is not a valid float, a {@link CannotParseFloatNotice} is generated and added to the returned list.
     * The same logic is applied for integer values, which generates {@link CannotParseIntegerNotice} notices.
     * The same logic is applied for date values, which generates {@link CannotParseDateNotice} notices.
     *
     * @param toValidate a {@link RawEntity} to validate
     * @return a collection of notices containing information about the validation process
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

                    if (!dateValidator.isValid(rawField, DATE_PATTERN)) {
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

    /**
     * Returns a parsed entity where fields' type have been determined. The {@link ParsedEntity} is formatted as follows:
     * - the entityId is the header name
     * - the contentByHeaderMap is a Map(String, Object) matching columns' header names with the type validated values
     * associated to the {@link RawEntity} to parse
     * - the {@link RawFileInfo} associated to the file being processed
     *
     * @param toParse a row of a GTFS file as raw string data
     * @return a parsed entity whose fields' type have been parsed
     */
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
                    if (dateValidator.isValid(rawField, DATE_PATTERN, Locale.US)) {
                        contentByHeaderMap.put(
                                //https://programminghints.com/2017/05/still-using-java-util-date-dont/
                                columnSpecProto.getName(), LocalDateTime.ofInstant(
                                        dateValidator.validate(rawField, DATE_PATTERN, Locale.US).toInstant(),
                                        ZoneId.of("America/Montreal") //FIXME: retrieve timezone from agency.txt
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
