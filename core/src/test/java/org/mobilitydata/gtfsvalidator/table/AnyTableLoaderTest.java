package org.mobilitydata.gtfsvalidator.table;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.annotation.FieldLevelEnum;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.notice.*;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestEntity;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestFileValidator;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestTableDescriptor;
import org.mobilitydata.gtfsvalidator.validator.DefaultValidatorProvider;
import org.mobilitydata.gtfsvalidator.validator.ValidationContext;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.TestUtils.toInputStream;
import static org.mobilitydata.gtfsvalidator.TestUtils.validationNoticeTypes;
import static org.mockito.Mockito.*;

public class AnyTableLoaderTest {

  @Rule public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  @Mock private GtfsTableContainer mockContainer;

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
    when(testTableDescriptor.createContainerForInvalidStatus(
            GtfsTableContainer.TableStatus.INVALID_HEADERS))
        .thenReturn(mockContainer);
    NoticeContainer loaderNotices = new NoticeContainer();

    var loadedContainer =
        AnyTableLoader.load(
            testTableDescriptor, mock(ValidatorProvider.class), null, loaderNotices);

    assertThat(validationNoticeTypes(loaderNotices)).containsExactly(CsvParsingFailedNotice.class);
    assertThat(loadedContainer).isEqualTo(mockContainer);
  }

  @Test
  public void emptyInputStream() {
    var testTableDescriptor = mock(GtfsTableDescriptor.class);
    when(testTableDescriptor.gtfsFilename()).thenReturn("filename");
    NoticeContainer loaderNotices = new NoticeContainer();
    when(testTableDescriptor.createContainerForInvalidStatus(
            GtfsTableContainer.TableStatus.EMPTY_FILE))
        .thenReturn(mockContainer);
    InputStream csvInputStream = toInputStream("");
    ValidatorProvider validatorProvider = mock(ValidatorProvider.class);

    var loadedContainer =
        AnyTableLoader.load(testTableDescriptor, validatorProvider, csvInputStream, loaderNotices);

    assertThat(loaderNotices.getValidationNotices())
        .containsExactly(new EmptyFileNotice("filename"));
    assertThat(loadedContainer).isEqualTo(mockContainer);
  }

  @Test
  public void invalidHeaders() {
    var testTableDescriptor = mock(GtfsTableDescriptor.class);
    when(testTableDescriptor.gtfsFilename()).thenReturn("filename");
    when(testTableDescriptor.getColumns())
        .thenReturn(
            ImmutableList.of(
                GtfsColumnDescriptor.builder()
                    .setColumnName("route_id")
                    .setHeaderRequired(true)
                    .setFieldLevel(FieldLevelEnum.REQUIRED)
                    .setIsMixedCase(false)
                    .setIsCached(false)
                    .build()));
    when(testTableDescriptor.createContainerForInvalidStatus(
            GtfsTableContainer.TableStatus.INVALID_HEADERS))
        .thenReturn(mockContainer);
    ValidatorProvider validatorProvider =
        spy(
            new DefaultValidatorProvider(
                mock(ValidationContext.class), mock(ValidatorLoader.class)));
    NoticeContainer loaderNotices = new NoticeContainer();
    InputStream csvInputStream = toInputStream("A file with no headers");

    var loadedContainer =
        AnyTableLoader.load(testTableDescriptor, validatorProvider, csvInputStream, loaderNotices);

    assertThat(loaderNotices.getValidationNotices())
        .containsExactly(
            new UnknownColumnNotice("filename", "A file with no headers", 1),
            new MissingRequiredColumnNotice("filename", "route_id"));
    assertThat(loadedContainer).isEqualTo(mockContainer);
  }

  @Test
  public void invalidRowLengthNotice() {
    var testTableDescriptor = spy(new GtfsTestTableDescriptor());
    when(testTableDescriptor.createContainerForInvalidStatus(
            GtfsTableContainer.TableStatus.UNPARSABLE_ROWS))
        .thenReturn(mockContainer);
    ValidatorProvider validatorProvider =
        new DefaultValidatorProvider(validationContext, ValidatorLoader.createEmpty());
    NoticeContainer loaderNotices = new NoticeContainer();
    InputStream inputStream = toInputStream("id,code\n" + "s1\n");

    var loadedContainer =
        AnyTableLoader.load(testTableDescriptor, validatorProvider, inputStream, loaderNotices);

    assertThat(loaderNotices.getValidationNotices())
        .containsExactly(new InvalidRowLengthNotice("filename.txt", 2, 1, 2));
    assertThat(loadedContainer).isEqualTo(mockContainer);
  }

  @Test
  public void missingRequiredColumn() {
    var testTableDescriptor = spy(new GtfsTestTableDescriptor());
    when(testTableDescriptor.getColumns())
        .thenReturn(
            ImmutableList.of(
                GtfsColumnDescriptor.builder()
                    .setColumnName(GtfsTestEntity.ID_FIELD_NAME)
                    .setHeaderRequired(true)
                    .setFieldLevel(FieldLevelEnum.REQUIRED)
                    .setIsMixedCase(false)
                    .setIsCached(false)
                    .build(),
                GtfsColumnDescriptor.builder()
                    .setColumnName(GtfsTestEntity.CODE_FIELD_NAME)
                    .setHeaderRequired(true)
                    .setFieldLevel(FieldLevelEnum.REQUIRED)
                    .setIsMixedCase(false)
                    .setIsCached(false)
                    .build()));
    GtfsTableContainer mockContainer = mock(GtfsTableContainer.class);
    when(testTableDescriptor.createContainerForInvalidStatus(
            GtfsTableContainer.TableStatus.INVALID_HEADERS))
        .thenReturn(mockContainer);
    ValidatorProvider validatorProvider =
        spy(new DefaultValidatorProvider(validationContext, ValidatorLoader.createEmpty()));
    NoticeContainer loaderNotices = new NoticeContainer();
    InputStream inputStream = toInputStream("id\n" + "s1 \n");

    var loadedContainer =
        AnyTableLoader.load(testTableDescriptor, validatorProvider, inputStream, loaderNotices);

    assertThat(loaderNotices.hasValidationErrors()).isTrue();
    assertThat(loaderNotices.getValidationNotices())
        .containsExactly(new MissingRequiredColumnNotice("filename.txt", "code"));
    assertThat(loadedContainer).isEqualTo(mockContainer);
  }

  @Test
  public void parsableTableRows() {
    var testTableDescriptor = spy(new GtfsTestTableDescriptor());
    ValidatorProvider validatorProvider =
        spy(new DefaultValidatorProvider(validationContext, ValidatorLoader.createEmpty()));
    NoticeContainer loaderNotices = new NoticeContainer();
    GtfsTestFileValidator validator = mock(GtfsTestFileValidator.class);
    when(validatorProvider.createSingleFileValidators(mockContainer))
        .thenReturn(List.of(validator));
    when(testTableDescriptor.createContainerForHeaderAndEntities(any(), any(), any()))
        .thenReturn(mockContainer);
    InputStream inputStream = toInputStream("id,stop_lat,_no_name_\n" + "s1, 23.00, no_value\n");

    var loadedContainer =
        AnyTableLoader.load(testTableDescriptor, validatorProvider, inputStream, loaderNotices);

    assertThat(loaderNotices.getValidationNotices())
        .contains(new UnknownColumnNotice("filename.txt", "_no_name_", 3));
    assertThat(loadedContainer).isEqualTo(mockContainer);
    verify(validator, times(1)).validate(any());
  }

  @Test
  public void missingRequiredField() {
    var testTableDescriptor = spy(new GtfsTestTableDescriptor());
    when(testTableDescriptor.getColumns())
        .thenReturn(
            ImmutableList.of(
                GtfsColumnDescriptor.builder()
                    .setColumnName(GtfsTestEntity.ID_FIELD_NAME)
                    .setHeaderRequired(true)
                    .setFieldLevel(FieldLevelEnum.REQUIRED)
                    .setIsMixedCase(false)
                    .setIsCached(false)
                    .build(),
                GtfsColumnDescriptor.builder()
                    .setColumnName(GtfsTestEntity.CODE_FIELD_NAME)
                    .setHeaderRequired(false)
                    .setFieldLevel(FieldLevelEnum.REQUIRED)
                    .setIsMixedCase(false)
                    .setIsCached(false)
                    .build()));
    when(testTableDescriptor.createContainerForInvalidStatus(
            GtfsTableContainer.TableStatus.UNPARSABLE_ROWS))
        .thenReturn(mockContainer);
    ValidatorProvider validatorProvider =
        spy(new DefaultValidatorProvider(validationContext, ValidatorLoader.createEmpty()));
    NoticeContainer loaderNotices = new NoticeContainer();
    InputStream inputStream = toInputStream("id,code\n" + "s1,\n");

    var loadedContainer =
        AnyTableLoader.load(testTableDescriptor, validatorProvider, inputStream, loaderNotices);

    assertThat(loaderNotices.getValidationNotices())
        .contains(new MissingRequiredFieldNotice("filename.txt", 2, "code"));
    assertThat(loadedContainer).isEqualTo(mockContainer);
  }
}
