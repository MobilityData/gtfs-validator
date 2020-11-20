package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Required;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

import java.util.Locale;

@GtfsTable(value = "feed_info.txt", singleRow = true)
public interface GtfsFeedInfoSchema extends GtfsEntity {
    @Required
    String feedPublisherName();

    @FieldType(FieldTypeEnum.URL)
    String feedPublisherUrl();

    Locale feedLang();

    Locale defaultLang();

    GtfsDate feedStartDate();

    GtfsDate feedEndDate();

    String feedVersion();

    @FieldType(FieldTypeEnum.EMAIL)
    String feedContactEmail();

    @FieldType(FieldTypeEnum.URL)
    String feedContactUrl();
}
