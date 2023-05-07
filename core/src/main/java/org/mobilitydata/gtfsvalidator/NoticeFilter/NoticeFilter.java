package org.mobilitydata.gtfsvalidator.NoticeFilter;

import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.parsing.RowParser;

import java.util.List;

public interface NoticeFilter {
    public abstract void filter(NoticeContainer noticeContainer);

}
