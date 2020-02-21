package org.mobilitydata.gtfsvalidator.db;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.ArrayList;
import java.util.Collection;
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

    @Override
    public Collection<Notice> getAll() {
        //TODO: might be memory intensive, check how to streamline
        Collection<Notice> toReturn = new ArrayList<>(
                infoNoticeList.size() +
                        warningNoticeList.size() +
                        errorNoticeList.size()
        );

        toReturn.addAll(infoNoticeList);
        toReturn.addAll(warningNoticeList);
        toReturn.addAll(errorNoticeList);

        return toReturn;
    }

    @Override
    public Notice addNotice(Notice newNotice) {
        return newNotice.visit(this);
    }
}
