package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.ConditionallyRequired;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Required;

import java.util.Locale;

@GtfsTable("translations.txt")
public interface GtfsTranslationSchema extends GtfsEntity {
    @Required
    String tableName();

    @Required
    String fieldName();

    @Required
    Locale language();

    @Required
    String translation();

    @ConditionallyRequired
    String recordId();

    @ConditionallyRequired
    String recordSubId();

    @ConditionallyRequired
    String fieldValue();
}
