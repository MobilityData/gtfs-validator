package org.mobilitydata.gtfsvalidator.usecase.port;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;

import java.util.Collection;

public interface ValidationResultRepository {

    InfoNotice addNotice(InfoNotice newInfo);

    WarningNotice addNotice(WarningNotice newWarning);

    ErrorNotice addNotice(ErrorNotice newError);

    Collection<Notice> getAll();

    Notice addNotice(Notice newNotice);
}
