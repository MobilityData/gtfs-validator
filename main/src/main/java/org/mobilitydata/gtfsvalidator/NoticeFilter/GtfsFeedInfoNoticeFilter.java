package org.mobilitydata.gtfsvalidator.NoticeFilter;

import com.google.auto.service.AutoService;
import com.google.gson.JsonElement;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;

import java.util.List;
import java.util.Map;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;


/**
 * For feed_contact_email and feed_contact_trl, only two fields are absent will show {@link  MissingRecommendedFieldNotice} for {@code feed_info.txt}.
 */

@AutoService(NoticeFilter.class)
public class GtfsFeedInfoNoticeFilter implements NoticeFilter {

    @Override
    public void filter(List<ValidationNotice> validationNotices) {
        long missReconnmandNotice = validationNotices.stream().parallel().filter(validationNotice -> isMissingFeedContact(validationNotice)).count();
        if (missReconnmandNotice!=2) {
            validationNotices.removeIf(validationNotice -> isMissingFeedContact(validationNotice));
        }
    }

    private boolean isMissingFeedContact(ValidationNotice validationNotice) {

        if (validationNotice instanceof MissingRecommendedFieldNotice) {
            Map<String, Object> validationNoticeMap = convertToMap(validationNotice);
            String fileName = (String) validationNoticeMap.get("filename");
            String fieldName = (String) validationNoticeMap.get("fieldName");
            if (GtfsFeedInfo.FILENAME.equals(fileName) && fieldName==null) {
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
