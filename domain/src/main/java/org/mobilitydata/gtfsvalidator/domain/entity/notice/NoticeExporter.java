package org.mobilitydata.gtfsvalidator.domain.entity.notice;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.*;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.*;

import java.io.IOException;

public interface NoticeExporter {

    String getExtension();

    void exportBegin() throws IOException;

    void exportEnd() throws IOException;

    void export(NonStandardHeaderNotice toExport) throws IOException;

    void export(InputZipContainsFolderNotice toExport) throws IOException;

    void export(NonAsciiOrNonPrintableCharNotice toExport) throws IOException;

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

    void export(DuplicatedHeaderNotice toExport) throws IOException;

    void export(MissingRequiredFileNotice toExport) throws IOException;

    void export(EmptyFileErrorNotice toExport) throws IOException;

    void export(EmptyFileWarningNotice toExport) throws IOException;

    void export(MissingRequiredValueNotice toExport) throws IOException;

    void export(MissingTripEdgeStopTimeNotice toExport) throws IOException;

    void export(CannotParseColorNotice toExport) throws IOException;

    void export(ExtraFileFoundNotice toExport) throws IOException;

    void export(InvalidTimeNotice toExport) throws IOException;

    void export(CannotParseDateNotice toExport) throws IOException;

    void export(InvalidCurrencyCodeNotice invalidCurrencyCodeNotice) throws IOException;

    void export(DuplicatedEntityNotice toExport) throws IOException;

    void export(IllegalFieldValueCombinationNotice toExport) throws IOException;

    void export(UnexpectedEnumValueNotice toExport) throws IOException;

    void export(InvalidLangNotice toExport) throws IOException;

    void export(InvalidEmailNotice toExport) throws IOException;

    void export(SameNameAndDescriptionForRouteNotice toExport) throws IOException;

    void export(RouteColorAndTextInsufficientContrastNotice toExport) throws IOException;

    void export(RouteShortNameTooLongNotice toExport) throws IOException;

    void export(InvalidRouteTypeNotice toExport) throws IOException;

    void export(MissingShortAndLongNameForRouteNotice toExport) throws IOException;

    void export(MissingRouteShortNameNotice toExport) throws IOException;

    void export(MissingRouteLongNameNotice toExport) throws IOException;

    void export(RouteLongNameEqualsShortNameNotice toExport) throws IOException;

    void export(RouteLongNameContainsShortNameNotice toExport) throws IOException;

    void export(CalendarEndDateBeforeStartDateNotice toExport) throws IOException;

    void export(MissingAgencyIdNotice toExport) throws IOException;

    void export(InconsistentAgencyTimezoneNotice inconsistentAgencyTimezoneNotice) throws IOException;

    void export(InvalidAgencyIdNotice invalidAgencyIdNotice) throws IOException;

    void export(final RouteIdNotFoundNotice routeIdNotFoundNotice) throws IOException;

    void export(final AgencyIdNotFoundNotice agencyIdNotFoundNotice) throws IOException;

    void export(final ShapeIdNotFoundNotice shapeIdNotFoundNotice) throws IOException;

    void export(final TripIdNotFoundNotice tripIdNotFoundNotice) throws IOException;

    void export(final ShapeNotUsedNotice shapeNotUsedNotice) throws IOException;

    void export(final ServiceIdNotFoundNotice serviceIdNotFoundNotice) throws IOException;

    void export(FeedInfoStartDateAfterEndDateNotice toExport) throws IOException;

    void export(FeedInfoExpiresInLessThan7DaysNotice feedInfoExpiresInLessThan7DaysNotice) throws IOException;

    void export(FeedInfoExpiresInLessThan30DaysNotice feedInfoExpiresInLessThan30DaysNotice) throws IOException;

    void export(MissingFeedEndDateNotice missingFeedEndDateNotice) throws IOException;

    void export(MissingFeedStartDateNotice missingFeedStartDateNotice) throws IOException;

    void export(final StationWithParentStationNotice stationWithParentStationNotice) throws IOException;

    void export(final ParentStationInvalidLocationTypeNotice parentStationInvalidLocationTypeNotice)
            throws IOException;

    void export(final StopTimeArrivalTimeAfterDepartureTimeNotice StopTimeArrivalTimeAfterDepartureTimeNotice)
            throws IOException;

    void export(final FastTravelBetweenStopsNotice fastTravelBetweenStopsNotice) throws IOException;

    void export(final FrequencyStartTimeAfterEndTimeNotice frequencyStartTimeAfterEndTimeNotice) throws IOException;

    void export(final BackwardsTimeTravelInStopNotice backwardsTimeTravelInStopNotice) throws IOException;

    void export(final TripNotUsedNotice tripNotUsedNotice) throws IOException;
}
