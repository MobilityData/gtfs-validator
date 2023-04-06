package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.*;
import static org.mobilitydata.gtfsvalidator.TestUtils.*;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableList;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.annotation.FieldLevelEnum;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.notice.*;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsStop;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsStopTableDescriptor;
import org.mobilitydata.gtfsvalidator.validator.DefaultValidatorProvider;
import org.mobilitydata.gtfsvalidator.validator.ValidationContext;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;

public class AnyTableLoaderTest {

  ValidationContext validationContext;

  @Before
  public void setup() {
    validationContext =
        ValidationContext.builder()
            .setCountryCode(CountryCode.forStringOrUnknown("CA"))
            .setCurrentDateTime(new CurrentDateTime(ZonedDateTime.now(ZoneId.systemDefault())))
            .build();
  }

  @Test
  public void invalidInputStream() {
    var testTableDescriptor = mock(GtfsTableDescriptor.class);
    when(testTableDescriptor.gtfsFilename()).thenReturn("_not_a_valid_file_");
    NoticeContainer loaderNotices = new NoticeContainer();

    AnyTableLoader.load(
        testTableDescriptor,
        mock(ValidatorProvider.class),
        retrieveInputStream("_not_a_valid_file_"),
        loaderNotices);

    assertTrue(loaderNotices.hasValidationErrors());

    assertValidationNotice(loaderNotices, CsvParsingFailedNotice.class);
    assertEquals(1, loaderNotices.getValidationNotices().size());
    verify(testTableDescriptor, times(0)).createContainerForHeaderAndEntities(any(), any(), any());
  }

  @Test
  public void emptyInputStream() {
    var testTableDescriptor = mock(GtfsTableDescriptor.class);
    when(testTableDescriptor.gtfsFilename()).thenReturn("filename");
    NoticeContainer loaderNotices = new NoticeContainer();
    InputStream csvInputStream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
    ValidatorProvider validatorProvider = mock(ValidatorProvider.class);

    AnyTableLoader.load(testTableDescriptor, validatorProvider, csvInputStream, loaderNotices);

    assertTrue(loaderNotices.hasValidationErrors());
    assertEquals(1, loaderNotices.getValidationNotices().size());
    assertThat(loaderNotices.getValidationNotices())
        .containsExactly(new EmptyFileNotice("filename"));
    verify(testTableDescriptor, times(0)).createContainerForHeaderAndEntities(any(), any(), any());
    verify(validatorProvider, times(0)).createSingleFileValidators(any());
  }

  @Test
  public void invalidHeaders() {
    var testTableDescriptor = mock(GtfsTableDescriptor.class);
    when(testTableDescriptor.gtfsFilename()).thenReturn("filename");
    GtfsColumnDescriptor[] columns =
        new GtfsColumnDescriptor[] {
          AutoValue_GtfsColumnDescriptor.builder()
              .setColumnName("route_id")
              .setHeaderRequired(true)
              .setFieldLevel(FieldLevelEnum.REQUIRED)
              .setIsMixedCase(false)
              .setIsCached(false)
              .build()
        };
    when(testTableDescriptor.getColumns()).thenReturn(ImmutableList.copyOf(columns));
    ValidatorProvider validatorProvider =
        spy(
            new DefaultValidatorProvider(
                mock(ValidationContext.class), mock(ValidatorLoader.class)));
    NoticeContainer loaderNotices = new NoticeContainer();
    InputStream csvInputStream =
        new ByteArrayInputStream("A file with no headers".getBytes(StandardCharsets.UTF_8));

    AnyTableLoader.load(testTableDescriptor, validatorProvider, csvInputStream, loaderNotices);

    assertTrue(loaderNotices.hasValidationErrors());
    assertEquals(2, loaderNotices.getValidationNotices().size());
    assertThat(loaderNotices.getValidationNotices())
        .contains(new UnknownColumnNotice("filename", "A file with no headers", 1));
    assertThat(loaderNotices.getValidationNotices())
        .contains(new MissingRequiredColumnNotice("filename", "route_id"));
    verify(testTableDescriptor, times(0)).createContainerForHeaderAndEntities(any(), any(), any());
    verify(validatorProvider, times(0)).createSingleFileValidators(any());
  }

  @Test
  public void invalidRowLengthNotice() {
    var testTableDescriptor = spy(new GtfsStopTableDescriptor());
    ValidatorProvider validatorProvider =
        spy(new DefaultValidatorProvider(validationContext, ValidatorLoader.createEmpty()));
    NoticeContainer loaderNotices = new NoticeContainer();
    InputStream inputStream = toInputStream("stop_id,stop_code\n" + "s1\n");

    AnyTableLoader.load(testTableDescriptor, validatorProvider, inputStream, loaderNotices);

    assertTrue(loaderNotices.hasValidationErrors());
    assertThat(loaderNotices.getValidationNotices())
        .containsExactly(new InvalidRowLengthNotice("stops.txt", 2, 1, 2));
    verify(testTableDescriptor, times(0)).createContainerForHeaderAndEntities(any(), any(), any());
    verify(validatorProvider, times(0)).createSingleFileValidators(any());
  }

