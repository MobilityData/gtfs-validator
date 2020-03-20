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
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.protos.GtfsSpecificationProto;
import org.mobilitydata.gtfsvalidator.usecase.notice.*;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class GtfsTypeValidator implements GtfsSpecRepository.ParsedEntityTypeValidator {
    private final GtfsSpecificationProto.CsvSpecProto fileSchema;
    private final FloatValidator floatValidator;
    private final IntegerValidator integerValidator;
    private final UrlValidator urlValidator;
    private final RegexValidator colorValidator;
    private final RegexValidator timeValidator;
    private final Set<String> timezoneSet;

    private static boolean isPrintableAscii(char ch) {
        return ch >= 32 && ch < 127;
    }

    public GtfsTypeValidator(GtfsSpecificationProto.CsvSpecProto fileSchema,
                             FloatValidator floatValidator,
                             IntegerValidator integerValidator,
                             UrlValidator urlValidator,
                             RegexValidator colorValidator,
                             RegexValidator timeValidator,
                             Set<String> timezoneSet) {
        this.fileSchema = fileSchema;
        this.floatValidator = floatValidator;
        this.integerValidator = integerValidator;
        this.urlValidator = urlValidator;
        this.colorValidator = colorValidator;
        this.timeValidator = timeValidator;
        this.timezoneSet = timezoneSet;
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
                    case INPUT_TYPE_UNSPECIFIED: // Text is default and does not require validation
                    case TEXT:
                    case DATE: //no special validation
                        break;
                    case FLOAT: {
                        if (!floatValidator.isInRange(
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
                    }
                    case INTEGER: {
                        if (!integerValidator.isInRange(
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
                    }
                    case COLOR: {  //Color
                        if (!colorValidator.isValid((String) value)) {
                            toReturn.add(new InvalidColorNotice(
                                    toValidate.getRawFileInfo().getFilename(),
                                    columnSpecProto.getName(),
                                    toValidate.getEntityId(),
                                    (String) value
                            ));
                        }
                        break;
                    }
                    case TIMEZONE: {

                        //noinspection RedundantCast
                        if (!timezoneSet.contains((String) value)) {
                            toReturn.add(new InvalidTimezoneNotice(
                                    toValidate.getRawFileInfo().getFilename(),
                                    columnSpecProto.getName(),
                                    toValidate.getEntityId(),
                                    (String) value
                            ));
                        }
                        break;
                    }
                    case ID: {
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
                        break;
                    }
                    case URL: {
                        if (!urlValidator.isValid((String) value)) {
                            toReturn.add(new InvalidUrlNotice(
                                    toValidate.getRawFileInfo().getFilename(),
                                    columnSpecProto.getName(),
                                    toValidate.getEntityId(),
                                    (String) value
                            ));
                        }
                        break;
                    }
                    case TIME: {
                        if (!timeValidator.isValid((String) value)) {
                            toReturn.add(new InvalidTimeNotice(
                                    toValidate.getRawFileInfo().getFilename(),
                                    columnSpecProto.getName(),
                                    toValidate.getEntityId(),
                                    (String) value
                            ));
                        }
                        break;
                    }
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