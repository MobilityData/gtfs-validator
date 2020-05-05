package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

class LoadAllEntityIntoMemoryTest {

    @Test
    void parsedEntityShouldBeAddedToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final ParsedEntity mockParsedEntity = mock(ParsedEntity.class);

        final RawFileInfo mockRawFileInfo = mock(RawFileInfo.class);
        when(mockParsedEntity.getRawFileInfo()).thenReturn(mockRawFileInfo);
        when(mockRawFileInfo.getFilename()).thenReturn("agency.txt", "routes.txt");

        final ProcessParsedAgency mockProcessParsedAgency = mock(ProcessParsedAgency.class);
        final ProcessParsedRoute mockProcessParsedRoute = mock(ProcessParsedRoute.class);

        final LoadAllEntityIntoMemory underTest = new LoadAllEntityIntoMemory(mockProcessParsedAgency,
                mockProcessParsedRoute);

        // agency.txt
        underTest.execute(mockParsedEntity);

        final InOrder inOrder = inOrder(mockResultRepo, mockGtfsDataRepo, mockParsedEntity, mockRawFileInfo,
                mockProcessParsedAgency, mockProcessParsedRoute);

        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockParsedEntity, times(1)).getRawFileInfo();
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockRawFileInfo, times(1)).getFilename();
        inOrder.verify(mockProcessParsedAgency, times(1)).execute(mockParsedEntity);
        verifyNoMoreInteractions(mockResultRepo, mockGtfsDataRepo, mockParsedEntity, mockRawFileInfo,
                mockProcessParsedAgency, mockProcessParsedRoute);

        // routes.txt
        underTest.execute(mockParsedEntity);

        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockParsedEntity, times(1)).getRawFileInfo();
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockRawFileInfo, times(1)).getFilename();
        inOrder.verify(mockProcessParsedRoute, times(1)).execute(mockParsedEntity);
        verifyNoMoreInteractions(mockResultRepo, mockGtfsDataRepo, mockParsedEntity, mockRawFileInfo,
                mockProcessParsedAgency, mockProcessParsedRoute);
    }
}