  @Test
  public void missingRequiredColumn() {
    var testTableDescriptor = spy(new GtfsStopTableDescriptor());
    GtfsColumnDescriptor[] columns =
        new GtfsColumnDescriptor[] {
          AutoValue_GtfsColumnDescriptor.builder()
              .setColumnName(GtfsStop.STOP_ID_FIELD_NAME)
              .setHeaderRequired(true)
              .setFieldLevel(FieldLevelEnum.REQUIRED)
              .setIsMixedCase(false)
              .setIsCached(false)
              .build(),
          AutoValue_GtfsColumnDescriptor.builder()
              .setColumnName(GtfsStop.STOP_CODE_FIELD_NAME)
              .setHeaderRequired(true)
              .setFieldLevel(FieldLevelEnum.REQUIRED)
              .setIsMixedCase(false)
              .setIsCached(false)
              .build()
        };
    when(testTableDescriptor.getColumns()).thenReturn(ImmutableList.copyOf(columns));
    ValidatorProvider validatorProvider =
        spy(new DefaultValidatorProvider(validationContext, ValidatorLoader.createEmpty()));
    NoticeContainer loaderNotices = new NoticeContainer();
    InputStream inputStream = toInputStream("stop_id\n" + "s1 \n");

    AnyTableLoader.load(testTableDescriptor, validatorProvider, inputStream, loaderNotices);

    assertTrue(loaderNotices.hasValidationErrors());
    assertThat(loaderNotices.getValidationNotices())
        .containsExactly(new MissingRequiredColumnNotice("stops.txt", "stop_code"));
    verify(testTableDescriptor, times(0)).createContainerForHeaderAndEntities(any(), any(), any());
    verify(validatorProvider, times(0)).createSingleFileValidators(any());
  }

  @Test
  public void parsableTableRows() {
    var testTableDescriptor = spy(new GtfsStopTableDescriptor());
    ValidatorProvider validatorProvider =
        spy(new DefaultValidatorProvider(validationContext, ValidatorLoader.createEmpty()));
    NoticeContainer loaderNotices = new NoticeContainer();
    InputStream inputStream =
        toInputStream("stop_id,stop_lat,_no_name_\n" + "s1, 23.00, no_value\n");

    AnyTableLoader.load(testTableDescriptor, validatorProvider, inputStream, loaderNotices);

    assertFalse(loaderNotices.hasValidationErrors());
    assertThat(loaderNotices.getValidationNotices())
        .contains(new UnknownColumnNotice("stops.txt", "_no_name_", 3));
    verify(testTableDescriptor, times(1)).createContainerForHeaderAndEntities(any(), any(), any());
    verify(validatorProvider, times(1)).createSingleFileValidators(any());
  }

  @Test
  public void missingRequiredField() {
    var testTableDescriptor = spy(new GtfsStopTableDescriptor());
    GtfsColumnDescriptor[] columns =
        new GtfsColumnDescriptor[] {
          AutoValue_GtfsColumnDescriptor.builder()
              .setColumnName(GtfsStop.STOP_ID_FIELD_NAME)
              .setHeaderRequired(true)
              .setFieldLevel(FieldLevelEnum.REQUIRED)
              .setIsMixedCase(false)
              .setIsCached(false)
              .build(),
          AutoValue_GtfsColumnDescriptor.builder()
              .setColumnName(GtfsStop.STOP_CODE_FIELD_NAME)
              .setHeaderRequired(false)
              .setFieldLevel(FieldLevelEnum.REQUIRED)
              .setIsMixedCase(false)
              .setIsCached(false)
              .build()
        };
    when(testTableDescriptor.getColumns()).thenReturn(ImmutableList.copyOf(columns));
    ValidatorProvider validatorProvider =
        spy(new DefaultValidatorProvider(validationContext, ValidatorLoader.createEmpty()));
    NoticeContainer loaderNotices = new NoticeContainer();
    InputStream inputStream = toInputStream("stop_id,stop_code\n" + "s1,\n");

    AnyTableLoader.load(testTableDescriptor, validatorProvider, inputStream, loaderNotices);

    assertTrue(loaderNotices.hasValidationErrors());
    assertThat(loaderNotices.getValidationNotices())
        .contains(new MissingRequiredFieldNotice("stops.txt", 2, "stop_code"));
    verify(testTableDescriptor, times(0)).createContainerForHeaderAndEntities(any(), any(), any());
    verify(validatorProvider, times(0)).createSingleFileValidators(any());
  }
}
