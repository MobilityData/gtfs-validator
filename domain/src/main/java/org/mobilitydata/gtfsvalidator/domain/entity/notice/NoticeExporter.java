package org.mobilitydata.gtfsvalidator.domain.entity.notice;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.*;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.ExtraFileFoundNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.InputZipContainsFolderNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.NonStandardHeaderNotice;

import java.io.IOException;

public interface NoticeExporter {

    String getExtension();

    void exportBegin() throws IOException;

    void exportEnd() throws IOException;

    void export(NonStandardHeaderNotice toExport) throws IOException;

    void export(InputZipContainsFolderNotice toExport) throws IOException;

    void export(NonAsciiOrNonPrintableCharNotice toExport) throws IOException;

    void export(CannotConstructDataProviderNotice toExport) throws IOException;

    void export(CannotDownloadArchiveFromNetworkNotice toExport) throws IOException;

    void export(CannotParseFloatNotice toExport) throws IOException;

    void export(CannotParseIntegerNotice toExport) throws IOException;

    void export(CannotUnzipInputArchiveNotice toExport) throws IOException;

    void export(FloatFieldValueOutOfRangeNotice toExport) throws IOException;

    void export(IntegerFieldValueOutOfRangeNotice toExport) throws IOException;

    void export(InvalidRowLengthNotice toExport) throws IOException;

    void export(InvalidTimezoneNotice toExport) throws IOException;

    void export(InvalidUrlNotice toExport) throws IOException;

    void export(MissingHeaderNotice toExport) throws IOException;

    void export(MissingRequiredFileNotice toExport) throws IOException;

    void export(MissingRequiredValueNotice toExport) throws IOException;

    void export(InvalidColorNotice toExport) throws IOException;

    void export(ExtraFileFoundNotice toExport) throws IOException;

    void export(InvalidTimeNotice toExport) throws IOException;

    void export(CannotParseDateNotice toExport) throws IOException;

    void export(InvalidCurrencyCodeNotice invalidCurrencyCodeNotice) throws IOException;
}
