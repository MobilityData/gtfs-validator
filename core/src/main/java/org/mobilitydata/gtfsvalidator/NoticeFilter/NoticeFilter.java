package org.mobilitydata.gtfsvalidator.NoticeFilter;

import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;

import java.util.List;

/***
 * For apply some rules to filter validation notices
 */
public interface NoticeFilter {
    public abstract void filter(List<ValidationNotice> validationNotices);

}
