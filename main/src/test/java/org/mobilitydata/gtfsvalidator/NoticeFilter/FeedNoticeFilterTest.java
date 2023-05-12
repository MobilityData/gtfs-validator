package org.mobilitydata.gtfsvalidator.NoticeFilter;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class FeedNoticeFilterTest {
    private NoticeFilter feedContactFilter=new GtfsFeedInfoNoticeFilter();
    private MissingRecommendedFieldNotice createMissingRecommendedFeedContact(){
        MissingRecommendedFieldNotice notice=new MissingRecommendedFieldNotice(GtfsFeedInfo.FILENAME,2,null);
        return notice;
    }

    @Test
    public void ifMissUrlOrEmailWillNotShowNotice(){
        MissingRecommendedFieldNotice notice=createMissingRecommendedFeedContact();
        List<ValidationNotice> notices=new ArrayList<>();
        notices.add(notice);
        feedContactFilter.filter(notices);
        assertEquals(notices.size(),0);
    }

    @Test
    public void warnIfMissUrlAndEmailWillNotShowNotice(){
        MissingRecommendedFieldNotice missEmail=createMissingRecommendedFeedContact();
        MissingRecommendedFieldNotice missUrl=createMissingRecommendedFeedContact();
        List<ValidationNotice> notices=new ArrayList<>();
        notices.add(missEmail);
        notices.add(missUrl);
        feedContactFilter.filter(notices);
        assertEquals(notices.size(),2);
    }

}
