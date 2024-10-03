package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsGeojsonFeaturesContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsGeojsonFileDescriptor;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.TableStatus;

import java.util.ArrayList;

import static com.google.common.truth.Truth.assertThat;

public class MissingStopsFileValidatorTest {
    @Test
    public void stopsTxtMissingFileShouldGenerateNotice() {
        // If stops.txt is missing and locations.geojson is missing, a notice should be generated
        NoticeContainer noticeContainer = new NoticeContainer();
        GtfsGeojsonFileDescriptor descriptor = new GtfsGeojsonFileDescriptor();
        GtfsGeojsonFeaturesContainer geoJsonFeaturesContainer = descriptor.createContainerForInvalidStatus(TableStatus.MISSING_FILE);
        GtfsStopTableContainer stopTableContainer = GtfsStopTableContainer.forStatus(TableStatus.MISSING_FILE);
        new MissingStopsFileValidator(stopTableContainer, geoJsonFeaturesContainer).validate(noticeContainer);
        assertThat(noticeContainer.getValidationNotices())
            .containsExactly(new MissingRequiredFileNotice("stops.txt"));
    }

    @Test
    public void stopsTxtMissingFileShouldNotGenerateNotice() {
        // If stops.txt is missing, but locations.geojson is present, no notice should be generated
        NoticeContainer noticeContainer = new NoticeContainer();
        GtfsGeojsonFileDescriptor descriptor = new GtfsGeojsonFileDescriptor();
        GtfsGeojsonFeaturesContainer geoJsonFeaturesContainer = descriptor.createContainerForEntities(
            new ArrayList<>(), noticeContainer
        );
        GtfsStopTableContainer stopTableContainer = GtfsStopTableContainer.forStatus(TableStatus.MISSING_FILE);
        new MissingStopsFileValidator(stopTableContainer, geoJsonFeaturesContainer).validate(noticeContainer);
        assertThat(noticeContainer.getValidationNotices()).isEmpty();
    }
}
