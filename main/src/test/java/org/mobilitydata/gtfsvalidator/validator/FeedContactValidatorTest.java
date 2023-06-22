package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

import java.util.Locale;

public class FeedContactValidatorTest {
    public static GtfsFeedInfo createFeedInfo(
            int csvRowNumber,
            String feedPublisherName,
            String feedPublisherUrl,
            Locale feedLang,
            Locale defaultLang,
            GtfsDate feedStartDate,
            GtfsDate feedEndDate,
            String feedVersion,
            String feedContactEmail,
            String feedContactUrl){
        return new GtfsFeedInfo.Builder()
                .setCsvRowNumber(csvRowNumber)
                .setFeedPublisherName(feedPublisherName)
                .setFeedPublisherUrl(feedPublisherUrl)
                .setFeedLang(feedLang)
                .setDefaultLang(defaultLang)
                .setFeedStartDate(feedStartDate)
                .setFeedEndDate(feedEndDate)
                .setFeedVersion(feedVersion)
                .setFeedContactEmail(feedContactEmail)
                .setFeedContactUrl(feedContactUrl)
                .build();
    }
}
