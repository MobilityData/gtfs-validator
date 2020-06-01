package org.mobilitydata.gtfsvalidator.domain.entity.notice;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.*;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.*;

import java.io.IOException;

public interface NoticeExporter {

    String getExtension();

    void exportBegin() throws IOException;

    void exportEnd() throws IOException;

    void export(final NonStandardHeaderNotice toExport) throws IOException;

    void export(final InputZipContainsFolderNotice toExport) throws IOException;

    void export(final NonAsciiOrNonPrintableCharNotice toExport) throws IOException;

    void export(final CannotConstructDataProviderNotice toExport) throws IOException;

    void export(final CannotDownloadArchiveFromNetworkNotice toExport) throws IOException;

    void export(final CannotParseFloatNotice toExport) throws IOException;

    void export(final CannotParseIntegerNotice toExport) throws IOException;

    void export(final CannotUnzipInputArchiveNotice toExport) throws IOException;

    void export(final FloatFieldValueOutOfRangeNotice toExport) throws IOException;

    void export(final IntegerFieldValueOutOfRangeNotice toExport) throws IOException;

    void export(final InvalidRowLengthNotice toExport) throws IOException;

    void export(final InvalidTimezoneNotice toExport) throws IOException;

    void export(final InvalidUrlNotice toExport) throws IOException;

    void export(final MissingHeaderNotice toExport) throws IOException;

    void export(final MissingRequiredFileNotice toExport) throws IOException;

    void export(final MissingRequiredValueNotice toExport) throws IOException;

    void export(final InvalidColorNotice toExport) throws IOException;

    void export(final ExtraFileFoundNotice toExport) throws IOException;

    void export(final InvalidTimeNotice toExport) throws IOException;

    void export(final CannotParseDateNotice toExport) throws IOException;

    void export(final InvalidCurrencyCodeNotice invalidCurrencyCodeNotice) throws IOException;

    void export(final DuplicatedEntityNotice toExport) throws IOException;

    void export(final IllegalFieldValueCombination toExport) throws IOException;

    void export(final UnexpectedEnumValueNotice toExport) throws IOException;

    void export(final InvalidLangNotice toExport) throws IOException;

    void export(final InvalidEmailNotice toExport) throws IOException;

    void export(final SameNameAndDescriptionForRouteNotice toExport) throws IOException;

    void export(final RouteColorAndTextInsufficientContrastNotice toExport) throws IOException;

    void export(final RouteShortNameTooLongNotice toExport) throws IOException;

    void export(final InvalidRouteTypeNotice toExport) throws IOException;

    void export(final MissingShortAndLongNameForRouteNotice toExport) throws IOException;

    void export(final MissingRouteShortNameNotice toExport) throws IOException;

    void export(final MissingRouteLongNameNotice toExport) throws IOException;

    void export(final RouteLongNameEqualsShortNameNotice toExport) throws IOException;

    void export(final RouteLongNameContainsShortNameNotice toExport) throws IOException;

    void export(final SuspiciousIntegerValueNotice toExport) throws IOException;

    void export(final SuspiciousFloatValueNotice toExport) throws IOException;
}
