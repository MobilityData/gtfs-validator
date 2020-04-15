package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.TransferType;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.UnexpectedValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProcessParsedTransferTest {

    @Test
    void processValidTransferShouldNotThrowExceptionAndBeAddedToGtfsDataRepository()
            throws SQLIntegrityConstraintViolationException {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Transfer mockTransfer = mock(Transfer.class);

        Transfer.TransferBuilder mockBuilder = mock(Transfer.TransferBuilder.class, RETURNS_SELF);
        when(mockBuilder.build()).thenReturn(mockTransfer);

        ProcessParsedTransfer underTest = new ProcessParsedTransfer(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedTransfer = mock(ParsedEntity.class);

        when(mockParsedTransfer.get("from_stop_id")).thenReturn("from_stop_id_0");
        when(mockParsedTransfer.get("to_stop_id")).thenReturn("to_stop_id_1");
        when(mockParsedTransfer.get("transfer_type")).thenReturn(1);
        when(mockParsedTransfer.get("min_transfer_time")).thenReturn(20);

        underTest.execute(mockParsedTransfer);

        verify(mockParsedTransfer, times(4)).get(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.eq("from_stop_id_0"));
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.eq("to_stop_id_1"));
        verify(mockBuilder, times(1)).transferType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).minTransferTime(ArgumentMatchers.eq(20));

        InOrder inOrder = inOrder(mockBuilder, mockResultRepo, mockGtfsDataRepo);

        inOrder.verify(mockBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo, times(1)).addTransfer(ArgumentMatchers.eq(mockTransfer));

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockParsedTransfer);
    }

    @Test
    void processTransferWithNullFromStopIdShouldThrowExceptionAndAddMissingRequiredValueNoticeToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Transfer.TransferBuilder mockBuilder = spy(Transfer.TransferBuilder.class);

        ProcessParsedTransfer underTest = new ProcessParsedTransfer(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedTransfer = mock(ParsedEntity.class);

        when(mockParsedTransfer.get("from_stop_id")).thenReturn(null);
        when(mockParsedTransfer.get("to_stop_id")).thenReturn("to_stop_id_1");
        when(mockParsedTransfer.get("transfer_type")).thenReturn(1);
        when(mockParsedTransfer.get("min_transfer_time")).thenReturn(20);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.execute(mockParsedTransfer));

        Assertions.assertEquals("field from_stop_id can not be null in transfers.txt", exception.getMessage());

        verify(mockParsedTransfer, times(4)).get(anyString());

        //noinspection ConstantConditions,ConstantConditions
        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.eq("to_stop_id_1"));
        verify(mockBuilder, times(1)).transferType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).minTransferTime(ArgumentMatchers.eq(20));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedTransfer, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assert (noticeList.get(0).getFilename().equals("transfers.txt"));
        assert (noticeList.get(0).getFieldName().equals("from_stop_id"));
        assert (noticeList.get(0).getEntityId().equals("no id"));

        verifyNoMoreInteractions(mockParsedTransfer, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void processTransferWithNullToStopIdShouldThrowExceptionAndAddMissingRequiredValueNoticeToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Transfer.TransferBuilder mockBuilder = spy(Transfer.TransferBuilder.class);

        ProcessParsedTransfer underTest = new ProcessParsedTransfer(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedTransfer = mock(ParsedEntity.class);

        when(mockParsedTransfer.get("from_stop_id")).thenReturn("from_stop_id_0");
        when(mockParsedTransfer.get("to_stop_id")).thenReturn(null);
        when(mockParsedTransfer.get("transfer_type")).thenReturn(1);
        when(mockParsedTransfer.get("min_transfer_time")).thenReturn(20);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.execute(mockParsedTransfer));

        Assertions.assertEquals("field to_stop_id can not be null in transfers.txt", exception.getMessage());

        verify(mockParsedTransfer, times(4)).get(anyString());

        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.eq("from_stop_id_0"));
        //noinspection ConstantConditions,ConstantConditions
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).transferType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).minTransferTime(ArgumentMatchers.eq(20));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedTransfer, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assert (noticeList.get(0).getFilename().equals("transfers.txt"));
        assert (noticeList.get(0).getFieldName().equals("to_stop_id"));
        assert (noticeList.get(0).getEntityId().equals("no id"));

        verifyNoMoreInteractions(mockParsedTransfer, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void processTransferWithInvalidTransferTypeShouldThrowExceptionAndAddUnexpectedValueNoticeToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Transfer.TransferBuilder mockBuilder = spy(Transfer.TransferBuilder.class);

        ProcessParsedTransfer underTest = new ProcessParsedTransfer(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedTransfer = mock(ParsedEntity.class);

        when(mockParsedTransfer.get("from_stop_id")).thenReturn("from_stop_id_0");
        when(mockParsedTransfer.get("to_stop_id")).thenReturn("to_stop_id_1");
        when(mockParsedTransfer.get("transfer_type")).thenReturn(4);
        when(mockParsedTransfer.get("min_transfer_time")).thenReturn(20);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.execute(mockParsedTransfer));

        Assertions.assertEquals("unexpected value encountered for field transfer_type transfers.txt",
                exception.getMessage());

        verify(mockParsedTransfer, times(4)).get(anyString());

        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.eq("from_stop_id_0"));
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.eq("to_stop_id_1"));
        verify(mockBuilder, times(1)).transferType(ArgumentMatchers.eq(4));
        verify(mockBuilder, times(1)).minTransferTime(ArgumentMatchers.eq(20));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedTransfer, times(1)).getEntityId();

        ArgumentCaptor<UnexpectedValueNotice> captor = ArgumentCaptor.forClass(UnexpectedValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<UnexpectedValueNotice> noticeList = captor.getAllValues();

        assertEquals("transfers.txt", noticeList.get(0).getFilename());
        assertEquals("transfer_type", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals("4", noticeList.get(0).getEnumValue());

        verifyNoMoreInteractions(mockParsedTransfer, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void processTransferWithInvalidMinTransferTimeShouldThrowExceptionAndAddIntegerFieldValueOutOfRangeNoticeToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Transfer.TransferBuilder mockBuilder = spy(Transfer.TransferBuilder.class);

        ProcessParsedTransfer underTest = new ProcessParsedTransfer(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedTransfer = mock(ParsedEntity.class);

        when(mockParsedTransfer.get("from_stop_id")).thenReturn("from_stop_id_0");
        when(mockParsedTransfer.get("to_stop_id")).thenReturn("to_stop_id_1");
        when(mockParsedTransfer.get("transfer_type")).thenReturn(1);
        when(mockParsedTransfer.get("min_transfer_time")).thenReturn(-20);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.execute(mockParsedTransfer));

        Assertions.assertEquals("invalid value encountered for field min_transfer_time transfers.txt",
                exception.getMessage());

        verify(mockParsedTransfer, times(4)).get(anyString());

        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.eq("from_stop_id_0"));
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.eq("to_stop_id_1"));
        verify(mockBuilder, times(1)).transferType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).minTransferTime(ArgumentMatchers.eq(-20));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedTransfer, times(1)).getEntityId();

        ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("transfers.txt", noticeList.get(0).getFilename());
        assertEquals("transfer_type", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(Integer.MAX_VALUE, noticeList.get(0).getRangeMax());
        assertEquals(-20, noticeList.get(0).getActualValue());

        verifyNoMoreInteractions(mockParsedTransfer, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void duplicateTransferShouldThrowExceptionAndAddEntityMustBeUniqueNoticeToResultRepo() throws SQLIntegrityConstraintViolationException {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Transfer mockTransfer = mock(Transfer.class);
        when(mockTransfer.getFromStopId()).thenReturn("from_stop_id_0");
        when(mockTransfer.getToStopId()).thenReturn("from_stop_id_1");
        when(mockTransfer.getTransferType()).thenReturn(TransferType.fromInt(1));

        Transfer.TransferBuilder mockBuilder = mock(Transfer.TransferBuilder.class, RETURNS_SELF);
        when(mockBuilder.build()).thenReturn(mockTransfer);

        ProcessParsedTransfer underTest = new ProcessParsedTransfer(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedTransfer = mock(ParsedEntity.class);
        when(mockParsedTransfer.get("from_stop_id")).thenReturn("from_stop_id_0");
        when(mockParsedTransfer.get("to_stop_id")).thenReturn("to_stop_id_1");
        when(mockParsedTransfer.get("transfer_type")).thenReturn(1);
        when(mockParsedTransfer.get("min_transfer_time")).thenReturn(20);

        when(mockGtfsDataRepo.addTransfer(mockTransfer)).thenThrow(
                new SQLIntegrityConstraintViolationException("transfer must be unique in dataset"));

        Exception exception = assertThrows(SQLIntegrityConstraintViolationException.class, () -> underTest.execute(mockParsedTransfer));

        Assertions.assertEquals("transfer must be unique in dataset", exception.getMessage());

        verify(mockParsedTransfer, times(4)).get(anyString());

        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.eq("from_stop_id_0"));
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.eq("to_stop_id_1"));
        verify(mockBuilder, times(1)).transferType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).minTransferTime(ArgumentMatchers.eq(20));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedTransfer, times(1)).getEntityId();
        verify(mockGtfsDataRepo, times(1)).addTransfer(ArgumentMatchers.eq(mockTransfer));

        ArgumentCaptor<EntityMustBeUniqueNotice> captor =
                ArgumentCaptor.forClass(EntityMustBeUniqueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        List<EntityMustBeUniqueNotice> noticeList = captor.getAllValues();

        assertEquals("transfers.txt", noticeList.get(0).getFilename());
        assertEquals("from_stop_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedTransfer, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }
}