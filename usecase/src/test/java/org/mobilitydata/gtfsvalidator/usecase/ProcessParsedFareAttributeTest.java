package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.GenericType;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes.FareAttribute;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProcessParsedFareAttributeTest {
    private static final String STRING_TEST = "string test";
    private static final float VALID_PRICE_FLOAT = 2.0f;
    private static final int VALID_TRANSFERS_INTEGER = 0;
    private static final int VALID_PAYMENT_METHOD_INTEGER = 1;
    private static final int VALID_TRANSFER_DURATION_INTEGER = 20;

    @Test
    void processFareAttributeWithNullFareIdShouldGenerateNoticeAndShouldNotBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class,
                RETURNS_SELF);
        final ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);

        when(mockNotice.getFilename()).thenReturn("fare_attributes.txt");
        when(mockNotice.getFieldName()).thenReturn("fare_id");
        when(mockNotice.getEntityId()).thenReturn("no id");
        noticeCollection.add(mockNotice);

        final var mockGenericObject = mock(GenericType.class);

        when(mockGenericObject.getData()).thenReturn(noticeCollection);
        when(mockGenericObject.getState()).thenReturn(false);
        when(mockBuilder.build(noticeCollection)).thenReturn(mockGenericObject);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(null);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedFareAttribute, noticeCollection);

        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("fare_id"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("price"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("currency_type"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("payment_method"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("transfers"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("agency_id"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("transfer_duration"));

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build(noticeCollection);

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
    void processFareAttributeWithNullPriceShouldGenerateNoticeAndShouldNotBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class,
                RETURNS_SELF);
        final ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);

        when(mockNotice.getFilename()).thenReturn("fare_attributes.txt");
        when(mockNotice.getFieldName()).thenReturn("price");
        when(mockNotice.getEntityId()).thenReturn("no id");
        noticeCollection.add(mockNotice);

        final var mockGenericObject = mock(GenericType.class);

        when(mockGenericObject.getData()).thenReturn(noticeCollection);
        when(mockGenericObject.getState()).thenReturn(false);
        when(mockBuilder.build(noticeCollection)).thenReturn(mockGenericObject);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(null);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedFareAttribute, noticeCollection);

        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("fare_id"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("price"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("currency_type"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("payment_method"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("transfers"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("agency_id"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("transfer_duration"));

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build(noticeCollection);

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
    void processFareAttributeWithNullCurrencyTypeShouldGenerateNoticeAndShouldNotBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class,
                RETURNS_SELF);
        final ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);

        when(mockNotice.getFilename()).thenReturn("fare_attributes.txt");
        when(mockNotice.getFieldName()).thenReturn("currency_type");
        when(mockNotice.getEntityId()).thenReturn("no id");
        noticeCollection.add(mockNotice);

        final var mockGenericObject = mock(GenericType.class);

        when(mockGenericObject.getData()).thenReturn(noticeCollection);
        when(mockGenericObject.getState()).thenReturn(false);
        when(mockBuilder.build(noticeCollection)).thenReturn(mockGenericObject);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(null);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedFareAttribute, noticeCollection);

        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("fare_id"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("price"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("currency_type"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("payment_method"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("transfers"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("agency_id"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("transfer_duration"));

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build(noticeCollection);

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
    void processFareAttributeWithNullPaymentMethodShouldGenerateNoticeAndShouldNotBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class,
                RETURNS_SELF);
        final ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);

        when(mockNotice.getFilename()).thenReturn("fare_attributes.txt");
        when(mockNotice.getFieldName()).thenReturn("payment_method");
        when(mockNotice.getEntityId()).thenReturn("no id");
        noticeCollection.add(mockNotice);

        final var mockGenericObject = mock(GenericType.class);

        when(mockGenericObject.getData()).thenReturn(noticeCollection);
        when(mockGenericObject.getState()).thenReturn(false);
        when(mockBuilder.build(noticeCollection)).thenReturn(mockGenericObject);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(null);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedFareAttribute, noticeCollection);

        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("fare_id"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("price"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("currency_type"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("payment_method"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("transfers"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("agency_id"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("transfer_duration"));

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build(noticeCollection);

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
    void processFareAttributeWithInvalidPaymentMethodShouldGenerateNoticeAndShouldNotBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class,
                RETURNS_SELF);
        final ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        final UnexpectedValueNotice mockNotice = mock(UnexpectedValueNotice.class);

        when(mockNotice.getFilename()).thenReturn("fare_attributes.txt");
        when(mockNotice.getFieldName()).thenReturn("payment_method");
        when(mockNotice.getEntityId()).thenReturn("no id");
        when(mockNotice.getEnumValue()).thenReturn("5");
        noticeCollection.add(mockNotice);

        final var mockGenericObject = mock(GenericType.class);

        when(mockGenericObject.getData()).thenReturn(noticeCollection);
        when(mockGenericObject.getState()).thenReturn(false);
        when(mockBuilder.build(noticeCollection)).thenReturn(mockGenericObject);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(5);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedFareAttribute, noticeCollection);

        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("fare_id"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("price"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("currency_type"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("payment_method"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("transfers"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("agency_id"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("transfer_duration"));

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(5));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build(noticeCollection);

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
    void processFareAttributeWithInvalidTransfersShouldGenerateNoticeAndShouldNotBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class,
                RETURNS_SELF);
        final ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        final UnexpectedValueNotice mockNotice = mock(UnexpectedValueNotice.class);

        when(mockNotice.getFilename()).thenReturn("fare_attributes.txt");
        when(mockNotice.getFieldName()).thenReturn("transfers");
        when(mockNotice.getEntityId()).thenReturn("no id");
        when(mockNotice.getEnumValue()).thenReturn("5");
        noticeCollection.add(mockNotice);

        final var mockGenericObject = mock(GenericType.class);

        when(mockGenericObject.getData()).thenReturn(noticeCollection);
        when(mockGenericObject.getState()).thenReturn(false);
        when(mockBuilder.build(noticeCollection)).thenReturn(mockGenericObject);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(5);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedFareAttribute, noticeCollection);

        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("fare_id"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("price"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("currency_type"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("payment_method"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("transfers"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("agency_id"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("transfer_duration"));

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(5));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build(noticeCollection);

        final ArgumentCaptor<UnexpectedValueNotice> captor = ArgumentCaptor.forClass(UnexpectedValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<UnexpectedValueNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("transfers", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals("5", noticeList.get(0).getEnumValue());

        verifyNoMoreInteractions(mockParsedFareAttribute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void processFareAttributeWithNullTransfersShouldNotGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class,
                RETURNS_SELF);
        final ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();

        final var mockGenericObject = mock(GenericType.class);
        final FareAttribute mockFareAttribute = mock(FareAttribute.class);

        when(mockGenericObject.getState()).thenReturn(true);
        when(mockBuilder.build(noticeCollection)).thenReturn(mockGenericObject);
        when(mockGenericObject.getData()).thenReturn(mockFareAttribute);
        when(mockGtfsDataRepo.addFareAttribute(mockFareAttribute)).thenReturn(mockFareAttribute);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(null);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedFareAttribute, noticeCollection);

        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("fare_id"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("price"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("currency_type"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("payment_method"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("transfers"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("agency_id"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("transfer_duration"));

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build(noticeCollection);

        final ArgumentCaptor<FareAttribute> captor = ArgumentCaptor.forClass(FareAttribute.class);

        verify(mockGtfsDataRepo, times(1)).addFareAttribute(captor.capture());

        final List<FareAttribute> fareAttributeCollection = captor.getAllValues();

        assertEquals(mockFareAttribute, fareAttributeCollection.get(0));

        verifyNoMoreInteractions(mockParsedFareAttribute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void processFareAttributeWithValidValuesShouldNotGenerateNoticeAndBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class,
                RETURNS_SELF);
        final ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();

        final var mockGenericObject = mock(GenericType.class);
        final FareAttribute mockFareAttribute = mock(FareAttribute.class);

        when(mockGenericObject.getState()).thenReturn(true);
        when(mockBuilder.build(noticeCollection)).thenReturn(mockGenericObject);
        when(mockGenericObject.getData()).thenReturn(mockFareAttribute);
        when(mockGtfsDataRepo.addFareAttribute(mockFareAttribute)).thenReturn(mockFareAttribute);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedFareAttribute, noticeCollection);

        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("fare_id"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("price"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("currency_type"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("payment_method"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("transfers"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("agency_id"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("transfer_duration"));

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build(noticeCollection);

        final ArgumentCaptor<FareAttribute> captor = ArgumentCaptor.forClass(FareAttribute.class);

        verify(mockGtfsDataRepo, times(1)).addFareAttribute(captor.capture());

        final List<FareAttribute> fareAttributeCollection = captor.getAllValues();

        assertEquals(mockFareAttribute, fareAttributeCollection.get(0));

        verifyNoMoreInteractions(mockParsedFareAttribute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void processTwiceSameFareAttributeShouldGenerateNoticeAndNotBeAddedToRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class,
                RETURNS_SELF);
        final ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();

        final var mockGenericObject = mock(GenericType.class);
        final FareAttribute mockFareAttribute = mock(FareAttribute.class);

        when(mockGenericObject.getState()).thenReturn(true);
        when(mockBuilder.build(noticeCollection)).thenReturn(mockGenericObject);
        when(mockGenericObject.getData()).thenReturn(mockFareAttribute);
        when(mockGtfsDataRepo.addFareAttribute(mockFareAttribute)).thenReturn(null);

        when(mockParsedFareAttribute.get("fare_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("price")).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get("currency_type")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("payment_method")).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get("transfers")).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get("agency_id")).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get("transfer_duration")).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedFareAttribute, noticeCollection);

        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("fare_id"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("price"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("currency_type"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("payment_method"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("transfers"));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq("agency_id"));
        verify(mockParsedFareAttribute, times(1))
                .get(ArgumentMatchers.eq("transfer_duration"));
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedFareAttribute, times(1)).getEntityId();

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));

        verify(mockBuilder, times(1)).build(noticeCollection);

        verify(mockGtfsDataRepo, times(1)).addFareAttribute(mockFareAttribute);
        final ArgumentCaptor<EntityMustBeUniqueNotice> captor = ArgumentCaptor.forClass(EntityMustBeUniqueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<EntityMustBeUniqueNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("fare_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedFareAttribute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }
}