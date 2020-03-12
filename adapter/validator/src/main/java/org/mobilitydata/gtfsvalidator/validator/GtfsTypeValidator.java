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

package org.mobilitydata.gtfsvalidator.validator;

import org.apache.commons.validator.routines.FloatValidator;
import org.apache.commons.validator.routines.IntegerValidator;
import org.apache.commons.validator.routines.RegexValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.mobilitydata.gtfsvalidator.adapter.protos.GtfsSpecificationProto;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.usecase.notice.*;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;

public class GtfsTypeValidator implements GtfsSpecRepository.ParsedEntityTypeValidator {
    private final GtfsSpecificationProto.CsvSpecProto fileSchema;

    //used to match fields to corresponding static Gtfs type - see https://gtfs.org/reference/static/#field-types
    private static final String URL_FIELD_NAME_IDENTIFIER = "_url";
    private static final String TIMEZONE_FIELD_NAME_IDENTIFIER = "_timezone";
    private static final String ID_FIELD_NAME_IDENTIFIER = "_id";

    private static final String[] VALID_URL_SCHEMES = {"http", "https"};

    private static boolean isPrintableAscii(char ch) {
        return ch >= 32 && ch < 127;
    }

    public GtfsTypeValidator(GtfsSpecificationProto.CsvSpecProto fileSchema) {
        this.fileSchema = fileSchema;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Collection<Notice> validate(ParsedEntity toValidate) {
        Collection<Notice> toReturn = new ArrayList<>();

        fileSchema.getColumnList().forEach(columnSpecProto -> {

            Object value = toValidate.get(columnSpecProto.getName());

            if (value != null &&
                    (!(value instanceof String) || !((String) value).isEmpty())) {
                switch (columnSpecProto.getType().getType()) {
                    case FLOAT_STD:
                        if (!FloatValidator.getInstance().isInRange(
                                (Float) value,
                                columnSpecProto.getFloatmin(),
                                columnSpecProto.getFloatmax())) {

                            toReturn.add(new FloatFieldValueOutOfRangeNotice(
                                    toValidate.getRawFileInfo().getFilename(),
                                    columnSpecProto.getName(),
                                    toValidate.getEntityId(),
                                    columnSpecProto.getFloatmin(),
                                    columnSpecProto.getFloatmax(),
                                    (Float) value
                            ));
                        }
                        break;
                    case INT_DEC:
                        if (!IntegerValidator.getInstance().isInRange(
                                (Integer) value,
                                columnSpecProto.getIntmin(),
                                columnSpecProto.getIntmax())) {

                            toReturn.add(new IntegerFieldValueOutOfRangeNotice(
                                    toValidate.getRawFileInfo().getFilename(),
                                    columnSpecProto.getName(),
                                    toValidate.getEntityId(),
                                    columnSpecProto.getIntmin(),
                                    columnSpecProto.getIntmax(),
                                    (Integer) value
                            ));
                        }
                        break;
                    case INT_HEX:   //Color
                        if (!new RegexValidator(columnSpecProto.getMatchregexp()).isValid((String) value)) {
                            toReturn.add(new InvalidColorNotice(
                                    toValidate.getRawFileInfo().getFilename(),
                                    columnSpecProto.getName(),
                                    toValidate.getEntityId(),
                                    (String) value
                            ));
                        }
                        break;
                    case INPUT_TYPE_UNSPECIFIED: // String is default
                    case STRING:
                        if (columnSpecProto.getName().contains(URL_FIELD_NAME_IDENTIFIER)) {
                            if (!new UrlValidator(VALID_URL_SCHEMES).isValid((String) value)) {
                                toReturn.add(new InvalidUrlNotice(
                                        toValidate.getRawFileInfo().getFilename(),
                                        columnSpecProto.getName(),
                                        toValidate.getEntityId(),
                                        (String) value
                                ));
                            }

                        } else if (columnSpecProto.getName().contains(TIMEZONE_FIELD_NAME_IDENTIFIER)) {
                            // Uses IANA timezone database shipped with JDK
                            // to update without updating JDK see https://www.oracle.com/technetwork/java/javase/tzupdater-readme-136440.html
                            //noinspection RedundantCast
                            if (!ZoneId.getAvailableZoneIds().contains((String) value)) {
                                toReturn.add(new InvalidTimezoneNotice(
                                        toValidate.getRawFileInfo().getFilename(),
                                        columnSpecProto.getName(),
                                        toValidate.getEntityId(),
                                        (String) value
                                ));
                            }

                        } else if (columnSpecProto.getName().contains(ID_FIELD_NAME_IDENTIFIER)) {
                            String stringValue = (String) value;
                            int charCount = stringValue.length();
                            for (int i = 0; i < charCount; ++i) {
                                if (!isPrintableAscii(stringValue.charAt(i))) {
                                    toReturn.add(new NonAsciiOrNonPrintableCharNotice(
                                            toValidate.getRawFileInfo().getFilename(),
                                            columnSpecProto.getName(),
                                            toValidate.getEntityId(),
                                            stringValue
                                    ));
                                    break;
                                }
                            }
                        }
                        break;
                    case INT:
                    case FLOAT:
                    case FLOAT_E6:
                    case FLOAT_E7:
                    case DATE:
                    case DATE_YYYYMMDD:
                    case TIME:
                    case TIME_SECONDS_SINCE_MIDNIGHT:
                    case TIME_AUTODETECT:
                    case TIME_HHCMM:
                    case TIME_HHMMSS:
                    case TIME_HHCMMCSS:
                        toReturn.add(new UnsupportedGtfsTypeNotice(
                                toValidate.getRawFileInfo().getFilename(),
                                columnSpecProto.getName(),
                                toValidate.getEntityId()
                        ));
                        break;
                }
            } else {    //value is null or an empty string
                if (columnSpecProto.getValueRequired()) {
                    toReturn.add(new MissingRequiredValueNotice(
                            toValidate.getRawFileInfo().getFilename(),
                            columnSpecProto.getName(),
                            toValidate.getEntityId()
                    ));
                }
            }
        });

        return toReturn;
    }
}