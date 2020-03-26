package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfsentity.Agency;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class ProcessParsedAgencyTest {

    GtfsSpecRepository mockGtfsSpecRepo = mock(GtfsSpecRepository.class);
    ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
    GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

    ParsedEntity mockParsedAgency = mock(ParsedEntity.class);

    @Test
    public void processingTest() {

        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);

        when(mockParsedAgency.get(anyString())).thenReturn("test value");

        ProcessParsedAgency underTest = spy(new ProcessParsedAgency(mockGtfsSpecRepo, mockResultRepo, mockGtfsDataRepo));

        doReturn(mockBuilder).when(underTest).getBuilder(anyString(), anyString(), anyString());

        when(mockBuilder.agencyId(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.agencyLang(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.agencyPhone(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.agencyFareUrl(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.agencyEmail(anyString())).thenReturn(mockBuilder);

        Agency mockAgency = mock(Agency.class);
        when(mockBuilder.build()).thenReturn(mockAgency);

        underTest.execute(mockParsedAgency);

        InOrder inOrder = Mockito.inOrder(mockGtfsDataRepo, mockResultRepo, mockGtfsDataRepo,
                mockParsedAgency, mockAgency, mockBuilder, underTest);

        inOrder.verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq("agency_id"));
        inOrder.verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq("agency_name"));
        inOrder.verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq("agency_url"));
        inOrder.verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq("agency_timezone"));
        inOrder.verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq("agency_lang"));
        inOrder.verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq("agency_phone"));
        inOrder.verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq("agency_fare_url"));
        inOrder.verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq("agency_email"));

        inOrder.verify(underTest, times(1)).getBuilder(anyString(), anyString(), anyString());

        inOrder.verify(mockBuilder, times(1)).agencyId(anyString());
        inOrder.verify(mockBuilder, times(1)).agencyLang(anyString());
        inOrder.verify(mockBuilder, times(1)).agencyPhone(anyString());
        inOrder.verify(mockBuilder, times(1)).agencyFareUrl(anyString());
        inOrder.verify(mockBuilder, times(1)).agencyEmail(anyString());

        inOrder.verify(mockBuilder, times(1)).build();

        inOrder.verify(mockGtfsDataRepo, times(1)).addEntity(ArgumentMatchers.eq(mockAgency));
    }
}