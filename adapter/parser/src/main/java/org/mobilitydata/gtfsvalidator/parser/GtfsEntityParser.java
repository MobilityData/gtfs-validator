package org.mobilitydata.gtfsvalidator.parser;

import com.google.common.base.Strings;
import org.apache.commons.validator.routines.FloatValidator;
import org.apache.commons.validator.routines.IntegerValidator;
import org.mobilitydata.gtfsvalidator.adapter.protos.GtfsSpecificationProto;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.CannotParseFloatNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.CannotParseIntegerNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;

import java.util.*;

public class GtfsEntityParser implements GtfsSpecRepository.RawEntityParser {
    private final GtfsSpecificationProto.CsvSpecProto fileSchema;
    private final RawFileInfo rawFileInfo;

    public GtfsEntityParser(GtfsSpecificationProto.CsvSpecProto fileSchema, RawFileInfo rawFileInfo) {
        this.fileSchema = fileSchema;
        this.rawFileInfo = rawFileInfo;
    }

    @Override
    public Collection<ErrorNotice> validateNumericTypes(RawEntity toValidate) {
        Collection<ErrorNotice> toReturn = new ArrayList<>();

        fileSchema.getColumnList().forEach(columnSpecProto -> {
            String rawField = toValidate.get(columnSpecProto.getName());

            //Skip values that weren't provided
            if (!Strings.isNullOrEmpty(rawField)) {

                if (columnSpecProto.getType().getType() == GtfsSpecificationProto.ColumnInputType.InputType.FLOAT_STD) {
                    FloatValidator floatValidator = FloatValidator.getInstance();

                    //FIXME: retrieve locale from agency_lang in agency.txt and if that doesn't exist,
                    //from feed_lang in feed_info.txt before defaulting to Locale.US
                    if (!floatValidator.isValid(rawField, Locale.US) || Float.isNaN(floatValidator.validate(rawField, Locale.US))) {

                        toReturn.add(new CannotParseFloatNotice(
                                        fileSchema.getFilename(),
                                        columnSpecProto.getName(),
                                        toValidate.getIndex(),
                                        rawField
                                )
                        );
                    }
                } else if (columnSpecProto.getType().getType() == GtfsSpecificationProto.ColumnInputType.InputType.INT_DEC) {

                    IntegerValidator integerValidator = IntegerValidator.getInstance();

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

                if (columnSpecProto.getType().getType() == GtfsSpecificationProto.ColumnInputType.InputType.FLOAT_STD) {
                    FloatValidator floatValidator = FloatValidator.getInstance();

                    //FIXME: retrieve locale from agency_lang in agency.txt and if that doesn't exist,
                    //from feed_lang in feed_info.txt before defaulting to Locale.US
                    if (floatValidator.isValid(rawField, Locale.US) && !Float.isNaN(floatValidator.validate(rawField, Locale.US))) {
                        contentByHeaderMap.put(columnSpecProto.getName(), floatValidator.validate(rawField, Locale.US));
                    }

                } else if (columnSpecProto.getType().getType() == GtfsSpecificationProto.ColumnInputType.InputType.INT_DEC) {
                    IntegerValidator integerValidator = IntegerValidator.getInstance();

                    //FIXME: retrieve locale from agency_lang in agency.txt and if that doesn't exist,
                    //from feed_lang in feed_info.txt before defaulting to Locale.US
                    if (integerValidator.isValid(rawField, Locale.US)) {
                        contentByHeaderMap.put(columnSpecProto.getName(), integerValidator.validate(rawField, Locale.US));
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
