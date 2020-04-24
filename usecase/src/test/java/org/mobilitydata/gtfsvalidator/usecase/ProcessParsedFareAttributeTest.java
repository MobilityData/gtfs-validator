package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes.FareAttribute;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.UnexpectedValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcessParsedFareAttributeTest {

    private static final String STRING_TEST = "string test";
    private static final float VALID_PRICE_FLOAT = 2.0f;
    private static final int VALID_TRANSFERS_INTEGER = 0;
    private static final int VALID_PAYMENT_METHOD_INTEGER = 1;
    private static final int VALID_TRANSFER_DURATION_INTEGER = 20;

    @Test
    void processFareAttributeWithNullFareIdShouldThrowExceptionAndMissingRequiredValueNoticeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        FareAttribute.FareAttributeBuilder mockBuilder = spy(FareAttribute.FareAttributeBuilder.class);

        ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(null);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedFareAttribute));

        Assertions.assertEquals("field `fare_id` in file `fare_attributes.txt` cannot be null",
                exception.getMessage());

        verify(mockParsedFareAttribute, times(7)).get(anyString());

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedFareAttribute, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("fare_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedFareAttribute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void processFareAttributeWithNullPriceShouldThrowExceptionAndMissingRequiredValueNoticeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        FareAttribute.FareAttributeBuilder mockBuilder = spy(FareAttribute.FareAttributeBuilder.class);

        ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(null);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedFareAttribute));

        Assertions.assertEquals("field `price` in file `fare_attributes.txt` cannot be null",
                exception.getMessage());

        verify(mockParsedFareAttribute, times(7)).get(anyString());

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedFareAttribute, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("price", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedFareAttribute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void processFareAttributeWithNullCurrencyTypeShouldThrowExceptionAndMissingRequiredValueNoticeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        FareAttribute.FareAttributeBuilder mockBuilder = spy(FareAttribute.FareAttributeBuilder.class);

        ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(null);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedFareAttribute));

        Assertions.assertEquals("field `currency_type` in file `fare_attributes.txt` cannot be null",
                exception.getMessage());

        verify(mockParsedFareAttribute, times(7)).get(anyString());

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedFareAttribute, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("currency_type", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedFareAttribute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void processFareAttributeWithNullPaymentMethodShouldThrowExceptionAndMissingRequiredValueNoticeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        FareAttribute.FareAttributeBuilder mockBuilder = spy(FareAttribute.FareAttributeBuilder.class);

        ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(null);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedFareAttribute));

        Assertions.assertEquals("unexpected value encountered for field `payment_method` in file" +
                        " `fare_attributes.txt`",
                exception.getMessage());

        verify(mockParsedFareAttribute, times(7)).get(anyString());

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).paymentMethod(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedFareAttribute, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("payment_method", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedFareAttribute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void processFareAttributeWithInvalidPaymentMethodShouldThrowExceptionAndUnexpectedValueNoticeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        FareAttribute.FareAttributeBuilder mockBuilder = spy(FareAttribute.FareAttributeBuilder.class);

        ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(5);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedFareAttribute));

        Assertions.assertEquals("unexpected value encountered for field `payment_method` in file" +
                        " `fare_attributes.txt`",
                exception.getMessage());

        verify(mockParsedFareAttribute, times(7)).get(anyString());

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).paymentMethod(ArgumentMatchers.eq(5));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedFareAttribute, times(1)).getEntityId();

        final ArgumentCaptor<UnexpectedValueNotice> captor =
                ArgumentCaptor.forClass(UnexpectedValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<UnexpectedValueNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("payment_method", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals("5", noticeList.get(0).getEnumValue());

        verifyNoMoreInteractions(mockParsedFareAttribute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void processFareAttributeWithInvalidTransfersShouldThrowExceptionAndUnexpectedValueNoticeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        FareAttribute.FareAttributeBuilder mockBuilder = spy(FareAttribute.FareAttributeBuilder.class);

        ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(5);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedFareAttribute));

        Assertions.assertEquals("unexpected value encountered for field `transfers` in file" +
                        " `fare_attributes.txt`",
                exception.getMessage());

        verify(mockParsedFareAttribute, times(7)).get(anyString());

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).paymentMethod(ArgumentMatchers
                .eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(5));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedFareAttribute, times(1)).getEntityId();

        final ArgumentCaptor<UnexpectedValueNotice> captor =
                ArgumentCaptor.forClass(UnexpectedValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<UnexpectedValueNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("transfers", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals("5", noticeList.get(0).getEnumValue());

        verifyNoMoreInteractions(mockParsedFareAttribute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void processFareAttributeWithNullTransfersShouldNotThrowException()
            throws SQLIntegrityConstraintViolationException {

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        FareAttribute mockFareAttribute = mock(FareAttribute.class);

        FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class);
        when(mockBuilder.fareId(any())).thenCallRealMethod();
        when(mockBuilder.price(any())).thenCallRealMethod();
        when(mockBuilder.currencyType(any())).thenCallRealMethod();
        when(mockBuilder.paymentMethod(any())).thenCallRealMethod();
        when(mockBuilder.transfers(any())).thenCallRealMethod();
        when(mockBuilder.agencyId(any())).thenCallRealMethod();
        when(mockBuilder.build()).thenReturn(mockFareAttribute);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(null);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        assertDoesNotThrow(() -> underTest.execute(mockParsedFareAttribute));

        verify(mockParsedFareAttribute, times(7)).get(anyString());
        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).paymentMethod(ArgumentMatchers
                .eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        InOrder inOrder = inOrder(mockBuilder, mockResultRepo, mockGtfsDataRepo);

        inOrder.verify(mockBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo, times(1))
                .addFareAttribute(ArgumentMatchers.eq(mockFareAttribute));

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockFareAttribute);
    }

    @Test
    void processFareAttributeWithValidValuesShouldNotThrowException()
            throws SQLIntegrityConstraintViolationException {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareAttribute mockFareAttribute = mock(FareAttribute.class);

        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class);
        when(mockBuilder.fareId(any())).thenCallRealMethod();
        when(mockBuilder.price(any())).thenCallRealMethod();
        when(mockBuilder.currencyType(any())).thenCallRealMethod();
        when(mockBuilder.paymentMethod(any())).thenCallRealMethod();
        when(mockBuilder.transfers(any())).thenCallRealMethod();
        when(mockBuilder.agencyId(any())).thenCallRealMethod();
        when(mockBuilder.build()).thenReturn(mockFareAttribute);

        final ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        assertDoesNotThrow(() -> underTest.execute(mockParsedFareAttribute));

        verify(mockParsedFareAttribute, times(7)).get(anyString());
        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).paymentMethod(ArgumentMatchers
                .eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        final InOrder inOrder = inOrder(mockBuilder, mockResultRepo, mockGtfsDataRepo);

        inOrder.verify(mockBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo, times(1))
                .addFareAttribute(ArgumentMatchers.eq(mockFareAttribute));

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockFareAttribute);
    }

    @Test
    void processTwiceSameFareAttributeShouldThrowExceptionAndEntityMustBeUniqueAddedToResultRepo()
            throws SQLIntegrityConstraintViolationException {

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);


        final FareAttribute mockFareAttribute = mock(FareAttribute.class);
        when(mockFareAttribute.getFareId()).thenReturn(STRING_TEST);

        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class,
                RETURNS_SELF);
        when(mockBuilder.build()).thenReturn(mockFareAttribute);

        final ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        when(mockGtfsDataRepo.addFareAttribute(mockFareAttribute))
                .thenThrow(new SQLIntegrityConstraintViolationException("fare attribute " +
                        "must be unique in dataset"));

        final Exception exception = Assertions.assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> underTest.execute(mockParsedFareAttribute));
        Assertions.assertEquals("fare attribute must be unique in dataset", exception.getMessage());

        verify(mockParsedFareAttribute, times(7)).get(anyString());

        verify(mockGtfsDataRepo, times(1))
                .addFareAttribute(ArgumentMatchers.eq(mockFareAttribute));

        verify(mockBuilder, times(1)).fareId(anyString());
        verify(mockBuilder, times(1)).price(anyFloat());
        verify(mockBuilder, times(1)).currencyType(anyString());
        verify(mockBuilder, times(1)).paymentMethod(anyInt());
        verify(mockBuilder, times(1)).transfers(anyInt());
        verify(mockBuilder, times(1)).agencyId(anyString());
        verify(mockBuilder, times(1)).transferDuration(anyInt());
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedFareAttribute, times(1)).getEntityId();

        final ArgumentCaptor<EntityMustBeUniqueNotice> captor = ArgumentCaptor.forClass(EntityMustBeUniqueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<EntityMustBeUniqueNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("fare_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
    }
}