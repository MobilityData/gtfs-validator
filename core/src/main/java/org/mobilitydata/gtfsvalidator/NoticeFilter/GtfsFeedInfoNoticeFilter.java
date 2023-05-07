package org.mobilitydata.gtfsvalidator.NoticeFilter;

import com.google.auto.service.AutoService;
import com.google.gson.JsonElement;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import java.util.List;
import java.util.Map;



/**
 *
 */

@AutoService(NoticeFilter.class)
public class GtfsFeedInfoNoticeFilter implements NoticeFilter {

    @Override
    public void filter(NoticeContainer noticeContainer) {
        List<ValidationNotice> validationNotices = noticeContainer.getValidationNotices();
        boolean containMissingFeedContactEmailNotice = validationNotices.stream().parallel().anyMatch(validationNotice -> isMissingFeedContactEmail(validationNotice));
        boolean containMissingFeedURLEmailNotice = validationNotices.stream().parallel().anyMatch(validationNotice -> isMissingFeedContactURL(validationNotice));
        if (!(containMissingFeedURLEmailNotice && containMissingFeedContactEmailNotice)) {
            noticeContainer.getValidationNotices().removeIf(validationNotice -> isMissingFeedContactURL(validationNotice) || isMissingFeedContactEmail(validationNotice));

        }
    }

    private boolean isMissingFeedContactEmail(ValidationNotice validationNotice) {
        if (validationNotice instanceof MissingRecommendedFieldNotice) {
            Map<String, Object> validationNoticeMap = convertToMap(validationNotice);
            String fileName = (String) validationNoticeMap.get("filename");
            String fieldName = (String) validationNoticeMap.get("fieldName");
            if (fileName.equals(GtfsFeedInfo.FILENAME) && fieldName.equals(GtfsFeedInfo.FEED_CONTACT_EMAIL_FIELD_NAME)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMissingFeedContactURL(ValidationNotice validationNotice) {
        if (validationNotice instanceof MissingRecommendedFieldNotice) {
            Map<String, Object> validationNoticeMap = convertToMap(validationNotice);
            String fileName = (String) validationNoticeMap.get("filename");
            String fieldName = (String) validationNoticeMap.get("fieldName");
            if (fileName.equals(GtfsFeedInfo.FILENAME) && fieldName.equals(GtfsFeedInfo.FEED_CONTACT_URL_FIELD_NAME)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Object> convertToMap(ValidationNotice validationNotice) {
        JsonElement validationValidationJson = validationNotice.getContext();
        return Notice.GSON.fromJson(validationValidationJson, Map.class);

    }
}
