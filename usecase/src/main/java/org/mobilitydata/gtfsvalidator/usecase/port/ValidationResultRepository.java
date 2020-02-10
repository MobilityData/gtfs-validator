package org.mobilitydata.gtfsvalidator.usecase.port;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.ErrorNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.InfoNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.WarningNotice;

import java.util.Collection;

public interface ValidationResultRepository {

    InfoNotice addNotice(InfoNotice newInfo);

    WarningNotice addNotice(WarningNotice newWarning);

    ErrorNotice addNotice(ErrorNotice newError);

    Collection<Notice> getAll();
}
