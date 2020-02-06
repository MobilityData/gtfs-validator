package org.mobilitydata.gtfsvalidator.db;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.ErrorNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.InfoNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryValidationResultRepository implements ValidationResultRepository {
    private final List<InfoNotice> infoNoticeList = new ArrayList<>();
    private final List<WarningNotice> warningNoticeList = new ArrayList<>();
    private final List<ErrorNotice> errorNoticeList = new ArrayList<>();


    @Override
    public InfoNotice addNotice(InfoNotice newInfo) {
        infoNoticeList.add(newInfo);
        return newInfo;
    }

    @Override
    public WarningNotice addNotice(WarningNotice newWarning) {
        warningNoticeList.add(newWarning);
        return newWarning;
    }

    @Override
    public ErrorNotice addNotice(ErrorNotice newError) {
        errorNoticeList.add(newError);
        return newError;
    }
}
